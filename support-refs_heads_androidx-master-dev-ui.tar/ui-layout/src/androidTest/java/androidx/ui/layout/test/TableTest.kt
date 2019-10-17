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

import androidx.compose.Composable
import androidx.test.filters.SmallTest
import androidx.ui.core.Alignment
import androidx.ui.core.Density
import androidx.ui.core.IntPx
import androidx.ui.core.OnChildPositioned
import androidx.ui.core.PxPosition
import androidx.ui.core.PxSize
import androidx.ui.core.Ref
import androidx.ui.core.coerceAtLeast
import androidx.ui.core.ipx
import androidx.ui.core.max
import androidx.ui.core.min
import androidx.ui.core.withDensity
import androidx.ui.layout.Align
import androidx.ui.layout.AspectRatio
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.Table
import androidx.ui.layout.TableColumnWidth
import androidx.ui.layout.TableMeasurable
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.pow

@SmallTest
@RunWith(JUnit4::class)
class TableTest : LayoutTest() {
    @Test
    fun testTable() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()
        val tableWidth = 256.ipx
        val tableWidthDp = tableWidth.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                ConstrainedBox(constraints = DpConstraints(maxWidth = tableWidthDp)) {
                    OnChildPositioned(onPositioned = { coordinates ->
                        tableSize.value = coordinates.size
                        positionedLatch.countDown()
                    }) {
                        Table(columns = columns) {
                            for (i in 0 until rows) {
                                tableRow {
                                    for (j in 0 until columns) {
                                        Container(height = sizeDp, expanded = true) {
                                            SaveLayoutInfo(
                                                size = childSize[i][j],
                                                position = childPosition[i][j],
                                                positionedLatch = positionedLatch
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(tableWidth, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(tableWidth / columns, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(tableWidth * j / columns, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_flex() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()
        val tableWidth = 256.ipx
        val tableWidthDp = tableWidth.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        val flexes = Array(columns) { j -> 2f.pow(max(j - 1, 0)) }
        val totalFlex = flexes.sum()

        show {
            Align(Alignment.TopLeft) {
                ConstrainedBox(constraints = DpConstraints(maxWidth = tableWidthDp)) {
                    OnChildPositioned(onPositioned = { coordinates ->
                        tableSize.value = coordinates.size
                        positionedLatch.countDown()
                    }) {
                        Table(columns = columns, columnWidth = { j ->
                            TableColumnWidth.Flex(flex = flexes[j])
                        }) {
                            for (i in 0 until rows) {
                                tableRow {
                                    for (j in 0 until columns) {
                                        Container(height = sizeDp, expanded = true) {
                                            SaveLayoutInfo(
                                                size = childSize[i][j],
                                                position = childPosition[i][j],
                                                positionedLatch = positionedLatch
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(tableWidth, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(tableWidth * flexes[j] / totalFlex, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(tableWidth * flexes.take(j).sum() / totalFlex, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_wrap() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    tableSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Table(columns = columns, columnWidth = { TableColumnWidth.Wrap }) {
                        for (i in 0 until rows) {
                            tableRow {
                                for (j in 0 until columns) {
                                    Container(width = sizeDp, height = sizeDp) {
                                        SaveLayoutInfo(
                                            size = childSize[i][j],
                                            position = childPosition[i][j],
                                            positionedLatch = positionedLatch
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(size * columns, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(size, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(size * j, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_wrap_flexible() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 32.ipx
        val sizeDp = size.toDp()
        val tableWidth = 256.ipx
        val tableWidthDp = tableWidth.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        val flexes = Array(columns) { j -> 2f.pow(max(j - 1, 0)) }
        val totalFlex = flexes.sum()

        show {
            Align(Alignment.TopLeft) {
                ConstrainedBox(constraints = DpConstraints(maxWidth = tableWidthDp)) {
                    OnChildPositioned(onPositioned = { coordinates ->
                        tableSize.value = coordinates.size
                        positionedLatch.countDown()
                    }) {
                        Table(columns = columns, columnWidth = { j ->
                            TableColumnWidth.Wrap.flexible(flex = flexes[j])
                        }) {
                            for (i in 0 until rows) {
                                tableRow {
                                    for (j in 0 until columns) {
                                        Container(width = sizeDp, height = sizeDp) {
                                            SaveLayoutInfo(
                                                size = childSize[i][j],
                                                position = childPosition[i][j],
                                                positionedLatch = positionedLatch
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        val availableSpace = (tableWidth - size * columns).coerceAtLeast(IntPx.Zero)

        assertEquals(
            PxSize(size * columns, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(size, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(
                        size * j + availableSpace * flexes.take(j).sum() / totalFlex,
                        size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_minIntrinsic() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    tableSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Table(
                        columns = columns,
                        columnWidth = { TableColumnWidth.MinIntrinsic }
                    ) {
                        for (i in 0 until rows) {
                            tableRow {
                                for (j in 0 until columns) {
                                    Container(width = sizeDp, height = sizeDp) {
                                        SaveLayoutInfo(
                                            size = childSize[i][j],
                                            position = childPosition[i][j],
                                            positionedLatch = positionedLatch
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(size * columns, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(size, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(size * j, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_maxIntrinsic() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    tableSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Table(
                        columns = columns,
                        columnWidth = { TableColumnWidth.MaxIntrinsic }
                    ) {
                        for (i in 0 until rows) {
                            tableRow {
                                for (j in 0 until columns) {
                                    Container(width = sizeDp, height = sizeDp) {
                                        SaveLayoutInfo(
                                            size = childSize[i][j],
                                            position = childPosition[i][j],
                                            positionedLatch = positionedLatch
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(size * columns, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(size, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(size * j, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_fixed() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    tableSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Table(
                        columns = columns,
                        columnWidth = { TableColumnWidth.Fixed(width = sizeDp) }
                    ) {
                        for (i in 0 until rows) {
                            tableRow {
                                for (j in 0 until columns) {
                                    Container(height = sizeDp, expanded = true) {
                                        SaveLayoutInfo(
                                            size = childSize[i][j],
                                            position = childPosition[i][j],
                                            positionedLatch = positionedLatch
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(size * columns, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(size, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(size * j, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_fraction() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()
        val tableWidth = 256.ipx
        val tableWidthDp = tableWidth.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        val fractions = Array(columns) { j -> 1 / 2f.pow(j + 1) }

        show {
            Align(Alignment.TopLeft) {
                ConstrainedBox(constraints = DpConstraints(maxWidth = tableWidthDp)) {
                    OnChildPositioned(onPositioned = { coordinates ->
                        tableSize.value = coordinates.size
                        positionedLatch.countDown()
                    }) {
                        Table(columns = columns, columnWidth = { j ->
                            TableColumnWidth.Fraction(fraction = fractions[j])
                        }) {
                            for (i in 0 until rows) {
                                tableRow {
                                    for (j in 0 until columns) {
                                        Container(height = sizeDp, expanded = true) {
                                            SaveLayoutInfo(
                                                size = childSize[i][j],
                                                position = childPosition[i][j],
                                                positionedLatch = positionedLatch
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(tableWidth * fractions.sum(), size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(tableWidth * fractions[j], size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(tableWidth * fractions.take(j).sum(), size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_min() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()
        val minWidth = 24.ipx
        val minWidthDp = minWidth.toDp()
        val tableWidth = 256.ipx
        val tableWidthDp = tableWidth.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                ConstrainedBox(constraints = DpConstraints(maxWidth = tableWidthDp)) {
                    OnChildPositioned(onPositioned = { coordinates ->
                        tableSize.value = coordinates.size
                        positionedLatch.countDown()
                    }) {
                        Table(columns = columns, columnWidth = { j ->
                            TableColumnWidth.Min(
                                a = TableColumnWidth.Fixed(width = minWidthDp),
                                b = TableColumnWidth.Fraction(
                                    fraction = if (j % 2 == 0) 1f / columns else 1f / (columns * 2)
                                )
                            )
                        }) {
                            for (i in 0 until rows) {
                                tableRow {
                                    for (j in 0 until columns) {
                                        Container(height = sizeDp, expanded = true) {
                                            SaveLayoutInfo(
                                                size = childSize[i][j],
                                                position = childPosition[i][j],
                                                positionedLatch = positionedLatch
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        val expectedWidths = Array(columns) { j ->
            min(minWidth, if (j % 2 == 0) tableWidth / columns else tableWidth / (columns * 2))
        }

        assertEquals(
            PxSize(expectedWidths.sum(), size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(expectedWidths[j], size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(expectedWidths.take(j).sum(), size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_max() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()
        val maxWidth = 24.ipx
        val maxWidthDp = maxWidth.toDp()
        val tableWidth = 256.ipx
        val tableWidthDp = tableWidth.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                ConstrainedBox(constraints = DpConstraints(maxWidth = tableWidthDp)) {
                    OnChildPositioned(onPositioned = { coordinates ->
                        tableSize.value = coordinates.size
                        positionedLatch.countDown()
                    }) {
                        Table(columns = columns, columnWidth = { j ->
                            TableColumnWidth.Max(
                                a = TableColumnWidth.Fixed(width = maxWidthDp),
                                b = TableColumnWidth.Fraction(
                                    fraction = if (j % 2 == 0) 1f / columns else 1f / (columns * 2)
                                )
                            )
                        }) {
                            for (i in 0 until rows) {
                                tableRow {
                                    for (j in 0 until columns) {
                                        Container(height = sizeDp, expanded = true) {
                                            SaveLayoutInfo(
                                                size = childSize[i][j],
                                                position = childPosition[i][j],
                                                positionedLatch = positionedLatch
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        val expectedWidths = Array(columns) { j ->
            max(maxWidth, if (j % 2 == 0) tableWidth / columns else tableWidth / (columns * 2))
        }

        assertEquals(
            PxSize(expectedWidths.sum(), size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(expectedWidths[j], size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(expectedWidths.take(j).sum(), size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_min_oneWrap() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()
        val halfSize = 32.ipx
        val halfSizeDp = halfSize.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    tableSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Table(columns = columns, columnWidth = {
                        TableColumnWidth.Min(
                            a = TableColumnWidth.Wrap,
                            b = TableColumnWidth.Fixed(width = sizeDp)
                        )
                    }) {
                        for (i in 0 until rows) {
                            tableRow {
                                for (j in 0 until columns) {
                                    Container(width = halfSizeDp, height = sizeDp) {
                                        SaveLayoutInfo(
                                            size = childSize[i][j],
                                            position = childPosition[i][j],
                                            positionedLatch = positionedLatch
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(halfSize * columns, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(halfSize, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(halfSize * j, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_max_oneWrap() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()
        val halfSize = 32.ipx
        val halfSizeDp = halfSize.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    tableSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Table(columns = columns, columnWidth = {
                        TableColumnWidth.Max(
                            a = TableColumnWidth.Wrap,
                            b = TableColumnWidth.Fixed(width = sizeDp)
                        )
                    }) {
                        for (i in 0 until rows) {
                            tableRow {
                                for (j in 0 until columns) {
                                    Container(width = halfSizeDp, height = sizeDp) {
                                        SaveLayoutInfo(
                                            size = childSize[i][j],
                                            position = childPosition[i][j],
                                            positionedLatch = positionedLatch
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(size * columns, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(halfSize, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(size * j, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_min_twoWraps() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    tableSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Table(columns = columns, columnWidth = {
                        TableColumnWidth.Min(TableColumnWidth.Wrap, TableColumnWidth.Wrap)
                    }) {
                        for (i in 0 until rows) {
                            tableRow {
                                for (j in 0 until columns) {
                                    Container(width = sizeDp, height = sizeDp) {
                                        SaveLayoutInfo(
                                            size = childSize[i][j],
                                            position = childPosition[i][j],
                                            positionedLatch = positionedLatch
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(size * columns, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(size, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(size * j, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_max_twoWraps() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    tableSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Table(columns = columns, columnWidth = {
                        TableColumnWidth.Max(TableColumnWidth.Wrap, TableColumnWidth.Wrap)
                    }) {
                        for (i in 0 until rows) {
                            tableRow {
                                for (j in 0 until columns) {
                                    Container(width = sizeDp, height = sizeDp) {
                                        SaveLayoutInfo(
                                            size = childSize[i][j],
                                            position = childPosition[i][j],
                                            positionedLatch = positionedLatch
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(size * columns, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(size, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(size * j, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withColumnWidth_custom() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()
        val halfSize = 32.ipx
        val halfSizeDp = halfSize.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        val customSpec = object : TableColumnWidth.Inflexible() {
            override fun preferredWidth(
                cells: List<TableMeasurable>,
                containerWidth: IntPx,
                density: Density
            ): IntPx {
                return cells.first().preferredWidth()
            }
        }

        show {
            Align(Alignment.TopLeft) {
                OnChildPositioned(onPositioned = { coordinates ->
                    tableSize.value = coordinates.size
                    positionedLatch.countDown()
                }) {
                    Table(columns = columns, columnWidth = { customSpec }) {
                        for (i in 0 until rows) {
                            tableRow {
                                for (j in 0 until columns) {
                                    Container(
                                        width = if (i == 0) sizeDp else halfSizeDp,
                                        height = sizeDp
                                    ) {
                                        SaveLayoutInfo(
                                            size = childSize[i][j],
                                            position = childPosition[i][j],
                                            positionedLatch = positionedLatch
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(size * columns, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(if (i == 0) size else halfSize, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(size * j, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withDifferentRowHeights() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()
        val halfSize = 32.ipx
        val halfSizeDp = halfSize.toDp()
        val tableWidth = 256.ipx
        val tableWidthDp = tableWidth.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                ConstrainedBox(constraints = DpConstraints(maxWidth = tableWidthDp)) {
                    OnChildPositioned(onPositioned = { coordinates ->
                        tableSize.value = coordinates.size
                        positionedLatch.countDown()
                    }) {
                        Table(columns = columns) {
                            for (i in 0 until rows) {
                                tableRow {
                                    for (j in 0 until columns) {
                                        Container(
                                            height = if (j % 2 == 0) sizeDp else halfSizeDp,
                                            expanded = true
                                        ) {
                                            SaveLayoutInfo(
                                                size = childSize[i][j],
                                                position = childPosition[i][j],
                                                positionedLatch = positionedLatch
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(tableWidth, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(tableWidth / columns, if (j % 2 == 0) size else halfSize),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(tableWidth * j / columns, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_withDifferentColumnWidths() = withDensity(density) {
        val rows = 8
        val columns = 5

        val size = 64.ipx
        val sizeDp = size.toDp()
        val halfSize = 32.ipx
        val halfSizeDp = halfSize.toDp()
        val tableWidth = 256.ipx
        val tableWidthDp = tableWidth.toDp()

        val tableSize = Ref<PxSize>()
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + 1)

        show {
            Align(Alignment.TopLeft) {
                ConstrainedBox(constraints = DpConstraints(maxWidth = tableWidthDp)) {
                    OnChildPositioned(onPositioned = { coordinates ->
                        tableSize.value = coordinates.size
                        positionedLatch.countDown()
                    }) {
                        Table(columns = columns, columnWidth = { j ->
                            when (j) {
                                0 -> TableColumnWidth.Wrap
                                1 -> TableColumnWidth.Flex(flex = 1f)
                                2 -> TableColumnWidth.Flex(flex = 3f)
                                3 -> TableColumnWidth.Fixed(width = sizeDp)
                                else -> TableColumnWidth.Fraction(fraction = 0.5f)
                            }
                        }) {
                            for (i in 0 until rows) {
                                tableRow {
                                    for (j in 0 until columns) {
                                        Container(
                                            width = if (j == 0) halfSizeDp else null,
                                            height = sizeDp,
                                            expanded = j != 0
                                        ) {
                                            SaveLayoutInfo(
                                                size = childSize[i][j],
                                                position = childPosition[i][j],
                                                positionedLatch = positionedLatch
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(tableWidth, size * rows),
            tableSize.value
        )
        for (i in 0 until rows) {
            // Wrap column 0
            assertEquals(
                PxSize(halfSize, size),
                childSize[i][0].value
            )
            assertEquals(
                PxPosition(0.ipx, size * i),
                childPosition[i][0].value
            )
            // Flex column 1
            assertEquals(
                PxSize((tableWidth / 2 - size - halfSize) / 4, size),
                childSize[i][1].value
            )
            assertEquals(
                PxPosition(halfSize, size * i),
                childPosition[i][1].value
            )
            // Flex column 2
            assertEquals(
                PxSize((tableWidth / 2 - size - halfSize) * 3 / 4, size),
                childSize[i][2].value
            )
            assertEquals(
                PxPosition(halfSize + (tableWidth / 2 - size - halfSize) / 4, size * i),
                childPosition[i][2].value
            )
            // Fixed column 3
            assertEquals(
                PxSize(size, size),
                childSize[i][3].value
            )
            assertEquals(
                PxPosition(tableWidth / 2 - size, size * i),
                childPosition[i][3].value
            )
            // Fraction column 4
            assertEquals(
                PxSize(tableWidth / 2, size),
                childSize[i][4].value
            )
            assertEquals(
                PxPosition(tableWidth / 2, size * i),
                childPosition[i][4].value
            )
        }
    }

    @Test
    fun testTable_withDecorations() = withDensity(density) {
        val rows = 8
        val columns = 8
        val decorations = 3

        val size = 64.ipx
        val sizeDp = size.toDp()
        val tableWidth = 256.ipx
        val tableWidthDp = tableWidth.toDp()

        val tableSize = Ref<PxSize>()
        val decorationSize = Array(decorations) { Ref<PxSize>() }
        val decorationPosition = Array(decorations) { Ref<PxPosition>() }
        val childSize = Array(rows) { Array(columns) { Ref<PxSize>() } }
        val childPosition = Array(rows) { Array(columns) { Ref<PxPosition>() } }
        val positionedLatch = CountDownLatch(rows * columns + decorations + 1)

        show {
            Align(Alignment.TopLeft) {
                ConstrainedBox(constraints = DpConstraints(maxWidth = tableWidthDp)) {
                    OnChildPositioned(onPositioned = { coordinates ->
                        tableSize.value = coordinates.size
                        positionedLatch.countDown()
                    }) {
                        Table(columns = columns) {
                            for (i in 0 until decorations) {
                                tableDecoration(overlay = true) {
                                    Container {
                                        SaveLayoutInfo(
                                            size = decorationSize[i],
                                            position = decorationPosition[i],
                                            positionedLatch = positionedLatch
                                        )
                                    }
                                }
                            }
                            for (i in 0 until rows) {
                                tableRow {
                                    for (j in 0 until columns) {
                                        Container(height = sizeDp, expanded = true) {
                                            SaveLayoutInfo(
                                                size = childSize[i][j],
                                                position = childPosition[i][j],
                                                positionedLatch = positionedLatch
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        positionedLatch.await(1, TimeUnit.SECONDS)

        assertEquals(
            PxSize(tableWidth, size * rows),
            tableSize.value
        )
        for (i in 0 until decorations) {
            assertEquals(
                PxSize(tableWidth, size * rows),
                decorationSize[i].value
            )
            assertEquals(
                PxPosition(IntPx.Zero, IntPx.Zero),
                decorationPosition[i].value
            )
        }
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                assertEquals(
                    PxSize(tableWidth / columns, size),
                    childSize[i][j].value
                )
                assertEquals(
                    PxPosition(tableWidth * j / columns, size * i),
                    childPosition[i][j].value
                )
            }
        }
    }

    @Test
    fun testTable_hasCorrectIntrinsicMeasurements_flex() = withDensity(density) {
        val rows = 8
        val columns = 8

        val testDimension = 256.ipx

        testIntrinsics(
            @Composable {
                Table(
                    columns = columns,
                    columnWidth = { TableColumnWidth.Flex(flex = 1f) }
                ) {
                    for (i in 0 until rows) {
                        tableRow {
                            for (j in 0 until columns) {
                                Container(AspectRatio(2f)) { }
                            }
                        }
                    }
                }
            }
        ) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Zero))
            assertEquals(0.ipx, minIntrinsicWidth(testDimension))
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Infinity))

            // Min height
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Zero))
            assertEquals(testDimension / columns / 2 * rows, minIntrinsicHeight(testDimension))
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Infinity))

            // Max width
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Zero))
            assertEquals(0.ipx, maxIntrinsicWidth(testDimension))
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Infinity))

            // Max height
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Zero))
            assertEquals(testDimension / columns / 2 * rows, maxIntrinsicHeight(testDimension))
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Infinity))
        }
    }

    @Test
    fun testTable_hasCorrectIntrinsicMeasurements_wrap() = withDensity(density) {
        val rows = 8
        val columns = 8

        val testDimension = 256.ipx

        testIntrinsics(
            @Composable {
                Table(
                    columns = columns,
                    columnWidth = { TableColumnWidth.Wrap }
                ) {
                    for (i in 0 until rows) {
                        tableRow {
                            for (j in 0 until columns) {
                                Container(AspectRatio(2f)) { }
                            }
                        }
                    }
                }
            }
        ) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Zero))
            assertEquals(testDimension * columns * 2 / rows, minIntrinsicWidth(testDimension))
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Infinity))

            // Min height
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Zero))
            assertEquals(0.ipx, minIntrinsicHeight(testDimension))
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Infinity))

            // Max width
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Zero))
            assertEquals(testDimension * columns * 2 / rows, maxIntrinsicWidth(testDimension))
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Infinity))

            // Max height
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Zero))
            assertEquals(0.ipx, maxIntrinsicHeight(testDimension))
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Infinity))
        }
    }

    @Test
    fun testTable_hasCorrectIntrinsicMeasurements_wrap_flexible() = withDensity(density) {
        val rows = 8
        val columns = 8

        val testDimension = 256.ipx

        testIntrinsics(
            @Composable {
                Table(
                    columns = columns,
                    columnWidth = { TableColumnWidth.Wrap.flexible(flex = 1f) }
                ) {
                    for (i in 0 until rows) {
                        tableRow {
                            for (j in 0 until columns) {
                                Container(AspectRatio(2f)) { }
                            }
                        }
                    }
                }
            }
        ) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Zero))
            assertEquals(testDimension * columns * 2 / rows, minIntrinsicWidth(testDimension))
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Infinity))

            // Min height
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Zero))
            assertEquals(testDimension / columns / 2 * rows, minIntrinsicHeight(testDimension))
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Infinity))

            // Max width
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Zero))
            assertEquals(testDimension * columns * 2 / rows, maxIntrinsicWidth(testDimension))
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Infinity))

            // Max height
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Zero))
            assertEquals(testDimension / columns / 2 * rows, maxIntrinsicHeight(testDimension))
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Infinity))
        }
    }

    @Test
    fun testTable_hasCorrectIntrinsicMeasurements_minIntrinsic() = withDensity(density) {
        val rows = 8
        val columns = 8

        val testDimension = 256.ipx

        testIntrinsics(
            @Composable {
                Table(
                    columns = columns,
                    columnWidth = { TableColumnWidth.MinIntrinsic }
                ) {
                    for (i in 0 until rows) {
                        tableRow {
                            for (j in 0 until columns) {
                                Container(AspectRatio(2f)) { }
                            }
                        }
                    }
                }
            }
        ) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Zero))
            assertEquals(testDimension * columns * 2 / rows, minIntrinsicWidth(testDimension))
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Infinity))

            // Min height
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Zero))
            assertEquals(0.ipx, minIntrinsicHeight(testDimension))
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Infinity))

            // Max width
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Zero))
            assertEquals(testDimension * columns * 2 / rows, maxIntrinsicWidth(testDimension))
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Infinity))

            // Max height
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Zero))
            assertEquals(0.ipx, maxIntrinsicHeight(testDimension))
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Infinity))
        }
    }

    @Test
    fun testTable_hasCorrectIntrinsicMeasurements_maxIntrinsic() = withDensity(density) {
        val rows = 8
        val columns = 8

        val testDimension = 256.ipx

        testIntrinsics(
            @Composable {
                Table(
                    columns = columns,
                    columnWidth = { TableColumnWidth.MaxIntrinsic }
                ) {
                    for (i in 0 until rows) {
                        tableRow {
                            for (j in 0 until columns) {
                                Container(AspectRatio(2f)) { }
                            }
                        }
                    }
                }
            }
        ) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Zero))
            assertEquals(testDimension * columns * 2 / rows, minIntrinsicWidth(testDimension))
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Infinity))

            // Min height
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Zero))
            assertEquals(0.ipx, minIntrinsicHeight(testDimension))
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Infinity))

            // Max width
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Zero))
            assertEquals(testDimension * columns * 2 / rows, maxIntrinsicWidth(testDimension))
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Infinity))

            // Max height
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Zero))
            assertEquals(0.ipx, maxIntrinsicHeight(testDimension))
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Infinity))
        }
    }

    @Test
    fun testTable_hasCorrectIntrinsicMeasurements_fixed() = withDensity(density) {
        val rows = 8
        val columns = 8

        val size = 64.ipx
        val sizeDp = size.toDp()
        val testDimension = 256.ipx

        testIntrinsics(
            @Composable {
                Table(
                    columns = columns,
                    columnWidth = { TableColumnWidth.Fixed(width = sizeDp) }
                ) {
                    for (i in 0 until rows) {
                        tableRow {
                            for (j in 0 until columns) {
                                Container(AspectRatio(2f)) { }
                            }
                        }
                    }
                }
            }
        ) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width
            assertEquals(size * 8, minIntrinsicWidth(IntPx.Zero))
            assertEquals(size * 8, minIntrinsicWidth(testDimension))
            assertEquals(size * 8, minIntrinsicWidth(IntPx.Infinity))

            // Min height
            assertEquals(size * 4, minIntrinsicHeight(IntPx.Zero))
            assertEquals(size * 4, minIntrinsicHeight(testDimension))
            assertEquals(size * 4, minIntrinsicHeight(IntPx.Infinity))

            // Max width
            assertEquals(size * 8, maxIntrinsicWidth(IntPx.Zero))
            assertEquals(size * 8, maxIntrinsicWidth(testDimension))
            assertEquals(size * 8, maxIntrinsicWidth(IntPx.Infinity))

            // Max height
            assertEquals(size * 4, maxIntrinsicHeight(IntPx.Zero))
            assertEquals(size * 4, maxIntrinsicHeight(testDimension))
            assertEquals(size * 4, maxIntrinsicHeight(IntPx.Infinity))
        }
    }

    @Test
    fun testTable_hasCorrectIntrinsicMeasurements_fraction() = withDensity(density) {
        val rows = 8
        val columns = 8

        val testDimension = 256.ipx

        testIntrinsics(
            @Composable {
                Table(
                    columns = columns,
                    columnWidth = { TableColumnWidth.Fraction(fraction = 1f / columns) }
                ) {
                    for (i in 0 until rows) {
                        tableRow {
                            for (j in 0 until columns) {
                                Container(AspectRatio(2f)) { }
                            }
                        }
                    }
                }
            }
        ) { minIntrinsicWidth, minIntrinsicHeight, maxIntrinsicWidth, maxIntrinsicHeight ->
            // Min width
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Zero))
            assertEquals(0.ipx, minIntrinsicWidth(testDimension))
            assertEquals(0.ipx, minIntrinsicWidth(IntPx.Infinity))

            // Min height
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Zero))
            assertEquals(testDimension / 2, minIntrinsicHeight(testDimension))
            assertEquals(0.ipx, minIntrinsicHeight(IntPx.Infinity))

            // Max width
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Zero))
            assertEquals(0.ipx, maxIntrinsicWidth(testDimension))
            assertEquals(0.ipx, maxIntrinsicWidth(IntPx.Infinity))

            // Max height
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Zero))
            assertEquals(testDimension / 2, maxIntrinsicHeight(testDimension))
            assertEquals(0.ipx, maxIntrinsicHeight(IntPx.Infinity))
        }
    }

    private fun Array<IntPx>.sum() = this.fold(IntPx.Zero) { a, b -> a + b }
    private fun Collection<IntPx>.sum() = this.fold(IntPx.Zero) { a, b -> a + b }
}