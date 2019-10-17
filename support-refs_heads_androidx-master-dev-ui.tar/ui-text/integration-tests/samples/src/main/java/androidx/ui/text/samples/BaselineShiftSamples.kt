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
import androidx.compose.Composable
import androidx.ui.core.Span
import androidx.ui.core.Text
import androidx.ui.core.sp
import androidx.ui.text.AnnotatedString
import androidx.ui.text.TextStyle
import androidx.ui.text.style.BaselineShift

@Sampled
@Composable
fun BaselineShiftSample() {
    Text {
        Span(text = "Hello", style = TextStyle(fontSize = 20.sp)) {
            Span(
                text = "superscript",
                style = TextStyle(
                    baselineShift = BaselineShift.Superscript,
                    fontSize = 16.sp
                )
            ) {
                Span(
                    text = "subscript",
                    style = TextStyle(
                        baselineShift = BaselineShift.Subscript,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

@Sampled
@Composable
fun BaselineShiftAnnotatedStringSample() {
    val item1 = AnnotatedString.Item(
        style = TextStyle(),
        start = 0,
        end = 4
    )
    val item2 = AnnotatedString.Item(
        style = TextStyle(baselineShift = BaselineShift.Superscript),
        start = 0,
        end = 4
    )
    val annotatedString = AnnotatedString(
        text = "Text Demo",
        textStyles = listOf(item1, item2)
    )
    Text(text = annotatedString)
}
