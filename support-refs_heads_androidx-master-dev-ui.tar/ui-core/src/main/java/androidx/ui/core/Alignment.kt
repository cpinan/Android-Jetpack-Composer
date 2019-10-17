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
 * Represents a positioning of a point inside a 2D box. [Alignment] is often used to define
 * the alignment of a box inside a parent container.
 * The coordinate space of the 2D box is the continuous [-1f, 1f] range in both dimensions,
 * so (verticalBias, horizontalBias) will be points in this space. (verticalBias=0f,
 * horizontalBias=0f) represents the center of the box, (verticalBias=-1f, horizontalBias=1f)
 * will be the top right, etc.
 */
enum class Alignment(private val verticalBias: Float, private val horizontalBias: Float) {
    TopLeft(-1f, -1f),
    TopCenter(-1f, 0f),
    TopRight(-1f, 1f),
    CenterLeft(0f, -1f),
    Center(0f, 0f),
    CenterRight(0f, 1f),
    BottomLeft(1f, -1f),
    BottomCenter(1f, 0f),
    BottomRight(1f, 1f);

    /**
     * Returns the position of a 2D point in a container of a given size,
     * according to this [Alignment].
     */
    fun align(size: IntPxSize): IntPxPosition {
        // Convert to Px first and only round at the end, to avoid rounding twice while calculating
        // the new positions
        val centerX = size.width.toPx() / 2f
        val centerY = size.height.toPx() / 2f
        val x = centerX * (1 + horizontalBias)
        val y = centerY * (1 + verticalBias)
        return IntPxPosition(x.round(), y.round())
    }
}
