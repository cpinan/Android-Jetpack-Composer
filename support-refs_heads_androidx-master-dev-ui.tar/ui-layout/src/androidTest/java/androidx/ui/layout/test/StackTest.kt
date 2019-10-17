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
import androidx.ui.core.Constraints
import androidx.ui.core.OnChildPositioned
import androidx.ui.core.IntPx
import androidx.ui.core.PxPosition
import androidx.ui.core.PxSize
import androidx.ui.core.Ref
import androidx.ui.core.WithConstraints
import androidx.ui.core.dp
import androidx.ui.core.ipx
import androidx.ui.core.px
import androidx.ui.core.withDensity
import androidx.ui.layout.Align
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.Stack
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.layout.AspectRatio
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SmallTest
@RunWith(JUnit4::class)
class StackTest : LayoutTest() {
    @Test
    fun testStack() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val positionedLatch = CountDownLatch(3)
        val stackSize = Ref<PxSize>()
        val alignedChildSize = Ref<PxSize>()
        val alignedChildPosition = Ref<PxPosition>()
        val positionedChildSize = Ref<PxSize>()
        val positionedChildPosition = Ref<PxPosition>()
        show {
            Align(alignment = Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    stackSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Stack {
                        aligned(Alignment.BottomRight) {
                            Container(width = sizeDp, height = sizeDp) {
                                SaveLayoutInfo(
                                    size = alignedChildSize,
                                    position = alignedChildPosition,
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                        positioned(
                            leftInset = 10.dp,
                            topInset = 10.dp,
                            rightInset = 10.dp,
                            bottomInset = 10.dp
                        ) {
                            Container {
                                SaveLayoutInfo(
                                    size = positionedChildSize,
                                    position = positionedChildPosition,
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                    }
                }
            }
        }
        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(PxSize(size, size), stackSize.value)
        assertEquals(PxSize(size, size), alignedChildSize.value)
        assertEquals(PxPosition(0.px, 0.px), alignedChildPosition.value)
        assertEquals(PxSize(30.dp.toIntPx(), 30.dp.toIntPx()), positionedChildSize.value)
        assertEquals(PxPosition(10.dp.toIntPx(), 10.dp.toIntPx()), positionedChildPosition.value)
    }

    @Test
    fun testStack_withMultipleAlignedChildren() = withDensity(density) {
        val size = 250.ipx
        val sizeDp = size.toDp()
        val doubleSizeDp = sizeDp * 2
        val doubleSize = (sizeDp * 2).toIntPx()

        val positionedLatch = CountDownLatch(3)
        val stackSize = Ref<PxSize>()
        val childSize = arrayOf(Ref<PxSize>(), Ref<PxSize>())
        val childPosition = arrayOf(Ref<PxPosition>(), Ref<PxPosition>())
        show {
            Align(alignment = Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    stackSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Stack {
                        aligned(Alignment.BottomRight) {
                            Container(width = sizeDp, height = sizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[0],
                                    position = childPosition[0],
                                    positionedLatch = positionedLatch
                                )
                            }
                            Container(width = doubleSizeDp, height = doubleSizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[1],
                                    position = childPosition[1],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                    }
                }
            }
        }
        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(PxSize(doubleSize, doubleSize), stackSize.value)
        assertEquals(PxSize(size, size), childSize[0].value)
        assertEquals(PxPosition(size, size), childPosition[0].value)
        assertEquals(PxSize(doubleSize, doubleSize), childSize[1].value)
        assertEquals(PxPosition(0.px, 0.px), childPosition[1].value)
    }

    @Test
    fun testStack_withPositionedChildren() = withDensity(density) {
        val size = 250.ipx
        val sizeDp = size.toDp()
        val halfSizeDp = sizeDp / 2
        val halfSize = (sizeDp / 2).toIntPx()
        val inset = 50.ipx
        val insetDp = inset.toDp()

        val positionedLatch = CountDownLatch(8)
        val stackSize = Ref<PxSize>()
        val childSize = Array(7) { Ref<PxSize>() }
        val childPosition = Array(7) { Ref<PxPosition>() }
        show {
            Align(alignment = Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    stackSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Stack {
                        aligned(Alignment.Center) {
                            Container(width = sizeDp, height = sizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[0],
                                    position = childPosition[0],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                        positioned(leftInset = insetDp, topInset = insetDp) {
                            Container(width = halfSizeDp, height = halfSizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[1],
                                    position = childPosition[1],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                        positioned(rightInset = insetDp, bottomInset = insetDp) {
                            Container(width = halfSizeDp, height = halfSizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[2],
                                    position = childPosition[2],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                        positioned(leftInset = insetDp, fallbackAlignment = Alignment.BottomRight) {
                            Container(width = halfSizeDp, height = halfSizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[3],
                                    position = childPosition[3],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                        positioned(topInset = insetDp, fallbackAlignment = Alignment.BottomRight) {
                            Container(width = halfSizeDp, height = halfSizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[4],
                                    position = childPosition[4],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                        positioned(
                            rightInset = insetDp,
                            fallbackAlignment = Alignment.BottomRight
                        ) {
                            Container(width = halfSizeDp, height = halfSizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[5],
                                    position = childPosition[5],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                        positioned(
                            bottomInset = insetDp,
                            fallbackAlignment = Alignment.BottomRight
                        ) {
                            Container(width = halfSizeDp, height = halfSizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[6],
                                    position = childPosition[6],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                    }
                }
            }
        }
        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(PxSize(size, size), stackSize.value)
        assertEquals(PxSize(size, size), childSize[0].value)
        assertEquals(PxPosition(0.px, 0.px), childPosition[0].value)
        assertEquals(PxSize(halfSize, halfSize), childSize[1].value)
        assertEquals(PxPosition(inset, inset), childPosition[1].value)
        assertEquals(PxSize(halfSize, halfSize), childSize[2].value)
        assertEquals(
            PxPosition(size - inset - halfSize, size - inset - halfSize),
            childPosition[2].value
        )
        assertEquals(PxSize(halfSize, halfSize), childSize[3].value)
        assertEquals(PxPosition(inset, halfSize), childPosition[3].value)
        assertEquals(PxSize(halfSize, halfSize), childSize[4].value)
        assertEquals(PxPosition(halfSize, inset), childPosition[4].value)
        assertEquals(PxSize(halfSize, halfSize), childSize[5].value)
        assertEquals(PxPosition(size - inset - halfSize, halfSize), childPosition[5].value)
        assertEquals(PxSize(halfSize, halfSize), childSize[6].value)
        assertEquals(PxPosition(halfSize, size - inset - halfSize), childPosition[6].value)
    }

    @Test
    fun testStack_withPositionedChildren_fallbackAlignment() = withDensity(density) {
        val size = 250.ipx
        val sizeDp = size.toDp()
        val halfSizeDp = sizeDp / 2
        val halfSize = (sizeDp / 2).toIntPx()
        val inset = 50.ipx
        val insetDp = inset.toDp()

        val positionedLatch = CountDownLatch(6)
        val stackSize = Ref<PxSize>()
        val childSize = Array(5) { Ref<PxSize>() }
        val childPosition = Array(5) { Ref<PxPosition>() }
        show {
            Align(alignment = Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    stackSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Stack {
                        aligned(Alignment.Center) {
                            Container(width = sizeDp, height = sizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[0],
                                    position = childPosition[0],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                        positioned(leftInset = insetDp) {
                            Container(width = halfSizeDp, height = halfSizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[1],
                                    position = childPosition[1],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                        positioned(leftInset = insetDp, fallbackAlignment = Alignment.Center) {
                            Container(width = halfSizeDp, height = halfSizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[2],
                                    position = childPosition[2],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                        positioned(leftInset = insetDp, fallbackAlignment = Alignment.TopLeft) {
                            Container(width = halfSizeDp, height = halfSizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[3],
                                    position = childPosition[3],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                        positioned(leftInset = insetDp, fallbackAlignment = Alignment.BottomRight) {
                            Container(width = halfSizeDp, height = halfSizeDp) {
                                SaveLayoutInfo(
                                    size = childSize[4],
                                    position = childPosition[4],
                                    positionedLatch = positionedLatch
                                )
                            }
                        }
                    }
                }
            }
        }
        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(PxSize(size, size), stackSize.value)
        assertEquals(PxSize(size, size), childSize[0].value)
        assertEquals(PxPosition(0.px, 0.px), childPosition[0].value)
        assertEquals(PxSize(halfSize, halfSize), childSize[1].value)
        assertEquals(PxPosition(inset, (size - halfSize) / 2), childPosition[1].value)
        assertEquals(PxSize(halfSize, halfSize), childSize[2].value)
        assertEquals(PxPosition(inset, (size - halfSize) / 2), childPosition[2].value)
        assertEquals(PxSize(halfSize, halfSize), childSize[3].value)
        assertEquals(PxPosition(inset, 0.ipx), childPosition[3].value)
        assertEquals(PxSize(halfSize, halfSize), childSize[4].value)
        assertEquals(PxPosition(inset, halfSize), childPosition[4].value)
    }

    @Test
    fun testStack_fit() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()
        val halfSizeDp = sizeDp / 2
        val halfSize = (sizeDp / 2).toIntPx()

        val constraintsLatch = CountDownLatch(3)
        val childConstraints = Array(3) { Constraints() }
        show {
            Align(alignment = Alignment.TopLeft) {
                ConstrainedBox(
                    constraints = DpConstraints(
                        minWidth = halfSizeDp, maxWidth = sizeDp,
                        minHeight = halfSizeDp, maxHeight = sizeDp
                    )
                ) {
                    Stack {
                        aligned(Alignment.Center, loose = true) {
                            WithConstraints { constraints ->
                                childConstraints[0] = constraints
                                constraintsLatch.countDown()
                            }
                        }
                        expanded {
                            WithConstraints { constraints ->
                                childConstraints[1] = constraints
                                constraintsLatch.countDown()
                            }
                        }
                        aligned(Alignment.Center, loose = false) {
                            WithConstraints { constraints ->
                                childConstraints[2] = constraints
                                constraintsLatch.countDown()
                            }
                        }
                    }
                }
            }
        }
        constraintsLatch.await(1, TimeUnit.SECONDS)

        assertEquals(Constraints(0.ipx, size, 0.ipx, size), childConstraints[0])
        assertEquals(Constraints(size, size, size, size), childConstraints[1])
        assertEquals(Constraints(halfSize, size, halfSize, size), childConstraints[2])
    }

    @Test
    fun testStack_hasCorrectIntrinsicMeasurements() = withDensity(density) {
        val testWidth = 90.ipx.toDp()
        val testHeight = 80.ipx.toDp()

        val testDimension = 200.ipx
        // When measuring the height with testDimension, width should be double
        val expectedWidth = testDimension * 2
        // When measuring the width with testDimension, height should be half
        val expectedHeight = testDimension / 2

        testIntrinsics(@Composable {
            Stack {
                aligned(Alignment.TopLeft) {
                    Container(AspectRatio(2f)) { }
                }
                aligned(Alignment.BottomCenter) {
                    ConstrainedBox(DpConstraints.tightConstraints(testWidth, testHeight)) { }
                }
                positioned(10.dp, 10.dp, 10.dp, 10.dp) {
                    ConstrainedBox(DpConstraints.tightConstraints(200.dp, 200.dp)) { }
                }
            }
        }) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width.
            assertEquals(testWidth.toIntPx(), minIntrinsicWidth(0.dp.toIntPx()))
            assertEquals(expectedWidth, minIntrinsicWidth(testDimension))
            assertEquals(testWidth.toIntPx(), minIntrinsicWidth(IntPx.Infinity))
            // Min height.
            assertEquals(testHeight.toIntPx(), minIntrinsicHeight(0.dp.toIntPx()))
            assertEquals(expectedHeight, minIntrinsicHeight(testDimension))
            assertEquals(testHeight.toIntPx(), minIntrinsicHeight(IntPx.Infinity))
            // Max width.
            assertEquals(testWidth.toIntPx(), maxIntrinsicWidth(0.dp.toIntPx()))
            assertEquals(expectedWidth, maxIntrinsicWidth(testDimension))
            assertEquals(testWidth.toIntPx(), maxIntrinsicWidth(IntPx.Infinity))
            // Max height.
            assertEquals(testHeight.toIntPx(), maxIntrinsicHeight(0.dp.toIntPx()))
            assertEquals(expectedHeight, maxIntrinsicHeight(testDimension))
            assertEquals(testHeight.toIntPx(), maxIntrinsicHeight(IntPx.Infinity))
        }
    }

    @Test
    fun testStack_hasCorrectIntrinsicMeasurements_withNoAlignedChildren() = withDensity(density) {
        testIntrinsics(@Composable {
            Stack {
                positioned(10.dp, 10.dp, 10.dp, 10.dp) {
                    ConstrainedBox(DpConstraints.tightConstraints(200.dp, 200.dp)) { }
                }
            }
        }) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width.
            assertEquals(0.dp.toIntPx(), minIntrinsicWidth(50.dp.toIntPx()))
            assertEquals(0.dp.toIntPx(), minIntrinsicWidth(IntPx.Infinity))
            // Min height.
            assertEquals(0.dp.toIntPx(), minIntrinsicHeight(50.dp.toIntPx()))
            assertEquals(0.dp.toIntPx(), minIntrinsicHeight(IntPx.Infinity))
            // Max width.
            assertEquals(0.dp.toIntPx(), maxIntrinsicWidth(50.dp.toIntPx()))
            assertEquals(0.dp.toIntPx(), maxIntrinsicWidth(IntPx.Infinity))
            // Max height.
            assertEquals(0.dp.toIntPx(), maxIntrinsicHeight(50.dp.toIntPx()))
            assertEquals(0.dp.toIntPx(), maxIntrinsicHeight(IntPx.Infinity))
        }
    }
}
