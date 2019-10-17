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
import androidx.compose.CompositionContext
import androidx.compose.FrameManager
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Draw
import androidx.ui.core.dp
import androidx.ui.core.px
import androidx.ui.core.setContent
import androidx.ui.core.toRect
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.LayoutSize
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Paint
import androidx.ui.graphics.PaintingStyle
import androidx.ui.test.ComposeTestCase
import androidx.ui.test.ToggleableTestCase

/**
 * Test case that puts a large number of boxes in a column in a vertical scroller to force scrolling.
 */
class ScrollerTestCase(
    activity: Activity
) : ComposeTestCase(activity), ToggleableTestCase {
    private val scrollerPosition = ScrollerPosition()

    override fun setComposeContent(activity: Activity): CompositionContext = activity.setContent {
        VerticalScroller(
            scrollerPosition = scrollerPosition
        ) {
            Column(
                mainAxisSize = LayoutSize.Expand,
                crossAxisAlignment = CrossAxisAlignment.Start
            ) {
                for (green in 0..0xFF) {
                    ColorStripe(0xFF, green, 0)
                }
                for (red in 0xFF downTo 0) {
                    ColorStripe(red, 0xFF, 0)
                }
                for (blue in 0..0xFF) {
                    ColorStripe(0, 0xFF, blue)
                }
                for (green in 0xFF downTo 0) {
                    ColorStripe(0, green, 0xFF)
                }
                for (red in 0..0xFF) {
                    ColorStripe(red, 0, 0xFF)
                }
                for (blue in 0xFF downTo 0) {
                    ColorStripe(0xFF, 0, blue)
                }
            }
        }
    }!!

    override fun toggleState() {
        scrollerPosition.value = if (scrollerPosition.value == 0.px) 10.px else 0.px
        FrameManager.nextFrame()
    }

    @Composable
    fun ColorStripe(red: Int, green: Int, blue: Int) {
        val paint = +memo { Paint() }
        Container(height = 5.dp, width = 45.dp) {
            Draw { canvas, parentSize ->
                paint.color = Color(red = red, green = green, blue = blue)
                paint.style = PaintingStyle.fill
                canvas.drawRect(parentSize.toRect(), paint)
            }
        }
    }
}
