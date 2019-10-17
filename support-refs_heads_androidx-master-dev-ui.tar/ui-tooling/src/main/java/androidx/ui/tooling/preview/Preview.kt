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

package androidx.ui.tooling.preview

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.Composable

/**
 * [Preview] can be applied to @[Composable] methods with no parameters to show them in the Android
 * Studio preview.
 *
 * The annotation contains a number of parameters that allow to define the way the @[Composable]
 * will be rendered within the preview.
 *
 * The passed parameters are only read by Studio when rendering the preview.
 *
 * @param name Display name of this preview allowing to identify it in the panel.
 * @param apiLevel API level to be used when rendering the annotated @[Composable]
 * @param theme Theme name to be used when rendering the annotated @[Composable]
 * @param widthDp Max width in DP the annotated @[Composable] will be rendered in. Use this to
 * restrict the size of the rendering viewport.
 * @param heightDp Max height in DP the annotated @[Composable] will be rendered in. Use this to
 * restrict the size of the rendering viewport.
 * @param locale Current user preference for the locale, corresponding to
 * [locale]({@docRoot}guide/topics/resources/providing-resources.html#LocaleQualifier) resource
 * qualifier. By default, the `default` folder will be used.
 * @param fontScale User preference for the scaling factor for fonts, relative to the base
 * density scaling.
 */
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.FUNCTION
)
annotation class Preview(
    val name: String = "",
    @IntRange(from = 1) val apiLevel: Int = -1,
    val theme: String = "",
    // TODO(mount): Make this Dp when they are inline classes
    val widthDp: Int = -1,
    // TODO(mount): Make this Dp when they are inline classes
    val heightDp: Int = -1,
    val locale: String = "",
    @FloatRange(from = 0.01) val fontScale: Float = 1f
)