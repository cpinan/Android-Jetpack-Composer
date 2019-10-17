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

import androidx.ui.text.AnnotatedString.Builder
import java.util.SortedSet

/**
 * The basic data structure of text with multiple styles. To construct an [AnnotatedString] you
 * can use [Builder].
 */
data class AnnotatedString(
    val text: String,
    val textStyles: List<Item<TextStyle>> = listOf(),
    val paragraphStyles: List<Item<ParagraphStyle>> = listOf()
) {

    init {
        var lastStyleEnd = -1
        for (paragraphStyle in paragraphStyles) {
            require(paragraphStyle.start >= lastStyleEnd) { "ParagraphStyle should not overlap" }
            require(paragraphStyle.end <= text.length) {
                "ParagraphStyle range [${paragraphStyle.start}, ${paragraphStyle.end})" +
                        " is out of boundary"
            }
            lastStyleEnd = paragraphStyle.end
        }
    }

    override fun toString(): String = text

    /**
     * The information attached on the text such as a TextStyle.
     *
     * @param style The style being applied on this part of [AnnotatedString].
     * @param start The start of the range where [style] takes effect. It's inclusive.
     * @param end The end of the range where [style] takes effect. It's exclusive.
     */
    // TODO(haoyuchang): Check some other naming options.
    data class Item<T>(val style: T, val start: Int, val end: Int) {
        init {
            require(start <= end) { "Reversed range is not supported" }
        }
    }

    /**
     * Builder class for AnnotatedString. Enables construction of an [AnnotatedString] using
     * methods such as [append] and [addStyle].
     *
     * @sample androidx.ui.text.samples.AnnotatedStringBuilderSample
     */
    class Builder() {

        private data class MutableItem<T>(
            val style: T,
            val start: Int,
            var end: Int = Int.MIN_VALUE
        ) {
            /**
             * Create an immutable [Item] object.
             *
             * @param defaultEnd if the end is not set yet, it will be set to this value.
             */
            fun toItem(defaultEnd: Int = Int.MIN_VALUE): Item<T> {
                val end = if (end == Int.MIN_VALUE) defaultEnd else end
                check(end != Int.MIN_VALUE) { "Item.end should be set first" }
                return Item(style = style, start = start, end = end)
            }
        }

        private val text: StringBuilder = StringBuilder()
        private val textStyles: MutableList<MutableItem<TextStyle>> = mutableListOf()
        private val paragraphStyles: MutableList<MutableItem<ParagraphStyle>> = mutableListOf()
        private val styleStack: MutableList<MutableItem<out Any>> = mutableListOf()

        constructor(text: String) : this() {
            append(text)
        }

        /**
         * Create an [Builder] instance
         */
        constructor(text: AnnotatedString) : this() {
            append(text)
        }

        /**
         * Returns the length of the [String].
         */
        val length: Int get() = text.length

        override fun toString(): String = text.toString()

        /**
         * Set a [TextStyle] for the given [range].
         *
         * @param style [TextStyle] to be applied
         * @param start the inclusive starting offset of the range
         * @param end the exclusive end offset of the range
         */
        fun addStyle(style: TextStyle, start: Int, end: Int) {
            textStyles.add(MutableItem(style = style, start = start, end = end))
        }

        /**
         * Set a [ParagraphStyle] for the given [range]. When a [ParagraphStyle] is applied to the
         * [AnnotatedString], it will be rendered as a separate paragraph.
         *
         * @param style [ParagraphStyle] to be applied.
         * @param start the inclusive starting offset of the range
         * @param end the exclusive end offset of the range
         */
        fun addStyle(style: ParagraphStyle, start: Int, end: Int) {
            paragraphStyles.add(MutableItem(style = style, start = start, end = end))
        }

        /**
         * Appends the given [String] to this [Builder].
         *
         * @param text the text to append
         */
        fun append(text: String) {
            this.text.append(text)
        }

        /**
         * Appends the given [AnnotatedString] to this [Builder].
         *
         * @param text the text to append
         */
        fun append(text: AnnotatedString) {
            val start = this.text.length
            this.text.append(text.text)
            // offset every style with start and add to the builder
            text.textStyles.forEach {
                addStyle(it.style, start + it.start, start + it.end)
            }
            text.paragraphStyles.forEach {
                addStyle(it.style, start + it.start, start + it.end)
            }
        }

        /**
         * Applies the given [TextStyle] to any appended text until a corresponding [pop] is called.
         *
         * @sample androidx.ui.text.samples.AnnotatedStringBuilderPushSample
         *
         * @param style TextStyle to be applied
         */
        fun push(style: TextStyle): Int {
            MutableItem(style = style, start = text.length).also {
                styleStack.add(it)
                textStyles.add(it)
            }
            return styleStack.size - 1
        }

        /**
         * Applies the given [ParagraphStyle] to any appended text until a corresponding [pop] is
         * called.
         *
         * @sample androidx.ui.text.samples.AnnotatedStringBuilderPushParagraphStyleSample
         *
         * @param style ParagraphStyle to be applied
         */
        fun push(style: ParagraphStyle): Int {
            MutableItem(style = style, start = text.length).also {
                styleStack.add(it)
                paragraphStyles.add(it)
            }
            return styleStack.size - 1
        }

        /**
         * Ends the style that was added via a push operation before.
         *
         * @see push
         */
        fun pop() {
            check(styleStack.isNotEmpty()) { "Nothing to pop." }
            // pop the last element
            val item = styleStack.removeAt(styleStack.size - 1)
            item.end = text.length
        }

        /**
         * Ends the styles up to and `including` the [push] that returned the given index.
         *
         * @param index
         *
         * @see pop
         * @see push
         */
        fun pop(index: Int) {
            check(index < styleStack.size) { "$index should be less than ${styleStack.size}" }
            while ((styleStack.size - 1) >= index) {
                pop()
            }
        }

        /**
         * Constructs an [AnnotatedString] based on the configurations applied to the [Builder].
         */
        fun toAnnotatedString(): AnnotatedString {
            return AnnotatedString(
                text = text.toString(),
                textStyles = textStyles.map { it.toItem(text.length) }.toList(),
                paragraphStyles = paragraphStyles.map { it.toItem(text.length) }.toList()
            )
        }
    }
}

