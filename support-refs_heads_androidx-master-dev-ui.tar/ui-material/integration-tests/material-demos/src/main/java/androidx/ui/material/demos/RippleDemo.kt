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

package androidx.ui.material.demos

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.ComposeView
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.px
import androidx.ui.foundation.shape.border.Border
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Card
import androidx.ui.material.themeTextStyle

@Composable
fun RippleDemo() {
    ComposeView {
        MaterialTheme {
            Padding(padding = 50.dp) {
                Card(
                    shape = RoundedCornerShape(100.px),
                    border = Border(Color(0x80000000), 1.dp)
                ) {
                    Ripple(bounded = true) {
                        Container(expanded = true) {
                            Ripple(bounded = true) {
                                Container(width = 100.dp, height = 50.dp) {
                                    Text(text = "inner", style = +themeTextStyle { body1 })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
