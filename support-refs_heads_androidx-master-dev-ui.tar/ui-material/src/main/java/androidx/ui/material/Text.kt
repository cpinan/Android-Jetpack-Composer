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

import androidx.annotation.FloatRange
import androidx.ui.graphics.Color
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.unaryPlus
import androidx.ui.core.currentTextStyle
import androidx.ui.text.TextStyle

/**
 * Tries to match the background color to correlated text color. For example,
 * on [MaterialColors.primary] background [MaterialColors.onPrimary] will be used.
 * If the background is not from the [MaterialColors] the text color will not be
 * automatically resolved.
 */
fun textColorForBackground(background: Color) = effectOf<Color?> {
    with(+ambient(Colors)) {
        when (background) {
            primary -> onPrimary
            primaryVariant -> onPrimary
            secondary -> onSecondary
            secondaryVariant -> onSecondary
            this.background -> onBackground
            surface -> onSurface
            error -> onError
            else -> null
        }
    }
}

/**
 * TEMPORARY solution to apply an opacity for the [TextStyle] even if it has no
 * color provided. We wanted to have this right now to improve our tutorial for ADS.
 * But the problem is actually way wider and we would need to rethink how we apply
 * modifications like this to our styles and do we need similar methods for other
 * params we have in TextStyle.
 * We will continue investigation as part of b/141362712
 *
 * @param opacity the text color opacity to apply for this [TextStyle].
 */
fun TextStyle.withOpacity(@FloatRange(from = 0.0, to = 1.0) opacity: Float): TextStyle {
    val color = color ?: (+currentTextStyle()).color ?: Color.Black
    return copy(color = color.copy(alpha = opacity))
}
