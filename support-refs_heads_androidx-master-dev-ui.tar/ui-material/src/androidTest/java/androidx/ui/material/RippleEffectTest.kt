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
package androidx.ui.material

import androidx.compose.Composable
import androidx.compose.Effect
import androidx.compose.Model
import androidx.compose.effectOf
import androidx.test.filters.MediumTest
import androidx.ui.core.Density
import androidx.ui.core.Dp
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.PxPosition
import androidx.ui.core.TestTag
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.layout.Wrap
import androidx.ui.material.ripple.CurrentRippleTheme
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.ripple.RippleEffect
import androidx.ui.material.ripple.RippleEffectFactory
import androidx.ui.material.ripple.RippleTheme
import androidx.ui.material.surface.Card
import androidx.ui.test.createComposeRule
import androidx.ui.test.doClick
import androidx.ui.test.findByTag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@MediumTest
@RunWith(JUnit4::class)
class RippleEffectTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rippleEffectMatrixHasOffsetFromSurface() {
        val latch = CountDownLatch(1)

        val padding = 10.dp
        composeTestRule.setMaterialContent {
            RippleCallback(onRippleDrawn = {
                latch.countDown()
            }) {
                Card {
                    Padding(padding = padding) {
                        TestTag(tag = "ripple") {
                            RippleButton()
                        }
                    }
                }
            }
        }

        findByTag("ripple")
            .doClick()

        // wait for drawEffect to be called
        assertTrue(latch.await(1, TimeUnit.SECONDS))

