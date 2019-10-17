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

package androidx.ui.layout.demos

import android.os.Handler
import androidx.ui.core.Constraints
import androidx.ui.core.Draw
import androidx.ui.core.IntPx
import androidx.ui.core.IntPxSize
import androidx.ui.core.Layout
import androidx.ui.core.WithConstraints
import androidx.ui.core.constrain
import androidx.ui.core.dp
import androidx.ui.core.ipx
import androidx.ui.core.max
import androidx.ui.core.toRect
import androidx.ui.layout.Align
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.FlexColumn
import androidx.ui.layout.FlexRow
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.layout.Stack
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.onCommit
import androidx.ui.core.Alignment
import androidx.ui.core.FirstBaseline
import androidx.ui.core.HorizontalAlignmentLine
import androidx.ui.core.Text
import androidx.ui.core.VerticalAlignmentLine
import androidx.ui.core.sp
import androidx.ui.layout.AlignmentLineOffset
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.Wrap
import androidx.ui.layout.samples.DrawRectangle
import androidx.ui.layout.samples.SizedRectangle
import androidx.ui.text.TextStyle

/**
 * A composable that forces its only child to be as wide as its min intrinsic width.
 */
@Composable
fun IntrinsicWidth(children: @Composable() () -> Unit) {
    Layout(
        children,
        minIntrinsicWidthMeasureBlock = { measurables, h ->
            measurables.first().minIntrinsicWidth(h)
        },
        minIntrinsicHeightMeasureBlock = { measurables, w ->
            measurables.first().minIntrinsicHeight(w)
        },
        maxIntrinsicWidthMeasureBlock = { measurables, h ->
            measurables.first().minIntrinsicWidth(h)
        },
        maxIntrinsicHeightMeasureBlock = { measurables, w ->
            measurables.first().maxIntrinsicHeight(w)
        }
    ) { measurables, constraints ->
        // Force child be as wide as its min intrinsic width.
        val width = measurables.first().minIntrinsicWidth(constraints.minHeight)
        val childConstraints = Constraints(
            width,
            width,
            constraints.minHeight,
            constraints.maxHeight
        )
        val childPlaceable = measurables.first().measure(childConstraints)
        layout(childPlaceable.width, childPlaceable.height) {
            childPlaceable.place(IntPx.Zero, IntPx.Zero)
        }
    }
}

/**
 * Draws an rectangle of fixed (80.dp, 80.dp) size, while providing intrinsic dimensions as well.
 */
@Composable
fun RectangleWithIntrinsics(color: Color) {
    Layout(
        { DrawRectangle(color = color) },
        minIntrinsicWidthMeasureBlock = { _, _ -> 30.ipx },
        minIntrinsicHeightMeasureBlock = { _, _ -> 30.ipx },
        maxIntrinsicWidthMeasureBlock = { _, _ -> 150.ipx },
        maxIntrinsicHeightMeasureBlock = { _, _ -> 150.ipx },
        measureBlock = { _, _ -> layout(80.ipx, 80.ipx) {} }
    )
}

@Composable
fun FlexRowUsage() {
    FlexRow {
        expanded(flex = 2f) {
            Center {
                SizedRectangle(color = Color(0xFF0000FF), width = 40.dp, height = 40.dp)
            }
            SizedRectangle(color = Color(0xFF0000FF), height = 40.dp)
        }
        inflexible {
            SizedRectangle(color = Color(0xFFFF0000), width = 40.dp)
            SizedRectangle(color = Color(0xFF00FF00), width = 50.dp)
            SizedRectangle(color = Color(0xFF0000FF), width = 60.dp)
        }
        expanded(flex = 1f) {
            SizedRectangle(color = Color(0xFF00FF00))
        }
    }
}

@Composable
fun FlexColumnUsage() {
    FlexColumn {
        expanded(flex = 2f) {
            Center {
                SizedRectangle(color = Color(0xFF0000FF), width = 40.dp, height = 40.dp)
            }
            SizedRectangle(color = Color(0xFF0000FF), width = 40.dp)
        }
        inflexible {
            SizedRectangle(color = Color(0xFFFF0000), height = 40.dp)
            SizedRectangle(color = Color(0xFF00FF00), height = 50.dp)
            SizedRectangle(color = Color(0xFF0000FF), height = 60.dp)
        }
        expanded(flex = 1f) {
            SizedRectangle(color = Color(0xFF00FF00))
        }
    }
}

@Composable
fun RowUsage() {
    Row {
        SizedRectangle(color = Color(0xFF0000FF), width = 40.dp, height = 40.dp)
        SizedRectangle(color = Color(0xFFFF0000), width = 40.dp, height = 80.dp)
        SizedRectangle(color = Color(0xFF00FF00), width = 80.dp, height = 70.dp)
    }
}

