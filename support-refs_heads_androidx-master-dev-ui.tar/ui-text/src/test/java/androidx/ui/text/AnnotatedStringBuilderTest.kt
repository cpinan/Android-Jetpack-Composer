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

import androidx.ui.core.Sp
import androidx.ui.core.sp
import androidx.ui.graphics.Color
import androidx.ui.text.font.FontStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextAlign
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AnnotatedStringBuilderTest {

    @Test
    fun defaultConstructor() {
        val annotatedString = AnnotatedString.Builder().toAnnotatedString()

        assertThat(annotatedString.text).isEmpty()
        assertThat(annotatedString.textStyles).isEmpty()
        assertThat(annotatedString.paragraphStyles).isEmpty()
    }

    @Test
    fun constructorWithString() {
        val text = "a"
        val annotatedString = AnnotatedString.Builder(text).toAnnotatedString()

        assertThat(annotatedString.text).isEqualTo(text)
        assertThat(annotatedString.textStyles).isEmpty()
        assertThat(annotatedString.paragraphStyles).isEmpty()
    }

    @Test
    fun constructorWithAnnotatedString_hasSameAnnotatedStringAttributes() {
        val text = createAnnotatedString(text = "a")
        val annotatedString = AnnotatedString.Builder(text).toAnnotatedString()

        assertThat(annotatedString.text).isEqualTo(text.text)
        assertThat(annotatedString.textStyles).isEqualTo(text.textStyles)
        assertThat(annotatedString.paragraphStyles).isEqualTo(text.paragraphStyles)
    }

    @Test
    fun addStyle_withTextStyle_addsStyle() {
        val style = TextStyle(color = Color.Red)
        val range = TextRange(0, 1)
        val annotatedString = with(AnnotatedString.Builder("ab")) {
            addStyle(style, range.start, range.end)
            toAnnotatedString()
        }

        val expectedTextStyles = listOf(
            AnnotatedString.Item(style, range.start, range.end)
        )

        assertThat(annotatedString.paragraphStyles).isEmpty()
        assertThat(annotatedString.textStyles).isEqualTo(expectedTextStyles)
    }

    @Test
    fun addStyle_withParagraphStyle_addsStyle() {
        val style = ParagraphStyle(lineHeight = 30.sp)
        val range = TextRange(0, 1)
        val annotatedString = with(AnnotatedString.Builder("ab")) {
            addStyle(style, range.start, range.end)
            toAnnotatedString()
        }

        val expectedParagraphStyles = listOf(
            AnnotatedString.Item(style, range.start, range.end)
        )

        assertThat(annotatedString.textStyles).isEmpty()
        assertThat(annotatedString.paragraphStyles).isEqualTo(expectedParagraphStyles)
    }

    @Test
    fun append_withString_appendsTheText() {
        val text = "a"
        val appendedText = "b"
        val annotatedString = with(AnnotatedString.Builder(text)) {
            append(appendedText)
            toAnnotatedString()
        }

        val expectedString = "$text$appendedText"

        assertThat(annotatedString.text).isEqualTo(expectedString)
        assertThat(annotatedString.textStyles).isEmpty()
        assertThat(annotatedString.paragraphStyles).isEmpty()
    }

    @Test
    fun append_withString_andMultipleCalls_appendsAllOfTheText() {
        val annotatedString = with(AnnotatedString.Builder("a")) {
            append("b")
            append("c")
            toAnnotatedString()
        }

        assertThat(annotatedString.text).isEqualTo("abc")
    }

    @Test
    fun append_withAnnotatedString_appendsTheText() {
        val color = Color.Red
        val text = "a"
        val lineHeight = 20.sp
        val annotatedString = createAnnotatedString(
            text = text,
            color = color,
            lineHeight = lineHeight
        )

        val appendedColor = Color.Blue
        val appendedText = "b"
        val appendedLineHeight = 30.sp
        val appendedAnnotatedString = createAnnotatedString(
            text = appendedText,
            color = appendedColor,
            lineHeight = appendedLineHeight
        )

        val buildResult = with(AnnotatedString.Builder(annotatedString)) {
            append(appendedAnnotatedString)
            toAnnotatedString()
        }

        val expectedString = "$text$appendedText"
        val expectedTextStyles = listOf(
            AnnotatedString.Item(
                style = TextStyle(color),
                start = 0,
                end = text.length
            ),
            AnnotatedString.Item(
                style = TextStyle(appendedColor),
                start = text.length,
                end = expectedString.length
            )
        )

        val expectedParagraphStyles = listOf(
            AnnotatedString.Item(
                style = ParagraphStyle(lineHeight = lineHeight),
                start = 0,
                end = text.length
            ),
            AnnotatedString.Item(
                style = ParagraphStyle(lineHeight = appendedLineHeight),
                start = text.length,
                end = expectedString.length
            )
        )

        assertThat(buildResult.text).isEqualTo(expectedString)
        assertThat(buildResult.textStyles).isEqualTo(expectedTextStyles)
        assertThat(buildResult.paragraphStyles).isEqualTo(expectedParagraphStyles)
    }

    @Test
    fun pushStyle() {
        val text = "Test"
        val style = TextStyle(color = Color.Red)
        val buildResult = AnnotatedString.Builder().apply {
            push(style)
            append(text)
            pop()
        }.toAnnotatedString()

        assertThat(buildResult.text).isEqualTo(text)
        assertThat(buildResult.textStyles).hasSize(1)
        assertThat(buildResult.textStyles[0].style).isEqualTo(style)
        assertThat(buildResult.textStyles[0].start).isEqualTo(0)
        assertThat(buildResult.textStyles[0].end).isEqualTo(buildResult.length)
    }

    @Test
    fun pushStyle_without_pop() {
        val styles = arrayOf(
            TextStyle(color = Color.Red),
            TextStyle(fontStyle = FontStyle.Italic),
            TextStyle(fontWeight = FontWeight.Bold)
        )

        val buildResult = with(AnnotatedString.Builder()) {
            styles.forEachIndexed { index, textStyle ->
                // pop is intentionally not called here
                push(textStyle)
                append("Style$index")
            }
            toAnnotatedString()
        }

        assertThat(buildResult.text).isEqualTo("Style0Style1Style2")
        assertThat(buildResult.textStyles).hasSize(3)

        styles.forEachIndexed { index, textStyle ->
            assertThat(buildResult.textStyles[index].style).isEqualTo(textStyle)
            assertThat(buildResult.textStyles[index].end).isEqualTo(buildResult.length)
        }

        assertThat(buildResult.textStyles[0].start).isEqualTo(0)
        assertThat(buildResult.textStyles[1].start).isEqualTo("Style0".length)
        assertThat(buildResult.textStyles[2].start).isEqualTo("Style0Style1".length)
    }

    @Test
    fun pushStyle_with_multiple_styles() {
        val textStyle1 = TextStyle(color = Color.Red)
        val textStyle2 = TextStyle(fontStyle = FontStyle.Italic)

        val buildResult = with(AnnotatedString.Builder()) {
            push(textStyle1)
            append("Test")
            push(textStyle2)
            append(" me")
            pop()
            pop()
            toAnnotatedString()
        }

        assertThat(buildResult.text).isEqualTo("Test me")
        assertThat(buildResult.textStyles).hasSize(2)

        assertThat(buildResult.textStyles[0].style).isEqualTo(textStyle1)
        assertThat(buildResult.textStyles[0].start).isEqualTo(0)
        assertThat(buildResult.textStyles[0].end).isEqualTo(buildResult.length)

        assertThat(buildResult.textStyles[1].style).isEqualTo(textStyle2)
        assertThat(buildResult.textStyles[1].start).isEqualTo("Test".length)
        assertThat(buildResult.textStyles[1].end).isEqualTo(buildResult.length)
    }

    @Test
    fun pushStyle_with_multiple_styles_on_top_of_each_other() {
        val styles = arrayOf(
            TextStyle(color = Color.Red),
            TextStyle(fontStyle = FontStyle.Italic),
            TextStyle(fontWeight = FontWeight.Bold)
        )

        val buildResult = with(AnnotatedString.Builder()) {
            styles.forEach { textStyle ->
                // pop is intentionally not called here
                push(textStyle)
            }
            toAnnotatedString()
        }

        assertThat(buildResult.text).isEmpty()
        assertThat(buildResult.textStyles).hasSize(3)
        styles.forEachIndexed { index, textStyle ->
            assertThat(buildResult.textStyles[index].style).isEqualTo(textStyle)
            assertThat(buildResult.textStyles[index].start).isEqualTo(buildResult.length)
            assertThat(buildResult.textStyles[index].end).isEqualTo(buildResult.length)
        }
    }

    @Test
    fun pushStyle_with_multiple_stacks_should_construct_styles_in_the_same_order() {
        val styles = arrayOf(
            TextStyle(color = Color.Red),
            TextStyle(fontStyle = FontStyle.Italic),
            TextStyle(fontWeight = FontWeight.Bold),
            TextStyle(letterSpacing = 1.2f)
        )

        val buildResult = with(AnnotatedString.Builder()) {
            push(styles[0])
            append("layer1-1")
            push(styles[1])
            append("layer2-1")
            push(styles[2])
            append("layer3-1")
            pop()
            push(styles[3])
            append("layer3-2")
            pop()
            append("layer2-2")
            pop()
            append("layer1-2")
            toAnnotatedString()
        }

        assertThat(buildResult.textStyles).hasSize(4)
        styles.forEachIndexed { index, textStyle ->
            assertThat(buildResult.textStyles[index].style).isEqualTo(textStyle)
        }
    }

    @Test
    fun pushStyle_with_multiple_nested_styles_should_return_styles_in_same_order() {
        val styles = arrayOf(
            TextStyle(color = Color.Red),
            TextStyle(fontStyle = FontStyle.Italic),
            TextStyle(fontWeight = FontWeight.Bold),
            TextStyle(letterSpacing = 1.2f)
        )

        val buildResult = with(AnnotatedString.Builder()) {
            push(styles[0])
            append("layer1-1")
            push(styles[1])
            append("layer2-1")
            pop()
            push(styles[2])
            append("layer2-2")
            push(styles[3])
            append("layer3-1")
            pop()
            append("layer2-3")
            pop()
            append("layer1-2")
            pop()
            toAnnotatedString()
        }

        assertThat(buildResult.textStyles).hasSize(4)
        styles.forEachIndexed { index, textStyle ->
            assertThat(buildResult.textStyles[index].style).isEqualTo(textStyle)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun pop_when_empty_does_not_throw_exception() {
        AnnotatedString.Builder().pop()
    }

    @Test
    fun pop_in_the_middle() {
        val textStyle1 = TextStyle(color = Color.Red)
        val textStyle2 = TextStyle(fontStyle = FontStyle.Italic)

        val buildResult = with(AnnotatedString.Builder()) {
            append("Style0")
            push(textStyle1)
            append("Style1")
            pop()
            push(textStyle2)
            append("Style2")
            pop()
            append("Style3")
            toAnnotatedString()
        }

        assertThat(buildResult.text).isEqualTo("Style0Style1Style2Style3")
        assertThat(buildResult.textStyles).hasSize(2)

        // the order is first applied is in the second
        assertThat(buildResult.textStyles[0].style).isEqualTo((textStyle1))
        assertThat(buildResult.textStyles[0].start).isEqualTo(("Style0".length))
        assertThat(buildResult.textStyles[0].end).isEqualTo(("Style0Style1".length))

        assertThat(buildResult.textStyles[1].style).isEqualTo((textStyle2))
        assertThat(buildResult.textStyles[1].start).isEqualTo(("Style0Style1".length))
        assertThat(buildResult.textStyles[1].end).isEqualTo(("Style0Style1Style2".length))
    }

    @Test
    fun push_increments_the_style_index() {
        val style = TextStyle(color = Color.Red)
        with(AnnotatedString.Builder()) {
            val styleIndex0 = push(style)
            val styleIndex1 = push(style)
            val styleIndex2 = push(style)

            assertThat(styleIndex0).isEqualTo(0)
            assertThat(styleIndex1).isEqualTo(1)
            assertThat(styleIndex2).isEqualTo(2)
        }
    }

    @Test
    fun push_reduces_the_style_index_after_pop() {
        val textStyle = TextStyle(color = Color.Red)
        val paragraphStyle = ParagraphStyle(lineHeight = 18.sp)

        with(AnnotatedString.Builder()) {
            val styleIndex0 = push(textStyle)
            val styleIndex1 = push(textStyle)

            assertThat(styleIndex0).isEqualTo(0)
            assertThat(styleIndex1).isEqualTo(1)

            // a pop should reduce the next index to one
            pop()

            val paragraphStyleIndex = push(paragraphStyle)
            assertThat(paragraphStyleIndex).isEqualTo(1)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun pop_until_throws_exception_for_invalid_index() {
        val style = TextStyle(color = Color.Red)
        with(AnnotatedString.Builder()) {
            val styleIndex = push(style)

            // should throw exception
            pop(styleIndex + 1)
        }
    }

    @Test
    fun pop_until_index_pops_correctly() {
        val style = TextStyle(color = Color.Red)
        with(AnnotatedString.Builder()) {
            push(style)
            // store the index of second push
            val styleIndex = push(style)
            push(style)
            // pop up to and including styleIndex
            pop(styleIndex)
            // push again to get a new index to compare
            val newStyleIndex = push(style)

            assertThat(newStyleIndex).isEqualTo(styleIndex)
        }
    }

    @Test
    fun withStyle_applies_style_to_block() {
        val style = TextStyle(color = Color.Red)
        val buildResult = with(AnnotatedString.Builder()) {
            withStyle(style) {
                append("Style")
            }
            toAnnotatedString()
        }

        assertThat(buildResult.paragraphStyles).isEmpty()
        assertThat(buildResult.textStyles).isEqualTo(
            listOf(AnnotatedString.Item(style, 0, buildResult.length))
        )
    }

    @Test
    fun withStyle_with_paragraphStyle_applies_style_to_block() {
        val style = ParagraphStyle(lineHeight = 18.sp)
        val buildResult = with(AnnotatedString.Builder()) {
            withStyle(style) {
                append("Style")
            }
            toAnnotatedString()
        }

        assertThat(buildResult.textStyles).isEmpty()
        assertThat(buildResult.paragraphStyles).isEqualTo(
            listOf(AnnotatedString.Item(style, 0, buildResult.length))
        )
    }

    @Test
    fun builderLambda() {
        val text1 = "Hello"
        val text2 = "World"
        val textStyle1 = TextStyle(color = Color.Red)
        val textStyle2 = TextStyle(color = Color.Blue)
        val paragraphStyle1 = ParagraphStyle(textAlign = TextAlign.Right)
        val paragraphStyle2 = ParagraphStyle(textAlign = TextAlign.Right)

        val buildResult = AnnotatedString {
            withStyle(paragraphStyle1) {
                withStyle(textStyle1) {
                    append(text1)
                }
            }
            append(" ")
            push(paragraphStyle2)
            push(textStyle2)
            append(text2)
            pop()
        }

        val expectedString = "$text1 $text2"
        val expectedTextStyles = listOf(
            AnnotatedString.Item(textStyle1, 0, text1.length),
            AnnotatedString.Item(textStyle2, text1.length + 1, expectedString.length)
        )
        val expectedParagraphStyles = listOf(
            AnnotatedString.Item(paragraphStyle1, 0, text1.length),
            AnnotatedString.Item(paragraphStyle2, text1.length + 1, expectedString.length)
        )

        assertThat(buildResult.text).isEqualTo(expectedString)
        assertThat(buildResult.textStyles).isEqualTo(expectedTextStyles)
        assertThat(buildResult.paragraphStyles).isEqualTo(expectedParagraphStyles)
    }

    @Test
    fun toAnnotatedString_calling_twice_creates_equal_annotated_strings() {
        val builder = AnnotatedString.Builder().apply {
            // pushed styles not popped on purpose
            push(TextStyle(color = Color.Red))
            append("Hello")
            push(TextStyle(color = Color.Blue))
            append("World")
            this
        }

        assertThat(builder.toAnnotatedString()).isEqualTo(builder.toAnnotatedString())
    }

    @Test
    fun can_call_other_functions_after_toAnnotatedString() {
        val builder = AnnotatedString.Builder().apply {
            // pushed styles not popped on purpose
            push(TextStyle(fontSize = 12.sp))
            append("Hello")
            push(TextStyle(fontSize = 16.sp))
            append("World")
            this
        }

        val buildResult1 = builder.toAnnotatedString()
        val buildResult2 = with(builder) {
            pop()
            pop()
            push(TextStyle(fontSize = 18.sp))
            append("!")
            toAnnotatedString()
        }

        // buildResult2 should be the same as creating a new AnnotatedString based on the first
        // result and appending the same values
        val expectedResult = with(AnnotatedString.Builder(buildResult1)) {
            withStyle(TextStyle(fontSize = 18.sp)) {
                append("!")
            }
            toAnnotatedString()
        }

        assertThat(buildResult2).isEqualTo(expectedResult)
    }

    private fun createAnnotatedString(
        text: String,
        color: Color = Color.Red,
        lineHeight: Sp = 20.sp
    ): AnnotatedString {
        return AnnotatedString(
            text = text,
            textStyles = listOf(
                AnnotatedString.Item(
                    style = TextStyle(color),
                    start = 0,
                    end = text.length
                )
            ),
            paragraphStyles = listOf(
                AnnotatedString.Item(
                    style = ParagraphStyle(lineHeight = lineHeight),
                    start = 0,
                    end = text.length
                )
            )
        )
    }
}