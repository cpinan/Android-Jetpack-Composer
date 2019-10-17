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

package androidx.ui.text.font

/**
 * Given a [FontFamily], [FontWeight] and [FontStyle], matches the best font in the [FontFamily]
 * that satisfies the requirements of [FontWeight] and [FontStyle].
 *
 * For the case without font synthesis, applies the rules at
 * [CSS 4 Font Matching](https://www.w3.org/TR/css-fonts-4/#font-style-matching).
 */
internal open class FontMatcher {

    /**
     * Given a [FontFamily], [FontWeight] and [FontStyle], matches the best font in the
     * [FontFamily] that satisfies the requirements of [FontWeight] and [FontStyle]. If there is
     * not a font that exactly satisfies the given constraints of [FontWeight] and [FontStyle], the
     * best match will be returned. The rules for the best match are defined in
     * [CSS 4 Font Matching](https://www.w3.org/TR/css-fonts-4/#font-style-matching).
     *
     * @param fontFamily FontFamily to choose the [Font] from
     * @param fontWeight desired [FontWeight]
     * @param fontStyle desired [FontStyle]
     */
    open fun matchFont(fontFamily: FontFamily, fontWeight: FontWeight, fontStyle: FontStyle): Font {
        // check for exact match first
        fontFamily.filter { it.weight == fontWeight && it.style == fontStyle }.let {
            // TODO(b/130797349): IR compiler bug was here
            if (it.isNotEmpty()) {
                return it[0]
            }
        }

        // if no exact match, filter with style
        val fonts = fontFamily.filter { it.style == fontStyle }.let {
            if (it.isNotEmpty()) it else fontFamily.fonts
        }

        val result = if (fontWeight < FontWeight.W400) {
            // If the desired weight is less than 400
            // - weights less than or equal to the desired weight are checked in descending order
            // - followed by weights above the desired weight in ascending order
            fonts.filter { it.weight <= fontWeight }.maxBy { it.weight }
                ?: fonts.filter { it.weight > fontWeight }.minBy { it.weight }
        } else if (fontWeight > FontWeight.W500) {
            // If the desired weight is greater than 500
            // - weights greater than or equal to the desired weight are checked in ascending order
            // - followed by weights below the desired weight in descending order
            fonts.filter { it.weight >= fontWeight }.minBy { it.weight }
                ?: fonts.filter { it.weight < fontWeight }.maxBy { it.weight }
        } else {
            // If the desired weight is inclusively between 400 and 500
            // - weights greater than or equal to the target weight are checked in ascending order
            // until 500 is hit and checked,
            // - followed by weights less than the target weight in descending order,
            // - followed by weights greater than 500
            fonts.filter { it.weight >= fontWeight && it.weight <= FontWeight.W500 }
                .minBy { it.weight }
                ?: fonts.filter { it.weight < fontWeight }.maxBy { it.weight }
                ?: fonts.filter { it.weight > FontWeight.W500 }.minBy { it.weight }
        }

        return result ?: throw IllegalStateException("Cannot match any font")
    }
}