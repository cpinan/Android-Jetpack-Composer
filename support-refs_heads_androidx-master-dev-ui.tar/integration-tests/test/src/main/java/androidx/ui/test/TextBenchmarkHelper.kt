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

package androidx.ui.test

import androidx.ui.core.px
import androidx.ui.core.sp
import androidx.ui.engine.geometry.Offset
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shadow
import androidx.ui.text.AnnotatedString
import androidx.ui.text.LocaleList
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.BaselineShift
import androidx.ui.text.style.TextDecoration
import androidx.ui.text.style.TextGeometricTransform
import kotlin.math.ceil
import kotlin.random.Random

class RandomTextGenerator(
    private val alphabet: Alphabet = Alphabet.Latin,
    private val random: Random = Random(0)
) {
    // a set of predefined TextStyle's to add to styled text
    private val nonMetricAffectingTextStyles = arrayOf(
        TextStyle(color = Color.Blue),
        TextStyle(background = Color.Cyan),
        TextStyle(decoration = TextDecoration.Underline),
        TextStyle(shadow = Shadow(Color.Black, Offset(3f, 3f), 2.px))
    )

    private val metricAffectingTextStyles = arrayOf(
        TextStyle(fontSize = 18.sp),
        TextStyle(fontSizeScale = 2f),
        TextStyle(fontWeight = FontWeight.Bold),
        TextStyle(fontStyle = FontStyle.Italic),
        TextStyle(letterSpacing = 0.2f),
        TextStyle(baselineShift = BaselineShift.Subscript),
        TextStyle(textGeometricTransform = TextGeometricTransform(0.5f, 0.5f)),
        TextStyle(localeList = LocaleList("it"))
    )

    private fun getTextStyleList(hasMetricAffectingStyle: Boolean) =
        nonMetricAffectingTextStyles + if (hasMetricAffectingStyle) {
            metricAffectingTextStyles
        } else {
            arrayOf()
        }

    /**
     * Creates a sequence of characters group of length [length].
     */
    private fun nextWord(length: Int): String = List(length) {
        alphabet.charRanges.random(random).random(random).toChar()
    }.joinToString(separator = "")

    /**
     * Create a sequence of character groups separated by the [Alphabet.space]. Each character group consists of
     * [wordLength] characters. The total length of the returned string is [length].
     */
    fun nextParagraph(
        length: Int,
        wordLength: Int = 9
    ): String {
        return if (length == 0) {
            ""
        } else {
            StringBuilder().apply {
                while (this.length < length) {
                    append(nextWord(wordLength))
                    append(alphabet.space)
                }
            }.substring(0, length)
        }
    }

    /**
     * Given a [text] mark each character group with a predefined TextStyle. The order of TextStyles is predefined,
     * and not randomized on purpose in order to get a consistent result in our benchmarks.
     * @param text The text on which the markup is applied.
     * @param styleCount The number of the text styles applied on the [text].
     * @param hasMetricAffectingStyle Whether to apply metric affecting [TextStyle]s text, which
     *  increases the difficulty to measure text.
     */
    fun createStyles(
        text: String,
        styleCount: Int = text.split(alphabet.space).size,
        hasMetricAffectingStyle: Boolean = true
    ): List<AnnotatedString.Item<TextStyle>> {
        val textStyleList = getTextStyleList(hasMetricAffectingStyle)

        val words = text.split(alphabet.space)

        var index = 0
        var styleIndex = 0

        val stylePerWord = styleCount / words.size
        val remains = styleCount % words.size

        return words.withIndex().flatMap { (wordIndex, word) ->
            val start = index
            val end = start + word.length
            index += word.length + 1

            val styleCountOnWord = stylePerWord + if (wordIndex < remains) 1 else 0
            List(styleCountOnWord) {
                AnnotatedString.Item(
                    start = start,
                    end = end,
                    style = textStyleList[styleIndex++ % textStyleList.size]
                )
            }
        }
    }

    /**
     * Create an [AnnotatedString] with randomly generated text but predefined TextStyles.
     * @see nextParagraph
     * @see createStyles
     */
    fun nextAnnotatedString(
        length: Int,
        wordLength: Int = 9,
        styleCount: Int,
        hasMetricAffectingStyle: Boolean = true
    ): AnnotatedString {
        val text = nextParagraph(length, wordLength)
        return AnnotatedString(
            text = text,
            textStyles = createStyles(text, styleCount, hasMetricAffectingStyle)
        )
    }

    /**
     * Create a list of word(character group)-[TextStyle] pairs, words are randomly generated while
     * the [TextStyle] is created in a fixed order.
     */
    fun nextStyledWordList(
        length: Int,
        wordLength: Int = 9,
        hasMetricAffectingStyle: Boolean = true
    ): List<Pair<String, TextStyle>> {
        val textStyleList = getTextStyleList(hasMetricAffectingStyle)

        val wordCount = ceil(length.toFloat() / (wordLength + 1)).toInt()
        var styleIndex = 0
        return List(wordCount) {
            Pair("${nextWord(length)} ", textStyleList[styleIndex++ % textStyleList.size])
        }
    }
}

/**
 * Defines the character ranges to be picked randomly for a script.
 */
class Alphabet(
    val charRanges: List<IntRange>,
    val space: Char,
    val name: String
) {

    override fun toString(): String {
        return name
    }

    companion object {
        val Latin = Alphabet(
            charRanges = listOf(
                IntRange('a'.toInt(), 'z'.toInt()),
                IntRange('A'.toInt(), 'Z'.toInt())
            ),
            space = ' ',
            name = "Latin"
        )

        val Cjk = Alphabet(
            charRanges = listOf(
                IntRange(0x4E00, 0x62FF),
                IntRange(0x6300, 0x77FF),
                IntRange(0x7800, 0x8CFF)
            ),
            space = 0x3000.toChar(),
            name = "CJK"
        )
    }
}

/**
 * Used by [RandomTextGenerator] in order to create plain text or multi-styled text.
 */
enum class TextType {
    PlainText,
    StyledText
}

/**
 * Given a list of Arrays and make cartesian product each of them with the [array].
 */
fun List<Array<Any>>.cartesian(vararg array: Any): List<Array<Any>> {
    return flatMap { row -> array.map { row + it } }
}

/**
 * Creates a cartesian product of the given arrays.
 */
fun cartesian(vararg arrays: Array<Any>): List<Array<Any>> {
    return arrays.fold(listOf(arrayOf())) { acc, list ->
        // add items from the current list
        // to each list that was accumulated
        acc.flatMap { accListItem -> list.map { accListItem + it } }
    }
}