/**
 * A helper function used to determine the paragraph boundaries in [MultiParagraph].
 *
 * It reads paragraph information from [AnnotatedString.paragraphStyles] where only some parts of
 * text has [ParagraphStyle] specified, and unspecified parts(gaps between specified paragraphs)
 * are considered as default paragraph with default [ParagraphStyle].
 * For example, the following string with a specified paragraph denoted by "[]"
 *      "Hello WorldHi!"
 *      [          ]
 * The result paragraphs are "Hello World" and "Hi!".
 *
 * @param defaultParagraphStyle The default [ParagraphStyle]. It's used for both unspecified
 *  default paragraphs and specified paragraph. When a specified paragraph's [ParagraphStyle] has
 *  a null attribute, the default one will be used instead.
 */
internal fun AnnotatedString.normalizedParagraphStyles(
    defaultParagraphStyle: ParagraphStyle
): List<AnnotatedString.Item<ParagraphStyle>> {
    val length = text.length
    val paragraphStyles = paragraphStyles

    var lastOffset = 0
    val result = mutableListOf<AnnotatedString.Item<ParagraphStyle>>()
    for ((style, start, end) in paragraphStyles) {
        if (start != lastOffset) {
            result.add(AnnotatedString.Item(defaultParagraphStyle, lastOffset, start))
        }
        result.add(AnnotatedString.Item(defaultParagraphStyle.merge(style), start, end))
        lastOffset = end
    }
    if (lastOffset != length) {
        result.add(AnnotatedString.Item(defaultParagraphStyle, lastOffset, length))
    }
    // This is a corner case where annotatedString is an empty string without any ParagraphStyle.
    // In this case, a dummy ParagraphStyle is created.
    if (result.isEmpty()) {
        result.add(AnnotatedString.Item(defaultParagraphStyle, 0, 0))
    }
    return result
}

/**
 * Helper function used to find the [TextStyle]s in the given paragraph range and also convert the
 * range of those [TextStyle]s to paragraph local range.
 *
 * @param start The start index of the paragraph range, inclusive.
 * @param end The end index of the paragraph range, exclusive.
 * @return The list of converted [TextStyle]s in the given paragraph range.
 */
private fun AnnotatedString.getLocalStyles(
    start: Int,
    end: Int
): List<AnnotatedString.Item<TextStyle>> {
    if (start == end) {
        return listOf()
    }
    // If the given range covers the whole AnnotatedString, return textStyles without conversion.
    if (start == 0 && end >= this.text.length) {
        return textStyles
    }
    return textStyles.filter { it.start < end && it.end > start }
        .map {
            AnnotatedString.Item(
                it.style,
                it.start.coerceIn(start, end) - start,
                it.end.coerceIn(start, end) - start
            )
        }
}

