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
package androidx.ui.graphics

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.Size
import androidx.compose.Immutable
import androidx.ui.graphics.colorspace.ColorSpace
import androidx.ui.graphics.colorspace.ColorSpaces
import androidx.ui.graphics.colorspace.ColorModel
import androidx.ui.graphics.colorspace.Rgb
import androidx.ui.graphics.colorspace.connect
import androidx.ui.lerp
import androidx.ui.util.Float16
import kotlin.math.max
import kotlin.math.min

/**
 * The `Color` class contains color information to be used while painting
 * in [Canvas]. `Color` supports [ColorSpace]s with 3 [components][ColorSpace.componentCount],
 * plus one for [alpha].
 *
 * ### Creating
 *
 * `Color` can be created with one of these methods:
 *
 *     // from 4 separate [Float] components. Alpha and ColorSpace are optional
 *     val rgbaWhiteFloat = Color(red = 1f, green = 1f, blue = 1f, alpha = 1f,
 *         ColorSpace.get(ColorSpaces.Srgb))
 *
 *     // from a 32-bit SRGB color integer
 *     val fromIntWhite = Color(android.graphics.Color.WHITE)
 *     val fromLongBlue = Color(0xFF0000FF)
 *
 *     // from SRGB integer component values. Alpha is optional
 *     val rgbaWhiteInt = Color(red = 0xFF, green = 0xFF, blue = 0xFF, alpha = 0xFF)
 *
 * ### Representation
 *
 * A `Color` always defines a color using 4 components packed in a single
 * 64 bit long value. One of these components is always alpha while the other
 * three components depend on the color space's [color model][ColorModel].
 * The most common color model is the [RGB][ColorModel.Rgb] model in
 * which the components represent red, green, and blue values.
 *
 * **Component ranges:** the ranges defined in the tables
 * below indicate the ranges that can be encoded in a color long. They do not
 * represent the actual ranges as they may differ per color space. For instance,
 * the RGB components of a color in the [Display P3][ColorSpaces.DisplayP3]
 * color space use the `[0..1]` range. Please refer to the documentation of the
 * various [color spaces][ColorSpaces] to find their respective ranges.
 *
 * **Alpha range:** while alpha is encoded in a color long using
 * a 10 bit integer (thus using a range of `[0..1023]`), it is converted to and
 * from `[0..1]` float values when decoding and encoding color longs.
 *
 * **sRGB color space:** for compatibility reasons and ease of
 * use, `Color` encoded [sRGB][ColorSpaces.Srgb] colors do not
 * use the same encoding as other color longs.
 * ```
 * | Component | Name        | Size    | Range                 |
 * |-----------|-------------|---------|-----------------------|
 * | [RGB][ColorSpace.Model.Rgb] color model |
 * | R         | Red         | 16 bits | `[-65504.0, 65504.0]` |
 * | G         | Green       | 16 bits | `[-65504.0, 65504.0]` |
 * | B         | Blue        | 16 bits | `[-65504.0, 65504.0]` |
 * | A         | Alpha       | 10 bits | `[0..1023]`           |
 * |           | Color space | 6 bits  | `[0..63]`             |
 * | [SRGB][ColorSpaces.Srgb] color space |
 * | R         | Red         | 8 bits  | `[0..255]`            |
 * | G         | Green       | 8 bits  | `[0..255]`            |
 * | B         | Blue        | 8 bits  | `[0..255]`            |
 * | A         | Alpha       | 8 bits  | `[0..255]`            |
 * | X         | Unused      | 32 bits | `[0]`                 |
 * | [XYZ][ColorSpace.Model.Xyz] color model |
 * | X         | X           | 16 bits | `[-65504.0, 65504.0]` |
 * | Y         | Y           | 16 bits | `[-65504.0, 65504.0]` |
 * | Z         | Z           | 16 bits | `[-65504.0, 65504.0]` |
 * | A         | Alpha       | 10 bits | `[0..1023]`           |
 * |           | Color space | 6 bits  | `[0..63]`             |
 * | [Lab][ColorSpace.Model.Lab] color model |
 * | L         | L           | 16 bits | `[-65504.0, 65504.0]` |
 * | a         | a           | 16 bits | `[-65504.0, 65504.0]` |
 * | b         | b           | 16 bits | `[-65504.0, 65504.0]` |
 * | A         | Alpha       | 10 bits | `[0..1023]`           |
 * |           | Color space | 6 bits  | `[0..63]`             |
 * ```
 * The components in this table are listed in encoding order (see below),
 * which is why color longs in the RGB model are called RGBA colors (even if
 * this doesn't quite hold for the special case of sRGB colors).
 *
 * The color encoding relies on half-precision float values (fp16). If you
 * wish to know more about the limitations of half-precision float values, please
 * refer to the documentation of the [Float16] class.
 *
 * The values returned by these methods depend on the color space encoded
 * in the color long. The values are however typically in the `[0..1]` range
 * for RGB colors. Please refer to the documentation of the various
 * [color spaces][ColorSpaces] for the exact ranges.
 */
