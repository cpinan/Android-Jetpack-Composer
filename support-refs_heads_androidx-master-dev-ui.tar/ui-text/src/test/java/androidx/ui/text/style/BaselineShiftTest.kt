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

package androidx.ui.text.style

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BaselineShiftTest {
    @Test
    fun `lerp null with null returns null`() {
        assertThat(BaselineShift.lerp(null, null, 1.0f)).isNull()
    }

    @Test
    fun `lerp a with null returns a times (1 - t)`() {
        val a = BaselineShift(1.0f)
        val t = 0.3f

        val lerpBaselineShift = BaselineShift.lerp(a, null, t)
        assertThat(lerpBaselineShift?.multiplier).isEqualTo(a.multiplier * (1 - t))
    }

    @Test
    fun `lerp b with null returns b times t`() {
        val b = BaselineShift(1.0f)
        val t = 0.3f

        val lerpBaselineShift = BaselineShift.lerp(null, b, t)
        assertThat(lerpBaselineShift?.multiplier).isEqualTo(b.multiplier * t)
    }

    @Test
    fun `lerp a with b`() {
        val a = BaselineShift(1.0f)
        val b = BaselineShift(2.0f)
        val t = 0.3f

        val lerpBaselineShift = BaselineShift.lerp(a, b, t)
        assertThat(lerpBaselineShift?.multiplier).isEqualTo(1.3f)
    }
}