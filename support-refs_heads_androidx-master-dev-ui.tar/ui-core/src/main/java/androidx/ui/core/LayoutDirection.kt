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

package androidx.ui.core

/**
 * A class for defining layout directions.
 *
 * A layout direction can be left-to-right (LTR) or right-to-left (RTL).
 */
enum class LayoutDirection {
    /**
     * Horizontal layout direction is from Left to Right.
     */
    Ltr,

    /**
     * Horizontal layout direction is from Right to Left.
     */
    Rtl
}