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

package androidx.ui.test

import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.test.filters.MediumTest
import androidx.ui.core.semantics.getOrNull
import androidx.ui.foundation.selection.ToggleableState
import androidx.ui.foundation.semantics.FoundationSemanticsProperties
import androidx.ui.layout.Column
import androidx.ui.material.Checkbox
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@MediumTest
@RunWith(JUnit4::class)
class FindAllTest {

    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)

    @Test
    fun findAllTest_twoComponents_areChecked() {
        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    Column {
                        Checkbox(checked = true, onCheckedChange = null)
                        Checkbox(checked = true, onCheckedChange = null)
                    }
                }
            }
        }

        findAll {
            getOrNull(FoundationSemanticsProperties.ToggleableState) == ToggleableState.Checked
        }
            .assertCountEquals(2)
            .forEach {
                it.assertIsChecked()
            }
    }

    @Test
    fun findAllTest_twoComponents_toggleBoth() {
        composeTestRule.setContent {
            val (checked1, onCheckedChange1) = +state { false }
            val (checked2, onCheckedChange2) = +state { false }
            MaterialTheme {
                Surface {
                    Column {
                        Checkbox(
                            checked = checked1,
                            onCheckedChange = onCheckedChange1
                        )
                        Checkbox(
                            checked = checked2,
                            onCheckedChange = onCheckedChange2
                        )
                    }
                }
            }
        }

        findAll { isToggleable }
            .assertCountEquals(2)
            .forEach {
                it.doClick()
                it.assertIsChecked()
            }
    }

    @Test
    fun findAllTest_noNonCheckedComponent() {
        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    Column {
                        Checkbox(checked = true, onCheckedChange = null)
                        Checkbox(checked = true, onCheckedChange = null)
                    }
                }
            }
        }

        findAll {
            getOrNull(FoundationSemanticsProperties.ToggleableState) != ToggleableState.Checked
        }
            .assertCountEquals(0)
    }

    @Test
    fun findAllTest_twoComponents_toggleOne() {
        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    val (checked1, onCheckedChange1) = +state { false }
                    val (checked2, onCheckedChange2) = +state { false }

                    Column {
                        Checkbox(
                            checked = checked1,
                            onCheckedChange = onCheckedChange1
                        )
                        Checkbox(
                            checked = checked2,
                            onCheckedChange = onCheckedChange2
                        )
                    }
                }
            }
        }

        findAll { isToggleable }.apply {
            get(0)
                .doClick()
                .assertIsChecked()
            get(1)
                .assertIsUnchecked()
        }.assertCountEquals(2)
    }

    @Test
    fun findAllTest_twoComponents_togglesCreatesAnother() {
        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    val (checked, onCheckedChange) = +state { false }

                    Column {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                onCheckedChange(it)
                            }
                        )
                        Checkbox(
                            checked = false,
                            onCheckedChange = null
                        )

                        if (checked) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = null
                            )
                        }
                    }
                }
            }
        }

        findAll { isToggleable }.assertCountEquals(2).apply {
            get(0)
                .assertIsUnchecked()
                .doClick()
                .assertIsChecked()
        }

        findAll { isToggleable }.assertCountEquals(3).apply {
            get(2)
                .assertIsUnchecked()
        }
    }

    @Test
    fun findAllTest_twoComponents_toggleDeletesOne() {
        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    val (checked, onCheckedChange) = +state { false }

                    Column {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                onCheckedChange(it)
                            }
                        )
                        if (!checked) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = null
                            )
                        }
                    }
                }
            }
        }

        findAll { isToggleable }.assertCountEquals(2).apply {
            get(0)
                .assertIsUnchecked()
                .doClick()
                .assertIsChecked()
            get(1)
                .assertNoLongerExists()
        }
    }
}