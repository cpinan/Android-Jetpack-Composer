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
import androidx.ui.core.IntPx
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.PxPosition
import androidx.ui.core.PxSize
import androidx.ui.core.Ref
import androidx.ui.core.dp
import androidx.ui.core.ipx
import androidx.ui.core.px
import androidx.ui.core.round
import androidx.ui.core.toPx
import androidx.ui.core.withDensity
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.MainAxisAlignment
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.HorizontalAlignmentLine
import androidx.ui.core.OnChildPositioned
import androidx.ui.core.OnPositioned
import androidx.ui.core.VerticalAlignmentLine
import androidx.ui.core.min
import androidx.ui.layout.Align
import androidx.ui.layout.AspectRatio
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.FixedSpacer
import androidx.ui.layout.FlexColumn
import androidx.ui.layout.FlexRow
import androidx.ui.layout.Row
import androidx.ui.layout.Wrap
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SmallTest
@RunWith(JUnit4::class)
class FlexTest : LayoutTest() {
    @Test
    fun testRow() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Container(alignment = Alignment.TopLeft) {
                Row {
                    Container(width = sizeDp, height = sizeDp) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[0] = coordinates.size
                            childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }

                    Container(width = (sizeDp * 2), height = (sizeDp * 2)) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[1] = coordinates.size
                            childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxSize(size, size), childSize[0])
        assertEquals(
            PxSize((sizeDp.toPx() * 2).round(), (sizeDp.toPx() * 2).round()),
            childSize[1]
        )
        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition(size.toPx(), 0.px), childPosition[1])
    }

    @Test
    fun testRowFlex_withExpandedChildren() = withDensity(density) {
        val heightDp = 50.dp
        val childrenHeight = 50.dp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Container(alignment = Alignment.TopLeft) {
                FlexRow {
                    val widthDp = 50.px.toDp()

                    expanded(flex = 1f) {
                        Container(width = widthDp, height = heightDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childSize[0] = coordinates.size
                                childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                                drawLatch.countDown()
                            })
                        }
                    }

                    expanded(flex = 2f) {
                        Container(width = widthDp, height = (heightDp * 2)) {
                            OnPositioned(onPositioned = { coordinates ->
                                childSize[1] = coordinates.size
                                childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
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

        assertEquals(
            PxSize((root.width.px / 3).round().toPx(), childrenHeight.toPx()),
            childSize[0]
        )
        assertEquals(
            PxSize((root.width.px * 2 / 3).round().toPx(), (heightDp.toPx() * 2).round().toPx()),
            childSize[1]
        )
        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition((root.width.px / 3).round().toPx(), 0.px), childPosition[1])
    }

    @Test
    fun testRowFlex_withFlexibleChildren() = withDensity(density) {
        val childrenWidthDp = 50.dp
        val childrenWidth = childrenWidthDp.toIntPx()
        val childrenHeightDp = 50.dp
        val childrenHeight = childrenHeightDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Container(alignment = Alignment.TopLeft) {
                FlexRow {
                    flexible(flex = 1f) {
                        Container(width = childrenWidthDp, height = childrenHeightDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childSize[0] = coordinates.size
                                childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                                drawLatch.countDown()
                            })
                        }
                    }

                    flexible(flex = 2f) {
                        Container(width = childrenWidthDp, height = (childrenHeightDp * 2)) {
                            OnPositioned(onPositioned = { coordinates ->
                                childSize[1] = coordinates.size
                                childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
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

        assertEquals(PxSize(childrenWidth.toPx(), childrenHeight.toPx()), childSize[0])
        assertEquals(
            PxSize(childrenWidth.toPx(), (childrenHeightDp.toPx() * 2).round().toPx()),
            childSize[1]
        )
        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition(childrenWidth.toPx(), 0.px), childPosition[1])
    }

    @Test
    fun testColumn() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Container(alignment = Alignment.TopLeft) {
                Column {
                    Container(width = sizeDp, height = sizeDp) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[0] = coordinates.size
                            childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }
                    Container(width = (sizeDp * 2), height = (sizeDp * 2)) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[1] = coordinates.size
                            childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxSize(size, size), childSize[0])
        assertEquals(
            PxSize((sizeDp.toPx() * 2).round(), (sizeDp.toPx() * 2).round()),
            childSize[1]
        )
        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition(0.px, size.toPx()), childPosition[1])
    }

    @Test
    fun testColumnFlex_withExpandedChildren() = withDensity(density) {
        val widthDp = 50.dp
        val childrenWidth = widthDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Container(alignment = Alignment.TopLeft) {
                FlexColumn {
                    val heightDp = 50.px.toDp()

                    expanded(flex = 1f) {
                        Container(width = widthDp, height = heightDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childSize[0] = coordinates.size
                                childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                                drawLatch.countDown()
                            })
                        }
                    }

                    expanded(flex = 2f) {
                        Container(width = (widthDp * 2), height = heightDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childSize[1] = coordinates.size
                                childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
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

        assertEquals(
            PxSize(childrenWidth.toPx(), (root.height.px / 3).round().toPx()),
            childSize[0]
        )
        assertEquals(
            PxSize((widthDp.toPx() * 2).round(), (root.height.px * 2 / 3).round()),
            childSize[1]
        )
        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition(0.px, (root.height.px / 3).round().toPx()), childPosition[1])
    }

    @Test
    fun testColumnFlex_withFlexibleChildren() = withDensity(density) {
        val childrenWidthDp = 50.dp
        val childrenWidth = childrenWidthDp.toIntPx()
        val childrenHeightDp = 50.dp
        val childrenHeight = childrenHeightDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Container(alignment = Alignment.TopLeft) {
                FlexColumn {
                    flexible(flex = 1f) {
                        Container(width = childrenWidthDp, height = childrenHeightDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childSize[0] = coordinates.size
                                childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                                drawLatch.countDown()
                            })
                        }
                    }

                    flexible(flex = 2f) {
                        Container(width = (childrenWidthDp * 2), height = childrenHeightDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childSize[1] = coordinates.size
                                childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
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

        assertEquals(PxSize(childrenWidth.toPx(), childrenHeight.toPx()), childSize[0])
        assertEquals(
            PxSize((childrenWidthDp.toPx() * 2).round(), childrenHeight),
            childSize[1]
        )
        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition(0.px, childrenHeight.toPx()), childPosition[1])
    }

    @Test
    fun testRow_withStartCrossAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Align(Alignment.CenterLeft) {
                Row(crossAxisAlignment = CrossAxisAlignment.Start) {
                    Container(width = sizeDp, height = sizeDp) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[0] = coordinates.size
                            childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }

                    Container(width = (sizeDp * 2), height = (sizeDp * 2)) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[1] = coordinates.size
                            childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxSize(size, size), childSize[0])
        assertEquals(
            PxSize((sizeDp.toPx() * 2).round(), (sizeDp.toPx() * 2).round()),
            childSize[1]
        )
        assertEquals(
            PxPosition(0.px, (root.height.px / 2 - size.toPx()).round().toPx()),
            childPosition[0]
        )
        assertEquals(
            PxPosition(size.toPx(), (root.height.px / 2 - size.toPx()).round().toPx()),
            childPosition[1]
        )
    }

    @Test
    fun testRow_withEndCrossAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Align(Alignment.CenterLeft) {
                Row(crossAxisAlignment = CrossAxisAlignment.End) {
                    Container(width = sizeDp, height = sizeDp) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[0] = coordinates.size
                            childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }

                    Container(width = (sizeDp * 2), height = (sizeDp * 2)) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[1] = coordinates.size
                            childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxSize(size, size), childSize[0])
        assertEquals(
            PxSize((sizeDp.toPx() * 2).round(), (sizeDp.toPx() * 2).round()),
            childSize[1]
        )
        assertEquals(
            PxPosition(
                0.px, ((root.height.px + (sizeDp.toPx() * 2)
                    .round().toPx()) / 2 - size.toPx()).round().toPx()
            ),
            childPosition[0]
        )
        assertEquals(
            PxPosition(size.toPx(), (root.height.px / 2 - size.toPx()).round().toPx()),
            childPosition[1]
        )
    }

    @Test
    fun testRow_withStretchCrossAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Align(Alignment.CenterLeft) {
                Row(crossAxisAlignment = CrossAxisAlignment.Stretch) {
                    Container(width = sizeDp, height = sizeDp) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[0] = coordinates.size
                            childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }

                    Container(width = (sizeDp * 2), height = (sizeDp * 2)) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[1] = coordinates.size
                            childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxSize(size.toPx(), root.height.px), childSize[0])
        assertEquals(
            PxSize((sizeDp.toPx() * 2).round().toPx(), root.height.px),
            childSize[1]
        )
        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition(size.toPx(), 0.px), childPosition[1])
    }

    @Test
    fun testColumn_withStartCrossAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Align(Alignment.TopCenter) {
                Column(crossAxisAlignment = CrossAxisAlignment.Start) {
                    Container(width = sizeDp, height = sizeDp) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[0] = coordinates.size
                            childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }

                    Container(width = (sizeDp * 2), height = (sizeDp * 2)) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[1] = coordinates.size
                            childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxSize(size, size), childSize[0])
        assertEquals(
            PxSize((sizeDp.toPx() * 2).round(), (sizeDp.toPx() * 2).round()),
            childSize[1]
        )
        assertEquals(
            PxPosition((root.width.px / 2 - size.toPx()).round().toPx(), 0.px),
            childPosition[0]
        )
        assertEquals(
            PxPosition((root.width.px / 2 - size.toPx()).round().toPx(), size.toPx()),
            childPosition[1]
        )
    }

    @Test
    fun testColumn_withEndCrossAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Align(Alignment.TopCenter) {
                Column(crossAxisAlignment = CrossAxisAlignment.End) {
                    Container(width = sizeDp, height = sizeDp) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[0] = coordinates.size
                            childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }

                    Container(width = (sizeDp * 2), height = (sizeDp * 2)) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[1] = coordinates.size
                            childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxSize(size, size), childSize[0])
        assertEquals(
            PxSize((sizeDp.toPx() * 2).round(), (sizeDp.toPx() * 2).round()),
            childSize[1]
        )
        assertEquals(
            PxPosition(
                (((root.width.px + (sizeDp.toPx() * 2)
                    .round().toPx()) / 2).round() - size).toPx(),
                0.px
            ),
            childPosition[0]
        )
        assertEquals(
            PxPosition((root.width.px / 2 - size.toPx()).round().toPx(), size.toPx()),
            childPosition[1]
        )
    }

    @Test
    fun testColumn_withStretchCrossAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        val childSize = arrayOf(PxSize(-1.px, -1.px), PxSize(-1.px, -1.px))
        val childPosition = arrayOf(PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px))
        show {
            Align(Alignment.TopCenter) {
                Column(crossAxisAlignment = CrossAxisAlignment.Stretch) {
                    Container(width = sizeDp, height = sizeDp) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[0] = coordinates.size
                            childPosition[0] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }

                    Container(width = (sizeDp * 2), height = (sizeDp * 2)) {
                        OnPositioned(onPositioned = { coordinates ->
                            childSize[1] = coordinates.size
                            childPosition[1] = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxSize(root.width.px, size.toPx()), childSize[0])
        assertEquals(
            PxSize(root.width.px, (sizeDp.toPx() * 2).round().toPx()),
            childSize[1]
        )
        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition(0.px, size.toPx()), childPosition[1])
    }

    @Test
    fun testRow_withMaxMainAxisSize() = withDensity(density) {
        val sizeDp = 50.dp

        val drawLatch = CountDownLatch(1)
        lateinit var rowSize: PxSize
        show {
            Center {
                Row(mainAxisSize = LayoutSize.Expand) {
                    FixedSpacer(width = sizeDp, height = sizeDp)
                    FixedSpacer(width = (sizeDp * 2), height = (sizeDp * 2))

                    OnPositioned(onPositioned = { coordinates ->
                        rowSize = coordinates.size
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            root.width.ipx,
            rowSize.width.round()
        )
    }

    @Test
    fun testRow_withMinMainAxisSize() = withDensity(density) {
        val sizeDp = 50.dp

        val drawLatch = CountDownLatch(1)
        lateinit var rowSize: PxSize
        show {
            Center {
                Row(mainAxisSize = LayoutSize.Wrap) {
                    FixedSpacer(width = sizeDp, height = sizeDp)
                    FixedSpacer(width = (sizeDp * 2), height = (sizeDp * 2))

                    OnPositioned(onPositioned = { coordinates ->
                        rowSize = coordinates.size
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            (sizeDp * 3).toIntPx(),
            rowSize.width.round()
        )
    }

    @Test
    fun testRow_withMaxCrossAxisSize() = withDensity(density) {
        val sizeDp = 50.dp

        val drawLatch = CountDownLatch(1)
        lateinit var rowSize: PxSize
        show {
            Center {
                Row(crossAxisSize = LayoutSize.Expand) {
                    FixedSpacer(width = sizeDp, height = sizeDp)
                    FixedSpacer(width = (sizeDp * 2), height = (sizeDp * 2))

                    OnPositioned(onPositioned = { coordinates ->
                        rowSize = coordinates.size
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            root.height.ipx,
            rowSize.height.round()
        )
    }

    @Test
    fun testRow_withMinCrossAxisSize() = withDensity(density) {
        val sizeDp = 50.dp

        val drawLatch = CountDownLatch(1)
        lateinit var rowSize: PxSize
        show {
            Center {
                Row(crossAxisSize = LayoutSize.Wrap) {
                    FixedSpacer(width = sizeDp, height = sizeDp)
                    FixedSpacer(width = (sizeDp * 2), height = (sizeDp * 2))

                    OnPositioned(onPositioned = { coordinates ->
                        rowSize = coordinates.size
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            (sizeDp * 2).toIntPx(),
            rowSize.height.round()
        )
    }

    @Test
    fun testRow_withMaxMainAxisSize_respectsMaxWidthConstraint() = withDensity(density) {
        val sizeDp = 50.dp
        val rowWidthDp = 250.dp

        val drawLatch = CountDownLatch(1)
        lateinit var rowSize: PxSize
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints(maxWidth = rowWidthDp)) {
                    Row(mainAxisSize = LayoutSize.Expand) {
                        FixedSpacer(width = sizeDp, height = sizeDp)
                        FixedSpacer(width = sizeDp * 2, height = sizeDp * 2)

                        OnPositioned(onPositioned = { coordinates ->
                            rowSize = coordinates.size
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            min(root.width.ipx, rowWidthDp.toIntPx()),
            rowSize.width.round()
        )
    }

    @Test
    fun testRow_withMinMainAxisSize_respectsMinWidthConstraint() = withDensity(density) {
        val sizeDp = 50.dp
        val rowWidthDp = 250.dp

        val drawLatch = CountDownLatch(1)
        lateinit var rowSize: PxSize
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints(minWidth = rowWidthDp)) {
                    Row(mainAxisSize = LayoutSize.Wrap) {
                        FixedSpacer(width = sizeDp, height = sizeDp)
                        FixedSpacer(width = sizeDp * 2, height = sizeDp * 2)

                        OnPositioned(onPositioned = { coordinates ->
                            rowSize = coordinates.size
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            rowWidthDp.toIntPx(),
            rowSize.width.round()
        )
    }

    @Test
    fun testRow_withMaxCrossAxisSize_respectsMaxHeightConstraint() = withDensity(density) {
        val sizeDp = 50.dp
        val rowHeightDp = 250.dp

        val drawLatch = CountDownLatch(1)
        lateinit var rowSize: PxSize
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints(maxHeight = rowHeightDp)) {
                    Row(crossAxisSize = LayoutSize.Expand) {
                        FixedSpacer(width = sizeDp, height = sizeDp)
                        FixedSpacer(width = sizeDp * 2, height = sizeDp * 2)

                        OnPositioned(onPositioned = { coordinates ->
                            rowSize = coordinates.size
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            min(root.height.ipx, rowHeightDp.toIntPx()),
            rowSize.height.round()
        )
    }

    @Test
    fun testRow_withMinCrossAxisSize_respectsMinHeightConstraint() = withDensity(density) {
        val sizeDp = 50.dp
        val rowHeightDp = 250.dp

        val drawLatch = CountDownLatch(1)
        lateinit var rowSize: PxSize
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints(maxHeight = rowHeightDp)) {
                    Row(crossAxisSize = LayoutSize.Expand) {
                        FixedSpacer(width = sizeDp, height = sizeDp)
                        FixedSpacer(width = sizeDp * 2, height = sizeDp * 2)

                        OnPositioned(onPositioned = { coordinates ->
                            rowSize = coordinates.size
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            rowHeightDp.toIntPx(),
            rowSize.height.round()
        )
    }

    @Test
    fun testFlexRow_withMinMainAxisSize() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()
        val rowWidthDp = 250.dp
        val rowWidth = rowWidthDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        lateinit var rowSize: PxSize
        lateinit var expandedChildSize: PxSize
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints(minWidth = rowWidthDp)) {
                    FlexRow(mainAxisSize = LayoutSize.Wrap) {
                        expanded(flex = 1f) {
                            Container(width = sizeDp, height = sizeDp) {
                                OnPositioned(onPositioned = { coordinates ->
                                    expandedChildSize = coordinates.size
                                    drawLatch.countDown()
                                })
                            }
                        }
                        inflexible {
                            OnPositioned(onPositioned = { coordinates ->
                                rowSize = coordinates.size
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

        assertEquals(
            PxSize(rowWidth, size),
            rowSize
        )
        assertEquals(
            PxSize(rowWidth, size),
            expandedChildSize
        )
    }

    @Test
    fun testColumn_withMaxMainAxisSize() = withDensity(density) {
        val sizeDp = 50.dp

        val drawLatch = CountDownLatch(1)
        lateinit var columnSize: PxSize
        show {
            Center {
                Column(mainAxisSize = LayoutSize.Expand) {
                    FixedSpacer(width = sizeDp, height = sizeDp)
                    FixedSpacer(width = (sizeDp * 2), height = (sizeDp * 2))

                    OnPositioned(onPositioned = { coordinates ->
                        columnSize = coordinates.size
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            root.height.ipx,
            columnSize.height.round()
        )
    }

    @Test
    fun testColumn_withMinMainAxisSize() = withDensity(density) {
        val sizeDp = 50.dp

        val drawLatch = CountDownLatch(1)
        lateinit var columnSize: PxSize
        show {
            Center {
                Column(mainAxisSize = LayoutSize.Wrap) {
                    FixedSpacer(width = sizeDp, height = sizeDp)
                    FixedSpacer(width = (sizeDp * 2), height = (sizeDp * 2))

                    OnPositioned(onPositioned = { coordinates ->
                        columnSize = coordinates.size
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            (sizeDp * 3).toIntPx(),
            columnSize.height.round()
        )
    }

    @Test
    fun testColumn_withMaxCrossAxisSize() = withDensity(density) {
        val sizeDp = 50.dp

        val drawLatch = CountDownLatch(1)
        lateinit var columnSize: PxSize
        show {
            Center {
                Column(crossAxisSize = LayoutSize.Expand) {
                    FixedSpacer(width = sizeDp, height = sizeDp)
                    FixedSpacer(width = (sizeDp * 2), height = (sizeDp * 2))

                    OnPositioned(onPositioned = { coordinates ->
                        columnSize = coordinates.size
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            root.width.ipx,
            columnSize.width.round()
        )
    }

    @Test
    fun testColumn_withMinCrossAxisSize() = withDensity(density) {
        val sizeDp = 50.dp

        val drawLatch = CountDownLatch(1)
        lateinit var columnSize: PxSize
        show {
            Center {
                Column(crossAxisSize = LayoutSize.Wrap) {
                    FixedSpacer(width = sizeDp, height = sizeDp)
                    FixedSpacer(width = (sizeDp * 2), height = (sizeDp * 2))

                    OnPositioned(onPositioned = { coordinates ->
                        columnSize = coordinates.size
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            (sizeDp * 2).toIntPx(),
            columnSize.width.round()
        )
    }

    @Test
    fun testColumn_withMaxMainAxisSize_respectsMaxHeightConstraint() = withDensity(density) {
        val sizeDp = 50.dp
        val columnHeightDp = 250.dp

        val drawLatch = CountDownLatch(1)
        lateinit var columnSize: PxSize
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints(maxHeight = columnHeightDp)) {
                    Column(mainAxisSize = LayoutSize.Expand) {
                        FixedSpacer(width = sizeDp, height = sizeDp)
                        FixedSpacer(width = sizeDp * 2, height = sizeDp * 2)

                        OnPositioned(onPositioned = { coordinates ->
                            columnSize = coordinates.size
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            min(root.height.ipx, columnHeightDp.toIntPx()),
            columnSize.height.round()
        )
    }

    @Test
    fun testColumn_withMinMainAxisSize_respectsMinHeightConstraint() = withDensity(density) {
        val sizeDp = 50.dp
        val columnHeightDp = 250.dp

        val drawLatch = CountDownLatch(1)
        lateinit var columnSize: PxSize
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints(minHeight = columnHeightDp)) {
                    Column(mainAxisSize = LayoutSize.Wrap) {
                        FixedSpacer(width = sizeDp, height = sizeDp)
                        FixedSpacer(width = sizeDp * 2, height = sizeDp * 2)

                        OnPositioned(onPositioned = { coordinates ->
                            columnSize = coordinates.size
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            columnHeightDp.toIntPx(),
            columnSize.height.round()
        )
    }

    @Test
    fun testColumn_withMaxCrossAxisSize_respectsMaxWidthConstraint() = withDensity(density) {
        val sizeDp = 50.dp
        val columnWidthDp = 250.dp

        val drawLatch = CountDownLatch(1)
        lateinit var columnSize: PxSize
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints(maxWidth = columnWidthDp)) {
                    Column(crossAxisSize = LayoutSize.Expand) {
                        FixedSpacer(width = sizeDp, height = sizeDp)
                        FixedSpacer(width = sizeDp * 2, height = sizeDp * 2)

                        OnPositioned(onPositioned = { coordinates ->
                            columnSize = coordinates.size
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            min(root.width.ipx, columnWidthDp.toIntPx()),
            columnSize.width.round()
        )
    }

    @Test
    fun testColumn_withMinCrossAxisSize_respectsMinWidthConstraint() = withDensity(density) {
        val sizeDp = 50.dp
        val columnWidthDp = 250.dp

        val drawLatch = CountDownLatch(1)
        lateinit var columnSize: PxSize
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints(minWidth = columnWidthDp)) {
                    Column(crossAxisSize = LayoutSize.Wrap) {
                        FixedSpacer(width = sizeDp, height = sizeDp)
                        FixedSpacer(width = sizeDp * 2, height = sizeDp * 2)

                        OnPositioned(onPositioned = { coordinates ->
                            columnSize = coordinates.size
                            drawLatch.countDown()
                        })
                    }
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(
            columnWidthDp.toIntPx(),
            columnSize.width.round()
        )
    }

    @Test
    fun testFlexColumn_withMinMainAxisSize() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()
        val columnHeightDp = 250.dp
        val columnHeight = columnHeightDp.toIntPx()

        val drawLatch = CountDownLatch(2)
        lateinit var columnSize: PxSize
        lateinit var expandedChildSize: PxSize
        show {
            Center {
                ConstrainedBox(constraints = DpConstraints(minHeight = columnHeightDp)) {
                    FlexColumn(mainAxisSize = LayoutSize.Wrap) {
                        expanded(flex = 1f) {
                            Container(width = sizeDp, height = sizeDp) {
                                OnPositioned(onPositioned = { coordinates ->
                                    expandedChildSize = coordinates.size
                                    drawLatch.countDown()
                                })
                            }
                        }
                        inflexible {
                            OnPositioned(onPositioned = { coordinates ->
                                columnSize = coordinates.size
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

        assertEquals(
            PxSize(size, columnHeight),
            columnSize
        )
        assertEquals(
            PxSize(size, columnHeight),
            expandedChildSize
        )
    }

    @Test
    fun testRow_withStartMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Row(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.Start
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition(size.toPx(), 0.px), childPosition[1])
        assertEquals(PxPosition(size.toPx() * 2, 0.px), childPosition[2])
    }

    @Test
    fun testRow_withEndMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Row(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.End
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxPosition(root.width.px - size.toPx() * 3, 0.px), childPosition[0])
        assertEquals(PxPosition(root.width.px - size.toPx() * 2, 0.px), childPosition[1])
        assertEquals(PxPosition(root.width.px - size.toPx(), 0.px), childPosition[2])
    }

    @Test
    fun testRow_withCenterMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Row(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.Center
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        val extraSpace = root.width.px.round() - size * 3
        assertEquals(PxPosition((extraSpace / 2).toPx(), 0.px), childPosition[0])
        assertEquals(PxPosition((extraSpace / 2).toPx() + size.toPx(), 0.px), childPosition[1])
        assertEquals(PxPosition((extraSpace / 2).toPx() + size.toPx() * 2, 0.px), childPosition[2])
    }

    @Test
    fun testRow_withSpaceEvenlyMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Row(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.SpaceEvenly
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        val gap = (root.width.px - size.toPx() * 3) / 4
        assertEquals(PxPosition(gap.round().toPx(), 0.px), childPosition[0])
        assertEquals(PxPosition((size.toPx() + gap * 2).round().toPx(), 0.px), childPosition[1])
        assertEquals(PxPosition((size.toPx() * 2 + gap * 3).round().toPx(), 0.px), childPosition[2])
    }

    @Test
    fun testRow_withSpaceBetweenMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Row(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.SpaceBetween
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        val gap = (root.width.px - size.toPx() * 3) / 2
        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition((gap + size.toPx()).round().toPx(), 0.px), childPosition[1])
        assertEquals(PxPosition((gap * 2 + size.toPx() * 2).round().toPx(), 0.px), childPosition[2])
    }

    @Test
    fun testRow_withSpaceAroundMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Row(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.SpaceAround
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        val gap = (root.width.px.round() - size * 3) / 3
        assertEquals(PxPosition((gap / 2).toPx(), 0.px), childPosition[0])
        assertEquals(PxPosition((gap * 3 / 2).toPx() + size.toPx(), 0.px), childPosition[1])
        assertEquals(PxPosition((gap * 5 / 2).toPx() + size.toPx() * 2, 0.px), childPosition[2])
    }

    @Test
    fun testColumn_withStartMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Column(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.Start
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition(0.px, size.toPx()), childPosition[1])
        assertEquals(PxPosition(0.px, size.toPx() * 2), childPosition[2])
    }

    @Test
    fun testColumn_withEndMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Column(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.End
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        assertEquals(PxPosition(0.px, root.height.px - size.toPx() * 3), childPosition[0])
        assertEquals(PxPosition(0.px, root.height.px - size.toPx() * 2), childPosition[1])
        assertEquals(PxPosition(0.px, root.height.px - size.toPx()), childPosition[2])
    }

    @Test
    fun testColumn_withCenterMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Column(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.Center
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        val extraSpace = root.height.px.round() - size * 3
        assertEquals(PxPosition(0.px, (extraSpace / 2).toPx()), childPosition[0])
        assertEquals(PxPosition(0.px, (extraSpace / 2).toPx() + size.toPx()), childPosition[1])
        assertEquals(PxPosition(0.px, (extraSpace / 2).toPx() + size.toPx() * 2), childPosition[2])
    }

    @Test
    fun testColumn_withSpaceEvenlyMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Column(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.SpaceEvenly
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        val gap = (root.height.px - size.toPx() * 3) / 4
        assertEquals(PxPosition(0.px, gap.round().toPx()), childPosition[0])
        assertEquals(PxPosition(0.px, (size.toPx() + gap * 2).round().toPx()), childPosition[1])
        assertEquals(PxPosition(0.px, (size.toPx() * 2 + gap * 3).round().toPx()), childPosition[2])
    }

    @Test
    fun testColumn_withSpaceBetweenMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Column(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.SpaceBetween
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        val gap = (root.height.px - size.toPx() * 3) / 2
        assertEquals(PxPosition(0.px, 0.px), childPosition[0])
        assertEquals(PxPosition(0.px, (gap + size.toPx()).round().toPx()), childPosition[1])
        assertEquals(PxPosition(0.px, (gap * 2 + size.toPx() * 2).round().toPx()), childPosition[2])
    }

    @Test
    fun testColumn_withSpaceAroundMainAxisAlignment() = withDensity(density) {
        val sizeDp = 50.dp
        val size = sizeDp.toIntPx()

        val drawLatch = CountDownLatch(4)
        val childPosition = arrayOf(
            PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px), PxPosition(-1.px, -1.px)
        )
        val childLayoutCoordinates = arrayOfNulls<LayoutCoordinates?>(childPosition.size)
        show {
            Center {
                Column(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.SpaceAround
                ) {
                    for (i in 0 until childPosition.size) {
                        Container(width = sizeDp, height = sizeDp) {
                            OnPositioned(onPositioned = { coordinates ->
                                childLayoutCoordinates[i] = coordinates
                                drawLatch.countDown()
                            })
                        }
                    }
                    OnPositioned(onPositioned = { coordinates ->
                        for (i in 0 until childPosition.size) {
                            childPosition[i] = coordinates
                                .childToLocal(childLayoutCoordinates[i]!!, PxPosition(0.px, 0.px))
                        }
                        drawLatch.countDown()
                    })
                }
            }
        }
        drawLatch.await(1, TimeUnit.SECONDS)

        val root = findAndroidComposeView()
        waitForDraw(root)

        val gap = (root.height.px - size.toPx() * 3) / 3
        assertEquals(PxPosition(0.px, (gap / 2).round().toPx()), childPosition[0])
        assertEquals(
            PxPosition(0.px, ((gap * 3 / 2) + size.toPx()).round().toPx()),
            childPosition[1]
        )
        assertEquals(
            PxPosition(0.px, ((gap * 5 / 2) + size.toPx() * 2).round().toPx()),
            childPosition[2]
        )
    }

    @Test
    fun testRow_doesNotUseMinConstraintsOnChildren() = withDensity(density) {
        val sizeDp = 50.dp
        val childSizeDp = 30.dp
        val childSize = childSizeDp.toIntPx()

        val layoutLatch = CountDownLatch(1)
        val containerSize = Ref<PxSize>()
        show {
            Center {
                ConstrainedBox(
                    constraints = DpConstraints.tightConstraints(sizeDp, sizeDp)
                ) {
                    Row {
                        OnChildPositioned(onPositioned = { coordinates ->
                            containerSize.value = coordinates.size
                            layoutLatch.countDown()
                        }) {
                            FixedSpacer(width = childSizeDp, height = childSizeDp)
                        }
                    }
                }
            }
        }
        assertTrue(layoutLatch.await(1, TimeUnit.SECONDS))

        assertEquals(PxSize(childSize, childSize), containerSize.value)
    }

    @Test
    fun testColumn_doesNotUseMinConstraintsOnChildren() = withDensity(density) {
        val sizeDp = 50.dp
        val childSizeDp = 30.dp
        val childSize = childSizeDp.toIntPx()

        val layoutLatch = CountDownLatch(1)
        val containerSize = Ref<PxSize>()
        show {
            Center {
                ConstrainedBox(
                    constraints = DpConstraints.tightConstraints(sizeDp, sizeDp)
                ) {
                    Column {
                        OnChildPositioned(onPositioned = { coordinates ->
                            containerSize.value = coordinates.size
                            layoutLatch.countDown()
                        }) {
                            FixedSpacer(width = childSizeDp, height = childSizeDp)
                        }
                    }
                }
            }
        }
        assertTrue(layoutLatch.await(1, TimeUnit.SECONDS))

        assertEquals(PxSize(childSize, childSize), containerSize.value)
    }

    @Test
    fun testRow_hasCorrectIntrinsicMeasurements() = withDensity(density) {
        testIntrinsics(@Composable {
            Row {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Row(mainAxisSize = LayoutSize.Expand) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Row(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.Start,
                crossAxisAlignment = CrossAxisAlignment.Start
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Row(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.Center,
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Row(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.End,
                crossAxisAlignment = CrossAxisAlignment.End
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Row(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.SpaceAround,
                crossAxisAlignment = CrossAxisAlignment.Stretch
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Row(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.SpaceBetween
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Row(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.SpaceEvenly
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width.
            assertEquals(50.dp.toIntPx(), minIntrinsicWidth(0.dp.toIntPx()))
            assertEquals(25.dp.toIntPx() * 2 + 50.dp.toIntPx(), minIntrinsicWidth(25.dp.toIntPx()))
            assertEquals(50.dp.toIntPx(), minIntrinsicWidth(IntPx.Infinity))
            // Min height.
            assertEquals(40.dp.toIntPx(), minIntrinsicHeight(0.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), minIntrinsicHeight(70.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), minIntrinsicHeight(IntPx.Infinity))
            // Max width.
            assertEquals(50.dp.toIntPx(), maxIntrinsicWidth(0.dp.toIntPx()))
            assertEquals(25.dp.toIntPx() * 2 + 50.dp.toIntPx(), maxIntrinsicWidth(25.dp.toIntPx()))
            assertEquals(50.dp.toIntPx(), maxIntrinsicWidth(IntPx.Infinity))
            // Max height.
            assertEquals(40.dp.toIntPx(), maxIntrinsicHeight(0.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), maxIntrinsicHeight(70.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), maxIntrinsicHeight(IntPx.Infinity))
        }
    }

    @Test
    fun testColumn_hasCorrectIntrinsicMeasurements() = withDensity(density) {
        testIntrinsics(@Composable {
            Column {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Column(mainAxisSize = LayoutSize.Expand) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Column(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.Start,
                crossAxisAlignment = CrossAxisAlignment.Start
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Column(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.Center,
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Column(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.End,
                crossAxisAlignment = CrossAxisAlignment.End
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Column(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.SpaceAround,
                crossAxisAlignment = CrossAxisAlignment.Stretch
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Column(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.SpaceBetween
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }, @Composable {
            Column(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.SpaceEvenly
            ) {
                Container(AspectRatio(2f)) { }
                ConstrainedBox(DpConstraints.tightConstraints(50.dp, 40.dp)) { }
            }
        }) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width.
            assertEquals(50.dp.toIntPx(), minIntrinsicWidth(0.dp.toIntPx()))
            assertEquals(50.dp.toIntPx(), minIntrinsicWidth(25.dp.toIntPx()))
            assertEquals(50.dp.toIntPx(), minIntrinsicWidth(IntPx.Infinity))
            // Min height.
            assertEquals(40.dp.toIntPx(), minIntrinsicHeight(0.dp.toIntPx()))
            assertEquals(50.dp.toIntPx() / 2 + 40.dp.toIntPx(), minIntrinsicHeight(50.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), minIntrinsicHeight(IntPx.Infinity))
            // Max width.
            assertEquals(50.dp.toIntPx(), maxIntrinsicWidth(0.dp.toIntPx()))
            assertEquals(50.dp.toIntPx(), maxIntrinsicWidth(25.dp.toIntPx()))
            assertEquals(50.dp.toIntPx(), maxIntrinsicWidth(IntPx.Infinity))
            // Max height.
            assertEquals(40.dp.toIntPx(), maxIntrinsicHeight(0.dp.toIntPx()))
            assertEquals(50.dp.toIntPx() / 2 + 40.dp.toIntPx(), maxIntrinsicHeight(50.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), maxIntrinsicHeight(IntPx.Infinity))
        }
    }

    @Test
    fun testFlexRow_hasCorrectIntrinsicMeasurements() = withDensity(density) {
        testIntrinsics(@Composable {
            FlexRow {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 40.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(2f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
            }
        }, @Composable {
            FlexRow(mainAxisSize = LayoutSize.Wrap) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 40.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(2f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
            }
        }, @Composable {
            FlexRow(
                mainAxisAlignment = MainAxisAlignment.Start,
                crossAxisAlignment = CrossAxisAlignment.Start
            ) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 40.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(2f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
            }
        }, @Composable {
            FlexRow(
                mainAxisAlignment = MainAxisAlignment.Center,
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 40.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(2f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
            }
        }, @Composable {
            FlexRow(
                mainAxisAlignment = MainAxisAlignment.End,
                crossAxisAlignment = CrossAxisAlignment.End
            ) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 40.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(2f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
            }
        }, @Composable {
            FlexRow(
                mainAxisAlignment = MainAxisAlignment.SpaceAround,
                crossAxisAlignment = CrossAxisAlignment.Stretch
            ) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 40.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(2f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
            }
        }, @Composable {
            FlexRow(mainAxisAlignment = MainAxisAlignment.SpaceBetween) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 40.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(2f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
            }
        }, @Composable {
            FlexRow(mainAxisAlignment = MainAxisAlignment.SpaceEvenly) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 40.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(2f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(20.dp, 30.dp)) { }
                }
            }
        }) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width.
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                minIntrinsicWidth(0.ipx)
            )
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                minIntrinsicWidth(10.dp.toIntPx())
            )
            assertEquals(
                25.dp.toIntPx() * 2 / 2 * 7 + 20.dp.toIntPx(),
                minIntrinsicWidth(25.dp.toIntPx())
            )
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                minIntrinsicWidth(IntPx.Infinity)
            )
            // Min height.
            assertEquals(40.dp.toIntPx(), minIntrinsicHeight(0.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), minIntrinsicHeight(125.dp.toIntPx()))
            assertEquals(50.dp.toIntPx(), minIntrinsicHeight(370.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), minIntrinsicHeight(IntPx.Infinity))
            // Max width.
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                maxIntrinsicWidth(0.ipx)
            )
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                maxIntrinsicWidth(10.dp.toIntPx())
            )
            assertEquals(
                25.dp.toIntPx() * 2 / 2 * 7 + 20.dp.toIntPx(),
                maxIntrinsicWidth(25.dp.toIntPx())
            )
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                maxIntrinsicWidth(IntPx.Infinity)
            )
            // Max height.
            assertEquals(40.dp.toIntPx(), maxIntrinsicHeight(0.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), maxIntrinsicHeight(125.dp.toIntPx()))
            assertEquals(50.dp.toIntPx(), maxIntrinsicHeight(370.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), maxIntrinsicHeight(IntPx.Infinity))
        }
    }

    @Test
    fun testFlexColumn_hasCorrectIntrinsicMeasurements() = withDensity(density) {
        testIntrinsics(@Composable {
            FlexColumn {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(40.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(0.5f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
            }
        }, @Composable {
            FlexColumn(mainAxisSize = LayoutSize.Wrap) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(40.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(0.5f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
            }
        }, @Composable {
            FlexColumn(
                mainAxisAlignment = MainAxisAlignment.Start,
                crossAxisAlignment = CrossAxisAlignment.Start
            ) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(40.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(0.5f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
            }
        }, @Composable {
            FlexColumn(
                mainAxisAlignment = MainAxisAlignment.Center,
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(40.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(0.5f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
            }
        }, @Composable {
            FlexColumn(
                mainAxisAlignment = MainAxisAlignment.End,
                crossAxisAlignment = CrossAxisAlignment.End
            ) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(40.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(0.5f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
            }
        }, @Composable {
            FlexColumn(
                mainAxisAlignment = MainAxisAlignment.SpaceAround,
                crossAxisAlignment = CrossAxisAlignment.Stretch
            ) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(40.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(0.5f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
            }
        }, @Composable {
            FlexColumn(mainAxisAlignment = MainAxisAlignment.SpaceBetween) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(40.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(0.5f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
            }
        }, @Composable {
            FlexColumn(mainAxisAlignment = MainAxisAlignment.SpaceEvenly) {
                expanded(flex = 3f) {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
                expanded(flex = 2f) {
                    ConstrainedBox(DpConstraints.tightConstraints(40.dp, 30.dp)) { }
                }
                expanded(flex = 2f) {
                    Container(AspectRatio(0.5f)) { }
                }
                inflexible {
                    ConstrainedBox(DpConstraints.tightConstraints(30.dp, 20.dp)) { }
                }
            }
        }) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width.
            assertEquals(40.dp.toIntPx(), minIntrinsicWidth(0.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), minIntrinsicWidth(125.dp.toIntPx()))
            assertEquals(50.dp.toIntPx(), minIntrinsicWidth(370.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), minIntrinsicWidth(IntPx.Infinity))
            // Min height.
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                minIntrinsicHeight(0.ipx)
            )
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                minIntrinsicHeight(10.dp.toIntPx())
            )
            assertEquals(
                25.dp.toIntPx() * 2 / 2 * 7 + 20.dp.toIntPx(),
                minIntrinsicHeight(25.dp.toIntPx())
            )
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                minIntrinsicHeight(IntPx.Infinity)
            )
            // Max width.
            assertEquals(40.dp.toIntPx(), maxIntrinsicWidth(0.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), maxIntrinsicWidth(125.dp.toIntPx()))
            assertEquals(50.dp.toIntPx(), maxIntrinsicWidth(370.dp.toIntPx()))
            assertEquals(40.dp.toIntPx(), maxIntrinsicWidth(IntPx.Infinity))
            // Max height.
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                maxIntrinsicHeight(0.ipx)
            )
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                maxIntrinsicHeight(10.dp.toIntPx())
            )
            assertEquals(
                25.dp.toIntPx() * 2 / 2 * 7 + 20.dp.toIntPx(),
                maxIntrinsicHeight(25.dp.toIntPx())
            )
            assertEquals(
                30.dp.toIntPx() / 2 * 7 + 20.dp.toIntPx(),
                maxIntrinsicHeight(IntPx.Infinity)
            )
        }
    }

    @Test
    fun testRow_alignmentUsingAlignmentKey() = withDensity(density) {
        val TestAlignmentLine = HorizontalAlignmentLine(::min)
        val rowSize = Ref<PxSize>()
        val childPosition = arrayOf<Ref<PxPosition>>(Ref(), Ref(), Ref())
        val layoutLatch = CountDownLatch(4)
        show {
            Wrap {
                Row(crossAxisAlignment = CrossAxisAlignment.AlignmentLine(TestAlignmentLine)) {
                    SaveLayoutInfo(rowSize, Ref(), layoutLatch)
                    OnChildPositioned({ coordinates ->
                        childPosition[0].value = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                        layoutLatch.countDown()
                    }) {
                        FixedSizeLayout(10.ipx, 30.ipx, mapOf(TestAlignmentLine to 10.ipx))
                    }
                    OnChildPositioned({ coordinates ->
                        childPosition[1].value = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                        layoutLatch.countDown()
                    }) {
                        FixedSizeLayout(10.ipx, 10.ipx, mapOf())
                    }
                    OnChildPositioned({ coordinates ->
                        childPosition[2].value = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                        layoutLatch.countDown()
                    }) {
                        FixedSizeLayout(10.ipx, 30.ipx, mapOf(TestAlignmentLine to 20.ipx))
                    }
                }
            }
        }
        assertTrue(layoutLatch.await(1, TimeUnit.SECONDS))

        assertEquals(PxSize(30.ipx, 40.ipx), rowSize.value)
        assertEquals(PxPosition(0.ipx, 10.ipx), childPosition[0].value)
        assertEquals(PxPosition(10.ipx, 0.ipx), childPosition[1].value)
        assertEquals(PxPosition(20.ipx, 0.ipx), childPosition[2].value)
    }

    @Test
    fun testColumn_alignmentUsingAlignmentKey() = withDensity(density) {
        val TestAlignmentLine = VerticalAlignmentLine(::min)
        val columnSize = Ref<PxSize>()
        val childPosition = arrayOf<Ref<PxPosition>>(Ref(), Ref(), Ref())
        val layoutLatch = CountDownLatch(4)
        show {
            Wrap {
                Column(crossAxisAlignment = CrossAxisAlignment.AlignmentLine(TestAlignmentLine)) {
                    SaveLayoutInfo(columnSize, Ref(), layoutLatch)
                    OnChildPositioned({ coordinates ->
                        childPosition[0].value = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                        layoutLatch.countDown()
                    }) {
                        FixedSizeLayout(30.ipx, 10.ipx, mapOf(TestAlignmentLine to 10.ipx))
                    }
                    OnChildPositioned({ coordinates ->
                        childPosition[1].value = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                        layoutLatch.countDown()
                    }) {
                        FixedSizeLayout(10.ipx, 10.ipx, mapOf())
                    }
                    OnChildPositioned({ coordinates ->
                        childPosition[2].value = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                        layoutLatch.countDown()
                    }) {
                        FixedSizeLayout(30.ipx, 10.ipx, mapOf(TestAlignmentLine to 20.ipx))
                    }
                }
            }
        }
        assertTrue(layoutLatch.await(1, TimeUnit.SECONDS))

        assertEquals(PxSize(40.ipx, 30.ipx), columnSize.value)
        assertEquals(PxPosition(10.ipx, 0.ipx), childPosition[0].value)
        assertEquals(PxPosition(0.ipx, 10.ipx), childPosition[1].value)
        assertEquals(PxPosition(0.ipx, 20.ipx), childPosition[2].value)
    }

    @Test
    fun testFlexModifiersChain_leftMostWins() = withDensity(density) {
        val positionedLatch = CountDownLatch(1)
        val containerSize = Ref<PxSize>()
        val containerPosition = Ref<PxPosition>()
        val sizeIntPx = 40.dp.toIntPx()

        show {
            Align(Alignment.TopLeft) {
                Column(mainAxisSize = LayoutSize.Expand) {
                    OnChildPositioned(onPositioned = { coordinates ->
                        containerSize.value = coordinates.size
                        containerPosition.value = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                        positionedLatch.countDown()
                    }) {
                        Container(Inflexible wraps Flexible(1f), width = 40.dp, height = 40.dp) {}
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertNotNull(containerSize)
        assertEquals(PxSize(sizeIntPx, sizeIntPx), containerSize.value)
    }
}
