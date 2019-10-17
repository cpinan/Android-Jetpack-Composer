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
import androidx.ui.core.Dp
import androidx.ui.core.Modifier
import androidx.ui.core.dp
import androidx.ui.foundation.ColoredRect
import androidx.ui.graphics.Color
import androidx.ui.layout.Spacing

/**
 * A divider is a thin line that groups content in lists and layouts
 *
 * @param color color of the divider line, [MaterialColors.onSurface] will be used by default
 * @param height height of the divider line, 1 dp is used by default
 * @param indent left offset of this line, no offset by default
 */
@Composable
fun Divider(
    modifier: Modifier = Modifier.None,
    color: Color = +themeColor { onSurface },
    height: Dp = 1.dp,
    indent: Dp = 0.dp
) {
    val indentMod = if (indent.value != 0f) Spacing(left = indent) else Modifier.None
    ColoredRect(color, modifier wraps indentMod, height = height)
}