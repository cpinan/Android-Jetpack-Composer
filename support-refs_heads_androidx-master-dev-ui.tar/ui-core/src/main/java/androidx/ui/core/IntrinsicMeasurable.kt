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
 * A part of the composition that can be measured. This represents a layout.
 * The instance should never be stored.
 */
interface IntrinsicMeasurable {
    /**
     * Data provided by the `ParentData`
     */
    val parentData: Any?

    /**
     * Calculates the minimum width that the layout can be such that
     * the content of the layout will be painted correctly.
     */
    fun minIntrinsicWidth(height: IntPx): IntPx

    /**
     * Calculates the smallest width beyond which increasing the width never
     * decreases the height.
     */
    fun maxIntrinsicWidth(height: IntPx): IntPx

    /**
     * Calculates the minimum height that the layout can be such that
     * the content of the layout will be painted correctly.
     */
    fun minIntrinsicHeight(width: IntPx): IntPx

    /**
     * Calculates the smallest height beyond which increasing the height never
     * decreases the width.
     */
    fun maxIntrinsicHeight(width: IntPx): IntPx
}

/**
 * A function for performing intrinsic measurement.
 */
typealias IntrinsicMeasureBlock = DensityScope.(List<IntrinsicMeasurable>, IntPx) -> IntPx
