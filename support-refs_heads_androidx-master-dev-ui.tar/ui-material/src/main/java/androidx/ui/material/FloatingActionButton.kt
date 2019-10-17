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
import androidx.compose.unaryPlus
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.Dp
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.engine.geometry.Shape
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.SimpleImage
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.layout.WidthSpacer
import androidx.ui.graphics.Image
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Surface
import androidx.ui.text.TextStyle

/**
 * A floating action button (FAB) is a [Button] to represents the primary action of a screen.
 *
 * By default it adds the circle shape, the [FabSize] size and the centering for the content.
 *
 * @sample androidx.ui.material.samples.FloatingActionButtonCustomContent
 *
 * @see FloatingActionButton overload for the variants with an icon or an icon and a text.
 *
 * @param onClick will be called when user clicked on the button. The button will be disabled
 *  when it is null.
 * @param minSize Minimum size of the button. Defaults to [FabSize]
 * @param shape Defines the Button's shape as well its shadow. When null is provided it uses
 *  the [Shapes.button] from [CurrentShapeAmbient].
 * @param color The background color. [MaterialColors.primary] is used by default.
 * @param elevation The z-coordinate at which to place this button. This controls the size
 *  of the shadow below the button.
 */
@Composable
fun FloatingActionButton(
    onClick: (() -> Unit)? = null,
    minSize: Dp = FabSize,
    shape: Shape = CircleShape,
    color: Color = +themeColor { primary },
    elevation: Dp = 6.dp,
    children: @Composable() () -> Unit
) {
    Surface(shape = shape, color = color, elevation = elevation) {
        Ripple(bounded = true, enabled = onClick != null) {
            Clickable(onClick) {
                Container(constraints = DpConstraints(minWidth = minSize, minHeight = minSize)) {
                    CurrentTextStyleProvider(+themeTextStyle { button }, children)
                }
            }
        }
    }
}

/**
 * A floating action button (FAB) is a [Button] to represents the primary action of a screen.
 *
 * It draws the [icon] in the center of the FAB with [FabSize] size.
 *
 * @sample androidx.ui.material.samples.FloatingActionButtonSimple
 *
 * @see FloatingActionButton overload for the variants with a custom content or an icon and a text.
 *
 * @param icon Image to draw in the center.
 * @param onClick will be called when user clicked on the button. The button will be disabled
 *  when it is null.
 * @param color The background color. [MaterialColors.primary] is used by default.
 * @param elevation The z-coordinate at which to place this button. This controls the size
 *  of the shadow below the button.
 */
@Composable
fun FloatingActionButton(
    icon: Image,
    onClick: (() -> Unit)? = null,
    shape: Shape = CircleShape,
    color: Color = +themeColor { primary },
    elevation: Dp = 6.dp
) {
    FloatingActionButton(onClick = onClick, shape = shape, color = color, elevation = elevation) {
        SimpleImage(image = icon)
    }
}

/**
 * An extended [FloatingActionButton] with an [icon] and a [text].
 *
 * @sample androidx.ui.material.samples.FloatingActionButtonExtended
 *
 * @see FloatingActionButton overload for the variants with a custom content or an icon.
 *
 * @param text Text to display.
 * @param icon Image to draw to the left of the text. It is optional.
 * @param textStyle Optional [TextStyle] to apply for a [text]
 * @param onClick will be called when user clicked on the button. The button will be disabled
 *  when it is null.
 * @param color The background color. [MaterialColors.primary] is used by default.
 * @param elevation The z-coordinate at which to place this button. This controls the size
 *  of the shadow below the button.
 */
@Composable
fun FloatingActionButton(
    text: String,
    icon: Image? = null,
    textStyle: TextStyle? = null,
    onClick: (() -> Unit)? = null,
    color: Color = +themeColor { primary },
    elevation: Dp = 6.dp
) {
    FloatingActionButton(
        onClick = onClick,
        color = color,
        elevation = elevation,
        minSize = ExtendedFabHeight) {
        if (icon == null) {
            Padding(left = ExtendedFabTextPadding, right = ExtendedFabTextPadding) {
                Text(text = text, style = textStyle)
            }
        } else {
            Padding(left = ExtendedFabIconPadding, right = ExtendedFabTextPadding) {
                Row {
                    SimpleImage(image = icon)
                    WidthSpacer(width = ExtendedFabIconPadding)
                    Text(text = text, style = textStyle)
                }
            }
        }
    }
}

val FabSize = 56.dp
val ExtendedFabHeight = 48.dp
val ExtendedFabIconPadding = 12.dp
val ExtendedFabTextPadding = 20.dp
