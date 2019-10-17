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
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.test.filters.LargeTest
import androidx.ui.core.Alignment
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.OnChildPositioned
import androidx.ui.core.PxPosition
import androidx.ui.core.dp
import androidx.ui.core.toPx
import androidx.ui.core.withDensity
import androidx.ui.foundation.ColoredRect
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.material.samples.ScrollingTextTabs
import androidx.ui.material.samples.TextTabs
import androidx.ui.material.surface.Surface
import androidx.ui.graphics.Image
import androidx.ui.graphics.ImageConfig
import androidx.ui.test.assertCountEquals
import androidx.ui.test.assertIsUnselected
import androidx.ui.test.assertIsSelected
import androidx.ui.test.createComposeRule
import androidx.ui.test.doClick
import androidx.ui.test.findAll
import androidx.ui.test.isInMutuallyExclusiveGroup
import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@LargeTest
@RunWith(JUnit4::class)
class TabTest {

    private val ExpectedSmallTabHeight = 48.dp
    private val ExpectedLargeTabHeight = 72.dp

    private val image = Image(
        width = 10,
        height = 10,
        config = ImageConfig.Argb8888,
        hasAlpha = false
    )

    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)

    @Test
    fun textTab_height() {
        composeTestRule
            .setMaterialContentAndCollectSizes {
                Container {
                    Surface {
                        Tab(text = "Text", selected = true, onSelected = {})
                    }
                }
            }
            .assertHeightEqualsTo(ExpectedSmallTabHeight)
    }

    @Test
    fun iconTab_height() {
        composeTestRule
            .setMaterialContentAndCollectSizes {
                Container {
                    Surface {
                        Tab(icon = image, selected = true, onSelected = {})
                    }
                }
            }
            .assertHeightEqualsTo(ExpectedSmallTabHeight)
    }

    @Test
    fun textAndIconTab_height() {
        composeTestRule
            .setMaterialContentAndCollectSizes {
                Container {
                    Surface {
                        Tab(text = "Text And Icon", icon = image, selected = true, onSelected = {})
                    }
                }
            }
            .assertHeightEqualsTo(ExpectedLargeTabHeight)
    }

    @Test
    fun fixedTabRow_indicatorPosition() {
        val indicatorHeight = 1.dp
        var tabRowCoords: LayoutCoordinates? = null
        var indicatorCoords: LayoutCoordinates? = null

        composeTestRule
            .setMaterialContent {
                // TODO: Go back to delegate syntax when b/141741358 is fixed
                val (state, setState) = +state { 0 }
                val titles = listOf("TAB 1", "TAB 2")

                val indicatorContainer = @Composable { tabPositions: List<TabRow.TabPosition> ->
                    TabRow.IndicatorContainer(tabPositions, state) {
                        OnChildPositioned({ indicatorCoords = it }) {
                            ColoredRect(Color.Red, height = indicatorHeight)
                        }
                    }
                }

                Container(alignment = Alignment.TopCenter) {
                    OnChildPositioned({ tabRowCoords = it }) {
                        TabRow(
                            items = titles,
                            selectedIndex = state,
                            indicatorContainer = indicatorContainer
                        ) { index, text ->
                            Tab(text = text, selected = state == index) {
                                setState(index)
                            }
                        }
                    }
                }
            }

        val tabRowWidth = tabRowCoords!!.size.width
        val tabRowHeight = tabRowCoords!!.size.height

        // Indicator should be placed in the bottom left of the first tab
        withDensity(composeTestRule.density) {
            val indicatorPositionX = indicatorCoords!!.localToGlobal(PxPosition.Origin).x
            val expectedPositionX = 0.dp.toPx()
            Truth.assertThat(indicatorPositionX).isEqualTo(expectedPositionX)

            val indicatorPositionY = indicatorCoords!!.localToGlobal(PxPosition.Origin).y
            val expectedPositionY = tabRowHeight - indicatorHeight.toIntPx().toPx()
            Truth.assertThat(indicatorPositionY).isEqualTo(expectedPositionY)
        }

        // Click the second tab
        findAll { isInMutuallyExclusiveGroup }[1].doClick()

        // TODO: we aren't correctly waiting for recompositions after clicking, so we need to wait
        // again
        findAll { isInMutuallyExclusiveGroup }

        // Indicator should now be placed in the bottom left of the second tab, so its x coordinate
        // should be in the middle of the TabRow
        withDensity(composeTestRule.density) {
            val indicatorPositionX = indicatorCoords!!.localToGlobal(PxPosition.Origin).x
            val expectedPositionX = tabRowWidth / 2
            Truth.assertThat(indicatorPositionX).isEqualTo(expectedPositionX)

            val indicatorPositionY = indicatorCoords!!.localToGlobal(PxPosition.Origin).y
            val expectedPositionY = tabRowHeight - indicatorHeight.toIntPx().toPx()
            Truth.assertThat(indicatorPositionY).isEqualTo(expectedPositionY)
        }
    }

    @Test
    fun scrollableTabRow_indicatorPosition() {
        val indicatorHeight = 1.dp
        val scrollableTabRowOffset = 52.dp
        val minimumTabWidth = 90.dp
        var tabRowCoords: LayoutCoordinates? = null
        var indicatorCoords: LayoutCoordinates? = null

        composeTestRule
            .setMaterialContent {
                // TODO: Go back to delegate syntax when b/141741358 is fixed
                val (state, setState) = +state { 0 }
                val titles = listOf("TAB 1", "TAB 2")

                val indicatorContainer = @Composable { tabPositions: List<TabRow.TabPosition> ->
                    TabRow.IndicatorContainer(tabPositions, state) {
                        OnChildPositioned({ indicatorCoords = it }) {
                            ColoredRect(Color.Red, height = indicatorHeight)
                        }
                    }
                }

                Container(alignment = Alignment.TopCenter) {
                    OnChildPositioned({ tabRowCoords = it }) {
                        TabRow(
                            items = titles,
                            scrollable = true,
                            selectedIndex = state,
                            indicatorContainer = indicatorContainer
                        ) { index, text ->
                            Tab(text = text, selected = state == index) {
                                setState(index)
                            }
                        }
                    }
                }
            }

        val tabRowHeight = tabRowCoords!!.size.height

        // Indicator is drawn in a recomposition, so wait until we recompose and are stable before
        // running assertions
        findAll { isInMutuallyExclusiveGroup }

        // Indicator should be placed in the bottom left of the first tab
        withDensity(composeTestRule.density) {
            val indicatorPositionX = indicatorCoords!!.localToGlobal(PxPosition.Origin).x
            // Tabs in a scrollable tab row are offset 52.dp from each end
            val expectedPositionX = scrollableTabRowOffset.toIntPx().toPx()
            Truth.assertThat(indicatorPositionX).isEqualTo(expectedPositionX)

            val indicatorPositionY = indicatorCoords!!.localToGlobal(PxPosition.Origin).y
            val expectedPositionY = tabRowHeight - indicatorHeight.toIntPx().toPx()
            Truth.assertThat(indicatorPositionY).isEqualTo(expectedPositionY)
        }

        // Click the second tab
        findAll { isInMutuallyExclusiveGroup }[1].doClick()

        // TODO: we aren't correctly waiting for recompositions after clicking, so we need to wait
        // again
        findAll { isInMutuallyExclusiveGroup }

        // Indicator should now be placed in the bottom left of the second tab, so its x coordinate
        // should be in the middle of the TabRow
        withDensity(composeTestRule.density) {
            val indicatorPositionX = indicatorCoords!!.localToGlobal(PxPosition.Origin).x
            val expectedPositionX = (scrollableTabRowOffset + minimumTabWidth).toIntPx().toPx()
            Truth.assertThat(indicatorPositionX).isEqualTo(expectedPositionX)

            val indicatorPositionY = indicatorCoords!!.localToGlobal(PxPosition.Origin).y
            val expectedPositionY = tabRowHeight - indicatorHeight.toIntPx().toPx()
            Truth.assertThat(indicatorPositionY).isEqualTo(expectedPositionY)
        }
    }

    @Test
    fun fixedTabRow_initialTabSelected() {
        composeTestRule
            .setMaterialContent {
                TextTabs()
            }

        findAll { isInMutuallyExclusiveGroup }.apply {
            forEachIndexed { index, interaction ->
                if (index == 0) {
                    interaction.assertIsSelected()
                } else {
                    interaction.assertIsUnselected()
                }
            }
        }.assertCountEquals(3)
    }

    @Test
    fun fixedTabRow_selectNewTab() {
        composeTestRule
            .setMaterialContent {
                TextTabs()
            }

        // Only the first tab should be selected
        findAll { isInMutuallyExclusiveGroup }.apply {
            forEachIndexed { index, interaction ->
                if (index == 0) {
                    interaction.assertIsSelected()
                } else {
                    interaction.assertIsUnselected()
                }
            }
        }.assertCountEquals(3)

        // Click the last tab
        findAll { isInMutuallyExclusiveGroup }.last().doClick()

        // Now only the last tab should be selected
        findAll { isInMutuallyExclusiveGroup }.apply {
            forEachIndexed { index, interaction ->
                if (index == lastIndex) {
                    interaction.assertIsSelected()
                } else {
                    interaction.assertIsUnselected()
                }
            }
        }.assertCountEquals(3)
    }

    @Test
    fun scrollableTabRow_initialTabSelected() {
        composeTestRule
            .setMaterialContent {
                ScrollingTextTabs()
            }

        findAll { isInMutuallyExclusiveGroup }.apply {
            forEachIndexed { index, interaction ->
                if (index == 0) {
                    interaction.assertIsSelected()
                } else {
                    interaction.assertIsUnselected()
                }
            }
        }.assertCountEquals(10)
    }

    @Test
    fun scrollableTabRow_selectNewTab() {
        composeTestRule
            .setMaterialContent {
                ScrollingTextTabs()
            }

        // Only the first tab should be selected
        findAll { isInMutuallyExclusiveGroup }.apply {
            forEachIndexed { index, interaction ->
                if (index == 0) {
                    interaction.assertIsSelected()
                } else {
                    interaction.assertIsUnselected()
                }
            }
        }.assertCountEquals(10)

        // Click the second tab
        findAll { isInMutuallyExclusiveGroup }[1].doClick()

        // Now only the second tab should be selected
        findAll { isInMutuallyExclusiveGroup }.apply {
            forEachIndexed { index, interaction ->
                if (index == 1) {
                    interaction.assertIsSelected()
                } else {
                    interaction.assertIsUnselected()
                }
            }
        }.assertCountEquals(10)
    }
}
