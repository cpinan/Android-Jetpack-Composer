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

package androidx.ui.painting

import androidx.test.filters.SmallTest
import androidx.ui.engine.geometry.Rect
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.graphics.Paint
import androidx.ui.graphics.Path
import androidx.ui.graphics.toArgb
import androidx.ui.vectormath64.PI
import androidx.ui.vectormath64.radians
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@SmallTest
@RunWith(JUnit4::class)
class PathTest {

    @Test
    fun testAddArcPath() {
        val width = 100
        val height = 100
        val image = Image(width, height)
        val canvas = Canvas(image)
        val path1 = Path().apply {
            addArcRad(
                Rect.fromLTWH(0.0f, 0.0f, width.toFloat(), height.toFloat()),
                0.0f,
                PI / 2
            )
        }

        val arcColor = Color.Cyan
        val arcPaint = Paint().apply { color = arcColor }
        canvas.drawPath(path1, arcPaint)

        val path2 = Path().apply {
            arcToRad(
                Rect.fromLTWH(0.0f, 0.0f, width.toFloat(), height.toFloat()),
                PI,
                PI / 2,
                false
            )
            close()
        }

        canvas.drawPath(path2, arcPaint)

        val x = (50.0 * Math.cos(radians(45.0f).toDouble())).toInt()
        assertEquals(arcColor.toArgb(),
            image.nativeImage.getPixel(
                width / 2 + x - 1,
                height / 2 + x - 1
            )
        )

        assertEquals(arcColor.toArgb(),
            image.nativeImage.getPixel(
                width / 2 - x,
                height / 2 - x
            )
        )
    }
}