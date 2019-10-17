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

package androidx.ui.layout

import androidx.compose.Composable
import androidx.ui.core.AlignmentLine
import androidx.ui.core.Constraints
import androidx.ui.core.DensityScope
import androidx.ui.core.IntPx
import androidx.ui.core.IntPxPosition
import androidx.ui.core.IntPxSize
import androidx.ui.core.Layout
import androidx.ui.core.LayoutModifier
import androidx.ui.core.Measurable
import androidx.ui.core.ipx
import androidx.ui.core.isFinite
import androidx.ui.core.satisfiedBy

/**
 * A layout modifier that attempts to size a layout to match a specified aspect ratio. The layout
 * modifier will try to match one of the incoming constraints, in the following order: maxWidth,
 * maxHeight, minWidth, minHeight. The size in the other dimension will then be computed
 * according to the aspect ratio. Note that the provided aspectRatio will always correspond to
 * the width/height ratio.
 *
 * If a valid size that satisfies the constraints is found this way, the modifier will size the
 * target layout to it: the layout will be measured with the tight constraints to match the size.
 * If a child is present, it will be measured with tight constraints to match the size.
 * If no valid size is found, the aspect ratio will not be satisfied, and the target layout will
 * be measured with the original constraints.
 *
 * Example usage:
 * @sample androidx.ui.layout.samples.SimpleAspectRatio
 *
 * @param value Must be positive non-zero Float.
 */
fun AspectRatio(value: Float): LayoutModifier = AspectRatioModifier(value)

/**
 * A [LayoutModifier] that applies an aspect ratio to the wrapped UI element's size.
 */
private data class AspectRatioModifier(val aspectRatio: Float) : LayoutModifier {
    init {
        require(aspectRatio > 0) {
            "Received aspect ratio value $aspectRatio is expected to be positive non-zero."
        }
    }

    override fun DensityScope.modifyConstraints(constraints: Constraints): Constraints {
        val size = constraints.findSizeWith(aspectRatio)
        return if (size != null)
            Constraints.tightConstraints(size.width, size.height)
        else
            constraints
    }

    override fun DensityScope.modifySize(
        constraints: Constraints,
        childSize: IntPxSize
    ) = childSize

    override fun DensityScope.minIntrinsicWidthOf(measurable: Measurable, height: IntPx): IntPx {
        return if (height == IntPx.Infinity) measurable.minIntrinsicWidth(height)
        else height * aspectRatio
    }

    override fun DensityScope.maxIntrinsicWidthOf(measurable: Measurable, height: IntPx): IntPx {
        return if (height == IntPx.Infinity) measurable.maxIntrinsicWidth(height)
        else height * aspectRatio
    }

    override fun DensityScope.minIntrinsicHeightOf(measurable: Measurable, width: IntPx): IntPx {
        return if (width == IntPx.Infinity) measurable.minIntrinsicHeight(width)
        else width / aspectRatio
    }

    override fun DensityScope.maxIntrinsicHeightOf(measurable: Measurable, width: IntPx): IntPx {
        return if (width == IntPx.Infinity) measurable.maxIntrinsicHeight(width)
        else width / aspectRatio
    }

    override fun DensityScope.modifyPosition(
        childPosition: IntPxPosition,
        childSize: IntPxSize,
        containerSize: IntPxSize
    ): IntPxPosition = childPosition

    override fun DensityScope.modifyAlignmentLine(line: AlignmentLine, value: IntPx?) = value

    override fun DensityScope.modifyParentData(parentData: Any?): Any? = parentData
}

/**
 * Layout composable that attempts to size itself and a potential layout child to match a specified
 * aspect ratio. The composable will try to match one of the incoming constraints, in the following
 * order: maxWidth, maxHeight, minWidth, minHeight. The size in the other dimension will then be
 * computed according to the aspect ratio. Note that the provided aspectRatio will always
 * correspond to the width/height ratio.
 *
 * If a valid size that satisfies the constraints is found this way, the composable will size itself to
 * this size. If a child is present, this will be measured with tight constraints to match the size.
 * If no valid size is found, the aspect ratio will not be satisfied, and the composable will
 * wrap its child, which is measured with the same constraints. If there is no child, the composable
 * will size itself to the incoming min constraints.
 *
 * Example usage:
 *     AspectRatio(2f) {
 *         DrawRectangle(color = Color.Blue)
 *         Container(padding = EdgeInsets(20.dp)) {
 *             SizedRectangle(color = Color.Black)
 *         }
 *     }
 * The AspectRatio composable will make the Container have the width twice its height.
 */
@Deprecated(
    "Use AspectRatio layout modifier instead.",
    ReplaceWith("modifier = AspectRatio(aspectRatio)"),
    DeprecationLevel.WARNING
)
@Composable
fun AspectRatio(
    aspectRatio: Float,
    children: @Composable() () -> Unit
) {
    require(aspectRatio > 0) {
        "Received aspect ratio value $aspectRatio is expected to be positive non-zero."
    }
    Layout(children) { measurables, constraints ->
        val size = constraints.findSizeWith(aspectRatio)

        val measurable = measurables.firstOrNull()
        val childConstraints = if (size != null) {
            Constraints.tightConstraints(size.width, size.height)
        } else {
            constraints
        }
        val placeable = measurable?.measure(childConstraints)

        layout(
            size?.width ?: placeable?.width ?: constraints.minWidth,
            size?.height ?: placeable?.height ?: constraints.minHeight
        ) {
            placeable?.place(0.ipx, 0.ipx)
        }
    }
}

private fun Constraints.findSizeWith(aspectRatio: Float): IntPxSize? {
    return listOf(
        IntPxSize(this.maxWidth, this.maxWidth / aspectRatio),
        IntPxSize(this.maxHeight * aspectRatio, this.maxHeight),
        IntPxSize(this.minWidth, this.minWidth / aspectRatio),
        IntPxSize(this.minHeight * aspectRatio, this.minHeight)
    ).find {
        this.satisfiedBy(it) &&
                it.width != 0.ipx && it.height != 0.ipx &&
                it.width.isFinite() && it.height.isFinite()
    }
}