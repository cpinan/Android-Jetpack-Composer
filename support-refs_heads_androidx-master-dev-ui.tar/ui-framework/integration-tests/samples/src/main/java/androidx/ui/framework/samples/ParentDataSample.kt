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

package androidx.ui.framework.samples

import androidx.annotation.Sampled
import androidx.compose.Composable
import androidx.ui.core.Layout
import androidx.ui.core.ParentData
import androidx.ui.core.dp
import androidx.ui.graphics.Color

@Sampled
@Composable
fun ParentDataSample() {
    val parentDataComposable = @Composable {
        ParentData(data = 5) {
            SizedRectangle(color = Color.Blue, width = 50.dp, height = 50.dp)
        }
    }
    Layout(parentDataComposable) { measurables, constraints ->
        // The parentData will be 5.
        measurables[0].parentData as Int
        layout(constraints.maxWidth, constraints.maxHeight) {}
    }
}
