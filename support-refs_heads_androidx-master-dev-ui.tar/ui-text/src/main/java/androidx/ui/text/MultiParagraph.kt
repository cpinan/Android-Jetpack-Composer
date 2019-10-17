/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.ui.text

import androidx.ui.core.Density
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.px
import androidx.ui.engine.geometry.Offset
import androidx.ui.engine.geometry.Rect
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Path
import androidx.ui.text.font.Font
import androidx.ui.text.style.TextDirection
import kotlin.math.max

/**
 * Lays out and renders multiple paragraphs at once. Unlike [Paragraph], supports multiple
 * [ParagraphStyle]s in a given text.
 *
 * @param intrinsics previously calculated text intrinsics
 * @param maxLines the maximum number of lines that the text can have
 * @param ellipsis whether to ellipsize text, applied only when [maxLines] is set
 */
internal class MultiParagraph(
    val intrinsics: MultiParagraphIntrinsics,
    val maxLines: Int? = null,
    ellipsis: Boolean? = null,
    constraints: ParagraphConstraints
) {

    /**
     *  Lays out a given [annotatedString] with the given constraints. Unlike a [Paragraph],
     *  [MultiParagraph] can handle a text what has multiple paragraph styles.
     *
     * @param annotatedString the text to be laid out
     * @param textStyle the [TextStyle] to be applied to the whole text
     * @param paragraphStyle the [ParagraphStyle] to be applied to the whole text
     * @param maxLines the maximum number of lines that the text can have
     * @param ellipsis whether to ellipsize text, applied only when [maxLines] is set
     * @param constraints how wide the text is allowed to be
     * @param density density of the device
     * @param resourceLoader [Font.ResourceLoader] to be used to load the font given in [TextStyle]s
     *
     * @throws IllegalArgumentException if [ParagraphStyle.textDirectionAlgorithm] is not set
     */
    constructor(
        annotatedString: AnnotatedString,
        textStyle: TextStyle,
        paragraphStyle: ParagraphStyle,
        maxLines: Int? = null,
        ellipsis: Boolean? = null,
        constraints: ParagraphConstraints,
        density: Density,
        resourceLoader: Font.ResourceLoader
    ) : this(
        intrinsics = MultiParagraphIntrinsics(
            annotatedString = annotatedString,
            textStyle = textStyle,
            paragraphStyle = paragraphStyle,
            density = density,
            resourceLoader = resourceLoader
        ),
        maxLines = maxLines,
        ellipsis = ellipsis,
        constraints = constraints
    )

    private val annotatedString get() = intrinsics.annotatedString

    /**
     * The width for text if all soft wrap opportunities were taken.
     */
    val minIntrinsicWidth: Float
        get() = paragraphInfoList.foldRight(0f) { paragraphInfo, minWidth ->
            max(paragraphInfo.paragraph.minIntrinsicWidth, minWidth)
        }

    /**
     * Returns the smallest width beyond which increasing the width never
     * decreases the height.
     */
    val maxIntrinsicWidth: Float
        get() = paragraphInfoList.foldRight(0f) { paragraphInfo, maxWidth ->
            max(paragraphInfo.paragraph.maxIntrinsicWidth, maxWidth)
        }

    /**
     * True if there is more vertical content, but the text was truncated, either
     * because we reached `maxLines` lines of text or because the `maxLines` was
     * null, `ellipsis` was not null, and one of the lines exceeded the width
     * constraint.
     *
     * See the discussion of the `maxLines` and `ellipsis` arguments at [ParagraphStyle].
     */
    val didExceedMaxLines: Boolean

    /**
     * The amount of horizontal space this paragraph occupies.
     */
    val width: Float

    /**
     * The amount of vertical space this paragraph occupies.
     *
     * Valid only after [layout] has been called.
     */
    val height: Float

    /**
     * The distance from the top of the paragraph to the alphabetic
     * baseline of the first line, in logical pixels.
     */
    val firstBaseline: Float
        get() {
            return if (paragraphInfoList.isEmpty()) {
                0f
            } else {
                paragraphInfoList[0].paragraph.firstBaseline
            }
        }

    /**
     * The distance from the top of the paragraph to the alphabetic
     * baseline of the first line, in logical pixels.
     */
    val lastBaseline: Float
        get() {
            return if (paragraphInfoList.isEmpty()) {
                0f
            } else {
                paragraphInfoList.last().paragraph.lastBaseline
            }
        }

    /** The total number of lines in the text. */
    val lineCount: Int

    private val paragraphInfoList: List<ParagraphInfo>

    init {
        // create sub paragraphs and layouts
        this.paragraphInfoList = intrinsics.infoList.map {
            ParagraphInfo(
                paragraph = Paragraph(
                    it.intrinsics,
                    maxLines,
                    ellipsis,
                    constraints
                ),
                startIndex = it.startIndex,
                endIndex = it.endIndex
            )
        }

        // final layout
        var didExceedMaxLines = false
        var currentLineCount = 0
        var currentHeight = 0f

        for ((index, paragraphInfo) in paragraphInfoList.withIndex()) {
            val paragraph = paragraphInfo.paragraph

            paragraphInfo.startLineIndex = currentLineCount
            paragraphInfo.endLineIndex = currentLineCount + paragraph.lineCount
            currentLineCount = paragraphInfo.endLineIndex

            paragraphInfo.top = currentHeight.px
            paragraphInfo.bottom = (currentHeight + paragraph.height).px
            currentHeight += paragraph.height

            // TODO(haoyuchang): solve the corner case where the ellipsis won't be applied when
            //  currentLineNum == maxLines but there are still more paragraphs
            if (paragraph.didExceedMaxLines ||
                (currentLineCount == maxLines && index != this.paragraphInfoList.lastIndex)
            ) {
                didExceedMaxLines = true
                break
            }
        }
        this.didExceedMaxLines = didExceedMaxLines
        this.lineCount = currentLineCount
        this.height = currentHeight
        this.width = constraints.width
    }

    /** Paint the paragraphs to canvas. */
    fun paint(canvas: Canvas) {
        canvas.save()
        paragraphInfoList.forEach {
            it.paragraph.paint(canvas)
            canvas.translate(0f, it.paragraph.height)
        }
        canvas.restore()
    }

    /** Returns path that enclose the given text range. */
    fun getPathForRange(start: Int, end: Int): Path {
        if (start !in 0..end || end > annotatedString.text.length) {
            throw AssertionError(
                "Start($start) or End($end) is out of range [0..${annotatedString.text.length})," +
                        " or start > end!"
            )
        }

        if (start == end) return Path()

        val paragraphIndex = findParagraphByIndex(paragraphInfoList, start)
        val path = Path()

        paragraphInfoList.drop(paragraphIndex)
            .takeWhile { it.startIndex < end }
            .filterNot { it.startIndex == it.endIndex }
            .forEach {
                with(it) {
                    path.addPath(
                        path = paragraph.getPathForRange(
                            start = start.toLocalIndex(),
                            end = end.toLocalIndex()
                        ).toGlobal()
                    )
                }
            }
        return path
    }

    /** Returns the character offset closest to the given graphical position. */
    fun getOffsetForPosition(position: PxPosition): Int {
        val paragraphIndex = when {
            position.y.value <= 0f -> 0
            position.y.value >= height -> paragraphInfoList.lastIndex
            else -> findParagraphByY(paragraphInfoList, position.y)
        }
        return with(paragraphInfoList[paragraphIndex]) {
            if (length == 0) {
                max(0, startIndex - 1)
            } else {
                paragraph.getOffsetForPosition(position.toLocal()).toGlobalIndex()
            }
        }
    }

    /**
     * Returns the bounding box as Rect of the character for given character offset. Rect
     * includes the top, bottom, left and right of a character.
     */
    fun getBoundingBox(offset: Int): Rect {
        assertIndexInRange(offset)

        val paragraphIndex = findParagraphByIndex(paragraphInfoList, offset)
        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getBoundingBox(offset.toLocalIndex()).toGlobal()
        }
    }

    /** Get the primary horizontal position for the specified text offset. */
    fun getPrimaryHorizontal(offset: Int): Float {
        if (offset !in 0..annotatedString.text.length) {
            throw AssertionError("offset($offset) is out of bounds " +
                    "(0,${annotatedString.text.length}")
        }

        val paragraphIndex = if (offset == annotatedString.text.length) {
            paragraphInfoList.lastIndex
        } else {
            findParagraphByIndex(paragraphInfoList, offset)
        }

        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getPrimaryHorizontal(offset.toLocalIndex())
        }
    }

    /** Get the secondary horizontal position for the specified text offset. */
    fun getSecondaryHorizontal(offset: Int): Float {
        if (offset !in 0..annotatedString.text.length) {
            throw AssertionError("offset($offset) is out of bounds " +
                    "(0,${annotatedString.text.length}")
        }

        val paragraphIndex = if (offset == annotatedString.text.length) {
            paragraphInfoList.lastIndex
        } else {
            findParagraphByIndex(paragraphInfoList, offset)
        }

        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getSecondaryHorizontal(offset.toLocalIndex())
        }
    }

    /**
     * Get the text direction of the paragraph containing the given offset.
     */
    fun getParagraphDirection(offset: Int): TextDirection {
        if (offset !in 0..annotatedString.text.length) {
            throw AssertionError("offset($offset) is out of bounds " +
                    "(0,${annotatedString.text.length}")
        }

        val paragraphIndex = if (offset == annotatedString.text.length) {
            paragraphInfoList.lastIndex
        } else {
            findParagraphByIndex(paragraphInfoList, offset)
        }

        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getParagraphDirection(offset.toLocalIndex())
        }
    }

    /**
     * Get the text direction of the character at the given offset.
     */
    fun getBidiRunDirection(offset: Int): TextDirection {
        if (offset !in 0..annotatedString.text.length) {
            throw AssertionError("offset($offset) is out of bounds " +
                    "(0,${annotatedString.text.length}")
        }

        val paragraphIndex = if (offset == annotatedString.text.length) {
            paragraphInfoList.lastIndex
        } else {
            findParagraphByIndex(paragraphInfoList, offset)
        }

        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getBidiRunDirection(offset.toLocalIndex())
        }
    }

    /**
     * Returns the TextRange of the word at the given character offset. Characters not
     * part of a word, such as spaces, symbols, and punctuation, have word breaks
     * on both sides. In such cases, this method will return TextRange(offset, offset+1).
     * Word boundaries are defined more precisely in Unicode Standard Annex #29
     * http://www.unicode.org/reports/tr29/#Word_Boundaries
     */
    fun getWordBoundary(offset: Int): TextRange {
        assertIndexInRange(offset)

        val paragraphIndex = findParagraphByIndex(paragraphInfoList, offset)

        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getWordBoundary(offset.toLocalIndex()).toGlobal()
        }
    }

    /** Returns rectangle of the cursor area. */
    fun getCursorRect(offset: Int): Rect {
        if (offset !in 0..annotatedString.text.length) {
            throw AssertionError("offset($offset) is out of bounds " +
                    "(0,${annotatedString.text.length}")
        }

        val paragraphIndex = if (offset == annotatedString.text.length) {
            paragraphInfoList.lastIndex
        } else {
            findParagraphByIndex(paragraphInfoList, offset)
        }

        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getCursorRect(offset.toLocalIndex()).toGlobal()
        }
    }

    /**
     * Returns the line number on which the specified text offset appears.
     * If you ask for a position before 0, you get 0; if you ask for a position
     * beyond the end of the text, you get the last line.
     */
    fun getLineForOffset(offset: Int): Int {
        assertIndexInRange(offset)

        val paragraphIndex = findParagraphByIndex(paragraphInfoList, offset)
        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getLineForOffset(offset.toLocalIndex()).toGlobalLineIndex()
        }
    }

    /** Returns the left x Coordinate of the given line. */
    fun getLineLeft(lineIndex: Int): Float {
        assertLineIndexInRange(lineIndex)

        val paragraphIndex = findParagraphByLineIndex(paragraphInfoList, lineIndex)

        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getLineLeft(lineIndex.toLocalLineIndex())
        }
    }

    /** Returns the right x Coordinate of the given line. */
    fun getLineRight(lineIndex: Int): Float {
        assertLineIndexInRange(lineIndex)

        val paragraphIndex = findParagraphByLineIndex(paragraphInfoList, lineIndex)

        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getLineRight(lineIndex.toLocalLineIndex())
        }
    }

    /** Returns the bottom y coordinate of the given line. */
    fun getLineBottom(lineIndex: Int): Float {
        assertLineIndexInRange(lineIndex)

        val paragraphIndex = findParagraphByLineIndex(paragraphInfoList, lineIndex)

        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getLineBottom(lineIndex.toLocalLineIndex())
        }
    }

    /** Returns the height of the given line. */
    fun getLineHeight(lineIndex: Int): Float {
        assertLineIndexInRange(lineIndex)

        val paragraphIndex = findParagraphByLineIndex(paragraphInfoList, lineIndex)

        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getLineHeight(lineIndex.toLocalLineIndex())
        }
    }

    /** Returns the width of the given line. */
    fun getLineWidth(lineIndex: Int): Float {
        assertLineIndexInRange(lineIndex)

        val paragraphIndex = findParagraphByLineIndex(paragraphInfoList, lineIndex)

        return with(paragraphInfoList[paragraphIndex]) {
            paragraph.getLineWidth(lineIndex.toLocalLineIndex())
        }
    }

    private fun assertIndexInRange(offset: Int) {
        if (offset !in (0 until annotatedString.text.length)) {
            throw IndexOutOfBoundsException("offset($offset) is out of bounds" +
                    " [0, ${annotatedString.text.length})")
        }
    }

    private fun assertLineIndexInRange(lineIndex: Int) {
        if (lineIndex !in (0 until lineCount)) {
            throw IndexOutOfBoundsException("lineIndex($lineIndex) is out of bounds" +
                    " [0, $lineIndex)")
        }
    }
}

