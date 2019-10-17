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

package androidx.animation

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

private const val epsilon = 0.00001f

@RunWith(JUnit4::class)
class DecayAnimationTest {

    @Test
    fun testExponentialDecay() {
        val anim = ExponentialDecay(absVelocityThreshold = 2.0f)
        val startValue = 200f
        val startVelocity = -800f

        val animWrapper = anim.createWrapper(startValue, startVelocity)
        // Obtain finish value by passing in an absurdly large playtime.
        val finishValue = animWrapper.getValue(Int.MAX_VALUE.toLong())

        for (playTime in 0L..4000L step 200L) {
            val value = anim.getValue(playTime, startValue, startVelocity)
            val velocity = anim.getVelocity(playTime, startValue, startVelocity)
            val finished = anim.isFinished(playTime, startValue, startVelocity)
            assertTrue(finished == animWrapper.isFinished(playTime))

            if (!finished) {
                // Before the animation finishes, absolute velocity is above the threshold
                assertTrue(Math.abs(velocity) >= 2.0f)
                assertEquals(value, animWrapper.getValue(playTime), epsilon)
                assertEquals(velocity, animWrapper.getVelocity(playTime), epsilon)
            } else {
                // When the animation is finished, expect absolute velocity < threshold
                assertTrue(Math.abs(velocity) < 2.0f)

                // Once the animation is finished, the value should not change any more
                assertEquals(finishValue, animWrapper.getValue(playTime), epsilon)
            }
        }
    }

    /**
     * This test verifies that the velocity threshold is stopping the animation at the right value
     * when velocity reaches that threshold.
     */
    @Test
    fun testDecayThreshold() {
        // TODO: Use parameterized tests
        val threshold = 500f
        val anim1 = ExponentialDecay(absVelocityThreshold = threshold)
        val anim2 = ExponentialDecay(absVelocityThreshold = 0f)

        val startValue = 2000f
        val startVelocity = 800f
        val fullAnim = ExponentialDecay(absVelocityThreshold = 0f).createWrapper(startValue,
            startVelocity)

        val finishValue = fullAnim.getValue(Int.MAX_VALUE.toLong())

        val finishValue1 = anim1.createWrapper(startValue, startVelocity)
            .getValue(Int.MAX_VALUE.toLong())

        val finishVelocity1 = anim1.createWrapper(startValue, startVelocity)
            .getVelocity(Int.MAX_VALUE.toLong())

        // Verify that the finish velocity is at the threshold
        assertEquals(threshold, finishVelocity1, epsilon)

        // Feed in the finish value from anim1 to anim2
        val finishValue2 = anim2.createWrapper(finishValue1, finishVelocity1)
            .getValue(Int.MAX_VALUE.toLong())

        assertEquals(finishValue, finishValue2, 2f)
    }
}