@Immutable
class Color @PublishedApi internal constructor(
    val value: Long,
    @Suppress("UNUSED_PARAMETER") unused: Boolean
) {
    /**
     * Returns this color's color space.
     *
     * @return A non-null instance of [ColorSpace]
     */
    val colorSpace: ColorSpace
        get() = ColorSpaces.getColorSpace((value and 0x3fL).toInt())

    /**
     * Converts this color from its color space to the specified color space.
     * The conversion is done using the default rendering intent as specified
     * by [ColorSpace.connect].
     *
     * @param colorSpace The destination color space, cannot be null
     *
     * @return A non-null color instance in the specified color space
     */
    fun convert(colorSpace: ColorSpace): Color {
        if (colorSpace == this.colorSpace) {
            return this // nothing to convert
        }
        val connector = this.colorSpace.connect(colorSpace)
        val color = getComponents()
        connector.transform(color)
        return Color(
            red = color[0],
            green = color[1],
            blue = color[2],
            alpha = color[3],
            colorSpace = colorSpace
        )
    }

    /**
     * Returns the value of the red component in the range defined by this
     * color's color space (see [ColorSpace.getMinValue] and
     * [ColorSpace.getMaxValue]).
     *
     * If this color's color model is not [RGB][ColorModel.Rgb],
     * calling this is the first component of the ColorSpace.
     *
     * @see alpha
     * @see blue
     * @see green
     */
    val red: Float
        get() {
            return if ((value and 0x3fL) == 0L) {
                ((value shr 48) and 0xffL).toFloat() / 255.0f
            } else {
                Float16(((value shr 48) and 0xffffL).toShort()).toFloat()
            }
        }

    /**
     * Returns the value of the green component in the range defined by this
     * color's color space (see [ColorSpace.getMinValue] and
     * [ColorSpace.getMaxValue]).
     *
     * If this color's color model is not [RGB][ColorModel.Rgb],
     * calling this is the second component of the ColorSpace.
     *
     * @see alpha
     * @see red
     * @see blue
     */
    val green: Float
        get() {
            return if ((value and 0x3fL) == 0L) {
                ((value shr 40) and 0xffL).toFloat() / 255.0f
            } else {
                Float16(((value shr 32) and 0xffffL).toShort()).toFloat()
            }
        }

    /**
     * Returns the value of the blue component in the range defined by this
     * color's color space (see [ColorSpace.getMinValue] and
     * [ColorSpace.getMaxValue]).
     *
     * If this color's color model is not [RGB][ColorModel.Rgb],
     * calling this is the third component of the ColorSpace.
     *
     * @see alpha
     * @see red
     * @see green
     */
    val blue: Float
        get() {
            return if ((value and 0x3fL) == 0L) {
                ((value shr 32) and 0xffL).toFloat() / 255.0f
            } else {
                Float16(((value shr 16) and 0xffffL).toShort()).toFloat()
            }
        }

    /**
     * Returns the value of the alpha component in the range `[0..1]`.
     *
     * @see red
     * @see green
     * @see blue
     */
    val alpha: Float
        get() {
            return if ((value and 0x3fL) == 0L) {
                ((value shr 56) and 0xffL).toFloat() / 255.0f
            } else {
                ((value shr 6) and 0x3ffL).toFloat() / 1023.0f
            }
        }

    /**
     * Copies the existing color, changing only the provided values. The [ColorSpace][colorSpace]
     * of the returned [Color] is the same as this [colorSpace].
     */
    fun copy(
        alpha: Float = this.alpha,
        red: Float = this.red,
        green: Float = this.green,
        blue: Float = this.blue
    ): Color = Color(
        red = red,
        green = green,
        blue = blue,
        alpha = alpha,
        colorSpace = this.colorSpace
    )

    override fun equals(other: Any?): Boolean {
        if (other !is Color) {
            return false
        }
        return other.value == value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    /**
     * Returns a string representation of the object. This method returns
     * a string equal to the value of:
     *
     *     "Color($r, $g, $b, $a, ${colorSpace.name})"
     *
     * For instance, the string representation of opaque black in the sRGB
     * color space is equal to the following value:
     *
     *     Color(0.0, 0.0, 0.0, 1.0, sRGB IEC61966-2.1)
     *
     * @return A non-null string representation of the object
     */
    override fun toString(): String {
        return "Color($red, $green, $blue, $alpha, ${colorSpace.name})"
    }

    companion object {
        val Black = Color(0xFF000000)
        val DarkGray = Color(0xFF444444)
        val Gray = Color(0xFF888888)
        val LightGray = Color(0xFFCCCCCC)
        val White = Color(0xFFFFFFFF)
        val Red = Color(0xFFFF0000)
        val Green = Color(0xFF00FF00)
        val Blue = Color(0xFF0000FF)
        val Yellow = Color(0xFFFFFF00)
        val Cyan = Color(0xFF00FFFF)
        val Magenta = Color(0xFFFF00FF)
        val Transparent = Color(0x00000000)
    }
}

/**
 * Create a [Color] by passing individual [red], [green], [blue], [alpha], and [colorSpace]
 * components. The default [color space][ColorSpace] is [SRGB][ColorSpaces.Srgb] and
 * the default [alpha] is `1.0` (opaque). [colorSpace] must have a [ColorSpace.componentCount] of
 * 3.
 */
fun Color(
    red: Float,
    green: Float,
    blue: Float,
    alpha: Float = 1f,
    colorSpace: ColorSpace = ColorSpaces.Srgb
): Color {
    require(colorSpace.componentCount == 3) {
        "Color only works with ColorSpaces with 3 components"
    }
    if (colorSpace.isSrgb) {
        val argb = (((alpha * 255.0f + 0.5f).toInt() shl 24) or
                ((red * 255.0f + 0.5f).toInt() shl 16) or
                ((green * 255.0f + 0.5f).toInt() shl 8) or
                (blue * 255.0f + 0.5f).toInt())
        return Color(value = (argb.toLong() and 0xffffffffL) shl 32, unused = false)
    }

    val id = colorSpace.id
    // TODO(mount): restore MinId when const vals work in IR module
    require(id != -1 /*ColorSpace.MinId*/) {
        "Unknown color space, please use a color space in ColorSpaces"
    }

    val r = Float16(red)
    val g = Float16(green)
    val b = Float16(blue)

    val a = (max(0.0f, min(alpha, 1.0f)) * 1023.0f + 0.5f).toInt()

    // Suppress sign extension
    return Color(value = (((r.halfValue.toLong() and 0xffffL) shl 48) or (
            (g.halfValue.toLong() and 0xffffL) shl 32) or (
            (b.halfValue.toLong() and 0xffffL) shl 16) or (
            (a.toLong() and 0x3ffL) shl 6) or (
            id.toLong() and 0x3fL)),
        unused = false)
}

/**
 * Creates a new [Color] instance from an ARGB color int.
 * The resulting color is in the [sRGB][ColorSpaces.Srgb]
 * color space.
 *
 * @param color The ARGB color int to create a <code>Color</code> from.
 * @return A non-null instance of {@link Color}
 */
fun Color(@ColorInt color: Int): Color {
    return Color(value = color.toLong() shl 32, unused = false)
}

/**
 * Creates a new [Color] instance from an ARGB color int.
 * The resulting color is in the [sRGB][ColorSpaces.Srgb]
 * color space. This is useful for specifying colors with alpha
 * greater than 0x80 in numeric form without using [Long.toInt]:
 *
 *     val color = Color(0xFF000080)
 *
 * @param color The 32-bit ARGB color int to create a <code>Color</code>
 * from
 * @return A non-null instance of {@link Color}
 */
fun Color(color: Long): Color {
    return Color(value = (color and 0xffffffff) shl 32, unused = false)
}

/**
 * Creates a new [Color] instance from an ARGB color components.
 * The resulting color is in the [sRGB][ColorSpaces.Srgb]
 * color space. The default alpha value is `0xFF` (opaque).
 *
 * @return A non-null instance of {@link Color}
 */
fun Color(
    @IntRange(from = 0, to = 0xFF) red: Int,
    @IntRange(from = 0, to = 0xFF) green: Int,
    @IntRange(from = 0, to = 0xFF) blue: Int,
    @IntRange(from = 0, to = 0xFF) alpha: Int = 0xFF
): Color {
    @ColorInt val color = ((alpha and 0xFF) shl 24) or
            ((red and 0xFF) shl 16) or
            ((green and 0xFF) shl 8) or
            (blue and 0xFF)
    return Color(color)
}

/**
 * Linear interpolate between two [Colors][Color], [start] and [stop] with [fraction] fraction
 * between the two. The [ColorSpace] of the result is always the [ColorSpace][Color.colorSpace]
 * of [stop].
 */
fun lerp(start: Color, stop: Color, @FloatRange(from = 0.0, to = 1.0) fraction: Float): Color {
    val linearColorSpace = ColorSpaces.LinearExtendedSrgb
    val startColor = start.convert(linearColorSpace)
    val endColor = stop.convert(linearColorSpace)

    val startA = startColor.alpha
    val startR = startColor.red
    val startG = startColor.green
    val startB = startColor.blue

    val endA = endColor.alpha
    val endR = endColor.red
    val endG = endColor.green
    val endB = endColor.blue

    val interpolated = Color(
        alpha = lerp(startA, endA, fraction),
        red = lerp(startR, endR, fraction),
        green = lerp(startG, endG, fraction),
        blue = lerp(startB, endB, fraction),
        colorSpace = linearColorSpace
    )
    return interpolated.convert(stop.colorSpace)
}

/**
 * Returns this color's components as a new array. The last element of the
 * array is always the alpha component.
 *
 * @return A new, non-null array whose size is 4
 */
@Size(value = 4)
private fun Color.getComponents(): FloatArray = floatArrayOf(red, green, blue, alpha)

/**
 * Returns the relative luminance of this color.
 *
 * Based on the formula for relative luminance defined in WCAG 2.0,
 * W3C Recommendation 11 December 2008.
 *
 * @return A value between 0 (darkest black) and 1 (lightest white)
 *
 * @throws IllegalArgumentException If the this color's color space
 * does not use the [RGB][ColorModel.Rgb] color model
 */
fun Color.luminance(): Float {
    val colorSpace = colorSpace
    require(colorSpace.model === ColorModel.Rgb) {
        "The specified color must be encoded in an RGB color space. " +
                "The supplied color space is ${colorSpace.model}"
    }

    val eotf = (colorSpace as Rgb).eotf
    val r = eotf(red.toDouble())
    val g = eotf(green.toDouble())
    val b = eotf(blue.toDouble())

    return saturate(((0.2126 * r) + (0.7152 * g) + (0.0722 * b)).toFloat())
}

private fun saturate(v: Float): Float {
    return if (v <= 0.0f) 0.0f else (if (v >= 1.0f) 1.0f else v)
}

/**
 * Converts this color to an ARGB color int. A color int is always in
 * the [sRGB][ColorSpaces.Srgb] color space. This implies
 * a color space conversion is applied if needed.
 *
 * @return An ARGB color in the sRGB color space
 */
@ColorInt
fun Color.toArgb(): Int {
    val colorSpace = colorSpace
    if (colorSpace.isSrgb) {
        return (this.value shr 32).toInt()
    }

    val color = getComponents()
    // The transformation saturates the output
    colorSpace.connect().transform(color)

    return (color[3] * 255.0f + 0.5f).toInt() shl 24 or
            ((color[0] * 255.0f + 0.5f).toInt() shl 16) or
            ((color[1] * 255.0f + 0.5f).toInt() shl 8) or
            (color[2] * 255.0f + 0.5f).toInt()
}