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

package androidx.ui.core

import android.app.Activity
import androidx.benchmark.junit4.BenchmarkRule
import androidx.compose.Composable
import androidx.compose.FrameManager
import androidx.compose.State
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.ui.benchmark.toggleStateMeasureLayout
import androidx.ui.layout.Center
import androidx.ui.layout.Container
import androidx.ui.test.ComposeTestCase
import androidx.ui.test.ToggleableTestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@LargeTest
@RunWith(JUnit4::class)
class OnPositionedBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @get:Rule
    val activityRule = ActivityTestRule(Activity::class.java)

    private val activity: Activity get() = activityRule.activity

    @Test
    fun deepHierarchyOnPositioned_layout() {
        benchmarkRule.toggleStateMeasureLayout(
            activity,
            DeepHierarchyOnPositionedTestCase(activity)
        )
    }
}

private class DeepHierarchyOnPositionedTestCase(
    activity: Activity
) : ComposeTestCase(activity), ToggleableTestCase {

    private lateinit var state: State<Dp>

    override fun setComposeContent(activity: Activity) = activity.setContent {
        val size = +state { 200.dp }
        this.state = size
        Center {
            Container(width = size.value, height = size.value) {
                StaticChildren(100)
            }
        }
    }!!

    @Composable
    private fun StaticChildren(count: Int) {
        if (count > 0) {
            Container(width = 100.dp, height = 100.dp) {
                StaticChildren(count - 1)
            }
        } else {
            OnPositioned { coordinates -> coordinates.position }
        }
    }

    override fun toggleState() {
        state.value = if (state.value == 200.dp) 150.dp else 200.dp
        FrameManager.nextFrame()
    }
}
