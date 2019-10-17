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

package androidx.ui.foundation.shape

import androidx.ui.core.Density
import androidx.ui.core.PxSize
import androidx.ui.engine.geometry.Outline
import androidx.ui.engine.geometry.Shape
import androidx.ui.graphics.Path

/**
 * Creates [Shape] defined by applying the provided [builder] on a [Path].
 *
 * @param builder the builder lambda to apply on a [Path]
 */
data class GenericShape(
    private val builder: Path.(size: PxSize) -> Unit
) : Shape {
    override fun createOutline(size: PxSize, density: Density): Outline {
        val path = Path().apply {
            builder(size)
            close()
        }
        return Outline.Generic(path)
    }
}
