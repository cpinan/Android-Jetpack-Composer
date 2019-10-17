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

package androidx.ui.framework.samples

import androidx.annotation.Sampled
import androidx.compose.Composable
import androidx.ui.core.Constraints
import androidx.ui.core.Layout
import androidx.ui.core.ipx

@Sampled
@Composable
fun LayoutWithProvidedIntrinsicsUsage(children: @Composable() () -> Unit) {
    // We build a layout that will occupy twice as much space as its children,
    // and will position them to be bottom right aligned.
    Layout(
        children,
        minIntrinsicWidthMeasureBlock = { measurables, h ->
            // The min intrinsic width of this layout will be twice the largest min intrinsic
            // width of a child. Note that we call minIntrinsicWidth with h / 2 for children,
            // since we should be double the size of the children.
            (measurables.map { it.minIntrinsicWidth(h / 2) }.maxBy { it.value } ?: 0.ipx) * 2
        },
        minIntrinsicHeightMeasureBlock = { measurables, w ->
            (measurables.map { it.minIntrinsicHeight(w / 2) }.maxBy { it.value } ?: 0.ipx) * 2
        },
        maxIntrinsicWidthMeasureBlock = { measurables, h ->
            (measurables.map { it.maxIntrinsicHeight(h / 2) }.maxBy { it.value } ?: 0.ipx) * 2
        },
        maxIntrinsicHeightMeasureBlock = { measurables, w ->
            (measurables.map { it.maxIntrinsicHeight(w / 2) }.maxBy { it.value } ?: 0.ipx) * 2
        }
    ) { measurables, constraints ->
        // measurables contains one element corresponding to each of our layout children.
        // constraints are the constraints that our parent is currently measuring us with.
        val childConstraints = Constraints(
            minWidth = constraints.minWidth / 2,
            minHeight = constraints.minHeight / 2,
            maxWidth = constraints.maxWidth / 2,
            maxHeight = constraints.maxHeight / 2
        )
        // We measure the children with half our constraints, to ensure we can be double
        // the size of the children.
        val placeables = measurables.map { it.measure(childConstraints) }
        val layoutWidth = (placeables.maxBy { it.width.value }?.width ?: 0.ipx) * 2
        val layoutHeight = (placeables.maxBy { it.height.value }?.height ?: 0.ipx) * 2
        // We call layout to set the size of the current layout and to provide the positioning
        // of the children. The children are placed relative to the current layout place.
        layout(layoutWidth, layoutHeight) {
            placeables.forEach { it.place(layoutWidth - it.width, layoutHeight - it.height) }
        }
    }
}

@Sampled
@Composable
fun LayoutUsage(children: @Composable() () -> Unit) {
    // We build a layout that will occupy twice as much space as its children,
    // and will position them to be bottom right aligned.
    Layout(children) { measurables, constraints ->
        // measurables contains one element corresponding to each of our layout children.
        // constraints are the constraints that our parent is currently measuring us with.
        val childConstraints = Constraints(
            minWidth = constraints.minWidth / 2,
            minHeight = constraints.minHeight / 2,
            maxWidth = constraints.maxWidth / 2,
            maxHeight = constraints.maxHeight / 2
        )
        // We measure the children with half our constraints, to ensure we can be double
        // the size of the children.
        val placeables = measurables.map { it.measure(childConstraints) }
        val layoutWidth = (placeables.maxBy { it.width.value }?.width ?: 0.ipx) * 2
        val layoutHeight = (placeables.maxBy { it.height.value }?.height ?: 0.ipx) * 2
        // We call layout to set the size of the current layout and to provide the positioning
        // of the children. The children are placed relative to the current layout place.
        layout(layoutWidth, layoutHeight) {
            placeables.forEach { it.place(layoutWidth - it.width, layoutHeight - it.height) }
        }
    }
}

@Sampled
@Composable
fun LayoutVarargsUsage(header: @Composable() () -> Unit, footer: @Composable() () -> Unit) {
    Layout(header, footer) { children, constraints ->
        val headerMeasurables = children[header]
        val footerMeasurables = children[footer]

        val headerPlaceables = headerMeasurables.map { child ->
            // You should use appropriate constraints.
            // This is shortened for the sake of a short example.
            child.measure(Constraints.tightConstraints(100.ipx, 100.ipx))
        }
        val footerPlaceables = footerMeasurables.map { child ->
            child.measure(constraints)
        }
        // Size should be derived from headerMeasurables and footerMeasurables measured
        // sizes, but this is shortened for the purposes of the example.
        layout(100.ipx, 100.ipx) {
            headerPlaceables.forEach { it.place(0.ipx, 0.ipx) }
            footerPlaceables.forEach { it.place(0.ipx, 0.ipx) }
        }
    }
}
