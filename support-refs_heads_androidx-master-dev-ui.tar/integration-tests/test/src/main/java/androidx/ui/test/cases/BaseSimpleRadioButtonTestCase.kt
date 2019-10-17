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
import androidx.compose.FrameManager
import androidx.compose.State
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.test.ComposeTestCase
import androidx.ui.test.ToggleableTestCase

abstract class BaseSimpleRadioButtonTestCase(
    activity: Activity
) : ComposeTestCase(activity), ToggleableTestCase {

    private var state: State<Dp>? = null

    fun getInnerSize(): State<Dp> {
        val innerSize = +state { 10.dp }
        state = innerSize
        return innerSize
    }

    override fun toggleState() {
        with(state!!) {
            value = if (value == 10.dp) {
                20.dp
            } else {
                10.dp
            }
        }
        FrameManager.nextFrame()
    }
}
