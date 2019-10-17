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
import androidx.ui.core.Alignment
import androidx.ui.core.IntPx
import androidx.ui.core.OnChildPositioned
import androidx.ui.core.PxPosition
import androidx.ui.core.PxSize
import androidx.ui.core.Ref
import androidx.ui.core.ipx
import androidx.ui.core.withDensity
import androidx.ui.layout.Align
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.FlowColumn
import androidx.ui.layout.FlowCrossAxisAlignment
import androidx.ui.layout.FlowMainAxisAlignment
import androidx.ui.layout.FlowRow
import androidx.ui.layout.LayoutSize
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SmallTest
@RunWith(JUnit4::class)
class FlowTest : LayoutTest() {
    @Test
    fun testFlowRow() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 5, height = size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i % 5), y = size * (i / 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withMainAxisSize_wrap() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(mainAxisSize = LayoutSize.Wrap) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 5, height = size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i % 5), y = size * (i / 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withMainAxisSize_expand() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(mainAxisSize = LayoutSize.Expand) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = flowWidth, height = size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i % 5), y = size * (i / 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withMainAxisAlignment_center() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.Center
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = flowWidth, height = size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = (flowWidth - size * 5) / 2 + size * (i % 5), y = size * (i / 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withMainAxisAlignment_start() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.Start
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = flowWidth, height = size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i % 5), y = size * (i / 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withMainAxisAlignment_end() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.End
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = flowWidth, height = size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = flowWidth - size * 5 + size * (i % 5), y = size * (i / 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withMainAxisAlignment_spaceEvenly() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = flowWidth, height = size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = (flowWidth - size * 5) * (i % 5 + 1) / 6 + size * (i % 5),
                    y = size * (i / 5)
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withMainAxisAlignment_spaceBetween() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = flowWidth, height = size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = (flowWidth - size * 5) * (i % 5) / 4 + size * (i % 5),
                    y = size * (i / 5)
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withMainAxisAlignment_spaceAround() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceAround
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = flowWidth, height = size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = (flowWidth - size * 5) * (i % 5 + 0.5f) / 5 + size * (i % 5),
                    y = size * (i / 5)
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withLastLineMainAxisAlignment_justify_center() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                            lastLineMainAxisAlignment = FlowMainAxisAlignment.Center
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(flowWidth, size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = if (i < 10) {
                        (flowWidth - size * 5) * (i % 5) / 4 + size * (i % 5)
                    } else {
                        (flowWidth - size * 5) / 2 + size * (i % 5)
                    },
                    y = size * (i / 5)
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withLastLineMainAxisAlignment_justify_start() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                            lastLineMainAxisAlignment = FlowMainAxisAlignment.Start
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(flowWidth, size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = if (i < 10) {
                        (flowWidth - size * 5) * (i % 5) / 4 + size * (i % 5)
                    } else {
                        size * (i % 5)
                    },
                    y = size * (i / 5)
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withLastLineMainAxisAlignment_justify_end() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                            lastLineMainAxisAlignment = FlowMainAxisAlignment.End
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(flowWidth, size * 3),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = if (i < 10) {
                        (flowWidth - size * 5) * (i % 5) / 4 + size * (i % 5)
                    } else {
                        (flowWidth - size * 5) + size * (i % 5)
                    },
                    y = size * (i / 5)
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withMainAxisSpacing() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val spacing = 32.ipx
        val spacingDp = spacing.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(mainAxisSpacing = spacingDp) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 3 + spacing * 2, height = size * 5),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = (size + spacing) * (i % 3), y = size * (i / 3)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withCrossAxisAlignment_center() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(crossAxisAlignment = FlowCrossAxisAlignment.Center) {
                            for (i in 0 until numberOfSquares) {
                                Container(
                                    width = sizeDp,
                                    height = if (i % 2 == 0) sizeDp else sizeDp * 2
                                ) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 5, height = size * 6),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(
                    width = size,
                    height = if (i % 2 == 0) size else size * 2
                ),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = size * (i % 5),
                    y = size * 2 * (i / 5) + if (i % 2 == 0) size / 2 else IntPx.Zero
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withCrossAxisAlignment_start() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(crossAxisAlignment = FlowCrossAxisAlignment.Start) {
                            for (i in 0 until numberOfSquares) {
                                Container(
                                    width = sizeDp,
                                    height = if (i % 2 == 0) sizeDp else sizeDp * 2
                                ) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 5, height = size * 6),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(
                    width = size,
                    height = if (i % 2 == 0) size else size * 2
                ),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i % 5), y = size * 2 * (i / 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withCrossAxisAlignment_end() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(crossAxisAlignment = FlowCrossAxisAlignment.End) {
                            for (i in 0 until numberOfSquares) {
                                Container(
                                    width = sizeDp,
                                    height = if (i % 2 == 0) sizeDp else sizeDp * 2
                                ) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 5, height = size * 6),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(
                    width = size,
                    height = if (i % 2 == 0) size else size * 2
                ),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = size * (i % 5),
                    y = size * 2 * (i / 5) + if (i % 2 == 0) size else IntPx.Zero
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowRow_withCrossAxisSpacing() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val spacing = 32.ipx
        val spacingDp = spacing.toDp()
        val flowWidth = 256.ipx
        val flowWidthDp = flowWidth.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxWidth = flowWidthDp)) {
                        FlowRow(crossAxisSpacing = spacingDp) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 5, height = size * 3 + spacing * 2),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i % 5), y = (size + spacing) * (i / 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 3, height = size * 5),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i / 5), y = size * (i % 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withMainAxisSize_wrap() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(mainAxisSize = LayoutSize.Wrap) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 3, height = size * 5),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i / 5), y = size * (i % 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withMainAxisSize_expand() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(mainAxisSize = LayoutSize.Expand) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 3, height = flowHeight),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i / 5), y = size * (i % 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withMainAxisAlignment_center() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.Center
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 3, height = flowHeight),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i / 5), y = (flowHeight - size * 5) / 2 + size * (i % 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withMainAxisAlignment_start() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.Start
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 3, height = flowHeight),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i / 5), y = size * (i % 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withMainAxisAlignment_end() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.End
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 3, height = flowHeight),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i / 5), y = flowHeight - size * 5 + size * (i % 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withMainAxisAlignment_spaceEvenly() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 3, height = flowHeight),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = size * (i / 5),
                    y = (flowHeight - size * 5) * (i % 5 + 1) / 6 + size * (i % 5)
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withMainAxisAlignment_spaceBetween() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 3, height = flowHeight),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = size * (i / 5),
                    y = (flowHeight - size * 5) * (i % 5) / 4 + size * (i % 5)
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withMainAxisAlignment_spaceAround() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceAround
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 3, height = flowHeight),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = size * (i / 5),
                    y = (flowHeight - size * 5) * (i % 5 + 0.5f) / 5 + size * (i % 5)
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withLastLineMainAxisAlignment_justify_center() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                            lastLineMainAxisAlignment = FlowMainAxisAlignment.Center
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(size * 3, flowHeight),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = size * (i / 5),
                    y = if (i < 10) {
                        (flowHeight - size * 5) * (i % 5) / 4 + size * (i % 5)
                    } else {
                        (flowHeight - size * 5) / 2 + size * (i % 5)
                    }
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withLastLineMainAxisAlignment_justify_start() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                            lastLineMainAxisAlignment = FlowMainAxisAlignment.Start
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(size * 3, flowHeight),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = size * (i / 5),
                    y = if (i < 10) {
                        (flowHeight - size * 5) * (i % 5) / 4 + size * (i % 5)
                    } else {
                        size * (i % 5)
                    }
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withLastLineMainAxisAlignment_justify_end() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(
                            mainAxisSize = LayoutSize.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                            lastLineMainAxisAlignment = FlowMainAxisAlignment.End
                        ) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(size * 3, flowHeight),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = size * (i / 5),
                    y = if (i < 10) {
                        (flowHeight - size * 5) * (i % 5) / 4 + size * (i % 5)
                    } else {
                        (flowHeight - size * 5) + size * (i % 5)
                    }
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withMainAxisSpacing() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val spacing = 32.ipx
        val spacingDp = spacing.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(mainAxisSpacing = spacingDp) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 5, height = size * 3 + spacing * 2),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * (i / 3), y = (size + spacing) * (i % 3)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withCrossAxisAlignment_center() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(crossAxisAlignment = FlowCrossAxisAlignment.Center) {
                            for (i in 0 until numberOfSquares) {
                                Container(
                                    width = if (i % 2 == 0) sizeDp else sizeDp * 2,
                                    height = sizeDp
                                ) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 6, height = size * 5),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(
                    width = if (i % 2 == 0) size else size * 2,
                    height = size
                ),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = size * 2 * (i / 5) + if (i % 2 == 0) size / 2 else IntPx.Zero,
                    y = size * (i % 5)
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withCrossAxisAlignment_start() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(crossAxisAlignment = FlowCrossAxisAlignment.Start) {
                            for (i in 0 until numberOfSquares) {
                                Container(
                                    width = if (i % 2 == 0) sizeDp else sizeDp * 2,
                                    height = sizeDp
                                ) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 6, height = size * 5),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = if (i % 2 == 0) size else size * 2, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = size * 2 * (i / 5), y = size * (i % 5)),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withCrossAxisAlignment_end() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(crossAxisAlignment = FlowCrossAxisAlignment.End) {
                            for (i in 0 until numberOfSquares) {
                                Container(
                                    width = if (i % 2 == 0) sizeDp else sizeDp * 2,
                                    height = sizeDp
                                ) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 6, height = size * 5),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(
                    width = if (i % 2 == 0) size else size * 2,
                    height = size
                ),
                childSize[i].value
            )
            assertEquals(
                PxPosition(
                    x = size * 2 * (i / 5) + if (i % 2 == 0) size else IntPx.Zero,
                    y = size * (i % 5)
                ),
                childPosition[i].value
            )
        }
    }

    @Test
    fun testFlowColumn_withCrossAxisSpacing() = withDensity(density) {
        val numberOfSquares = 15
        val size = 48.ipx
        val sizeDp = size.toDp()
        val spacing = 32.ipx
        val spacingDp = spacing.toDp()
        val flowHeight = 256.ipx
        val flowHeightDp = flowHeight.toDp()

        val flowSize = Ref<PxSize>()
        val childSize = Array(numberOfSquares) { Ref<PxSize>() }
        val childPosition = Array(numberOfSquares) { Ref<PxPosition>() }
        val positionedLatch = CountDownLatch(numberOfSquares + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    flowSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    ConstrainedBox(constraints = DpConstraints(maxHeight = flowHeightDp)) {
                        FlowColumn(crossAxisSpacing = spacingDp) {
                            for (i in 0 until numberOfSquares) {
                                Container(width = sizeDp, height = sizeDp) {
                                    SaveLayoutInfo(
                                        size = childSize[i],
                                        position = childPosition[i],
                                        positionedLatch = positionedLatch
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        assertTrue(positionedLatch.await(1, TimeUnit.SECONDS))

        assertEquals(
            PxSize(width = size * 3 + spacing * 2, height = size * 5),
            flowSize.value
        )
        for (i in 0 until numberOfSquares) {
            assertEquals(
                PxSize(width = size, height = size),
                childSize[i].value
            )
            assertEquals(
                PxPosition(x = (size + spacing) * (i / 5), y = size * (i % 5)),
                childPosition[i].value
            )
        }
    }
}