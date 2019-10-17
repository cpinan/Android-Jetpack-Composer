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

package androidx.ui.core.test

import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.test.filters.SmallTest
import androidx.ui.core.EditorModel
import androidx.ui.core.FocusManagerAmbient
import androidx.ui.core.TestTag
import androidx.ui.core.TextField
import androidx.ui.core.TextInputServiceAmbient
import androidx.ui.core.input.FocusManager
import androidx.ui.input.CommitTextEditOp
import androidx.ui.input.DeleteSurroundingTextEditOp
import androidx.ui.input.EditOperation
import androidx.ui.input.FinishComposingTextEditOp
import androidx.ui.input.SetComposingRegionEditOp
import androidx.ui.input.SetComposingTextEditOp
import androidx.ui.input.SetSelectionEditOp
import androidx.ui.input.TextInputService
import androidx.ui.test.createComposeRule
import androidx.ui.test.doClick
import androidx.ui.test.findByTag
import androidx.ui.test.waitForIdleCompose
import androidx.ui.text.TextRange
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@SmallTest
@RunWith(JUnit4::class)
class TextFieldOnValueChangeEditorModelTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    val onValueChange: (EditorModel) -> Unit = mock()

    lateinit var onEditCommandCallback: (List<EditOperation>) -> Unit

    @Before
    fun setUp() {
        val focusManager = mock<FocusManager>()
        val textInputService = mock<TextInputService>()
        val inputSessionToken = 10 // any positive number is fine.

        // Always give focus to the passed node.
        whenever(focusManager.requestFocus(any())).thenAnswer {
            (it.arguments[0] as FocusManager.FocusNode).onFocus()
        }
        whenever(textInputService.startInput(any(), any(), any(), any(), any()))
            .thenReturn(inputSessionToken)

        composeTestRule.setContent {
            FocusManagerAmbient.Provider(value = focusManager) {
                TextInputServiceAmbient.Provider(value = textInputService) {
                    TestTag(tag = "textField") {
                        val state = +state { EditorModel("abcde", TextRange(0, 0)) }
                        TextField(
                            value = state.value,
                            onValueChange = {
                                state.value = it
                                onValueChange(it)
                            })
                    }
                }
            }
        }

        // Perform click to focus in.
        val element = findByTag("textField")
        element.doClick()

        // Verify startInput is called and capture the callback.
        val onEditCommandCaptor = argumentCaptor<(List<EditOperation>) -> Unit>()
        verify(textInputService, times(1)).startInput(
            initModel = any(),
            keyboardType = any(),
            imeAction = any(),
            onEditCommand = onEditCommandCaptor.capture(),
            onImeActionPerformed = any()
        )
        assertThat(onEditCommandCaptor.allValues.size).isEqualTo(1)
        onEditCommandCallback = onEditCommandCaptor.firstValue
        assertThat(onEditCommandCallback).isNotNull()

        clearInvocations(onValueChange)
    }

    private fun performEditOperation(op: EditOperation) {
        arrayOf(listOf(op)).forEach {
            composeTestRule.runOnUiThread { onEditCommandCallback(it) }
            waitForIdleCompose()
        }
    }

    @Test
    fun commitText_onValueChange_call_once() {
        // Committing text should be reported as value change
        performEditOperation(CommitTextEditOp("ABCDE", 1))
        composeTestRule.runOnUiThread {
            verify(onValueChange, times(1)).invoke(eq(EditorModel("ABCDEabcde", TextRange(5, 5))))
        }
        waitForIdleCompose()
    }

    @Test
    fun setComposingRegion_onValueChange_never_call() {
        // Composition conversion is not counted as a value change in EditorModel text field.
        performEditOperation(SetComposingRegionEditOp(0, 5))
        composeTestRule.runOnUiThread {
            verify(onValueChange, never()).invoke(any())
        }
        waitForIdleCompose()
    }

    @Test
    fun setCompsingText_onValueChange_call_once() {
        performEditOperation(SetComposingTextEditOp("ABCDE", 1))
        composeTestRule.runOnUiThread {
            verify(onValueChange, times(1)).invoke(eq(EditorModel("ABCDEabcde", TextRange(5, 5))))
        }
        waitForIdleCompose()
    }

    @Test
    fun setSelection_onValueChange_call_once() {
        // Selection change is a part of value-change in EditorModel text field
        performEditOperation(SetSelectionEditOp(1, 1))
        composeTestRule.runOnUiThread {
            verify(onValueChange, times(1)).invoke(eq(EditorModel("abcde", TextRange(1, 1))))
        }
        waitForIdleCompose()
    }

    @Test
    fun clearComposition_onValueChange_call_once() {
        performEditOperation(SetComposingTextEditOp("ABCDE", 1))
        composeTestRule.runOnUiThread {
            verify(onValueChange, times(1)).invoke(eq(EditorModel("ABCDEabcde", TextRange(5, 5))))
        }
        waitForIdleCompose()

        // Finishing composition change is not counted as a value change in EditorModel text field.
        clearInvocations(onValueChange)
        performEditOperation(FinishComposingTextEditOp())
        composeTestRule.runOnUiThread { verify(onValueChange, never()).invoke(any()) }
        waitForIdleCompose()
    }

    @Test
    fun deleteSurroundingText_onValueChange_call_once() {
        performEditOperation(DeleteSurroundingTextEditOp(0, 1))
        composeTestRule.runOnUiThread {
            verify(onValueChange, times(1)).invoke(eq(EditorModel("bcde", TextRange(0, 0))))
        }
        waitForIdleCompose()
    }
}
