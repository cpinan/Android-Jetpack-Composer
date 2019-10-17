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

package androidx.ui.core.test

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.Composable
import androidx.compose.Model
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule
import androidx.ui.core.Density
import androidx.ui.core.Dp
import androidx.ui.core.Draw
import androidx.ui.core.DrawShadow
import androidx.ui.core.Opacity
import androidx.ui.core.PxSize
import androidx.ui.core.RepaintBoundary
import androidx.ui.core.dp
import androidx.ui.core.ipx
import androidx.ui.core.setContent
import androidx.ui.core.toRect
import androidx.ui.engine.geometry.Outline
import androidx.ui.engine.geometry.Shape
import androidx.ui.framework.test.TestActivity
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SmallTest
@RunWith(JUnit4::class)
class DrawShadowTest {

    @get:Rule
    val rule = ActivityTestRule<TestActivity>(TestActivity::class.java)
    private lateinit var activity: TestActivity
    private lateinit var drawLatch: CountDownLatch

    // TODO move RectangleShape to ui-framework b/137222372
    private val rectShape = object : Shape {
        override fun createOutline(size: PxSize, density: Density): Outline =
            Outline.Rectangle(size.toRect())
    }

    @Before
    fun setup() {
        activity = rule.activity
        activity.hasFocusLatch.await(5, TimeUnit.SECONDS)
        drawLatch = CountDownLatch(1)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun shadowDrawn() {
        rule.runOnUiThreadIR {
            activity.setContent {
                ShadowContainer()
            }
        }

        assertTrue(drawLatch.await(1, TimeUnit.SECONDS))
        takeScreenShot(12).apply {
            hasShadow()
        }
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun shadowDrawnInsideRenderNode() {
        rule.runOnUiThreadIR {
            activity.setContent {
                RepaintBoundary {
                    ShadowContainer()
                }
            }
        }

        assertTrue(drawLatch.await(1, TimeUnit.SECONDS))
        takeScreenShot(12).apply {
            hasShadow()
        }
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun switchFromShadowToNoShadow() {
        val model = ShadowModel(12.dp)

        rule.runOnUiThreadIR {
            activity.setContent {
                ShadowContainer(model.elevation)
            }
        }
        assertTrue(drawLatch.await(1, TimeUnit.SECONDS))

        rule.runOnUiThreadIR {
            model.elevation = 0.dp
        }

        takeScreenShot(12).apply {
            assertEquals(color(5, 11), Color.White)
        }
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun opacityAppliedForTheShadow() {
        rule.runOnUiThreadIR {
            activity.setContent {
                AtLeastSize(size = 12.ipx) {
                    FillColor(Color.White)
                    Opacity(0.1f) {
                        AtLeastSize(size = 10.ipx) {
                            DrawShadow(rectShape, 4.dp)
                        }
                    }
                }
            }
        }

        assertTrue(drawLatch.await(1, TimeUnit.SECONDS))
        takeScreenShot(12).apply {
            val shadowColor = color(width / 2, height - 1)
            // assert the shadow is still visible
            assertNotEquals(shadowColor, Color.White)
            // but the shadow is not as dark as it would be without opacity.
            // with full opacity it is around 0.85, with 10% opacity it is 0.98
            assertTrue(shadowColor.luminance() > 0.95f)
        }
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun emitShadowLater() {
        val model = DoDraw(false)

        rule.runOnUiThreadIR {
            activity.setContent {
                AtLeastSize(size = 12.ipx) {
                    FillColor(Color.White)
                    AtLeastSize(size = 10.ipx) {
                        if (model.value) {
                            DrawShadow(rectShape, 8.dp)
                        }
                    }
                }
            }
        }
        assertTrue(drawLatch.await(1, TimeUnit.SECONDS))

        drawLatch = CountDownLatch(1)
        rule.runOnUiThreadIR {
            model.value = true
        }

        takeScreenShot(12).apply {
            hasShadow()
        }
    }

    @Composable
    private fun ShadowContainer(elevation: Dp = 8.dp) {
        AtLeastSize(size = 12.ipx) {
            FillColor(Color.White)
            AtLeastSize(size = 10.ipx) {
                DrawShadow(rectShape, elevation)
            }
        }
    }

    private fun Bitmap.hasShadow() {
        assertNotEquals(color(width / 2, height - 1), Color.White)
    }

    @Composable
    private fun FillColor(color: Color) {
        Draw { canvas, parentSize ->
            canvas.drawRect(parentSize.toRect(), Paint().apply {
                this.color = color
            })
            drawLatch.countDown()
        }
    }

    private fun takeScreenShot(width: Int, height: Int = width): Bitmap {
        val bitmap = rule.waitAndScreenShot()
        assertEquals(width, bitmap.width)
        assertEquals(height, bitmap.height)
        return bitmap
    }
}

@Model
private data class ShadowModel(var elevation: Dp)
