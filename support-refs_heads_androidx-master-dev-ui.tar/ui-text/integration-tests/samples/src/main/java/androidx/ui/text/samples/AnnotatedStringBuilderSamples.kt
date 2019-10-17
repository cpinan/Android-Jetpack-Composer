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

package androidx.ui.text.samples

import androidx.annotation.Sampled
import androidx.ui.core.sp
import androidx.ui.graphics.Color
import androidx.ui.text.AnnotatedString
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextStyle
import androidx.ui.text.withStyle

@Sampled
fun AnnotatedStringBuilderSample() {
    with(AnnotatedString.Builder("Hello")) {
        // push green text style so that any appended text will be green
        push(TextStyle(color = Color.Green))
        // append new text, this text will be rendered as green
        append(" World")
        // pop the green style
        pop()
        // append a string without style
        append("!")
        // then style the last added word as red, exclamation mark will be red
        addStyle(TextStyle(color = Color.Red), "Hello World".length, this.length)

        toAnnotatedString()
    }
}

@Sampled
fun AnnotatedStringBuilderPushSample() {
    with(AnnotatedString.Builder()) {
        // push green text color so that any appended text will be rendered green
        push(TextStyle(color = Color.Green))
        // append string, this text will be rendered green
        append("Hello")
        // pop the green text style
        pop()
        // append new string, this string will be default color
        append(" World")

        toAnnotatedString()
    }
}

@Sampled
fun AnnotatedStringBuilderPushParagraphStyleSample() {
    with(AnnotatedString.Builder()) {
        // push a ParagraphStyle to be applied to any appended text after this point.
        push(ParagraphStyle(lineHeight = 18.sp))
        // append a paragraph which will have lineHeight 18.sp
        append("Paragraph One\n")
        // pop the ParagraphStyle
        pop()
        // append new paragraph, this paragraph will not have the line height defined.
        append("Paragraph Two\n")

        toAnnotatedString()
    }
}

@Sampled
fun AnnotatedStringBuilderWithStyleSample() {
    with(AnnotatedString.Builder()) {
        withStyle(TextStyle(color = Color.Green)) {
            // green text style will be applied to all text in this block
            append("Hello")
        }
        toAnnotatedString()
    }
}

@Sampled
fun AnnotatedStringBuilderLambdaSample() {
    // create an AnnotatedString using the lambda builder
    AnnotatedString {
        // append "Hello" with red text color
        withStyle(TextStyle(color = Color.Red)) {
            append("Hello")
        }
        append(" ")
        // append "Hello" with blue text color
        withStyle(TextStyle(color = Color.Blue)) {
            append("World!")
        }
    }
}
