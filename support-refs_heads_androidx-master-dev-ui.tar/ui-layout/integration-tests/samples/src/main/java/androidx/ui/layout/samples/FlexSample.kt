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
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.FlexColumn
import androidx.ui.layout.FlexRow
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Row

@Sampled
@Composable
fun SimpleFlexRow() {
    FlexRow {
        expanded(flex = 2f) {
            Center {
                SizedRectangle(color = Color(0xFF0000FF), width = 40.dp, height = 40.dp)
            }
        }
        inflexible {
            SizedRectangle(color = Color(0xFFFF0000), width = 40.dp)
        }
        expanded(flex = 1f) {
            SizedRectangle(color = Color(0xFF00FF00))
        }
    }
}

@Sampled
@Composable
fun SimpleFlexColumn() {
    FlexColumn {
        expanded(flex = 2f) {
            Center {
                SizedRectangle(color = Color(0xFF0000FF), width = 40.dp, height = 40.dp)
            }
        }
        inflexible {
            SizedRectangle(color = Color(0xFFFF0000), height = 40.dp)
        }
        expanded(flex = 1f) {
            SizedRectangle(color = Color(0xFF00FF00))
        }
    }
}

@Sampled
@Composable
fun SimpleRow() {
    Row(mainAxisSize = LayoutSize.Expand) {
        // The child with no flexibility modifier is inflexible by default, will have the specified
        // size.
        SizedRectangle(color = Color.Magenta, width = 40.dp, height = 80.dp)
        // Inflexible, the child will have the specified size.
        SizedRectangle(Inflexible, color = Color.Red, width = 80.dp, height = 40.dp)
        // Flexible, the child will occupy have of the remaining width.
        SizedRectangle(Flexible(1f), color = Color.Yellow, height = 40.dp)
        // Flexible not tight, the child will occupy at most half of the remaining width.
        SizedRectangle(Flexible(1f, tight = false), color = Color.Green, height = 80.dp)
    }
}

@Sampled
@Composable
fun SimpleColumn() {
    Column(mainAxisSize = LayoutSize.Expand) {
        // The child with no flexibility modifier is inflexible by default, will have the specified
        // size.
        SizedRectangle(color = Color.Magenta, width = 40.dp, height = 80.dp)
        // Inflexible, the child will have the specified size.
        SizedRectangle(Inflexible, color = Color.Red, width = 80.dp, height = 40.dp)
        // Flexible, the child will occupy have of the remaining height.
        SizedRectangle(Flexible(1f), color = Color.Yellow, width = 40.dp)
        // Flexible not tight, the child will occupy at most half of the remaining height.
        SizedRectangle(Flexible(1f, tight = false), color = Color.Green, width = 80.dp)
    }
}