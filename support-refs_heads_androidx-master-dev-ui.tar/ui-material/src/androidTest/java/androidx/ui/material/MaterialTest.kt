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

package androidx.ui.material

import androidx.compose.Composable
import androidx.ui.core.PxSize
import androidx.ui.test.BigTestConstraints
import androidx.ui.test.CollectedSizes
import androidx.ui.test.setContentAndGetPixelSize
import androidx.ui.layout.DpConstraints
import androidx.ui.material.surface.Surface
import androidx.ui.test.ComposeTestRule

fun ComposeTestRule.setMaterialContent(composable: @Composable() () -> Unit) {
    setContent {
        MaterialTheme {
            Surface {
                composable()
            }
        }
    }
}

fun ComposeTestRule.setMaterialContentAndCollectSizes(
    parentConstraints: DpConstraints = BigTestConstraints,
    children: @Composable() () -> Unit
): CollectedSizes {
    val sizes = setMaterialContentAndGetPixelSize(parentConstraints, children)
    return CollectedSizes(sizes, density)
}

fun ComposeTestRule.setMaterialContentAndGetPixelSize(
    parentConstraints: DpConstraints = BigTestConstraints,
    children: @Composable() () -> Unit
): PxSize = setContentAndGetPixelSize(
    parentConstraints,
    { setMaterialContent(it) }
) {
    MaterialTheme {
        Surface {
            children()
        }
    }
}