@Composable
fun ColumnUsage() {
    Column {
        SizedRectangle(color = Color(0xFF0000FF), width = 40.dp, height = 40.dp)
        SizedRectangle(color = Color(0xFFFF0000), width = 40.dp, height = 80.dp)
        SizedRectangle(color = Color(0xFF00FF00), width = 80.dp, height = 70.dp)
    }
}

@Composable
fun AlignUsage() {
    Align(alignment = Alignment.BottomRight) {
        SizedRectangle(color = Color(0xFF0000FF), width = 40.dp, height = 40.dp)
    }
}

@Composable
fun StackUsage() {
    Stack {
        aligned(Alignment.Center) {
            SizedRectangle(color = Color(0xFF0000FF), width = 300.dp, height = 300.dp)
        }
        aligned(Alignment.TopLeft) {
            SizedRectangle(color = Color(0xFF00FF00), width = 150.dp, height = 150.dp)
        }
        aligned(Alignment.BottomRight) {
            SizedRectangle(color = Color(0xFFFF0000), width = 150.dp, height = 150.dp)
        }
        positioned(
            leftInset = null, topInset = 20.dp, rightInset = null, bottomInset = 20.dp,
            fallbackAlignment = Alignment.BottomRight
        ) {
            SizedRectangle(color = Color(0xFFFFA500), width = 80.dp)
            SizedRectangle(color = Color(0xFFA52A2A), width = 20.dp)
        }
        positioned(
            leftInset = 40.dp, topInset = null, rightInset = null, bottomInset = null,
            fallbackAlignment = Alignment.BottomRight
        ) {
            SizedRectangle(color = Color(0xFFB22222), width = 20.dp)
        }
        positioned(
            leftInset = null, topInset = null, rightInset = 40.dp, bottomInset = null,
            fallbackAlignment = Alignment.BottomRight
        ) {
            SizedRectangle(color = Color(0xFFFFFF00), width = 40.dp)
        }
    }
}

@Composable
fun ConstrainedBoxUsage() {
    Align(alignment = Alignment.Center) {
        ConstrainedBox(constraints = DpConstraints.tightConstraints(50.dp, 50.dp)) {
            SizedRectangle(color = Color(0xFFFF0000))
        }
    }
}

@Composable
fun PaddingUsage() {
    Row {
        Padding(padding = 20.dp) {
            SizedRectangle(color = Color(0xFFFF0000), width = 20.dp, height = 20.dp)
        }
        Padding(padding = 20.dp) {
            SizedRectangle(color = Color(0xFFFF0000), width = 20.dp, height = 20.dp)
        }
    }
}

@Composable
fun SingleCompositionRow(children: @Composable() () -> Unit) {
    Layout(children) { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(constraints.copy(minWidth = 0.ipx, maxWidth = IntPx.Infinity))
        }
        val width = placeables.fold(0.ipx) { sum, placeable -> sum + placeable.width }
        val height = placeables.fold(0.ipx) { max, placeable -> max(max, placeable.height) }

        layout(width, height) {
            var left = 0.ipx
            placeables.forEach { placeable ->
                placeable.place(left, 0.ipx)
                left += placeable.width
            }
        }
    }
}

@Composable
fun SingleCompositionColumn(children: @Composable() () -> Unit) {
    Layout(children) { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(constraints.copy(minHeight = 0.ipx, maxHeight = IntPx.Infinity))
        }
        val width = placeables.fold(0.ipx) { max, placeable -> max(max, placeable.width) }
        val height = placeables.fold(0.ipx) { sum, placeable -> sum + placeable.height }

        layout(width, height) {
            var top = 0.ipx
            placeables.forEach { placeable ->
                placeable.place(0.ipx, top)
                top += placeable.height
            }
        }
    }
}

@Composable
fun SingleCompositionRect() {
    val Rectangle = @Composable {
        Draw { canvas, parentSize ->
            val paint = Paint().apply { this.color = rectColorModel.color }
            canvas.drawRect(parentSize.toRect(), paint)
        }
    }
    Layout(Rectangle) { _, constraints ->
        val size = constraints.constrain(IntPxSize(30.ipx, 30.ipx))
        layout(size.width, size.height) { }
    }
}

@Model
class RectColor(var color: Color = Color(0xFF00FF00), var cnt: Int = 4)

val rectColorModel = RectColor()

