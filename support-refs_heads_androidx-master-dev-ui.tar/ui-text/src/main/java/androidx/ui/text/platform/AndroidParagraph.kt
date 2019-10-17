/*
 * Copyright 2018 The Android Open Source Project
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
package androidx.ui.text.platform

import android.text.TextPaint
import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import androidx.text.LayoutCompat.ALIGN_CENTER
import androidx.text.LayoutCompat.ALIGN_LEFT
import androidx.text.LayoutCompat.ALIGN_NORMAL
import androidx.text.LayoutCompat.ALIGN_OPPOSITE
import androidx.text.LayoutCompat.ALIGN_RIGHT
import androidx.text.LayoutCompat.DEFAULT_ALIGNMENT
import androidx.text.LayoutCompat.DEFAULT_JUSTIFICATION_MODE
import androidx.text.LayoutCompat.DEFAULT_LINESPACING_MULTIPLIER
import androidx.text.LayoutCompat.DEFAULT_MAX_LINES
import androidx.text.LayoutCompat.JUSTIFICATION_MODE_INTER_WORD
import androidx.text.TextLayout
import androidx.text.selection.WordBoundary
import androidx.ui.core.Density
import androidx.ui.core.PxPosition
import androidx.ui.engine.geometry.Rect
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Path
import androidx.ui.text.AnnotatedString
import androidx.ui.text.Paragraph
import androidx.ui.text.ParagraphConstraints
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextRange
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextAlign
import androidx.ui.text.style.TextDirection
import java.util.Locale as JavaLocale

/**
 * Android specific implementation for [Paragraph]
 */
