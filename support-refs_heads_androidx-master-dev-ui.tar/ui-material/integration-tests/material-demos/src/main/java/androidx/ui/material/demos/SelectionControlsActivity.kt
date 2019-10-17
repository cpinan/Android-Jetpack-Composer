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
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.Padding
import androidx.ui.material.surface.Surface
import androidx.ui.material.samples.TriStateCheckboxSample
import androidx.ui.material.samples.SwitchSample
import androidx.ui.material.samples.CustomRadioGroupSample
import androidx.ui.material.samples.DefaultRadioGroupSample
import androidx.ui.material.samples.RadioButtonSample
import androidx.ui.material.themeTextStyle

class SelectionControlsActivity : MaterialDemoActivity() {

    @Composable
    override fun materialContent() {
        val headerStyle = +themeTextStyle { h6 }
        val padding = EdgeInsets(10.dp)

        Surface(color = Color.White) {
            Padding(padding = padding) {
                Column(crossAxisAlignment = CrossAxisAlignment.Start) {
                    Text(text = "Checkbox", style = headerStyle)
                    Padding(padding = padding) {
                        TriStateCheckboxSample()
                    }
                    Text(text = "Switch", style = headerStyle)
                    Padding(padding = padding) {
                        SwitchSample()
                    }
                    Text(text = "RadioButton", style = headerStyle)
                    Padding(padding = padding) {
                        RadioButtonSample()
                    }
                    Text(text = "Radio group :: Default usage", style = headerStyle)
                    Padding(padding = padding) {
                        DefaultRadioGroupSample()
                    }
                    Text(text = "Radio group :: Custom usage", style = headerStyle)
                    Padding(padding = padding) {
                        CustomRadioGroupSample()
                    }
                }
            }
        }
    }
}