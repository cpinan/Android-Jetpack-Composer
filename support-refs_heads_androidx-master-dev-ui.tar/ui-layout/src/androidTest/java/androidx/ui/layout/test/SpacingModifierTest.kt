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

package androidx.ui.layout.test

import androidx.test.filters.SmallTest
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.OnPositioned
import androidx.ui.core.PxPosition
import androidx.ui.core.PxSize
import androidx.ui.core.dp
import androidx.ui.core.ipx
import androidx.ui.core.px
import androidx.ui.core.toPx
import androidx.ui.core.withDensity
import androidx.ui.layout.Center
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.compose.Composable
import androidx.ui.core.Layout
import androidx.ui.core.Modifier
import androidx.ui.core.min
import androidx.ui.layout.AspectRatio
import androidx.ui.layout.Spacing
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SmallTest
@RunWith(JUnit4::class)
class SpacingModifierTest : LayoutTest() {

    /**
     * Tests that the [Spacing]-all and [Spacing] factories return equivalent modifiers.
     */
    @Test
    fun allEqualToAbsoluteWithExplicitSides() {
        Assert.assertEquals(
            Spacing(10.dp, 10.dp, 10.dp, 10.dp),
            Spacing(10.dp)
        )
    }

    /**
     * Tests the top-level [Spacing] modifier factory with a single "all sides" argument,
     * checking that a uniform spacing of all sides is applied to a child when plenty of space is
     * available for both content and spacing.
     */
    @Test
    fun spacingAllAppliedToChild() = withDensity(density) {
        val spacing = 10.dp
        testSpacingIsAppliedImplementation(spacing) { child: @Composable() () -> Unit ->
            TestBox(modifier = Spacing(spacing)) {
                child()
            }
        }
    }

    /**
     * Tests the top-level [Spacing] modifier factory with different values for left, top,
     * right and bottom spacings, checking that this spacing is applied as expected when plenty of
     * space is available for both the content and spacing.
     */
    @Test
    fun absoluteSpacingAppliedToChild() {
        val spacingLeft = 10.dp
        val spacingTop = 15.dp
        val spacingRight = 20.dp
        val spacingBottom = 30.dp
        val spacing = Spacing(spacingLeft, spacingTop, spacingRight, spacingBottom)
        testSpacingWithDifferentInsetsImplementation(
            spacingLeft,
            spacingTop,
            spacingRight,
            spacingBottom
        ) { child: @Composable() () -> Unit ->
            TestBox(modifier = spacing) {
                child()
            }
        }
    }

    /**
     * Tests the result of the [Spacing] modifier factory when not enough space is available to
     * accommodate both the spacing and the content. In this case, the spacing should still be
     * applied, modifying the final position of the content by its left and top spacings even if it
     * would result in constraints that the child content is unable or unwilling to satisfy.
     */
    @Test
    fun insufficientSpaceAvailable() = withDensity(density) {
        val spacing = 30.dp
        testSpacingWithInsufficientSpaceImplementation(spacing) { child: @Composable() () -> Unit ->
            TestBox(modifier = Spacing(spacing)) {
                child()
            }
        }
    }

    @Test
    fun intrinsicMeasurements() = withDensity(density) {
        val spacing = 100.ipx.toDp()

        val latch = CountDownLatch(1)
        var error: Throwable? = null
        testIntrinsics(@Composable {
            TestBox(modifier = Spacing(spacing)) {
                Container(AspectRatio(2f)) { }
            }
        }) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Spacing is applied on both sides of an axis
            val totalAxisSpacing = (spacing * 2).toIntPx()

            // When the width/height is measured as 3 x the spacing
            val testDimension = (spacing * 3).toIntPx()
            // The actual dimension for the AspectRatio will be: test dimension - total spacing
            val actualAspectRatioDimension = testDimension - totalAxisSpacing

            // When we measure the width first, the height will be half
            val expectedAspectRatioHeight = actualAspectRatioDimension / 2f
            // When we measure the height first, the width will be double
            val expectedAspectRatioWidth = actualAspectRatioDimension * 2

            // Add back the spacing on both sides to get the total expected height
            val expectedTotalHeight = expectedAspectRatioHeight + totalAxisSpacing
            // Add back the spacing on both sides to get the total expected height
            val expectedTotalWidth = expectedAspectRatioWidth + totalAxisSpacing

            try {
                // Min width.
                assertEquals(totalAxisSpacing, minIntrinsicWidth(0.dp.toIntPx()))
                assertEquals(expectedTotalWidth, minIntrinsicWidth(testDimension))
                assertEquals(totalAxisSpacing, minIntrinsicWidth(IntPx.Infinity))
                // Min height.
                assertEquals(totalAxisSpacing, minIntrinsicHeight(0.dp.toIntPx()))
                assertEquals(expectedTotalHeight, minIntrinsicHeight(testDimension))
                assertEquals(totalAxisSpacing, minIntrinsicHeight(IntPx.Infinity))
                // Max width.
                assertEquals(totalAxisSpacing, maxIntrinsicWidth(0.dp.toIntPx()))
                assertEquals(expectedTotalWidth, maxIntrinsicWidth(testDimension))
                assertEquals(totalAxisSpacing, maxIntrinsicWidth(IntPx.Infinity))
                // Max height.
                assertEquals(totalAxisSpacing, maxIntrinsicHeight(0.dp.toIntPx()))
                assertEquals(expectedTotalHeight, maxIntrinsicHeight(testDimension))
                assertEquals(totalAxisSpacing, maxIntrinsicHeight(IntPx.Infinity))
            } catch (t: Throwable) {
                error = t
            } finally {
                latch.countDown()
            }
        }

