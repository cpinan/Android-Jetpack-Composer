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

package androidx.ui.core

import android.app.Activity
import android.util.TypedValue
import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@SmallTest
@RunWith(JUnit4::class)
class SpDeviceTest {
    @get:Rule
    val activityTestRule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    private lateinit var activity: Activity

    @Before
    fun setup() {
        activity = activityTestRule.activity
    }

    @Test
    fun convertSpPx() {
        val dm = activity.resources.displayMetrics
        val sp10InPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, dm)
        withDensity(Density(activity)) {
            assertEquals(sp10InPx, 10.sp.toPx().value, 0.01f)
            assertEquals(10f, Px(sp10InPx).toSp().value, 0.01f)
        }
    }

    @Test
    fun convertSpDp() {
        withDensity(Density(activity)) {
            val px10InSp = Px(10f).toSp()
            val px10InDp = Px(10f).toDp()
            assertEquals(px10InDp.value, px10InSp.toDp().value, 0.01f)
            assertEquals(px10InSp.value, px10InDp.toSp().value, 0.01f)
        }
    }

    companion object {
        class TestActivity : Activity()
    }
}