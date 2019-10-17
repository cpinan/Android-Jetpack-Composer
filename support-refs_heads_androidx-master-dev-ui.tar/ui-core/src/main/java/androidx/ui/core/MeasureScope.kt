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

/**
 * The receiver scope of a layout's measure lambda. The return value of the
 * measure lambda is [LayoutResult], which should be returned by [layout]
 */
abstract class MeasureScope : DensityScope {
    /**
     * Interface holding the size and alignment lines of the measured layout, as well as the
     * children positioning logic.
     * [placeChildren] is the function used for positioning children. [Placeable.place] should
     * be called on children inside [placeChildren].
     * The alignment lines can be used by the parent layouts to decide layout, and can be queried
     * using the [Placeable.get] operator. Note that alignment lines will be inherited by parent
     * layouts, such that indirect parents will be able to query them as well.
     */
    interface LayoutResult {
        val width: IntPx
        val height: IntPx
        val alignmentLines: Map<AlignmentLine, IntPx>
        fun placeChildren(placementScope: Placeable.PlacementScope)
    }

    /**
     * Sets the size and alignment lines of the measured layout, as well as
     * the positioning block that defines the children positioning logic.
     * The [placementBlock] is a lambda used for positioning children. [Placeable.place] should
     * be called on children inside placementBlock.
     * The [alignmentLines] can be used by the parent layouts to decide layout, and can be queried
     * using the [Placeable.get] operator. Note that alignment lines will be inherited by parent
     * layouts, such that indirect parents will be able to query them as well.
     *
     * @param width the measured width of the layout
     * @param height the measured height of the layout
     * @param alignmentLines the alignment lines defined by the layout
     * @param placementBlock block defining the children positioning of the current layout
     */
    /*inline*/ fun layout(
        width: IntPx,
        height: IntPx,
        alignmentLines: Map<AlignmentLine, IntPx> = emptyMap(),
        /*crossinline*/
        placementBlock: Placeable.PlacementScope.() -> Unit
    ) = object : LayoutResult {
        override val width = width
        override val height = height
        override val alignmentLines = alignmentLines
        override fun placeChildren(placementScope: Placeable.PlacementScope) =
            placementScope.placementBlock()
    }
}

/**
 * A function for performing layout measurement.
 */
typealias MeasureBlock = MeasureScope.(List<Measurable>, Constraints) -> MeasureScope.LayoutResult
