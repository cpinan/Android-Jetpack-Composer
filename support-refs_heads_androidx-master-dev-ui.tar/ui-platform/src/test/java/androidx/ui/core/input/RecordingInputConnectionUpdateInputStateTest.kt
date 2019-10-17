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

package androidx.ui.core.input

import android.view.View
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import androidx.test.filters.SmallTest
import androidx.ui.text.TextRange
import androidx.ui.input.InputEventListener
import androidx.ui.input.InputState
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@SmallTest
@RunWith(JUnit4::class)
class RecordingInputConnectionUpdateInputStateTest {

    private lateinit var ic: RecordingInputConnection
    private lateinit var listener: InputEventListener

    @Before
    fun setup() {
        listener = mock()
        ic = RecordingInputConnection(InputState("", TextRange(0, 0)), listener)
    }

    @Test
    fun test_update_input_state() {
        val imm: InputMethodManager = mock()
        val view: View = mock()

        val inputState = InputState(text = "Hello, World.", selection = TextRange(0, 0))

        ic.updateInputState(inputState, imm, view)

        verify(imm, times(1)).updateSelection(eq(view), eq(0), eq(0), eq(-1), eq(-1))
        verify(imm, never()).updateExtractedText(any(), any(), any())
    }

    @Test
    fun test_update_input_state_extracted_text_monitor() {
        val imm: InputMethodManager = mock()
        val view: View = mock()

        ic.getExtractedText(null, InputConnection.GET_EXTRACTED_TEXT_MONITOR)

        val inputState = InputState(text = "Hello, World.", selection = TextRange(0, 0))

        ic.updateInputState(inputState, imm, view)

        verify(imm, times(1)).updateSelection(eq(view), eq(0), eq(0), eq(-1), eq(-1))

        val captor = argumentCaptor<ExtractedText>()

        verify(imm, times(1)).updateExtractedText(any(), any(), captor.capture())

        assertEquals(1, captor.allValues.size)
        assertEquals("Hello, World.", captor.firstValue.text)
        assertEquals(-1, captor.firstValue.partialStartOffset)
        assertEquals(0, captor.firstValue.selectionStart)
        assertEquals(0, captor.firstValue.selectionEnd)
    }
}