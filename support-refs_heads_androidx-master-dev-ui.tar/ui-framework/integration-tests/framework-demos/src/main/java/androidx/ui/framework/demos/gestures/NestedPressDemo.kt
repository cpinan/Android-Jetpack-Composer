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
import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.px
import androidx.compose.memo
import androidx.ui.core.Dp
import androidx.ui.core.PxPosition
import androidx.ui.core.dp
import androidx.ui.core.gesture.DoubleTapGestureDetector
import androidx.ui.core.gesture.LongPressGestureDetector
import androidx.ui.core.gesture.PressIndicatorGestureDetector
import androidx.ui.core.gesture.PressReleasedGestureDetector
import androidx.ui.core.setContent

/**
 * Demonstration of how various press/tap gesture interact together in a nested fashion.
 */
class NestedPressDemo : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PressableContainer(
                paddingLeft = 48.dp,
                paddingRight = 48.dp,
                paddingTop = 96.dp,
                paddingBottom = 96.dp
            ) {
                PressableContainer(
                    paddingLeft = 48.dp,
                    paddingRight = 48.dp,
                    paddingTop = 96.dp,
                    paddingBottom = 96.dp
                ) {
                    PressableContainer {}
                }
            }
        }
    }
}

@Composable
fun PressableContainer(
    paddingLeft: Dp? = null,
    paddingTop: Dp? = null,
    paddingRight: Dp? = null,
    paddingBottom: Dp? = null,
    children: (@Composable() () -> Unit)
) {
    val defaultColor = DefaultBackgroundColor
    val pressedColor = PressedColor

    val currentColor = +memo { Single(defaultColor) }
    val pressed = +state { false }

    val onStart: (Any) -> Unit = {
        pressed.value = true
    }

    val onStop: () -> Unit = {
        pressed.value = false
    }

    val onLongPress = { _: PxPosition ->
        pressed.value = false
        currentColor.value = defaultColor
    }

    val onTap = {
        currentColor.value = currentColor.value.next()
    }

    val onDoubleTap = { _: PxPosition ->
        currentColor.value = currentColor.value.prev().prev()
    }

    val color = if (pressed.value) {
        pressedColor.over(currentColor.value)
    } else {
        currentColor.value
    }

    PressIndicatorGestureDetector(
        onStart = onStart,
        onStop = onStop,
        onCancel = onStop
    ) {
        PressReleasedGestureDetector(onTap, false) {
            DoubleTapGestureDetector(onDoubleTap) {
                LongPressGestureDetector(onLongPress) {
                    Border(BorderColor, 2.dp) {
                        DrawBox(
                            0.px,
                            0.px,
                            (-1).dp,
                            (-1).dp,
                            color
                        )
                        Padding(paddingLeft, paddingTop, paddingRight, paddingBottom) {
                            children()
                        }
                    }
                }
            }
        }
    }
}

data class Single<T>(var value: T)