internal class AndroidParagraph constructor(
    val paragraphIntrinsics: AndroidParagraphIntrinsics,
    val maxLines: Int?,
    val ellipsis: Boolean?,
    val constraints: ParagraphConstraints
) : Paragraph {

    constructor(
        text: String,
        style: TextStyle,
        paragraphStyle: ParagraphStyle,
        textStyles: List<AnnotatedString.Item<TextStyle>>,
        maxLines: Int?,
        ellipsis: Boolean?,
        constraints: ParagraphConstraints,
        typefaceAdapter: TypefaceAdapter,
        density: Density
    ) : this(
        paragraphIntrinsics = AndroidParagraphIntrinsics(
            text = text,
            style = style,
            paragraphStyle = paragraphStyle,
            textStyles = textStyles,
            typefaceAdapter = typefaceAdapter,
            density = density
        ),
        maxLines = maxLines,
        ellipsis = ellipsis,
        constraints = constraints
    )

    private val layout: TextLayout

    override val width: Float

    init {
        maxLines?.let {
            require(it >= 1) { "maxLines should be greater than 0" }
        }

        val paragraphStyle = paragraphIntrinsics.paragraphStyle

        val alignment = toLayoutAlign(paragraphStyle.textAlign)

        val maxLines = maxLines ?: DEFAULT_MAX_LINES
        val justificationMode = when (paragraphStyle.textAlign) {
            TextAlign.Justify -> JUSTIFICATION_MODE_INTER_WORD
            else -> DEFAULT_JUSTIFICATION_MODE
        }

        val ellipsize = if (ellipsis == true) {
            TextUtils.TruncateAt.END
        } else {
            null
        }

        this.width = constraints.width

        layout = TextLayout(
            charSequence = paragraphIntrinsics.charSequence,
            width = constraints.width,
            textPaint = textPaint,
            ellipsize = ellipsize,
            alignment = alignment,
            textDirectionHeuristic = paragraphIntrinsics.textDirectionHeuristic,
            lineSpacingMultiplier = DEFAULT_LINESPACING_MULTIPLIER,
            maxLines = maxLines,
            justificationMode = justificationMode,
            layoutIntrinsics = paragraphIntrinsics.layoutIntrinsics
        )
    }

    override val height: Float
        get() = layout.let {
            // TODO(haoyuchang): Figure out a way to add bottomPadding properly
            val lineCount = it.lineCount
            if (maxLines != null &&
                maxLines >= 0 &&
                maxLines < lineCount
            ) {
                it.getLineBottom(maxLines - 1)
            } else {
                it.height.toFloat()
            }
        }

    override val maxIntrinsicWidth: Float
        get() = paragraphIntrinsics.maxIntrinsicWidth

    override val minIntrinsicWidth: Float
        get() = paragraphIntrinsics.minIntrinsicWidth

    override val firstBaseline: Float
        get() = layout.getLineBaseline(0)

    override val lastBaseline: Float
        get() = if (maxLines != null && maxLines in 0 until lineCount) {
            layout.getLineBaseline(maxLines - 1)
        } else {
            layout.getLineBaseline(lineCount - 1)
        }

    override val didExceedMaxLines: Boolean
        get() = layout.didExceedMaxLines

    @VisibleForTesting
    internal val textLocale: JavaLocale
        get() = paragraphIntrinsics.textPaint.textLocale

    override val lineCount: Int
        get() = layout.lineCount

    @VisibleForTesting
    internal val charSequence: CharSequence
        get() = paragraphIntrinsics.charSequence

    @VisibleForTesting
    internal val textPaint: TextPaint
        get() = paragraphIntrinsics.textPaint

    override fun getOffsetForPosition(position: PxPosition): Int {
        val line = layout.getLineForVertical(position.y.value.toInt())
        return layout.getOffsetForHorizontal(line, position.x.value)
    }

    /**
     * Returns the bounding box as Rect of the character for given character offset. Rect includes
     * the top, bottom, left and right of a character.
     */
    // TODO:(qqd) Implement RTL case.
    override fun getBoundingBox(offset: Int): Rect {
        val left = layout.getPrimaryHorizontal(offset)
        val right = layout.getPrimaryHorizontal(offset + 1)

        val line = layout.getLineForOffset(offset)
        val top = layout.getLineTop(line)
        val bottom = layout.getLineBottom(line)

        return Rect(top = top, bottom = bottom, left = left, right = right)
    }

    override fun getPathForRange(start: Int, end: Int): Path {
        if (start !in 0..end || end > charSequence.length) {
            throw AssertionError(
                "Start($start) or End($end) is out of Range(0..${charSequence.length})," +
                        " or start > end!"
            )
        }
        val path = android.graphics.Path()
        layout.getSelectionPath(start, end, path)
        return Path(path)
    }

    override fun getCursorRect(offset: Int): Rect {
        if (offset !in 0..charSequence.length) {
            throw AssertionError("offset($offset) is out of bounds (0,${charSequence.length}")
        }
        // TODO(nona): Support cursor drawable.
        val cursorWidth = 4.0f
        val horizontal = layout.getPrimaryHorizontal(offset)
        val line = layout.getLineForOffset(offset)

        return Rect(
            horizontal - 0.5f * cursorWidth,
            layout.getLineTop(line),
            horizontal + 0.5f * cursorWidth,
            layout.getLineBottom(line)
        )
    }

    private val wordBoundary: WordBoundary by lazy {
        WordBoundary(textLocale, layout.text)
    }

    override fun getWordBoundary(offset: Int): TextRange {
        return TextRange(wordBoundary.getWordStart(offset), wordBoundary.getWordEnd(offset))
    }

    override fun getLineLeft(lineIndex: Int): Float = layout.getLineLeft(lineIndex)

    override fun getLineRight(lineIndex: Int): Float = layout.getLineRight(lineIndex)

    override fun getLineBottom(lineIndex: Int): Float = layout.getLineBottom(lineIndex)

    override fun getLineHeight(lineIndex: Int): Float = layout.getLineHeight(lineIndex)

    override fun getLineWidth(lineIndex: Int): Float = layout.getLineWidth(lineIndex)

    override fun getLineForOffset(offset: Int): Int = layout.getLineForOffset(offset)

    override fun getPrimaryHorizontal(offset: Int): Float =
        layout.getPrimaryHorizontal(offset)

    override fun getSecondaryHorizontal(offset: Int): Float =
        layout.getSecondaryHorizontal(offset)

    override fun getParagraphDirection(offset: Int): TextDirection {
        val lineIndex = layout.getLineForOffset(offset)
        val direction = layout.getParagraphDirection(lineIndex)
        return if (direction == 1) TextDirection.Ltr else TextDirection.Rtl
    }

    override fun getBidiRunDirection(offset: Int): TextDirection {
        return if (layout.isRtlCharAt(offset)) TextDirection.Rtl else TextDirection.Ltr
    }

    /**
     * @return true if the given line is ellipsized, else false.
     */
    internal fun isEllipsisApplied(lineIndex: Int): Boolean =
        layout.isEllipsisApplied(lineIndex)

    override fun paint(canvas: Canvas) {
        val nativeCanvas = canvas.nativeCanvas
        if (didExceedMaxLines) {
            nativeCanvas.save()
            nativeCanvas.clipRect(0f, 0f, width, height)
        }
        layout.paint(nativeCanvas)
        if (didExceedMaxLines) {
            nativeCanvas.restore()
        }
    }
}

/**
 * Converts [TextAlign] into [TextLayout] alignment constants.
 */
private fun toLayoutAlign(align: TextAlign?): Int = when (align) {
    TextAlign.Left -> ALIGN_LEFT
    TextAlign.Right -> ALIGN_RIGHT
    TextAlign.Center -> ALIGN_CENTER
    TextAlign.Start -> ALIGN_NORMAL
    TextAlign.End -> ALIGN_OPPOSITE
    else -> DEFAULT_ALIGNMENT
}
