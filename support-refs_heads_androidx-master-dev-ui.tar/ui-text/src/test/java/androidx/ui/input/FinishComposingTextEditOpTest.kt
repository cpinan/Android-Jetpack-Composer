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

package androidx.ui.input

import androidx.test.filters.SmallTest
import androidx.ui.text.TextRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@SmallTest
@RunWith(JUnit4::class)
class FinishComposingTextEditOpTest {

    @Test
    fun test_set() {
        val eb = EditingBuffer("ABCDE", TextRange(0, 0))

        eb.setComposition(1, 4)
        FinishComposingTextEditOp().process(eb)

        assertEquals("ABCDE", eb.toString())
        assertEquals(0, eb.cursor)
        assertFalse(eb.hasComposition())
    }

    @Test
    fun test_preserve_selection() {
        val eb = EditingBuffer("ABCDE", TextRange(1, 4))

        eb.setComposition(2, 5)
        FinishComposingTextEditOp().process(eb)

        assertEquals("ABCDE", eb.toString())
        assertEquals(1, eb.selectionStart)
        assertEquals(4, eb.selectionEnd)
        assertFalse(eb.hasComposition())
    }
}