        // TODO(Andrey): Move waitAndScreenShot() method for drawn pixel assertions from
        // ui-foundation to ui-test to be able to use it here and reimplement this test
        // by asserting the drawing is happening in the correct position by checking
        // pixels on the result bitmap

//        // verify matrix contains the expected padding
//        assertNotNull(matrix)
//        val paddingFloat = withDensity(composeTestRule.density) {
//            padding.toIntPx().value.toFloat()
//        }
//        val expectedMatrix = Matrix4.translationValues(
//            paddingFloat,
//            paddingFloat,
//            0f
//        )
//        assertEquals(expectedMatrix, matrix)
    }

    @Test
    fun rippleEffectMatrixHasTheClickedChildCoordinates() {
        val latch = CountDownLatch(1)

        val size = 10.dp
        composeTestRule.setMaterialContent {
            RippleCallback(onRippleDrawn = {
                latch.countDown()
            }) {
                Card {
                    Wrap {
                        Row {
                            RippleButton(size)
                            TestTag(tag = "ripple") {
                                RippleButton(size)
                            }
                            RippleButton(size)
                        }
                    }
                }
            }
        }

        findByTag("ripple")
            .doClick()

        // wait for drawEffect to be called
        assertTrue(latch.await(1000, TimeUnit.SECONDS))

        // TODO(Andrey): Move waitAndScreenShot() method for drawn pixel assertions from
        // ui-foundation to ui-test to be able to use it here and reimplement this test
        // by asserting the drawing is happening in the correct position by checking
        // pixels on the result bitmap

//        // verify matrix contains the expected padding
//        assertNotNull(matrix)
//        val offsetFloat = withDensity(composeTestRule.density) { size.toIntPx().value.toFloat() }
//        val expectedMatrix = Matrix4.translationValues(
//            offsetFloat,
//            0f,
//            0f
//        )
//        assertEquals(expectedMatrix, matrix)
    }

    @Test
    fun twoEffectsDrawnAndDisposedCorrectly() {
        val drawLatch = CountDownLatch(2)
        val disposeLatch = CountDownLatch(2)
        val emit = DoEmit(true)

        composeTestRule.setMaterialContent {
            RippleCallback(
                onRippleDrawn = { drawLatch.countDown() },
                onDispose = { disposeLatch.countDown() }
            ) {
                Card {
                    if (emit.emit) {
                        Row {
                            TestTag(tag = "ripple") {
                                RippleButton(10.dp)
                            }
                        }
                    }
                }
            }
        }

        // create two effects
        findByTag("ripple")
            .doClick()
        findByTag("ripple")
            .doClick()

        // wait for drawEffect to be called
        assertTrue(drawLatch.await(1, TimeUnit.SECONDS))

        composeTestRule.runOnUiThread { emit.emit = false }

        // wait for dispose to be called
        assertTrue(disposeLatch.await(1, TimeUnit.SECONDS))
    }

    @Test
    fun rippleColorAndOpacityAreTakenFromTheme() {
        val drawLatch = CountDownLatch(1)
        val color = Color.Yellow
        val opacity = 0.2f
        composeTestRule.setMaterialContent {
            RippleCallback(
                defaultColor = effectOf { color },
                opacityCallback = effectOf { opacity },
                onRippleDrawn = { actualColor ->
                    assertEquals(color.copy(alpha = opacity), actualColor)
                    drawLatch.countDown()
                }
            ) {
                TestTag(tag = "ripple") {
                    RippleButton(10.dp)
                }
            }
        }

        findByTag("ripple")
            .doClick()

        // wait for drawEffect to be called
        assertTrue(drawLatch.await(1, TimeUnit.SECONDS))
    }

    @Test
    fun rippleOpacityIsTakenFromTheme() {
        val drawLatch = CountDownLatch(1)
        val color = Color.Green
        val opacity = 0.2f
        composeTestRule.setMaterialContent {
            RippleCallback(
                opacityCallback = effectOf { opacity },
                onRippleDrawn = { actualColor ->
                    assertEquals(color.copy(alpha = opacity), actualColor)
                    drawLatch.countDown()
                }
            ) {
                TestTag(tag = "ripple") {
                    RippleButton(10.dp, color)
                }
            }
        }

        findByTag("ripple")
            .doClick()

        // wait for drawEffect to be called
        assertTrue(drawLatch.await(1, TimeUnit.SECONDS))
    }

    @Test
    fun disabledRippleDoesntCreateEffects() {
        val createdLatch = CountDownLatch(1)

        composeTestRule.setMaterialContent {
            RippleCallback(
                onEffectCreated = { createdLatch.countDown() }
            ) {
                Card {
                    TestTag(tag = "ripple") {
                        RippleButton(enabled = false)
                    }
                }
            }
        }

        // create two effects
        findByTag("ripple")
            .doClick()

        // assert no effects has been created
        assertFalse(createdLatch.await(500, TimeUnit.MILLISECONDS))
    }

    private fun RippleButton(size: Dp? = null, color: Color? = null, enabled: Boolean = true) {
        Ripple(bounded = false, color = color, enabled = enabled) {
            Clickable(onClick = {}) {
                Container(width = size, height = size) {}
            }
        }
    }

    @Composable
    private fun RippleCallback(
        onRippleDrawn: (Color) -> Unit = {},
        onDispose: () -> Unit = {},
        onEffectCreated: () -> Unit = {},
        defaultColor: Effect<Color> = effectOf { Color(0) },
        opacityCallback: Effect<Float> = effectOf { 1f },
        children: @Composable() () -> Unit
    ) {
        val theme = RippleTheme(
            testRippleEffect(onRippleDrawn, onDispose, onEffectCreated),
            defaultColor,
            opacityCallback
        )
        CurrentRippleTheme.Provider(value = theme, children = children)
    }

    private fun testRippleEffect(
        onDraw: (Color) -> Unit,
        onDispose: () -> Unit,
        onEffectCreated: () -> Unit
    ): RippleEffectFactory =
        object : RippleEffectFactory {
            override fun create(
                coordinates: LayoutCoordinates,
                startPosition: PxPosition,
                density: Density,
                radius: Dp?,
                clipped: Boolean,
                requestRedraw: () -> Unit,
                onAnimationFinished: (RippleEffect) -> Unit
            ): RippleEffect {
                onEffectCreated()
                return object : RippleEffect {

                    private var onDrawCalled: Boolean = false

                    override fun draw(canvas: Canvas, color: Color) {
                        if (!onDrawCalled) {
                            onDraw(color)
                            onDrawCalled = true
                        }
                    }

                    override fun finish(canceled: Boolean) {
                    }

                    override fun dispose() {
                        onDispose()
                    }
                }
            }
        }
}

@Model
private data class DoEmit(var emit: Boolean)