/**
 * Helper function used to return another AnnotatedString that is a substring from [start] to
 * [end]. This will ignore the [ParagraphStyle]s and the resulting [AnnotatedString] will have no
 * [ParagraphStyle]s.
 *
 * @param start The start index of the paragraph range, inclusive.
 * @param end The end index of the paragraph range, exclusive.
 * @return The list of converted [TextStyle]s in the given paragraph range.
 */
private fun AnnotatedString.substringWithoutParagraphStyles(
    start: Int,
    end: Int
): AnnotatedString {
    return AnnotatedString(
        text = if (start != end) text.substring(start, end) else "",
        textStyles = getLocalStyles(start, end)
    )
}

internal inline fun <T> AnnotatedString.mapEachParagraphStyle(
    defaultParagraphStyle: ParagraphStyle,
    crossinline block: (
        annotatedString: AnnotatedString,
        paragraphStyle: AnnotatedString.Item<ParagraphStyle>
    ) -> T
): List<T> {
    return normalizedParagraphStyles(defaultParagraphStyle).map { paragraphStyleItem ->
        val annotatedString = substringWithoutParagraphStyles(
            paragraphStyleItem.start,
            paragraphStyleItem.end
        )
        block(annotatedString, paragraphStyleItem)
    }
}

/**
 * Create upper case transformed [AnnotatedString]
 *
 * The uppercase sometimes maps different number of characters. This function adjusts the text
 * style and paragraph style ranges to transformed offset.
 *
 * Note, if the style's offset is middle of the uppercase mapping context, this function won't
 * transform the character, e.g. style starts from between base alphabet character and accent
 * character.
 *
 * @param localeList A locale list used for upper case mapping. Only the first locale is
 *                   effective. If empty locale list is passed, use the current locale instead.
 * @return A uppercase transformed string.
 */
fun AnnotatedString.toUpperCase(localeList: LocaleList = LocaleList.current): AnnotatedString {
    return transform { str, start, end -> str.substring(start, end).toUpperCase(localeList) }
}

/**
 * Create lower case transformed [AnnotatedString]
 *
 * The lowercase sometimes maps different number of characters. This function adjusts the text
 * style and paragraph style ranges to transformed offset.
 *
 * Note, if the style's offset is middle of the lowercase mapping context, this function won't
 * transform the character, e.g. style starts from between base alphabet character and accent
 * character.
 *
 * @param localeList A locale list used for lower case mapping. Only the first locale is
 *                   effective. If empty locale list is passed, use the current locale instead.
 * @return A lowercase transformed string.
 */
fun AnnotatedString.toLowerCase(localeList: LocaleList = LocaleList.current): AnnotatedString {
    return transform { str, start, end -> str.substring(start, end).toLowerCase(localeList) }
}

/**
 * Create capitalized [AnnotatedString]
 *
 * The capitalization sometimes maps different number of characters. This function adjusts the
 * text style and paragraph style ranges to transformed offset.
 *
 * Note, if the style's offset is middle of the capitalization context, this function won't
 * transform the character, e.g. style starts from between base alphabet character and accent
 * character.
 *
 * @param localeList A locale list used for capitalize mapping. Only the first locale is
 *                   effective. If empty locale list is passed, use the current locale instead.
 *                   Note that, this locale is currently ignored since underlying Kotlin method
 *                   is experimental.
 * @return A capitalized string.
 */
fun AnnotatedString.capitalize(localeList: LocaleList = LocaleList.current): AnnotatedString {
    return transform { str, start, end ->
        if (start == 0) {
            str.substring(start, end).capitalize(localeList)
        } else {
            str.substring(start, end)
        }
    }
}

/**
 * Create capitalized [AnnotatedString]
 *
 * The decapitalization sometimes maps different number of characters. This function adjusts
 * the text style and paragraph style ranges to transformed offset.
 *
 * Note, if the style's offset is middle of the decapitalization context, this function won't
 * transform the character, e.g. style starts from between base alphabet character and accent
 * character.
 *
 * @param localeList A locale list used for decapitalize mapping. Only the first locale is
 *                   effective. If empty locale list is passed, use the current locale instead.
 *                   Note that, this locale is currently ignored since underlying Kotlin method
 *                   is experimental.
 * @return A decapitalized string.
 */
