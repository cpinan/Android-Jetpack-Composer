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

package androidx.ui.text

import androidx.ui.core.Sp
import androidx.ui.core.sp
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shadow
import androidx.ui.graphics.lerp
import androidx.ui.lerp
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontStyle
import androidx.ui.text.font.FontSynthesis
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.BaselineShift
import androidx.ui.text.style.TextDecoration
import androidx.ui.text.style.TextGeometricTransform
import androidx.ui.text.style.lerp

/**
 * Styling configuration for a `Text`.
 *
 * @sample androidx.ui.text.samples.TextStyleSample
 *
 * @param color The text color.
 * @param fontSize The size of glyphs (in logical pixels) to use when painting the text.
 * @param fontSizeScale The scale factor of the font size. When [fontSize] is also given in this
 *  TextStyle, the final fontSize will be the [fontSize] times this value.
 *  Otherwise, the final fontSize will be the current fontSize times this value.
 * @param fontWeight The typeface thickness to use when painting the text (e.g., bold).
 * @param fontStyle The typeface variant to use when drawing the letters (e.g., italic).
 * @param fontSynthesis Whether to synthesize font weight and/or style when the requested weight or
 *  style cannot be found in the provided custom font family.
 * @param fontFamily The font family to be used when rendering the text.
 * @param fontFeatureSettings The advanced typography settings provided by font. The format is the
 *  same as the CSS font-feature-settings attribute:
 *  https://www.w3.org/TR/css-fonts-3/#font-feature-settings-prop
 * @param letterSpacing The amount of space (in em) to add between each letter.
 * @param baselineShift The amount by which the text is shifted up from the current baseline.
 * @param textGeometricTransform The geometric transformation applied the text.
 * @param localeList The locale list used to select region-specific glyphs.
 * @param background The background color for the text.
 * @param decoration The decorations to paint near the text (e.g., an underline).
 * @param shadow The shadow effect applied on the text.
 *
 *  @see [AnnotatedString]
 */
