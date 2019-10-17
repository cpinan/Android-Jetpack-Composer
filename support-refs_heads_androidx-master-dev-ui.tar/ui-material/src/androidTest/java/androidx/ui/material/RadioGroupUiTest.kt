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
import androidx.compose.Model
import androidx.test.filters.MediumTest
import androidx.ui.core.TestTag
import androidx.ui.core.dp
import androidx.ui.foundation.Strings
import androidx.ui.layout.Column
import androidx.ui.semantics.accessibilityValue
import androidx.ui.test.assertIsInMutuallyExclusiveGroup
import androidx.ui.test.assertIsSelected
import androidx.ui.test.assertSemanticsIsEqualTo
import androidx.ui.test.copyWith
import androidx.ui.test.createComposeRule
import androidx.ui.test.createFullSemantics
import androidx.ui.test.doClick
import androidx.ui.test.findByTag
import androidx.ui.test.assertIsUnselected
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Model
internal class RadioGroupSelectedState<T>(var selected: T)

@MediumTest
@RunWith(JUnit4::class)
class RadioGroupUiTest {

    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)

    private val itemOne = "Bar"
    private val itemTwo = "Foo"
    private val itemThree = "Sap"

    private val unselectedRadioGroupItemSemantics = createFullSemantics(
        value = Strings.NotSelected
    )

    private val selectedRadioGroupItemSemantics = unselectedRadioGroupItemSemantics.copyWith {
        accessibilityValue = Strings.Selected
    }
    private val options = listOf(itemOne, itemTwo, itemThree)

    @Composable
    fun VerticalRadioGroupforTests(children: @Composable() RadioGroupScope.() -> Unit) {
        RadioGroup {
            Column {
                children(p1 = this@RadioGroup)
            }
        }
    }

    @Test
    fun radioGroupTest_defaultSemantics() {
        val select = RadioGroupSelectedState(itemOne)

        composeTestRule.setMaterialContent {
            VerticalRadioGroupforTests {
                options.forEach { item ->
                    TestTag(tag = item) {
                        RadioGroupTextItem(
                            text = item,
                            selected = (select.selected == item),
                            onSelect = { select.selected = item })
                    }
                }
            }
        }

        findByTag(itemOne).assertSemanticsIsEqualTo(selectedRadioGroupItemSemantics)
        findByTag(itemTwo).assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)
        findByTag(itemThree).assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)

        findByTag(itemOne)
            .assertIsInMutuallyExclusiveGroup()
            .assertIsSelected()
        findByTag(itemTwo)
            .assertIsInMutuallyExclusiveGroup()
            .assertIsUnselected()
        findByTag(itemThree)
            .assertIsInMutuallyExclusiveGroup()
            .assertIsUnselected()
    }

    @Test
    fun radioGroupTest_ensureUnselectable() {
        val select = RadioGroupSelectedState(itemOne)

        composeTestRule.setMaterialContent {
            VerticalRadioGroupforTests {
                options.forEach { item ->
                    TestTag(tag = item) {
                        RadioGroupTextItem(
                            text = item,
                            selected = (select.selected == item),
                            onSelect = { select.selected = item })
                    }
                }
            }
        }

        findByTag(itemOne)
            .assertSemanticsIsEqualTo(selectedRadioGroupItemSemantics)
            .doClick()
            .assertSemanticsIsEqualTo(selectedRadioGroupItemSemantics)

        findByTag(itemTwo)
            .assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)

        findByTag(itemThree)
            .assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)
    }

    @Test
    fun radioGroupTest_clickSelect() {
        val select = RadioGroupSelectedState(itemOne)
        composeTestRule.setMaterialContent {
            VerticalRadioGroupforTests {
                options.forEach { item ->
                    TestTag(tag = item) {
                        RadioGroupTextItem(
                            text = item,
                            selected = (select.selected == item),
                            onSelect = { select.selected = item })
                    }
                }
            }
        }
        findByTag(itemTwo)
            .assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)
            .doClick()
            .assertSemanticsIsEqualTo(selectedRadioGroupItemSemantics)

        findByTag(itemOne)
            .assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)

        findByTag(itemThree)
            .assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)
    }

    @Test
    fun radioGroupTest_clickSelectTwoDifferentItems() {
        val select = RadioGroupSelectedState(itemOne)

        composeTestRule.setMaterialContent {
            VerticalRadioGroupforTests {
                options.forEach { item ->
                    TestTag(tag = item) {
                        RadioGroupTextItem(
                            text = item,
                            selected = (select.selected == item),
                            onSelect = { select.selected = item })
                    }
                }
            }
        }

        findByTag(itemTwo)
            .assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)
            .doClick()
            .assertSemanticsIsEqualTo(selectedRadioGroupItemSemantics)

        findByTag(itemOne)
            .assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)

        findByTag(itemThree)
            .assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)
            .doClick()
            .assertSemanticsIsEqualTo(selectedRadioGroupItemSemantics)

        findByTag(itemOne)
            .assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)

        findByTag(itemTwo)
            .assertSemanticsIsEqualTo(unselectedRadioGroupItemSemantics)
    }

    @Test
    fun radioButton_materialSizes_whenSelected() {
        materialSizesTestForValue(selected = true)
    }

    @Test
    fun radioButton_materialSizes_whenNotSelected() {
        materialSizesTestForValue(selected = false)
    }

    private fun materialSizesTestForValue(selected: Boolean) {
        composeTestRule
            .setMaterialContentAndCollectSizes {
                RadioButton(selected = selected, onSelect = null)
            }
            .assertIsSquareWithSize { 2.dp.toIntPx() * 2 + 20.dp.toIntPx() }
    }
}