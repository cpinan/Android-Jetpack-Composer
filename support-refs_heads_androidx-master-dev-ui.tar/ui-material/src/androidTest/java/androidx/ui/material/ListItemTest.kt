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
import androidx.test.filters.SmallTest
import androidx.ui.core.Alignment
import androidx.ui.core.Dp
import androidx.ui.core.FirstBaseline
import androidx.ui.core.OnChildPositioned
import androidx.ui.core.Px
import androidx.ui.core.PxPosition
import androidx.ui.core.PxSize
import androidx.ui.core.Ref
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.ipx
import androidx.ui.core.px
import androidx.ui.core.round
import androidx.ui.core.toPx
import androidx.ui.core.withDensity
import androidx.ui.foundation.SimpleImage
import androidx.ui.graphics.Image
import androidx.ui.layout.Container
import androidx.ui.test.createComposeRule
import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.math.roundToInt

@SmallTest
@RunWith(JUnit4::class)
class ListItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val icon24x24 by lazy { Image(width = 24.dp.toIntPx(), height = 24.dp.toIntPx()) }
    val icon40x40 by lazy { Image(width = 40.dp.toIntPx(), height = 40.dp.toIntPx()) }
    val icon56x56 by lazy { Image(width = 56.dp.toIntPx(), height = 56.dp.toIntPx()) }

    @Test
    fun listItem_oneLine_size() {
        val dm = composeTestRule.displayMetrics
        val expectedHeightNoIcon = 48.dp
        val expectedHeightSmallIcon = 56.dp
        val expectedHeightLargeIcon = 72.dp
        composeTestRule
            .setMaterialContentAndCollectSizes {
                ListItem(text = "Primary text")
            }
            .assertHeightEqualsTo(expectedHeightNoIcon)
            .assertWidthEqualsTo { dm.widthPixels.ipx }
        composeTestRule
            .setMaterialContentAndCollectSizes {
                ListItem(text = "Primary text", icon = icon24x24)
            }
            .assertHeightEqualsTo(expectedHeightSmallIcon)
            .assertWidthEqualsTo { dm.widthPixels.ipx }
        composeTestRule
            .setMaterialContentAndCollectSizes {
                ListItem(text = "Primary text", icon = icon56x56)
            }
            .assertHeightEqualsTo(expectedHeightLargeIcon)
            .assertWidthEqualsTo { dm.widthPixels.ipx }
    }

    @Test
    fun listItem_twoLine_size() {
        val dm = composeTestRule.displayMetrics
        val expectedHeightNoIcon = 64.dp
        val expectedHeightWithIcon = 72.dp
        composeTestRule
            .setMaterialContentAndCollectSizes {
                ListItem(text = "Primary text", secondaryText = "Secondary text")
            }
            .assertHeightEqualsTo(expectedHeightNoIcon)
            .assertWidthEqualsTo { dm.widthPixels.ipx }
        composeTestRule
            .setMaterialContentAndCollectSizes {
                ListItem(
                    text = "Primary text",
                    secondaryText = "Secondary text",
                    icon = icon24x24
                )
            }
            .assertHeightEqualsTo(expectedHeightWithIcon)
            .assertWidthEqualsTo { dm.widthPixels.ipx }
    }

    @Test
    fun listItem_threeLine_size() {
        val dm = composeTestRule.displayMetrics
        val expectedHeight = 88.dp
        composeTestRule
            .setMaterialContentAndCollectSizes {
                ListItem(
                    overlineText = "OVERLINE",
                    text = "Primary text",
                    secondaryText = "Secondary text"
                )
            }
            .assertHeightEqualsTo(expectedHeight)
            .assertWidthEqualsTo { dm.widthPixels.ipx }
        composeTestRule
            .setMaterialContentAndCollectSizes {
                ListItem(
                    text = "Primary text",
                    secondaryText = "Secondary text with long text",
                    singleLineSecondaryText = false
                )
            }
            .assertHeightEqualsTo(expectedHeight)
            .assertWidthEqualsTo { dm.widthPixels.ipx }
        composeTestRule
            .setMaterialContentAndCollectSizes {
                ListItem(
                    overlineText = "OVERLINE",
                    text = "Primary text",
                    secondaryText = "Secondary text",
                    metaText = "meta"
                )
            }
            .assertHeightEqualsTo(expectedHeight)
            .assertWidthEqualsTo { dm.widthPixels.ipx }
        composeTestRule
            .setMaterialContentAndCollectSizes {
                ListItem(
                    text = "Primary text",
                    secondaryText = "Secondary text with long text",
                    singleLineSecondaryText = false,
                    metaText = "meta"
                )
            }
            .assertHeightEqualsTo(expectedHeight)
            .assertWidthEqualsTo { dm.widthPixels.ipx }
    }

    @Test
    fun listItem_oneLine_positioning_noIcon() {
        val listItemHeight = 48.dp
        val expectedLeftPadding = 16.dp
        val expectedRightPadding = 16.dp

        val textPosition = Ref<PxPosition>()
        val textSize = Ref<PxSize>()
        val trailingPosition = Ref<PxPosition>()
        val trailingSize = Ref<PxSize>()
        composeTestRule.setMaterialContent {
            Container(alignment = Alignment.TopLeft) {
                ListItem(
                    text = { SaveLayout(textPosition, textSize) { Text("Primary text") } },
                    trailing = {
                        SaveLayout(trailingPosition, trailingSize) { SimpleImage(icon24x24) }
                    }
                )
            }
        }
        withDensity(composeTestRule.density) {
            Truth.assertThat(textPosition.value!!.x).isEqualTo(expectedLeftPadding.toIntPx().toPx())
            Truth.assertThat(textPosition.value!!.y).isEqualTo(
                ((listItemHeight.toIntPx() - textSize.value!!.height.round()) / 2).toPx()
            )
            val dm = composeTestRule.displayMetrics
            Truth.assertThat(trailingPosition.value!!.x).isEqualTo(
                dm.widthPixels.px - trailingSize.value!!.width -
                        expectedRightPadding.toIntPx().toPx()
            )
            Truth.assertThat(trailingPosition.value!!.y).isEqualTo(
                ((listItemHeight.toIntPx() - trailingSize.value!!.height.round()) / 2).toPx()
            )
        }
    }

    @Test
    fun listItem_oneLine_positioning_withIcon() {
        val listItemHeight = 56.dp
        val expectedLeftPadding = 16.dp
        val expectedTextLeftPadding = 32.dp

        val textPosition = Ref<PxPosition>()
        val textSize = Ref<PxSize>()
        val iconPosition = Ref<PxPosition>()
        val iconSize = Ref<PxSize>()
        composeTestRule.setMaterialContent {
            Container(alignment = Alignment.TopLeft) {
                ListItem(
                    text = { SaveLayout(textPosition, textSize) { Text("Primary text") } },
                    icon = { SaveLayout(iconPosition, iconSize) { SimpleImage(icon24x24) } }
                )
            }
        }
        withDensity(composeTestRule.density) {
            Truth.assertThat(iconPosition.value!!.x).isEqualTo(expectedLeftPadding.toIntPx().toPx())
            Truth.assertThat(iconPosition.value!!.y).isEqualTo(
                ((listItemHeight.toIntPx() - iconSize.value!!.height.round()) / 2).toPx()
            )
            Truth.assertThat(textPosition.value!!.x).isEqualTo(
                expectedLeftPadding.toIntPx().toPx() + iconSize.value!!.width +
                        expectedTextLeftPadding.toIntPx().toPx()
            )
            Truth.assertThat(textPosition.value!!.y).isEqualTo(
                ((listItemHeight.toIntPx() - textSize.value!!.height.round()) / 2).toPx()
            )
        }
    }

    @Test
    fun listItem_twoLine_positioning_noIcon() {
        val expectedLeftPadding = 16.dp
        val expectedRightPadding = 16.dp
        val expectedTextBaseline = 28.dp
        val expectedSecondaryTextBaselineOffset = 20.dp

        val textPosition = Ref<PxPosition>()
        val textBaseline = Ref<Px>()
        val textSize = Ref<PxSize>()
        val secondaryTextPosition = Ref<PxPosition>()
        val secondaryTextBaseline = Ref<Px>()
        val secondaryTextSize = Ref<PxSize>()
        val trailingPosition = Ref<PxPosition>()
        val trailingBaseline = Ref<Px>()
        val trailingSize = Ref<PxSize>()
        composeTestRule.setMaterialContent {
            Container(alignment = Alignment.TopLeft) {
                ListItem(
                    text = {
                        SaveLayout(textPosition, textSize, textBaseline) {
                            Text("Primary text")
                        }
                    },
                    secondaryText = {
                        SaveLayout(
                            secondaryTextPosition,
                            secondaryTextSize,
                            secondaryTextBaseline
                        ) {
                            Text("Secondary text")
                        }
                    },
                    trailing = {
                        SaveLayout(trailingPosition, trailingSize, trailingBaseline) {
                            Text("meta")
                        }
                    }
                )
            }
        }
        withDensity(composeTestRule.density) {
            Truth.assertThat(textPosition.value!!.x).isEqualTo(expectedLeftPadding.toIntPx().toPx())
            Truth.assertThat(textBaseline.value!!).isEqualTo(expectedTextBaseline.toIntPx().toPx())
            Truth.assertThat(secondaryTextPosition.value!!.x).isEqualTo(
                expectedLeftPadding.toIntPx().toPx()
            )
            Truth.assertThat(secondaryTextBaseline.value!!).isEqualTo(
                expectedTextBaseline.toIntPx().toPx() +
                        expectedSecondaryTextBaselineOffset.toIntPx().toPx()
            )
            val dm = composeTestRule.displayMetrics
            Truth.assertThat(trailingPosition.value!!.x).isEqualTo(
                dm.widthPixels.px - trailingSize.value!!.width -
                        expectedRightPadding.toIntPx().toPx()
            )
            Truth.assertThat(trailingBaseline.value!!).isEqualTo(
                expectedTextBaseline.toIntPx().toPx()
            )
        }
    }

    @Test
    fun listItem_twoLine_positioning_withSmallIcon() {
        val expectedLeftPadding = 16.dp
        val expectedIconTopPadding = 16.dp
        val expectedContentLeftPadding = 32.dp
        val expectedTextBaseline = 32.dp
        val expectedSecondaryTextBaselineOffset = 20.dp

        val textPosition = Ref<PxPosition>()
        val textBaseline = Ref<Px>()
        val textSize = Ref<PxSize>()
        val secondaryTextPosition = Ref<PxPosition>()
        val secondaryTextBaseline = Ref<Px>()
        val secondaryTextSize = Ref<PxSize>()
        val iconPosition = Ref<PxPosition>()
        val iconSize = Ref<PxSize>()
        composeTestRule.setMaterialContent {
            Container(alignment = Alignment.TopLeft) {
                ListItem(
                    text = {
                        SaveLayout(textPosition, textSize, textBaseline) {
                            Text("Primary text")
                        }
                    },
                    secondaryText = {
                        SaveLayout(
                            secondaryTextPosition,
                            secondaryTextSize,
                            secondaryTextBaseline
                        ) {
                            Text("Secondary text")
                        }
                    },
                    icon = {
                        SaveLayout(iconPosition, iconSize) { SimpleImage(icon24x24) }
                    }
                )
            }
        }
        withDensity(composeTestRule.density) {
            Truth.assertThat(textPosition.value!!.x).isEqualTo(
                expectedLeftPadding.toIntPx().toPx() + iconSize.value!!.width +
                        expectedContentLeftPadding.toIntPx().toPx()
            )
            Truth.assertThat(textBaseline.value!!).isEqualTo(expectedTextBaseline.toIntPx().toPx())
            Truth.assertThat(secondaryTextPosition.value!!.x).isEqualTo(
                expectedLeftPadding.toIntPx().toPx() + iconSize.value!!.width +
                        expectedContentLeftPadding.toIntPx().toPx()
            )
            Truth.assertThat(secondaryTextBaseline.value!!).isEqualTo(
                expectedTextBaseline.toIntPx().toPx() +
                        expectedSecondaryTextBaselineOffset.toIntPx().toPx()
            )
            Truth.assertThat(iconPosition.value!!.x).isEqualTo(expectedLeftPadding.toIntPx().toPx())
            Truth.assertThat(iconPosition.value!!.y).isEqualTo(
                expectedIconTopPadding.toIntPx().toPx()
            )
        }
    }

    @Test
    fun listItem_twoLine_positioning_withLargeIcon() {
        val listItemHeight = 72.dp
        val expectedLeftPadding = 16.dp
        val expectedIconTopPadding = 16.dp
        val expectedContentLeftPadding = 16.dp
        val expectedTextBaseline = 32.dp
        val expectedSecondaryTextBaselineOffset = 20.dp
        val expectedRightPadding = 16.dp

        val textPosition = Ref<PxPosition>()
        val textBaseline = Ref<Px>()
        val textSize = Ref<PxSize>()
        val secondaryTextPosition = Ref<PxPosition>()
        val secondaryTextBaseline = Ref<Px>()
        val secondaryTextSize = Ref<PxSize>()
        val iconPosition = Ref<PxPosition>()
        val iconSize = Ref<PxSize>()
        val trailingPosition = Ref<PxPosition>()
        val trailingSize = Ref<PxSize>()
        composeTestRule.setMaterialContent {
            Container(alignment = Alignment.TopLeft) {
                ListItem(
                    text = {
                        SaveLayout(textPosition, textSize, textBaseline) {
                            Text("Primary text")
                        }
                    },
                    secondaryText = {
                        SaveLayout(
                            secondaryTextPosition,
                            secondaryTextSize,
                            secondaryTextBaseline
                        ) {
                            Text("Secondary text")
                        }
                    },
                    icon = {
                        SaveLayout(iconPosition, iconSize) { SimpleImage(icon40x40) }
                    },
                    trailing = {
                        SaveLayout(trailingPosition, trailingSize) { SimpleImage(icon24x24) }
                    }
                )
            }
        }
        withDensity(composeTestRule.density) {
            Truth.assertThat(textPosition.value!!.x).isEqualTo(
                expectedLeftPadding.toIntPx().toPx() + iconSize.value!!.width +
                        expectedContentLeftPadding.toIntPx().toPx()
            )
            Truth.assertThat(textBaseline.value!!).isEqualTo(expectedTextBaseline.toIntPx().toPx())
            Truth.assertThat(secondaryTextPosition.value!!.x).isEqualTo(
                expectedLeftPadding.toIntPx().toPx() + iconSize.value!!.width +
                        expectedContentLeftPadding.toIntPx().toPx()
            )
            Truth.assertThat(secondaryTextBaseline.value!!).isEqualTo(
                expectedTextBaseline.toIntPx().toPx() +
                        expectedSecondaryTextBaselineOffset.toIntPx().toPx()
            )
            Truth.assertThat(iconPosition.value!!.x).isEqualTo(expectedLeftPadding.toIntPx().toPx())
            Truth.assertThat(iconPosition.value!!.y).isEqualTo(
                expectedIconTopPadding.toIntPx().toPx()
            )
            val dm = composeTestRule.displayMetrics
            Truth.assertThat(trailingPosition.value!!.x).isEqualTo(
                dm.widthPixels.px - trailingSize.value!!.width -
                        expectedRightPadding.toIntPx().toPx()
            )
            Truth.assertThat(trailingPosition.value!!.y).isEqualTo(
                ((listItemHeight.toIntPx() - trailingSize.value!!.height.round()) / 2).toPx()
            )
        }
    }

    @Test
    fun listItem_threeLine_positioning_noOverline_metaText() {
        val expectedLeftPadding = 16.dp
        val expectedIconTopPadding = 16.dp
        val expectedContentLeftPadding = 32.dp
        val expectedTextBaseline = 28.dp
        val expectedSecondaryTextBaselineOffset = 20.dp
        val expectedRightPadding = 16.dp

        val textPosition = Ref<PxPosition>()
        val textBaseline = Ref<Px>()
        val textSize = Ref<PxSize>()
        val secondaryTextPosition = Ref<PxPosition>()
        val secondaryTextBaseline = Ref<Px>()
        val secondaryTextSize = Ref<PxSize>()
        val iconPosition = Ref<PxPosition>()
        val iconSize = Ref<PxSize>()
        val trailingPosition = Ref<PxPosition>()
        val trailingSize = Ref<PxSize>()
        composeTestRule.setMaterialContent {
            Container(alignment = Alignment.TopLeft) {
                ListItem(
                    text = {
                        SaveLayout(textPosition, textSize, textBaseline) {
                            Text("Primary text")
                        }
                    },
                    secondaryText = {
                        SaveLayout(
                            secondaryTextPosition,
                            secondaryTextSize,
                            secondaryTextBaseline
                        ) {
                            Text("Secondary text")
                        }
                    },
                    singleLineSecondaryText = false,
                    icon = {
                        SaveLayout(iconPosition, iconSize) { SimpleImage(icon24x24) }
                    },
                    trailing = {
                        SaveLayout(trailingPosition, trailingSize) { SimpleImage(icon24x24) }
                    }
                )
            }
        }
        withDensity(composeTestRule.density) {
            Truth.assertThat(textPosition.value!!.x).isEqualTo(
                expectedLeftPadding.toIntPx().toPx() + iconSize.value!!.width +
                        expectedContentLeftPadding.toIntPx().toPx()
            )
            Truth.assertThat(textBaseline.value!!).isEqualTo(expectedTextBaseline.toIntPx().toPx())
            Truth.assertThat(secondaryTextPosition.value!!.x).isEqualTo(
                expectedLeftPadding.toIntPx().toPx() + iconSize.value!!.width +
                        expectedContentLeftPadding.toIntPx().toPx()
            )
            Truth.assertThat(secondaryTextBaseline.value!!).isEqualTo(
                expectedTextBaseline.toIntPx().toPx() +
                        expectedSecondaryTextBaselineOffset.toIntPx().toPx()
            )
            Truth.assertThat(iconPosition.value!!.x).isEqualTo(expectedLeftPadding.toIntPx().toPx())
            Truth.assertThat(iconPosition.value!!.y).isEqualTo(
                expectedIconTopPadding.toIntPx().toPx()
            )
            val dm = composeTestRule.displayMetrics
            Truth.assertThat(trailingPosition.value!!.x).isEqualTo(
                dm.widthPixels.px - trailingSize.value!!.width -
                        expectedRightPadding.toIntPx().toPx()
            )
            Truth.assertThat(trailingPosition.value!!.y).isEqualTo(
                expectedIconTopPadding.toIntPx().toPx()
            )
        }
    }

    @Test
    fun listItem_threeLine_positioning_overline_trailingIcon() {
        val expectedLeftPadding = 16.dp
        val expectedIconTopPadding = 16.dp
        val expectedContentLeftPadding = 16.dp
        val expectedOverlineBaseline = 28.dp
        val expectedTextBaselineOffset = 20.dp
        val expectedSecondaryTextBaselineOffset = 20.dp
        val expectedRightPadding = 16.dp

        val textPosition = Ref<PxPosition>()
        val textBaseline = Ref<Px>()
        val textSize = Ref<PxSize>()
        val overlineTextPosition = Ref<PxPosition>()
        val overlineTextBaseline = Ref<Px>()
        val overlineTextSize = Ref<PxSize>()
        val secondaryTextPosition = Ref<PxPosition>()
        val secondaryTextBaseline = Ref<Px>()
        val secondaryTextSize = Ref<PxSize>()
        val iconPosition = Ref<PxPosition>()
        val iconSize = Ref<PxSize>()
        val trailingPosition = Ref<PxPosition>()
        val trailingSize = Ref<PxSize>()
        val trailingBaseline = Ref<Px>()
        composeTestRule.setMaterialContent {
            Container(alignment = Alignment.TopLeft) {
                ListItem(
                    overlineText = {
                        SaveLayout(
                            overlineTextPosition,
                            overlineTextSize,
                            overlineTextBaseline
                        ) {
                            Text("OVERLINE")
                        }
                    },
                    text = {
                        SaveLayout(textPosition, textSize, textBaseline) {
                            Text("Primary text")
                        }
                    },
                    secondaryText = {
                        SaveLayout(
                            secondaryTextPosition,
                            secondaryTextSize,
                            secondaryTextBaseline
                        ) {
                            Text("Secondary text")
                        }
                    },
                    icon = {
                        SaveLayout(iconPosition, iconSize) { SimpleImage(icon40x40) }
                    },
                    trailing = {
                        SaveLayout(
                            trailingPosition,
                            trailingSize,
                            trailingBaseline
                        ) {
                            Text("meta")
                        }
                    }
                )
            }
        }
        withDensity(composeTestRule.density) {
            Truth.assertThat(textPosition.value!!.x).isEqualTo(
                expectedLeftPadding.toIntPx().toPx() + iconSize.value!!.width +
                        expectedContentLeftPadding.toIntPx().toPx()
            )
            Truth.assertThat(textBaseline.value!!).isEqualTo(
                expectedOverlineBaseline.toIntPx().toPx() +
                        expectedTextBaselineOffset.toIntPx().toPx()
            )
            Truth.assertThat(overlineTextPosition.value!!.x).isEqualTo(
                expectedLeftPadding.toIntPx().toPx() + iconSize.value!!.width +
                        expectedContentLeftPadding.toIntPx().toPx()
            )
            Truth.assertThat(overlineTextBaseline.value!!).isEqualTo(
                expectedOverlineBaseline.toIntPx().toPx()
            )
            Truth.assertThat(secondaryTextPosition.value!!.x).isEqualTo(
                expectedLeftPadding.toIntPx().toPx() + iconSize.value!!.width +
                        expectedContentLeftPadding.toIntPx().toPx()
            )
            Truth.assertThat(secondaryTextBaseline.value!!).isEqualTo(
                expectedOverlineBaseline.toIntPx().toPx() +
                        expectedTextBaselineOffset.toIntPx().toPx() +
                        expectedSecondaryTextBaselineOffset.toIntPx().toPx()
            )
            Truth.assertThat(iconPosition.value!!.x).isEqualTo(expectedLeftPadding.toIntPx().toPx())
            Truth.assertThat(iconPosition.value!!.y).isEqualTo(
                expectedIconTopPadding.toIntPx().toPx()
            )
            val dm = composeTestRule.displayMetrics
            Truth.assertThat(trailingPosition.value!!.x).isEqualTo(
                dm.widthPixels.px - trailingSize.value!!.width -
                        expectedRightPadding.toIntPx().toPx()
            )
            Truth.assertThat(trailingBaseline.value!!).isEqualTo(
                expectedOverlineBaseline.toIntPx().toPx()
            )
        }
    }

    private fun Dp.toIntPx() = (this.value * composeTestRule.density.density).roundToInt()

    @Composable
    private fun SaveLayout(
        coords: Ref<PxPosition>,
        size: Ref<PxSize>,
        baseline: Ref<Px> = Ref(),
        children: @Composable() () -> Unit
    ) {
        OnChildPositioned(
            { coordinates ->
                coords.value = coordinates.localToGlobal(PxPosition(0.px, 0.px))
                baseline.value = coordinates.providedAlignmentLines[FirstBaseline]?.toPx()?.let {
                    it + coords.value!!.y
                }
                size.value = coordinates.size
            },
            children
        )
    }
}