data class TextStyle(
    val color: Color? = null,
    val fontSize: Sp? = null,
    val fontSizeScale: Float? = null,
    val fontWeight: FontWeight? = null,
    val fontStyle: FontStyle? = null,
    val fontSynthesis: FontSynthesis? = null,
    var fontFamily: FontFamily? = null,
    val fontFeatureSettings: String? = null,
    val letterSpacing: Float? = null,
    val baselineShift: BaselineShift? = null,
    val textGeometricTransform: TextGeometricTransform? = null,
    val localeList: LocaleList? = null,
    val background: Color? = null,
    val decoration: TextDecoration? = null,
    val shadow: Shadow? = null
) {
    /**
     * Returns a new text style that is a combination of this style and the given [other] style.
     *
     * [other] text style's null properties are replaced with the non-null properties of this text
     * style. Another way to think of it is that the "missing" properties of the [other] style are
     * _filled_ by the properties of this style.
     *
     * If the given text style is null, returns this text style.
     */
    fun merge(other: TextStyle? = null): TextStyle {
        if (other == null) return this

        return TextStyle(
            color = other.color ?: this.color,
            fontFamily = other.fontFamily ?: this.fontFamily,
            fontSize = other.fontSize ?: this.fontSize,
            fontSizeScale = other.fontSizeScale ?: this.fontSizeScale,
            fontWeight = other.fontWeight ?: this.fontWeight,
            fontStyle = other.fontStyle ?: this.fontStyle,
            fontSynthesis = other.fontSynthesis ?: this.fontSynthesis,
            fontFeatureSettings = other.fontFeatureSettings ?: this.fontFeatureSettings,
            letterSpacing = other.letterSpacing ?: this.letterSpacing,
            baselineShift = other.baselineShift ?: this.baselineShift,
            textGeometricTransform = other.textGeometricTransform ?: this.textGeometricTransform,
            localeList = other.localeList ?: this.localeList,
            background = other.background ?: this.background,
            decoration = other.decoration ?: this.decoration,
            shadow = other.shadow ?: this.shadow
        )
    }

    internal companion object {
        private fun lerpColor(a: Color?, b: Color?, t: Float): Color? {
            if (a == null && b == null) {
                return null
            }
            val start = a ?: b!!.copy(alpha = 0f)
            val end = b ?: a!!.copy(alpha = 0f)
            return lerp(start, end, t)
        }

        private fun lerpFloat(a: Float?, b: Float?, t: Float, default: Float = 0f): Float? {
            if (a == null && b == null) return null
            val start = a ?: default
            val end = b ?: default
            return lerp(start, end, t)
        }

        private fun lerpSp(a: Sp?, b: Sp?, t: Float, default: Sp = 0f.sp): Sp? {
            if (a == null && b == null) return null
            val start = a ?: default
            val end = b ?: default
            return androidx.ui.core.lerp(start, end, t)
        }

        private fun <T> lerpDiscrete(a: T?, b: T?, t: Float): T? = if (t < 0.5) a else b

        /**
         * Interpolate between two text styles.
         *
         * This will not work well if the styles don't set the same fields.
         *
         * The [fraction] argument represents position on the timeline, with 0.0 meaning
         * that the interpolation has not started, returning [start] (or something
         * equivalent to [start]), 1.0 meaning that the interpolation has finished,
         * returning [stop] (or something equivalent to [stop]), and values in between
         * meaning that the interpolation is at the relevant point on the timeline
         * between [start] and [stop]. The interpolation can be extrapolated beyond 0.0 and
         * 1.0, so negative values and values greater than 1.0 are valid.
         */
        // TODO(siyamed): This should not accept nullable values
        // TODO(siyamed): This should be in the file level, not a companon function
        fun lerp(start: TextStyle? = null, stop: TextStyle? = null, fraction: Float): TextStyle? {
            val aIsNull = start == null
            val bIsNull = stop == null

            if (aIsNull && bIsNull) return null

            if (start == null) {
                val newB = stop?.copy() ?: TextStyle()
                return if (fraction < 0.5) {
                    TextStyle(
                        color = lerpColor(null, newB.color, fraction),
                        fontWeight = FontWeight.lerp(null, newB.fontWeight, fraction)
                    )
                } else {
                    newB.copy(
                        color = lerpColor(null, newB.color, fraction),
                        fontWeight = FontWeight.lerp(null, newB.fontWeight, fraction)
                    )
                }
            }

            if (stop == null) {
                return if (fraction < 0.5) {
                    start.copy(
                        color = lerpColor(start.color, null, fraction),
                        fontWeight = FontWeight.lerp(start.fontWeight, null, fraction)
                    )
                } else {
                    TextStyle(
                        color = lerpColor(start.color, null, fraction),
                        fontWeight = FontWeight.lerp(start.fontWeight, null, fraction)
                    )
                }
            }

            return TextStyle(
                color = lerpColor(start.color, stop.color, fraction),
                fontFamily = lerpDiscrete(
                    start.fontFamily,
                    stop.fontFamily,
                    fraction
                ),
                fontSize = lerpSp(start.fontSize, stop.fontSize, fraction),
                fontSizeScale = lerpFloat(
                    start.fontSizeScale,
                    stop.fontSizeScale,
                    fraction,
                    1f
                ),
                fontWeight = FontWeight.lerp(start.fontWeight, stop.fontWeight, fraction),
                fontStyle = lerpDiscrete(
                    start.fontStyle,
                    stop.fontStyle,
                    fraction
                ),
                fontSynthesis = lerpDiscrete(
                    start.fontSynthesis,
                    stop.fontSynthesis,
                    fraction
                ),
                fontFeatureSettings = lerpDiscrete(
                    start.fontFeatureSettings,
                    stop.fontFeatureSettings,
                    fraction
                ),
                letterSpacing = lerpFloat(
                    start.letterSpacing,
                    stop.letterSpacing,
                    fraction
                ),
                baselineShift = BaselineShift.lerp(
                    start.baselineShift,
                    stop.baselineShift,
                    fraction
                ),
                textGeometricTransform = lerp(
                    start.textGeometricTransform ?: TextGeometricTransform.None,
                    stop.textGeometricTransform ?: TextGeometricTransform.None,
                    fraction
                ),
                localeList = lerpDiscrete(start.localeList, stop.localeList, fraction),
                background = lerpDiscrete(
                    start.background,
                    stop.background,
                    fraction
                ),
                decoration = lerpDiscrete(
                    start.decoration,
                    stop.decoration,
                    fraction
                ),
                shadow = lerp(
                    start.shadow ?: Shadow(),
                    stop.shadow ?: Shadow(),
                    fraction
                )
            )
        }
    }

    /**
     * Describe the difference between this style and another, in terms of how
     * much damage it will make to the rendering.
     *
     * See also:
     *
     *  * [TextSpan.compareTo], which does the same thing for entire [TextSpan]s.
     */
    internal fun compareTo(other: TextStyle): RenderComparison {
        if (this == other) {
            return RenderComparison.IDENTICAL
        }
        if (fontFamily != other.fontFamily ||
            fontSize != other.fontSize ||
            fontWeight != other.fontWeight ||
            fontStyle != other.fontStyle ||
            fontSynthesis != other.fontSynthesis ||
            fontFeatureSettings != other.fontFeatureSettings ||
            letterSpacing != other.letterSpacing ||
            baselineShift != other.baselineShift ||
            textGeometricTransform != other.textGeometricTransform ||
            localeList != other.localeList ||
            background != other.background
        ) {
            return RenderComparison.LAYOUT
        }
        if (color != other.color || decoration != other.decoration || shadow != other.shadow) {
            return RenderComparison.PAINT
        }
        return RenderComparison.IDENTICAL
    }
}