        latch.await(1, TimeUnit.SECONDS)
        error?.let { throw it }

        Unit
    }

    private fun testSpacingIsAppliedImplementation(
        spacing: Dp,
        spacingContainer: @Composable() (@Composable() () -> Unit) -> Unit
    ) = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()
        val spacingPx = spacing.toIntPx()

        val drawLatch = CountDownLatch(1)
        var childSize = PxSize(-1.px, -1.px)
        var childPosition = PxPosition(-1.px, -1.px)
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints.tightConstraints(sizeDp, sizeDp)) {
                    val children = @Composable {
                        Container {
                            OnPositioned(onPositioned = { coordinates ->
                                childSize = coordinates.size
                                childPosition =
                                    coordinates.localToGlobal(PxPosition(0.px, 0.px))
                                drawLatch.countDown()
                            })
                        }
                    }
                    spacingContainer(children)
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        val innerSize = (size - spacingPx * 2)
        assertEquals(PxSize(innerSize, innerSize), childSize)
        val left = ((root.width.ipx - size) / 2) + spacingPx
        val top = ((root.height.ipx - size) / 2) + spacingPx
        assertEquals(
            PxPosition(left.toPx(), top.toPx()),
            childPosition
        )
    }

    private fun testSpacingWithDifferentInsetsImplementation(
        left: Dp,
        top: Dp,
        right: Dp,
        bottom: Dp,
        spacingContainer: @Composable() ((@Composable() () -> Unit) -> Unit)
    ) = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(1)
        var childSize = PxSize(-1.px, -1.px)
        var childPosition = PxPosition(-1.px, -1.px)
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints.tightConstraints(sizeDp, sizeDp)) {
                    val children = @Composable {
                        Container {
                            OnPositioned(onPositioned = { coordinates ->
                                childSize = coordinates.size
                                childPosition =
                                    coordinates.localToGlobal(PxPosition(0.px, 0.px))
                                drawLatch.countDown()
                            })
                        }
                    }
                    spacingContainer(children)
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        val spacingLeft = left.toIntPx()
        val spacingRight = right.toIntPx()
        val spacingTop = top.toIntPx()
        val spacingBottom = bottom.toIntPx()
        assertEquals(
            PxSize(
                size - spacingLeft - spacingRight,
                size - spacingTop - spacingBottom
            ),
            childSize
        )
        val viewLeft = ((root.width.ipx - size) / 2) + spacingLeft
        val viewTop = ((root.height.ipx - size) / 2) + spacingTop
        assertEquals(
            PxPosition(viewLeft.toPx(), viewTop.toPx()),
            childPosition
        )
    }

    private fun testSpacingWithInsufficientSpaceImplementation(
        spacing: Dp,
        spacingContainer: @Composable() (@Composable() () -> Unit) -> Unit
    ) = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()
        val spacingPx = spacing.toIntPx()

        val drawLatch = CountDownLatch(1)
        var childSize = PxSize(-1.px, -1.px)
        var childPosition = PxPosition(-1.px, -1.px)
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints.tightConstraints(sizeDp, sizeDp)) {
                    spacingContainer {
                        Container {
                            OnPositioned(onPositioned = { coordinates ->
                                childSize = coordinates.size
                                childPosition = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                                drawLatch.countDown()
                            })
                        }
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxSize(0.px, 0.px), childSize)
        val left = ((root.width.ipx - size) / 2) + spacingPx
        val top = ((root.height.ipx - size) / 2) + spacingPx
        assertEquals(PxPosition(left.toPx(), top.toPx()), childPosition)
    }

    /**
     * A trivial layout that applies a [Modifier] and measures/lays out a single child
     * with the same constraints it received.
     */
    @Composable
    private fun TestBox(modifier: Modifier = Modifier.None, body: @Composable() () -> Unit) {
        Layout(children = body, modifier = modifier) { measurables, constraints ->
            require(measurables.size == 1) {
                "TestBox received ${measurables.size} children; must have exactly 1"
            }
            val placeable = measurables.first().measure(constraints)
            layout(
                min(placeable.width, constraints.maxWidth),
                min(placeable.height, constraints.maxHeight)
            ) {
                placeable.place(IntPx.Zero, IntPx.Zero)
            }
        }
    }
}
