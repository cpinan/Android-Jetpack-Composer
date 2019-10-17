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

import androidx.annotation.Sampled
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.dp
import androidx.ui.foundation.ColoredRect
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.RectangleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Align
import androidx.ui.layout.Container
import androidx.ui.layout.Expanded
import androidx.ui.layout.ExpandedHeight
import androidx.ui.layout.ExpandedWidth

@Sampled
@Composable
fun SimpleExpandedWidthModifier() {
    Container(modifier = ExpandedWidth) {
        DrawShape(RectangleShape, Color.Red)
        ColoredRect(color = Color.Magenta, width = 100.dp, height = 100.dp)
    }
}

@Sampled
@Composable
fun SimpleExpandedHeightModifier() {
    Align(alignment = Alignment.TopLeft) {
        Container(modifier = ExpandedHeight) {
            DrawShape(RectangleShape, Color.Red)
            ColoredRect(color = Color.Magenta, width = 100.dp, height = 100.dp)
        }
    }
}

@Sampled
@Composable
fun SimpleExpandedModifier() {
    Align(alignment = Alignment.TopLeft) {
        Container(modifier = Expanded) {
            DrawShape(RectangleShape, Color.Red)
            ColoredRect(color = Color.Magenta, width = 100.dp, height = 100.dp)
        }
    }
}