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
package androidx.ui.text.font

/**
 *  Possible options for font synthesis.
 *
 *  `FontSynthesis` is used to specify whether the system should fake bold or slanted
 *  glyphs when the [FontFamily] used does not contain bold or oblique [Font]s.
 *
 *  If the font family does not include a requested [FontWeight] or [FontStyle], the system
 *  fakes bold or slanted glyphs when the [Weight] or [Style], respectively, or both when [All]
 *  is set. If this is not desired, use [None] to disable font synthesis.
 *
 *  It is possible to fake an increase of [FontWeight] but not a decrease. It is possible to fake
 *  a regular font slanted, but not vice versa.
 *
 *  `FontSynthesis` works the same way as the [CSS font-synthesis](https://www.w3
 *  .org/TR/css-fonts-4/#font-synthesis) property.
 *
 *  @sample androidx.ui.text.samples.FontFamilySynthesisSample
 **/
enum class FontSynthesis {
    /**
     * Turns off font synthesis. Neither bold nor slanted faces are synthesized if they don't
     * exist in the [FontFamily]
     */
    None,

    /**
     * Only a bold font is synthesized, if it is not available in the [FontFamily]. Slanted fonts
     * will not be synthesized.
     */
    Weight,

    /**
     * Only an slanted font is synthesized, if it is not available in the [FontFamily]. Bold fonts
     * will not be synthesized.
     */
    Style,

    /**
     * The system synthesizes both bold and slanted fonts if either of them are not available in
     * the [FontFamily]
     */
    All;

    internal val isWeightOn: Boolean
        get() = this == All || this == Weight

    internal val isStyleOn: Boolean
        get() = this == All || this == Style
}
