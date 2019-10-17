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
import androidx.ui.layout.FlowColumn
import androidx.ui.layout.FlowRow

val sizes = listOf(
    50.dp, 50.dp, 50.dp, 30.dp, 40.dp, 110.dp, 100.dp, 40.dp, 30.dp,
    20.dp, 70.dp, 50.dp, 100.dp, 20.dp, 60.dp, 60.dp, 50.dp, 60.dp
)

@Sampled
@Composable
fun SimpleFlowRow() {
    FlowRow(
        mainAxisSpacing = 10.dp,
        crossAxisSpacing = 10.dp
    ) {
        sizes.forEach { size ->
            SizedRectangle(color = Color.Magenta, width = size, height = 20.dp)
        }
    }
}

@Sampled
@Composable
fun SimpleFlowColumn() {
    FlowColumn(
        mainAxisSpacing = 10.dp,
        crossAxisSpacing = 10.dp
    ) {
        sizes.forEach { size ->
            SizedRectangle(color = Color.Magenta, width = 20.dp, height = size)
        }
    }
}