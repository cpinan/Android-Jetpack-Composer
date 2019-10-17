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

package androidx.ui.layout.samples

import androidx.compose.Composable
import androidx.compose.composer
import androidx.ui.core.Dp
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.max
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.graphics.Color

/**
 * A rectangle layout that tries to size itself to specified width and height, subject to
 * the incoming constraints. The composable also draws a color in the space of the rectangle
 * layout. If the width and/or height of the rectangle are not specified, the rectangle will
 * be sized to the incoming max constraints.
 */
@Composable
fun SizedRectangle(
    modifier: Modifier = Modifier.None,
    color: Color,
    width: Dp? = null,
    height: Dp? = null
) {
    Layout(children = { DrawRectangle(color = color) }, modifier = modifier) { _, constraints ->
        val widthPx = max(width?.toIntPx() ?: constraints.maxWidth, constraints.minWidth)
        val heightPx = max(height?.toIntPx() ?: constraints.maxHeight, constraints.minHeight)
        layout(widthPx, heightPx) {}
    }
}

/**
 * Draws a rectangle of a given color in the space of the parent layout.
 */
@Composable
fun DrawRectangle(color: Color) {
    DrawShape(shape = RectangleShape, color = color)
}