/**
 * Given an character index of [MultiParagraph.annotatedString], find the corresponding
 * [ParagraphInfo] which covers the provided index.
 *
 * @param paragraphInfoList The list of [ParagraphInfo] containing the information of each
 *  paragraph in the [MultiParagraph].
 * @param index The target index in the [MultiParagraph]. It should be in the range of
 *  [0, text.length)
 * @return The index of the target [ParagraphInfo] in [paragraphInfoList].
 */
private fun findParagraphByIndex(paragraphInfoList: List<ParagraphInfo>, index: Int): Int {
    return paragraphInfoList.binarySearch { paragraphInfo ->
        when {
            paragraphInfo.startIndex > index -> 1
            paragraphInfo.endIndex <= index -> -1
            else -> 0
        }
    }
}

/**
 * Given the y graphical position relative to this [MultiParagraph], find the index of the
 * corresponding [ParagraphInfo] which occupies the provided position.
 *
 * @param paragraphInfoList The list of [ParagraphInfo] containing the information of each
 *  paragraph in the [MultiParagraph].
 * @param y The y coordinate position relative to the [MultiParagraph]. It should be in the range
 *  of [0, [MultiParagraph.height]].
 * @return The index of the target [ParagraphInfo] in [paragraphInfoList].
 */
private fun findParagraphByY(paragraphInfoList: List<ParagraphInfo>, y: Px): Int {
    return paragraphInfoList.binarySearch { paragraphInfo ->
        when {
            paragraphInfo.top > y -> 1
            paragraphInfo.bottom <= y -> -1
            else -> 0
        }
    }
}

