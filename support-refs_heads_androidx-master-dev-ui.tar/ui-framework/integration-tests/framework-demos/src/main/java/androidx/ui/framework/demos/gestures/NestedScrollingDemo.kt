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
import androidx.ui.core.Direction
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.PxPosition
import androidx.ui.core.coerceIn
import androidx.ui.core.gesture.TouchSlopDragGestureDetector
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.PressIndicatorGestureDetector
import androidx.ui.core.ipx
import androidx.ui.core.px
import androidx.ui.core.setContent
import androidx.ui.engine.geometry.Rect
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.core.gesture.LongPressGestureDetector
import androidx.ui.core.Draw
import androidx.ui.core.dp
import androidx.ui.core.gesture.DoubleTapGestureDetector
import androidx.ui.core.gesture.PressReleasedGestureDetector
import androidx.ui.core.round
import androidx.ui.core.toRect

/**
 * Demonstration for how multiple DragGestureDetectors interact.
 */
class NestedScrollingDemo : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Outer composable that scrollsAll mea
            Draggable {
                RepeatingList(repititions = 3) {
                    SimpleContainer(
                        width = (-1).dp,
                        height = 398.dp,
                        padding = 72.dp
                    ) {
                        // Inner composable that scrolls
                        Draggable {
                            RepeatingList(repititions = 5) {
                                // Composable that indicates it is being pressed
                                Pressable(
                                    height = 72.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * A very simple ScrollView like implementation that allows for vertical scrolling.
 */
@Composable
private fun Draggable(children: @Composable() () -> Unit) {
    val offset = +state { 0.px }
    val maxOffset = +state { 0.px }

    val dragObserver = object : DragObserver {
        override fun onDrag(dragDistance: PxPosition): PxPosition {
            val resultingOffset = offset.value + dragDistance.y
            val dyToConsume =
                if (resultingOffset > 0.px) {
                    0.px - offset.value
                } else if (resultingOffset < maxOffset.value) {
                    maxOffset.value - offset.value
                } else {
                    dragDistance.y
                }
            offset.value = offset.value + dyToConsume
            return PxPosition(0.px, dyToConsume)
        }
    }

    val canDrag = { direction: Direction ->
        when (direction) {
            Direction.UP -> true
            Direction.DOWN -> true
            else -> false
        }
    }

    TouchSlopDragGestureDetector(dragObserver, canDrag) {
        Layout(children = {
            Draw { canvas, parentSize ->
                canvas.save()
                canvas.clipRect(parentSize.toRect())
            }
            children()
            Draw { canvas, _ ->
                canvas.restore()
            }
        }, measureBlock = { measurables, constraints ->
            val placeable =
                measurables.first()
                    .measure(constraints.copy(minHeight = 0.ipx, maxHeight = IntPx.Infinity))

            maxOffset.value = constraints.maxHeight.value.px - placeable.height

            layout(constraints.maxWidth, constraints.maxHeight) {
                placeable.place(0.ipx, offset.value.round())
            }
        })
    }
}

/**
 * A very simple Button like implementation that visually indicates when it is being pressed.
 */
@Composable
private fun Pressable(
    height: Dp
) {

    val pressedColor = PressedColor
    val defaultColor = DefaultBackgroundColor

    val color = +state { defaultColor }
    val showPressed = +state { false }

    val onPress: (PxPosition) -> Unit = {
        showPressed.value = true
    }

    val onRelease = {
        showPressed.value = false
    }

    val onTap = {
        color.value = color.value.next()
    }

    val onDoubleTap: (PxPosition) -> Unit = {
        color.value = color.value.prev().prev()
    }

    val onLongPress = { _: PxPosition ->
        color.value = defaultColor
        showPressed.value = false
    }

    val children = @Composable {
        Draw { canvas, parentSize ->
            val backgroundPaint = Paint().apply { this.color = color.value }
            canvas.drawRect(
                Rect(0f, 0f, parentSize.width.value, parentSize.height.value),
                backgroundPaint
            )
            if (showPressed.value) {
                backgroundPaint.color = pressedColor
                canvas.drawRect(
                    Rect(0f, 0f, parentSize.width.value, parentSize.height.value),
                    backgroundPaint
                )
            }
        }
    }

    PressIndicatorGestureDetector(onPress, onRelease, onRelease) {
        PressReleasedGestureDetector(onTap, false) {
            DoubleTapGestureDetector(onDoubleTap) {
                LongPressGestureDetector(onLongPress) {
                    Layout(children) { _, constraints ->
                        layout(
                            constraints.maxWidth,
                            height.toIntPx().coerceIn(constraints.minHeight, constraints.maxHeight)
                        ) {}
                    }
                }
            }
        }
    }
}

/**
 * A simple composable that repeats it's children as a vertical list of divided items [repititions]
 * times.
 */
@Composable
private fun RepeatingList(repititions: Int, row: @Composable() () -> Unit) {
    Column {
        for (i in 1..repititions) {
            row()
            if (i != repititions) {
                Divider(1.dp, Color(0f, 0f, 0f, .12f))
            }
        }
    }
}

/**
 * A simple composable that arranges it's children as vertical list of items.
 */
@Composable
private fun Column(children: @Composable() () -> Unit) {
    Layout(children) { measurables, constraints ->
        var height = 0.ipx
        val placeables = measurables.map {
            val placeable = it.measure(
                constraints.copy(minHeight = 0.ipx, maxHeight = IntPx.Infinity)
            )
            height += placeable.height
            placeable
        }

        height = height.coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(constraints.maxWidth, height) {
            var currY = 0.ipx
            placeables.forEach {
                it.place(0.ipx, currY)
                currY += it.height
            }
        }
    }
}

/**
 * A simple composable that creates a divider that runs from left to right.
 */
@Composable
private fun Divider(height: Dp, color: Color) {
    val children = @Composable {
        Draw { canvas, parentSize ->
            val backgroundPaint = Paint().apply { this.color = color }
            canvas.drawRect(
                Rect(0f, 0f, parentSize.width.value, parentSize.height.value),
                backgroundPaint
            )
        }
    }

    Layout(children) { _, constraints ->
        layout(
            constraints.maxWidth,
            height.toIntPx().coerceIn(constraints.minHeight, constraints.maxHeight)
        ) {}
    }
}