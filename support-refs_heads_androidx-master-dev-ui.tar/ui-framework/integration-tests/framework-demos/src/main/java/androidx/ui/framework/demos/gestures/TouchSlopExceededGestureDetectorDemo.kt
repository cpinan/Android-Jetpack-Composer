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
import androidx.ui.graphics.Color
import androidx.ui.core.dp
import androidx.ui.core.Direction
import androidx.ui.core.gesture.TouchSlopExceededGestureDetector

/**
 * Simple demo that shows off TouchSlopExceededGestureDetector.
 */
class TouchSlopExceededGestureDetectorDemo : Activity() {

    val VerticalColor = Color(0xfff44336)
    val HorizontalColor = Color(0xff2196f3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val xOffset = +state { 0.px }
            val yOffset = +state { 0.px }
            val orientationVertical = +state { true }

            // This would be more efficient if onTouchSlopExceeded were memoized because it's
            // value doesn't need to change for each new composition.  Like this, every time
            // we recompose, a new lambda is created.  Here we aren't memoizing to demonstrate
            // that TouchSlopExceededGestureDetector behaves correctly when it is recomposed
            // because onTouchSlopExceeded changes.
            val onTouchSlopExceeded =
                {
                    orientationVertical.value = !orientationVertical.value
                }

            val canDrag =
                if (orientationVertical.value) {
                    { direction: Direction ->
                        when (direction) {
                            Direction.UP -> true
                            Direction.DOWN -> true
                            else -> false
                        }
                    }
                } else {
                    { direction: Direction ->
                        when (direction) {
                            Direction.LEFT -> true
                            Direction.RIGHT -> true
                            else -> false
                        }
                    }
                }

            val color =
                if (orientationVertical.value) {
                    VerticalColor
                } else {
                    HorizontalColor
                }

            TouchSlopExceededGestureDetector(onTouchSlopExceeded, canDrag) {
                MatchParent {
                    DrawBox(
                        xOffset.value,
                        yOffset.value,
                        96.dp,
                        96.dp,
                        color
                    )
                }
            }
        }
    }
}