fun AnnotatedString.decapitalize(localeList: LocaleList = LocaleList.current): AnnotatedString {
    return transform { str, start, end ->
        if (start == 0) {
            str.substring(start, end).decapitalize(localeList)
        } else {
            str.substring(start, end)
        }
    }
}

/**
 * The core function of [AnnotatedString] transformation.
 *
 * @param transform the transformation method
 * @return newly allocated transformed AnnotatedString
 */
private fun AnnotatedString.transform(transform: (String, Int, Int) -> String): AnnotatedString {
    val transitions = sortedSetOf<Int>()
    collectItemTransitions(textStyles, transitions)
    collectItemTransitions(paragraphStyles, transitions)

    var resultStr = ""
    val offsetMap = mutableMapOf(0 to 0)
    transitions.windowed(size = 2) { (start, end) ->
        resultStr += transform(text, start, end)
        offsetMap.put(end, resultStr.length)
    }

    val newTextStyles = mutableListOf<AnnotatedString.Item<TextStyle>>()
    val newParaStyles = mutableListOf<AnnotatedString.Item<ParagraphStyle>>()

    for (textStyle in textStyles) {
        // The offset map must have mapping entry from all style start, end position.
        newTextStyles.add(
            AnnotatedString.Item(
                textStyle.style,
                offsetMap[textStyle.start]!!,
                offsetMap[textStyle.end]!!
            )
        )
    }

    for (paraStyle in paragraphStyles) {
        newParaStyles.add(
            AnnotatedString.Item(
                paraStyle.style,
                offsetMap[paraStyle.start]!!,
                offsetMap[paraStyle.end]!!
            )
        )
    }

    return AnnotatedString(
        text = resultStr,
        textStyles = newTextStyles,
        paragraphStyles = newParaStyles)
}

/**
 * Adds all [AnnotatedString.Item] transition points
 *
 * @param items The list of AnnotatedString.Item
 * @param target The output list
 */
private fun <T> collectItemTransitions(
    items: List<AnnotatedString.Item<T>>,
    target: SortedSet<Int>
) {
    items.fold(target) { acc, item ->
        acc.apply {
            add(item.start)
            add(item.end)
        }
    }
}

/**
 * Returns the length of the [AnnotatedString].
 */
val AnnotatedString.length: Int get() = text.length

/**
 * Pushes [style] to the [AnnotatedString.Builder], executes [block] and then pops the [style].
 *
 * @sample androidx.ui.text.samples.AnnotatedStringBuilderWithStyleSample
 *
 * @param style [TextStyle] to be applied
 * @param block function to be executed
 *
 * @return result of the [block]
 *
 * @see AnnotatedString.Builder.push
 * @see AnnotatedString.Builder.pop
 */
inline fun <R : Any> Builder.withStyle(
    style: TextStyle,
    crossinline block: Builder.() -> R
): R {
    val index = push(style)
    return try {
        block(this)
    } finally {
        pop(index)
    }
}

/**
 * Pushes [style] to the [AnnotatedString.Builder], executes [block] and then pops the [style].
 *
 * @sample androidx.ui.text.samples.AnnotatedStringBuilderWithStyleSample
 *
 * @param style [TextStyle] to be applied
 * @param block function to be executed
 *
 * @return result of the [block]
 *
 * @see AnnotatedString.Builder.push
 * @see AnnotatedString.Builder.pop
 */
inline fun <R : Any> Builder.withStyle(
    style: ParagraphStyle,
    crossinline block: Builder.() -> R
): R {
    val index = push(style)
    return try {
        block(this)
    } finally {
        pop(index)
    }
}

operator fun AnnotatedString.plus(other: AnnotatedString): AnnotatedString {
    return with(Builder(this)) {
        append(other)
        toAnnotatedString()
    }
}

/**
 * Build a new AnnotatedString by populating newly created [AnnotatedString.Builder] provided
 * by [builder].
 *
 * @sample androidx.ui.text.samples.AnnotatedStringBuilderLambdaSample
 *
 * @param builder lambda to modify [AnnotatedString.Builder]
 */
inline fun AnnotatedString(builder: (Builder).() -> Unit): AnnotatedString =
    Builder().apply(builder).toAnnotatedString()