/**
 * Given an line index in [MultiParagraph], find the corresponding [ParagraphInfo] which
 * covers the provided line index.
 *
 * @param paragraphInfoList The list of [ParagraphInfo] containing the information of each
 *  paragraph in the [MultiParagraph].
 * @param lineIndex The target line index in the [MultiParagraph], it should be in the range of
 *  [0, [MultiParagraph.lineCount])
 * @return The index of the target [ParagraphInfo] in [paragraphInfoList].
 */
private fun findParagraphByLineIndex(paragraphInfoList: List<ParagraphInfo>, lineIndex: Int): Int {
    return paragraphInfoList.binarySearch { paragraphInfo ->
        when {
            paragraphInfo.startLineIndex > lineIndex -> 1
            paragraphInfo.endLineIndex <= lineIndex -> -1
            else -> 0
        }
    }
}

/**
 * This is a helper data structure to store the information of a single [Paragraph] in an
 * [MultiParagraph]. It's mainly used to convert a global index, lineNumber and [Offset] to the
 * local ones inside the [paragraph], and vice versa.
 *
 * @param paragraph The [Paragraph] object corresponding to this [ParagraphInfo].
 * @param startIndex The start index of this paragraph in the parent [MultiParagraph], inclusive.
 * @param endIndex The end index of this paragraph in the parent [MultiParagraph], exclusive.
 * @param startLineIndex The start line index of this paragraph in the parent [MultiParagraph],
 *  inclusive.
 * @param endLineIndex The end line index of this paragraph in the parent [MultiParagraph],
 *  exclusive.
 * @param top The top position of the [paragraph] relative to the parent [MultiParagraph].
 * @param bottom The bottom position of the [paragraph] relative to the parent [MultiParagraph].
 */
