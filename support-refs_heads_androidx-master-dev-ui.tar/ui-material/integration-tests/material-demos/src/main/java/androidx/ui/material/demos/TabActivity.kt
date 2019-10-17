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
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import androidx.ui.layout.FlexColumn
import androidx.ui.material.Button
import androidx.ui.material.ContainedButtonStyle
import androidx.ui.material.samples.FancyIndicatorContainerTabs
import androidx.ui.material.samples.FancyIndicatorTabs
import androidx.ui.material.samples.FancyTabs
import androidx.ui.material.samples.IconTabs
import androidx.ui.material.samples.ScrollingFancyIndicatorContainerTabs
import androidx.ui.material.samples.ScrollingTextTabs
import androidx.ui.material.samples.TextAndIconTabs
import androidx.ui.material.samples.TextTabs
import androidx.ui.graphics.imageFromResource

class TabActivity : MaterialDemoActivity() {

    @Composable
    override fun materialContent() {
        val favouriteImage = imageFromResource(resources, R.drawable.ic_favorite)
        FlexColumn {
            val showingSimple = +state { true }
            val buttonText = "Show ${if (showingSimple.value) "custom" else "simple"} tabs"

            expanded(flex = 1f) {
                if (showingSimple.value) {
                    TextTabs()
                    IconTabs(favouriteImage)
                    TextAndIconTabs(favouriteImage)
                    ScrollingTextTabs()
                } else {
                    FancyTabs()
                    FancyIndicatorTabs()
                    FancyIndicatorContainerTabs()
                    ScrollingFancyIndicatorContainerTabs()
                }
            }

            expanded(flex = 1f) {
                Center {
                    Button(
                        style = ContainedButtonStyle(color = Color.Cyan),
                        text = buttonText,
                        onClick = {
                            showingSimple.value = !showingSimple.value
                        })
                }
            }
        }
    }
}