@Composable
fun SingleCompositionRectWithIntrinsics() {
    val Rectangle = @Composable {
        Draw { canvas, parentSize ->
            val paint = Paint().apply { this.color = rectColorModel.color }
            canvas.drawRect(parentSize.toRect(), paint)
        }
    }
    Layout(
        Rectangle,
        minIntrinsicWidthMeasureBlock = { _, _ -> 50.ipx },
        maxIntrinsicWidthMeasureBlock = { _, _ -> 50.ipx },
        minIntrinsicHeightMeasureBlock = { _, _ -> 50.ipx },
        maxIntrinsicHeightMeasureBlock = { _, _ -> 50.ipx },
        measureBlock = { _, _ -> layout(50.ipx, 50.ipx) {} }
    )
}

@Composable
fun SingleCompositionRowWithIntrinsics(children: @Composable() () -> Unit) {
    Layout(
        children,
        minIntrinsicWidthMeasureBlock = { measurables, h ->
            measurables.map { it.minIntrinsicWidth(h) }.sum()
        },
        minIntrinsicHeightMeasureBlock = { measurables, w ->
            measurables.map { it.minIntrinsicHeight(w) }.max()
        },
        maxIntrinsicWidthMeasureBlock = { measurables, h ->
            measurables.map { it.maxIntrinsicWidth(h) }.sum()
        },
        maxIntrinsicHeightMeasureBlock = { measurables, w ->
            measurables.map { it.maxIntrinsicHeight(w) }.max()
        }
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            val childWidth = measurable.maxIntrinsicWidth(constraints.maxHeight)
            measurable.measure(
                Constraints(
                    childWidth, childWidth, 0.ipx, constraints.maxHeight
                )
            )
        }
        val width = placeables.map { it.width }.sum()
        val height = placeables.map { it.height }.max()

        layout(width, height) {
            var left = 0.ipx
            placeables.forEach { placeable ->
                placeable.place(left, 0.ipx)
                left += placeable.width
            }
        }
    }
}

@Composable
fun FillWithRectangles() {
    WithConstraints { constraints ->
        val rectangles = (constraints.maxWidth / 50).value
        Row {
            for (i in 0 until rectangles) {
                Padding(padding = 1.dp) {
                    SingleCompositionRect()
                }
            }
        }
    }
}

@Composable
fun PositionUsingAlignmentLine() {
    val DummyVertical = VerticalAlignmentLine(::max)
    val DummyHorizontal = HorizontalAlignmentLine(::max)
    val childWithLines = @Composable {
        Wrap {
            Layout({}) { _, _ ->
                layout(30.ipx, 50.ipx, mapOf(DummyVertical to 15.ipx, DummyHorizontal to 25.ipx)) {}
            }
            DrawRectangle(Color.Blue)
        }
    }

    Layout(childWithLines) { measurables, constraints ->
        val measurable = measurables.first()
        val placeable = measurable.measure(constraints)
        val distanceToVertical = 70.ipx
        val distanceToHorizontal = 70.ipx
        layout(
            distanceToVertical + placeable[DummyVertical]!! + placeable.width,
            distanceToHorizontal + placeable[DummyHorizontal]!! + placeable.height
        ) {
            placeable.place(
                distanceToVertical - placeable[DummyVertical]!!,
                distanceToHorizontal - placeable[DummyHorizontal]!!
            )
        }
    }
}

@Composable
fun RowBaselineAlignment() {
    Row(crossAxisAlignment = CrossAxisAlignment.AlignmentLine(FirstBaseline)) {
        Text("First text")
        Column {
            SizedRectangle(color = Color.Blue, width = 10.dp, height = 50.dp)
            Padding(30.dp) {
                Text("Second text", style = TextStyle(fontSize = 45.sp))
            }
        }
    }
}

@Composable
fun AlignmentLineOffsetUsage() {
    AlignmentLineOffset(FirstBaseline, before = 20.dp, after = 40.dp) {
        DrawRectangle(Color.Gray)
        Text("Text providing baseline")
    }
}

@Composable
fun ComplexLayoutDemos() {
    Wrap {
        AlignmentLineOffsetUsage()
    }
}

fun runDelayed(vararg millis: Int, block: () -> Unit) = effectOf<Unit> {
    val handler = +memo { Handler() }
    +onCommit {
        val runnable = object : Runnable {
            override fun run() {
                block()
            }
        }
        millis.forEach { millis ->
            handler.postDelayed(runnable, millis.toLong())
        }
        onDispose {
            handler.removeCallbacks(runnable)
        }
    }
}

fun Collection<IntPx>.sum(): IntPx {
    var result = 0.ipx
    for (item in this) {
        result += item
    }
    return result
}

fun Collection<IntPx>.max(): IntPx {
    var result = 0.ipx
    for (item in this) {
        result = max(result, item)
    }
    return result
}
