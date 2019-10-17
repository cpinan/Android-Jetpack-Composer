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
import androidx.ui.graphics.Color
import androidx.ui.layout.Stack

@Sampled
@Composable
fun SimpleStack() {
    Stack {
        expanded {
            SizedRectangle(color = Color(0xFFFFFFFF.toInt()))
        }
        aligned(Alignment.Center) {
            SizedRectangle(color = Color(0xFF0000FF), width = 300.dp, height = 300.dp)
        }
        aligned(Alignment.TopLeft) {
            SizedRectangle(color = Color(0xFF00FF00), width = 150.dp, height = 150.dp)
        }
        aligned(Alignment.BottomRight) {
            SizedRectangle(color = Color(0xFFFF0000), width = 150.dp, height = 150.dp)
        }
        positioned(null, 20.dp, null, 20.dp) {
            SizedRectangle(color = Color(0xFFFFA500), width = 80.dp)
            SizedRectangle(color = Color(0xFFA52A2A), width = 20.dp)
        }
    }
}