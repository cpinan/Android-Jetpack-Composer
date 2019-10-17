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

package androidx.ui.text.demos

import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Span
import androidx.ui.core.Text
import androidx.ui.core.selection.Selection
import androidx.ui.core.selection.SelectionMode
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.text.SelectionContainer
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Row
import androidx.ui.text.LocaleList
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontStyle
import androidx.ui.text.font.FontWeight

@Composable
fun TextSelectionDemo() {
    VerticalScroller {
        Column(
            mainAxisSize = LayoutSize.Expand,
            crossAxisAlignment = CrossAxisAlignment.Start
        ) {
            TagLine(tag = "selection")
            TextDemoSelection()
            TagLine(tag = "selection with string input")
            TextDemoSelectionWithStringInput()
            TagLine(tag = "selection in 2D Array Vertical")
            TextDemoSelection2DArrayVertical()
            TagLine(tag = "selection in 2D Array Horizontal")
            TextDemoSelection2DArrayHorizontal()
            TagLine(tag = "composable textspan")
        }
    }
}

@Composable
fun TextDemoSelection() {
    val selection = +state<Selection?> { null }
    val arabicSentence =
        "\nكلمة شين في قاموس المعاني الفوري مجال البحث مصطلحات المعجم الوسيط ،اللغة"
    SelectionContainer(
        selection = selection.value,
        onSelectionChange = { selection.value = it }) {
        Text {
            Span(
                style = TextStyle(
                    color = Color(0xFFFF0000),
                    fontSize = fontSize6,
                    fontWeight = FontWeight.W200,
                    fontStyle = FontStyle.Italic
                )
            ) {
                Span(text = "$displayText   ")
                Span(text = "$displayTextArabic   ")
                Span(text = "$displayTextChinese   ")
                Span(
                    text = displayTextHindi,
                    style = TextStyle(
                        color = Color(0xFF0000FF),
                        fontSize = fontSize10,
                        fontWeight = FontWeight.W800,
                        fontStyle = FontStyle.Normal
                    )
                )
                Span(text = "$arabicSentence")
                Span(
                    text = "\n先帝创业未半而中道崩殂，今天下三分，益州疲弊，此诚危急存亡之秋也。",
                    style = TextStyle(localeList = LocaleList("zh-CN"))
                )
                Span(
                    text = "\nまず、現在天下が魏・呉・蜀に分れており、そのうち蜀は疲弊していることを指摘する。",
                    style = TextStyle(localeList = LocaleList("ja-JP"))
                )
            }
        }
    }
}

@Composable
fun TextDemoSelectionWithStringInput() {
    val selection = +state<Selection?> { null }
    SelectionContainer(
        selection = selection.value,
        onSelectionChange = { selection.value = it }) {
        Text(
            text = "$displayText    $displayTextChinese    $displayTextHindi",
            style = TextStyle(
                color = Color(0xFFFF0000),
                fontSize = fontSize6,
                fontWeight = FontWeight.W200,
                fontStyle = FontStyle.Italic
            )
        )
    }
}

@Composable
fun TextDemoSelection2DArrayVertical() {
    var text = ""
    for (i in 1..3) {
        text = "$text$displayText" + "\n"
    }

    val colorList = listOf(
        Color(0xFFFF0000),
        Color(0xFF00FF00),
        Color(0xFF0000FF),
        Color(0xFF00FFFF),
        Color(0xFFFF00FF),
        Color(0xFFFFFF00),
        Color(0xFF0000FF),
        Color(0xFF00FF00),
        Color(0xFFFF0000)
    )

    val selection = +state<Selection?> { null }
    SelectionContainer(
        selection = selection.value,
        onSelectionChange = { selection.value = it }) {
        Column(mainAxisSize = LayoutSize.Expand) {
            for (i in 0..2) {
                Row(mainAxisSize = LayoutSize.Expand) {
                    for (j in 0..2) {
                        Text {
                            Span(
                                text = text,
                                style = TextStyle(
                                    color = colorList[i * 3 + j],
                                    fontSize = fontSize6
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TextDemoSelection2DArrayHorizontal() {
    var text = ""
    for (i in 1..3) {
        text = "$text$displayText" + "\n"
    }

    val colorList = listOf(
        Color(0xFFFF0000),
        Color(0xFF00FF00),
        Color(0xFF0000FF),
        Color(0xFF00FFFF),
        Color(0xFFFF00FF),
        Color(0xFFFFFF00),
        Color(0xFF0000FF),
        Color(0xFF00FF00),
        Color(0xFFFF0000)
    )

    val selection = +state<Selection?> { null }
    SelectionContainer(
        selection = selection.value,
        onSelectionChange = { selection.value = it },
        mode = SelectionMode.Horizontal
    ) {
        Column(mainAxisSize = LayoutSize.Expand) {
            for (i in 0..2) {
                Row(mainAxisSize = LayoutSize.Expand) {
                    for (j in 0..2) {
                        Text {
                            Span(
                                text = text,
                                style = TextStyle(
                                    color = colorList[i * 3 + j],
                                    fontSize = fontSize6
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
