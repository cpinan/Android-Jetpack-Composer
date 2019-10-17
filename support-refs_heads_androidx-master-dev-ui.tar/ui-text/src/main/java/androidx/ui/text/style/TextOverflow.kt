/*
 * Copyright 2018 The Android Open Source Project
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

/** How overflowing text should be handled. */
enum class TextOverflow {
    /** Clip the overflowing text to fix its container. */
    Clip,

    /** Fade the overflowing text to transparent. */
    Fade,

    /** Use an ellipsis to indicate that the text has overflowed. */
    Ellipsis,
}