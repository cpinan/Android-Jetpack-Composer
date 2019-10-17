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

import androidx.test.filters.MediumTest
import androidx.compose.unaryPlus
import androidx.ui.core.Dp
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.OnChildPositioned
import androidx.ui.core.OnPositioned
import androidx.ui.core.PxPosition
import androidx.ui.core.PxSize
import androidx.ui.core.TestTag
import androidx.ui.core.Text
import androidx.ui.core.currentTextStyle
import androidx.ui.core.dp
import androidx.ui.core.sp
import androidx.ui.core.toPx
import androidx.ui.core.withDensity
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.Wrap
import androidx.ui.test.assertSemanticsIsEqualTo
import androidx.ui.test.createComposeRule
import androidx.ui.test.createFullSemantics
import androidx.ui.test.doClick
import androidx.ui.test.findByTag
import androidx.ui.test.findByText
import androidx.ui.text.TextStyle
import com.google.common.truth.Truth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@MediumTest
@RunWith(JUnit4::class)
class ButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultButtonSemantics = createFullSemantics(
        isEnabled = true
    )

    @Test
    fun buttonTest_defaultSemantics() {

        composeTestRule.setMaterialContent {
            Center {
                TestTag(tag = "myButton") {
                    Button(onClick = {}, text = "myButton")
                }
            }
        }

        findByTag("myButton")
            .assertSemanticsIsEqualTo(defaultButtonSemantics)
    }

    @Test
    fun buttonTest_disabledSemantics() {

        composeTestRule.setMaterialContent {
            Center {
                TestTag(tag = "myButton") {
                    Button(text = "myButton")
                }
            }
        }

        findByTag("myButton")
            .assertSemanticsIsEqualTo(
                createFullSemantics(
                    isEnabled = false
                )
            )
    }

    @Test
    fun buttonTest_findByTextAndClick() {
        var counter = 0
        val onClick: () -> Unit = { ++counter }
        val text = "myButton"

        composeTestRule.setMaterialContent {
            Center {
                Button(onClick = onClick, text = text)
            }
        }

        // TODO(b/129400818): this actually finds the text, not the button as
        // merge semantics aren't implemented yet
        findByText(text)
            .doClick()

        Truth
            .assertThat(counter)
            .isEqualTo(1)
    }

    @Test
    fun buttonTest_ClickIsIndependentBetweenButtons() {
        var button1Counter = 0
        val button1OnClick: () -> Unit = { ++button1Counter }
        val button1Tag = "button1"

        var button2Counter = 0
        val button2OnClick: () -> Unit = { ++button2Counter }
        val button2Tag = "button2"

        val text = "myButton"

        composeTestRule.setMaterialContent {
            Column {
                TestTag(tag = button1Tag) {
                    Button(onClick = button1OnClick, text = text)
                }
                TestTag(tag = button2Tag) {
                    Button(onClick = button2OnClick, text = text)
                }
            }
        }

        findByTag(button1Tag)
            .doClick()

        Truth
            .assertThat(button1Counter)
            .isEqualTo(1)

        Truth
            .assertThat(button2Counter)
            .isEqualTo(0)

        findByTag(button2Tag)
            .doClick()

        Truth
            .assertThat(button1Counter)
            .isEqualTo(1)

        Truth
            .assertThat(button2Counter)
            .isEqualTo(1)
    }

    @Test
    fun buttonTest_ButtonHeightIsFromSpec() {
        if (composeTestRule.density.fontScale > 1f) {
            // This test can be reasonable failing on the non default font scales
            // so lets skip it.
            return
        }
        composeTestRule
            .setMaterialContentAndCollectSizes {
                Button(onClick = {}, text = "Test button")
            }
            .assertHeightEqualsTo(36.dp)
    }

    @Test
    fun buttonTest_ButtonWithLargeFontSizeIsLargerThenMinHeight() {
        val realSize: PxSize = composeTestRule.setMaterialContentAndGetPixelSize {
            Button(onClick = {}) {
                Text(
                    text = "Test button",
                    style = TextStyle(fontSize = 50.sp)
                )
            }
        }

        withDensity(composeTestRule.density) {
            Truth.assertThat(realSize.height.value)
                .isGreaterThan(36.dp.toIntPx().value.toFloat())
        }
    }

    @Test
    fun buttonTest_ContainedButtonPropagateDefaultTextStyle() {
        composeTestRule.setMaterialContent {
            Button(onClick = {}, style = ContainedButtonStyle()) {
                Truth.assertThat(+currentTextStyle())
                    .isEqualTo(+themeTextStyle { button.copy(color = +themeColor { onPrimary }) })
            }
        }
    }

    @Test
    fun buttonTest_OutlinedButtonPropagateDefaultTextStyle() {
        composeTestRule.setMaterialContent {
            Button(onClick = {}, style = OutlinedButtonStyle()) {
                Truth.assertThat(+currentTextStyle())
                    .isEqualTo(+themeTextStyle { button.copy(color = +themeColor { primary }) })
            }
        }
    }

    @Test
    fun buttonTest_TextButtonPropagateDefaultTextStyle() {
        composeTestRule.setMaterialContent {
            Button(onClick = {}, style = OutlinedButtonStyle()) {
                Truth.assertThat(+currentTextStyle())
                    .isEqualTo(+themeTextStyle { button.copy(color = +themeColor { primary }) })
            }
        }
    }

    @Test
    fun buttonTest_ContainedButtonHorPaddingIsFromSpec() {
        assertLeftPaddingIs(16.dp) {
            ContainedButtonStyle()
        }
    }

    @Test
    fun buttonTest_OutlinedButtonHorPaddingIsFromSpec() {
        assertLeftPaddingIs(16.dp) {
            OutlinedButtonStyle()
        }
    }

    @Test
    fun buttonTest_TextButtonHorPaddingIsFromSpec() {
        assertLeftPaddingIs(8.dp) {
            TextButtonStyle()
        }
    }

    private fun assertLeftPaddingIs(padding: Dp, style: () -> ButtonStyle) {
        var parentCoordinates: LayoutCoordinates? = null
        var childCoordinates: LayoutCoordinates? = null
        val parentLatch = CountDownLatch(1)
        val childLatch = CountDownLatch(1)
        composeTestRule.setMaterialContent {
            Wrap {
                Button(onClick = {}, style = style.invoke()) {
                    OnPositioned {
                        parentCoordinates = it
                        parentLatch.countDown()
                    }
                    OnChildPositioned(onPositioned = {
                        childCoordinates = it
                        childLatch.countDown()
                    }) {
                        Text("Test button")
                    }
                }
            }
        }

        assertTrue(parentLatch.await(1, TimeUnit.SECONDS))
        assertTrue(childLatch.await(1, TimeUnit.SECONDS))
        val topLeft = childCoordinates!!.localToGlobal(PxPosition.Origin).x -
                parentCoordinates!!.localToGlobal(PxPosition.Origin).x
        assertEquals(topLeft, withDensity(composeTestRule.density) { padding.toIntPx().toPx() })
    }
}
