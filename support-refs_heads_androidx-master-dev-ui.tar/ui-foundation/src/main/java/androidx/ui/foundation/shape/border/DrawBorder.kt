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

package androidx.ui.foundation.shape.border

import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Dp
import androidx.ui.core.Draw
import androidx.ui.core.PxSize
import androidx.ui.core.px
import androidx.ui.engine.geometry.Offset
import androidx.ui.engine.geometry.Shape
import androidx.ui.engine.geometry.addOutline
import androidx.ui.graphics.Paint
import androidx.ui.graphics.Path
import androidx.ui.graphics.PathOperation

/**
 * Draw the [Border] as an inner stroke for the provided [shape].
 *
 * @param shape the [Shape] to define the outline for drawing.
 * @param border the [Border] to draw.
 */
@Composable
fun DrawBorder(shape: Shape, border: Border) {
    with(+memo { DrawBorderCachesHolder() }) {
        lastShape = shape
        lastBorderWidth = border.width
        Draw { canvas, parentSize ->
            lastParentSize = parentSize

            if (!outerPathIsCached) {
                outerPath.reset()
                outerPath.addOutline(shape.createOutline(parentSize, density))
                outerPathIsCached = true
            }

            if (!diffPathIsCached) {
                // to have an inner path we provide a smaller parent size and shift the result
                val borderSize = if (border.width == Dp.Hairline) 1.px else border.width.toPx()
                val sizeMinusBorder = PxSize(
                    width = parentSize.width - borderSize * 2,
                    height = parentSize.height - borderSize * 2
                )
                innerPath.reset()
                innerPath.addOutline(shape.createOutline(sizeMinusBorder, density))
                innerPath.shift(Offset(borderSize.value, borderSize.value))

                // now we calculate the diff between the inner and the outer paths
                diffPath.op(outerPath, innerPath, PathOperation.difference)
                diffPathIsCached = true
            }

            border.brush.applyTo(paint)
            canvas.drawPath(diffPath, paint)
        }
    }
}

private class DrawBorderCachesHolder {
    val outerPath = Path()
    val innerPath = Path()
    val diffPath = Path()
    val paint = Paint().apply { isAntiAlias = true }
    var outerPathIsCached = false
    var diffPathIsCached = false
    var lastParentSize: PxSize? = null
        set(value) {
            if (value != field) {
                field = value
                outerPathIsCached = false
                diffPathIsCached = false
            }
        }
    var lastShape: Shape? = null
        set(value) {
            if (value != field) {
                field = value
                outerPathIsCached = false
                diffPathIsCached = false
            }
        }
    var lastBorderWidth: Dp? = null
        set(value) {
            if (value != field) {
                field = value
                diffPathIsCached = false
            }
        }
}
