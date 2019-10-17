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

import androidx.compose.Composable
import androidx.ui.engine.geometry.Shape

/**
 * Draws the shadow. The [elevation] defines the visual dept of the physical object.
 * The physical object has a shape specified by [shape].
 *
 * Example usage:
 *
 * @sample androidx.ui.framework.samples.DrawShadowSample
 *
 * @param elevation The z-coordinate at which to place this physical object.
 * @param shape Defines a shape of the physical object
 */
@Composable
@Suppress("NOTHING_TO_INLINE")
inline fun DrawShadow(shape: Shape, elevation: Dp) {
    RepaintBoundaryNode(name = null, shape = shape, elevation = elevation)
}
