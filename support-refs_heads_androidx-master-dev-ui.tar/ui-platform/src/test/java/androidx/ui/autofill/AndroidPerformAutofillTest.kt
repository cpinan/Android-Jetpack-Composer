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

package androidx.ui.autofill

import android.app.Activity
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.view.autofill.AutofillValue
import androidx.test.filters.SmallTest
import androidx.ui.ComposeUiRobolectricTestRunner
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@SmallTest
@RunWith(ComposeUiRobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    minSdk = 26)
class AndroidPerformAutofillTest {
    private val autofillTree = AutofillTree()
    private lateinit var androidAutofill: AndroidAutofill

    @Before
    fun setup() {
        val activity = Robolectric.setupActivity(Activity::class.java)
        val view = View(activity)
        activity.setContentView(view)

        androidAutofill = AndroidAutofill(view, autofillTree)
    }

    @Test
    fun performAutofill_name() {
        // Arrange.
        val expectedValue = "First Name"
        var autofilledValue = ""
        val autofillNode = AutofillNode(
            onFill = { autofilledValue = it },
            autofillTypes = listOf(AutofillType.Name),
            boundingBox = Rect(0, 0, 0, 0)
        )
        autofillTree += autofillNode

        val autofillValues = SparseArray<AutofillValue>()
            .apply { append(autofillNode.id, AutofillValue.forText(expectedValue)) }

        // Act.
        androidAutofill.performAutofill(autofillValues)

        // Assert.
        Truth.assertThat(autofilledValue).isEqualTo(expectedValue)
    }

    @Test
    fun performAutofill_email() {
        // Arrange.
        val expectedValue = "email@google.com"
        var autofilledValue = ""
        val autofillNode = AutofillNode(
            onFill = { autofilledValue = it },
            autofillTypes = listOf(AutofillType.EmailAddress),
            boundingBox = Rect(0, 0, 0, 0)
        )
        autofillTree += autofillNode

        val autofillValues = SparseArray<AutofillValue>()
            .apply { append(autofillNode.id, AutofillValue.forText(expectedValue)) }

        // Act.
        androidAutofill.performAutofill(autofillValues)

        // Assert.
        Truth.assertThat(autofilledValue).isEqualTo(expectedValue)
    }
}