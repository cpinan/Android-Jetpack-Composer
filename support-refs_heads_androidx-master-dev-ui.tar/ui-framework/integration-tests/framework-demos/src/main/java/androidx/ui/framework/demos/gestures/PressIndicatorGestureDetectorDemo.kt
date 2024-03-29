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

package androidx.ui.framework.demos.gestures

import android.app.Activity
import android.os.Bundle
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.px
import androidx.ui.core.setContent
import androidx.ui.core.PxPosition
import androidx.ui.core.dp
import androidx.ui.core.gesture.PressIndicatorGestureDetector

/**
 * Simple PressIndicatorGestureDetector demo.
 */
class PressIndicatorGestureDetectorDemo : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pressed = +state { false }

            val onStart: (PxPosition) -> Unit = {
                pressed.value = true
            }

            val onStop = {
                pressed.value = false
            }

            val color =
                if (pressed.value) {
                    PressedColor.over(Grey)
                } else {
                    Grey
                }

            PressIndicatorGestureDetector(onStart = onStart, onStop = onStop, onCancel = onStop) {
                MatchParent {
                    DrawBox(
                        0.px,
                        0.px,
                        96.dp,
                        96.dp,
                        color
                    )
                }
            }
        }
    }
}