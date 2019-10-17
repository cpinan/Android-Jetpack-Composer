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

@file:Suppress("PLUGIN_WARNING")

package androidx.ui.framework.demos

import androidx.ui.core.Constraints
import androidx.ui.core.Dp
import androidx.ui.core.Draw
import androidx.ui.core.Layout
import androidx.ui.core.coerceIn
import androidx.ui.core.ipx
import androidx.ui.core.toRect
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.compose.Composable
import androidx.ui.graphics.Brush
import androidx.ui.graphics.SolidColor

@Composable
fun ColoredRect(brush: Brush, width: Dp? = null, height: Dp? = null) {
    Layout(children = { DrawFillRect(brush = brush) }) { _, constraints ->
        layout(
            width?.toIntPx()?.coerceIn(constraints.minWidth, constraints.maxWidth)
                ?: constraints.maxWidth,
            height?.toIntPx()?.coerceIn(constraints.minHeight, constraints.maxHeight)
                ?: constraints.maxHeight
        ) {}
    }
}

@Composable
fun ColoredRect(color: Color, width: Dp? = null, height: Dp? = null) {
    ColoredRect(brush = SolidColor(color), width = width, height = height)
}

@Composable
private fun DrawFillRect(brush: Brush) {
    Draw { canvas, parentSize ->
        val paint = Paint()
        brush.applyTo(paint)
        canvas.drawRect(parentSize.toRect(), paint)
    }
}

@Composable
fun HeaderFooterLayout(
    header: @Composable() () -> Unit,
    footer: @Composable() () -> Unit,
    content: @Composable() () -> Unit
) {
    @Suppress("USELESS_CAST")
    Layout(header, content, footer) { measurables, constraints ->
        val headerPlaceable = measurables[header].first().measure(
            Constraints.tightConstraints(constraints.maxWidth, 100.ipx)
        )
        val footerPadding = 50.ipx
        val footerPlaceable = measurables[footer].first().measure(
            Constraints.tightConstraints(constraints.maxWidth - footerPadding * 2, 100.ipx)
        )
        val itemHeight =
            (constraints.maxHeight - headerPlaceable.height - footerPlaceable.height) /
                    measurables[content].size
        val contentPlaceables = measurables[content].map { measurable ->
            measurable.measure(Constraints.tightConstraints(constraints.maxWidth, itemHeight))
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            headerPlaceable.place(0.ipx, 0.ipx)
            footerPlaceable.place(footerPadding, constraints.maxHeight - footerPlaceable.height)
            var top = headerPlaceable.height
            contentPlaceables.forEach { placeable ->
                placeable.place(0.ipx, top)
                top += itemHeight
            }
        }
    }
}

@Composable
fun MultipleCollectTest() {
    val header = @Composable {
        ColoredRect(color = Color(android.graphics.Color.GRAY))
    }
    val footer = @Composable {
        ColoredRect(color = Color(android.graphics.Color.BLUE))
    }
    HeaderFooterLayout(header = header, footer = footer) {
        ColoredRect(color = Color(android.graphics.Color.GREEN))
        ColoredRect(color = Color(android.graphics.Color.YELLOW))
    }
}