internal data class ParagraphInfo(
    val paragraph: Paragraph,
    val startIndex: Int,
    val endIndex: Int,
    var startLineIndex: Int = -1,
    var endLineIndex: Int = -1,
    var top: Px = (-1).px,
    var bottom: Px = (-1).px
) {

    /**
     * The length of the text in the covered by this paragraph.
     */
    val length
        get() = endIndex - startIndex

    /**
     * Convert an index in the parent [MultiParagraph] to the local index in the [paragraph].
     */
    fun Int.toLocalIndex(): Int {
        return this.coerceIn(startIndex, endIndex) - startIndex
    }

    /**
     * Convert a local index in the [paragraph] to the global index in the parent [MultiParagraph].
     */
    fun Int.toGlobalIndex(): Int {
        return this + startIndex
    }

    /**
     * Convert a line index in the parent [MultiParagraph] to the local line index in the
     * [paragraph].
     *
     */
    fun Int.toLocalLineIndex(): Int {
        return this - startLineIndex
    }

    /**
     * Convert a local line index in the [paragraph] to the global line index in the parent
     * [MultiParagraph].
     */
    fun Int.toGlobalLineIndex(): Int {
        return this + startLineIndex
    }

    /**
     * Convert a [PxPosition] relative to the parent [MultiParagraph] to the local [PxPosition]
     * relative to the [paragraph].
     */
    fun PxPosition.toLocal(): PxPosition {
        return PxPosition(x = x, y = y - top)
    }

    /**
     * Convert a [Rect] relative to the [paragraph] to the [Rect] relative to the parent
     * [MultiParagraph].
     */
    fun Rect.toGlobal(): Rect {
        return shift(Offset(dx = 0f, dy = this@ParagraphInfo.top.value))
    }

    /**
     * Convert a [Path] relative to the [paragraph] to the [Path] relative to the parent
     * [MultiParagraph].
     *
     * Notice that this function changes the input value.
     */
    fun Path.toGlobal(): Path {
        shift(Offset(dx = 0f, dy = top.value))
        return this
    }

    /**
     * Convert a [TextRange] in to the [paragraph] to the [TextRange] in the parent
     * [MultiParagraph].
     */
    fun TextRange.toGlobal(): TextRange {
        return TextRange(start = start.toGlobalIndex(), end = end.toGlobalIndex())
    }
}