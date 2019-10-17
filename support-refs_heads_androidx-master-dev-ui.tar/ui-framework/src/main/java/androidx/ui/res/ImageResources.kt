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

package androidx.ui.res

import androidx.annotation.CheckResult
import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.ui.core.ContextAmbient
import androidx.ui.graphics.Image
import androidx.ui.graphics.imageFromResource

/**
 * Synchronously load an image resource.
 *
 * Note: This API is transient and will be likely removed for encouraging async resource loading.
 *
 * @param id the resource identifier
 * @return the decoded image data associated with the resource
 */
@CheckResult(suggest = "+")
fun imageResource(@DrawableRes id: Int) = effectOf<Image> {
    val context = +ambient(ContextAmbient)
    imageFromResource(context.resources, id)
}