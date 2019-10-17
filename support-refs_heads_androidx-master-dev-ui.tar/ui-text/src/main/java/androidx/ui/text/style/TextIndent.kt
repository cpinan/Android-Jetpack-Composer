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

package androidx.ui.text.style

import androidx.ui.core.Sp
import androidx.ui.core.lerp
import androidx.ui.core.sp

/**
 * Specify the indentation of a paragraph.
 *
 * @param firstLine the amount of indentation applied to the first line.
 * @param restLine the amount of indentation applied to every line except the first line.
 */
data class TextIndent(
    val firstLine: Sp = 0.sp,
    val restLine: Sp = 0.sp
)

/**
 * Linearly interpolate between two [TextIndent]s.
 *
 * @see [lerp]
 */
fun lerp(start: TextIndent, stop: TextIndent, fraction: Float): TextIndent {
    return TextIndent(
        lerp(start.firstLine, stop.firstLine, fraction),
        lerp(start.restLine, stop.restLine, fraction)
    )
}
