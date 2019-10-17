/*
 * Copyright (C) 2019 The Android Open Source Project
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
import androidx.test.filters.SmallTest
import androidx.ui.graphics.colorspace.ColorSpaces
import androidx.ui.lerp
import androidx.ui.toHexString
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@SmallTest
@RunWith(JUnit4::class)
class ColorTest {
    private val adobeColorSpace = ColorSpaces.AdobeRgb
    private val srgbColor = Color(0xFFFF8000)
    private val adobeColor = Color(red = 0.8916f, green = 0.4980f, blue = 0.1168f,
            colorSpace = ColorSpaces.AdobeRgb)
    private val epsilon = 0.0001f // Float16 squished into ColorLong isn't very accurate.

    @Test
    fun colorSpace() {
        assertEquals(ColorSpaces.Srgb, srgbColor.colorSpace)
        assertEquals(ColorSpaces.AdobeRgb, adobeColor.colorSpace)
    }

    @Test
    fun convert() {
        val targetColor = srgbColor.convert(adobeColorSpace)

        assertEquals(adobeColor.colorSpace, targetColor.colorSpace)
        assertEquals(adobeColor.red, targetColor.red, epsilon)
        assertEquals(adobeColor.green, targetColor.green, epsilon)
        assertEquals(adobeColor.blue, targetColor.blue, epsilon)
        assertEquals(adobeColor.alpha, targetColor.alpha, epsilon)
    }

    @Test
    fun toArgb_fromSrgb() {
        assertEquals(0xFFFF8000.toInt(), srgbColor.toArgb())
    }

    @Test
    fun toArgb_fromAdobeRgb() {
        assertEquals(0xFFFF8000.toInt(), adobeColor.toArgb())
    }

    @Test
    fun red() {
        assertEquals(1f, srgbColor.red, 0f)
        assertEquals(0.8916f, adobeColor.red, epsilon)
    }

    @Test
    fun green() {
        assertEquals(0.5019608f, srgbColor.green, epsilon)
        assertEquals(0.4980f, adobeColor.green, epsilon)
    }

    @Test
    fun blue() {
        assertEquals(0f, srgbColor.blue, 0f)
        assertEquals(0.1168f, adobeColor.blue, epsilon)
    }

    @Test
    fun alpha() {
        assertEquals(1f, srgbColor.alpha, 0f)
        assertEquals(1f, adobeColor.alpha, 0f)
    }

    @Test
    fun luminance() {
        assertEquals(0f, Color.Black.luminance(), 0f)
        assertEquals(0.0722f, Color.Blue.luminance(), epsilon)
        assertEquals(0.2126f, Color.Red.luminance(), epsilon)
        assertEquals(0.7152f, Color.Green.luminance(), epsilon)
        assertEquals(1f, Color.White.luminance(), 0f)
    }

    @Test
    fun testToString() {
        assertEquals("Color(1.0, 0.5019608, 0.0, 1.0, sRGB IEC61966-2.1)", srgbColor.toString())
    }

    @Test
    fun lerp() {
        val red = Color.Red
        val green = Color.Green

        val redLinear = red.convert(ColorSpaces.LinearExtendedSrgb)
        val greenLinear = green.convert(ColorSpaces.LinearExtendedSrgb)

        for (i in 0..255) {
            val t = i / 255f
            val color = lerp(red, green, t)
            val expectedLinear = Color(
                red = lerp(redLinear.red, greenLinear.red, t),
                green = lerp(redLinear.green, greenLinear.green, t),
                blue = lerp(redLinear.blue, greenLinear.blue, t),
                colorSpace = ColorSpaces.LinearExtendedSrgb
            )
            val expected = expectedLinear.convert(ColorSpaces.Srgb)
            val colorARGB = Color(color.toArgb())
            val expectedARGB = Color(expected.toArgb())
            assertEquals("at t = $t[$i] was ${colorARGB.toArgb().toHexString()}, " +
                    "expecting ${expectedARGB.toArgb().toHexString()}", expectedARGB, colorARGB)
        }

        val transparentRed = Color.Red.copy(alpha = 0f)
        for (i in 0..255) {
            val t = i / 255f
            val color = lerp(red, transparentRed, t)
            val expected = Color.Red.copy(alpha = lerp(1f, 0f, t))
            val colorARGB = Color(color.toArgb())
            val expectedARGB = Color(expected.toArgb())
            assertEquals("at t = $t[$i] was ${colorARGB.toArgb().toHexString()}, " +
                    "expecting ${expectedARGB.toArgb().toHexString()}", expectedARGB, colorARGB)
        }
    }
}
