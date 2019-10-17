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
 * A holder of the measured bounds for the layout (MeasureBox).
 */
// TODO(Andrey): Add Matrix transformation here when we would have this logic.
interface LayoutCoordinates {

    /**
     * The position within the parent of this layout.
     */
    val position: PxPosition

    /**
     * The size of this layout in the local coordinates space.
     */
    val size: PxSize

    /**
     * The alignment lines provided for this layout, relative to the current layout.
     * This map does not contain inherited lines.
     */
    val providedAlignmentLines: Map<AlignmentLine, IntPx>

    /**
     * The coordinates of the parent layout. Null if there is no parent.
     */
    val parentCoordinates: LayoutCoordinates?

    /**
     * Converts a global position into a local position within this layout.
     */
    fun globalToLocal(global: PxPosition): PxPosition

    /**
     * Converts a local position within this layout into a global one.
     */
    fun localToGlobal(local: PxPosition): PxPosition

    /**
     * Converts a local position within this layout into an offset from the root composable.
     */
    fun localToRoot(local: PxPosition): PxPosition

    /**
     * Converts a child layout position into a local position within this layout.
     */
    fun childToLocal(child: LayoutCoordinates, childLocal: PxPosition): PxPosition
}

/**
 * The global position of this layout.
 */
inline val LayoutCoordinates.globalPosition: PxPosition get() = localToGlobal(PxPosition.Origin)

/**
 * The position of this layout inside the root composable.
 */
inline val LayoutCoordinates.positionInRoot: PxPosition get() = localToGlobal(PxPosition.Origin)
