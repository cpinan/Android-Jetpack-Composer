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

package androidx.ui.test.cases

import android.app.Activity
import androidx.compose.Composable
import androidx.compose.FrameManager
import androidx.compose.State
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.foundation.ColoredRect
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import androidx.ui.test.ComposeTestCase
import androidx.ui.test.ToggleableTestCase

/**
 * Test case that puts the given amount of rectangles into a column layout and makes changes by
 * modifying the color used in the model.
 *
 * Note: Each rectangle has its own model so changes should always affect only the first one.
 */
class RectsInColumnTestCase(
    activity: Activity,
    private val amountOfRectangles: Int
) : ComposeTestCase(activity), ToggleableTestCase {

    private val states = mutableListOf<State<Color>>()

    override fun setComposeContent(activity: Activity) = activity.setContent {
        MaterialTheme {
            Surface {
                Column {
                    repeat(amountOfRectangles) {
                        ColoredRectWithModel()
                    }
                }
            }
        }
    }!!

    override fun toggleState() {
        val state = states.first()
        if (state.value == Color.Magenta) {
            state.value = Color.Blue
        } else {
            state.value = Color.Magenta
        }
        FrameManager.nextFrame()
    }

    @Composable
    fun ColoredRectWithModel() {
        val state = +state { Color.Black }
        states.add(state)
        ColoredRect(color = state.value, width = 100.dp, height = 50.dp)
    }
}