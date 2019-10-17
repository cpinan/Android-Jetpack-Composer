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

package androidx.ui.core.gesture

import androidx.ui.core.PointerEventPass
import androidx.ui.core.PxPosition
import androidx.ui.core.consumeDownChange
import androidx.ui.core.milliseconds
import androidx.ui.core.millisecondsToTimestamp
import androidx.ui.core.px
import androidx.ui.testutils.consume
import androidx.ui.testutils.down
import androidx.ui.testutils.invokeOverAllPasses
import androidx.ui.testutils.moveBy
import androidx.ui.testutils.moveTo
import androidx.ui.testutils.up
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

@ObsoleteCoroutinesApi
@RunWith(JUnit4::class)
class LongPressGestureDetectorTest {

    private val LongPressTimeoutMillis = 100.milliseconds
    private val testContext = TestCoroutineContext()
    private val onLongPress: (PxPosition) -> Unit = mock()
    private lateinit var mRecognizer: LongPressGestureRecognizer

    @Before
    fun setup() {
        mRecognizer = LongPressGestureRecognizer(testContext)
        mRecognizer.onLongPress = onLongPress
        mRecognizer.longPressTimeout = LongPressTimeoutMillis
    }

    // Tests that verify conditions under which onLongPress will not be called.

    @Test
    fun pointerInputHandler_down_onLongPressNotCalled() {
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down()))
        verify(onLongPress, never()).invoke(any())
    }

    @Test
    fun pointerInputHandler_downWithinTimeout_onLongPressNotCalled() {
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down()))
        testContext.advanceTimeBy(99, TimeUnit.MILLISECONDS)
        verify(onLongPress, never()).invoke(any())
    }

    @Test
    fun pointerInputHandler_DownMoveConsumed_onLongPressNotCalled() {
        val down = down(0)
        val move = down.moveBy(50.milliseconds, 1f, 1f).consume(1f, 0f)

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(move))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)

        verify(onLongPress, never()).invoke(any())
    }

    @Test
    fun pointerInputHandler_2Down1MoveConsumed_onLongPressNotCalled() {
        val down0 = down(0)
        val down1 = down(1)
        val move0 = down0.moveBy(50.milliseconds, 1f, 1f).consume(1f, 0f)
        val move1 = down0.moveBy(50.milliseconds, 0f, 0f)

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down0, down1))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(move0, move1))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)

        verify(onLongPress, never()).invoke(any())
    }

    @Test
    fun pointerInputHandler_DownUpConsumed_onLongPressNotCalled() {
        val down = down(0)
        val up = down.up(50L.millisecondsToTimestamp()).consumeDownChange()

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(up))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)

        verify(onLongPress, never()).invoke(any())
    }

    @Test
    fun pointerInputHandler_DownUpNotConsumed_onLongPressNotCalled() {
        val down = down(0)
        val up = down.up(50L.millisecondsToTimestamp())

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(up))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)

        verify(onLongPress, never()).invoke(any())
    }

    @Test
    fun pointerInputHandler_2DownIndependentlyUnderTimeoutAndDoNotOverlap_onLongPressNotCalled() {

        // Arrange

        val down0 = down(0)

        val up0 = down0.up(50L.millisecondsToTimestamp())

        val down1 = down(1, 51L.millisecondsToTimestamp())

        // Act

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            down0
        ))

        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            up0
        ))

        testContext.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            down1
        ))

        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)

        // Assert

        verify(onLongPress, never()).invoke(any())
    }

    // Tests that verify conditions under which onLongPress will be called.

    @Test
    fun pointerInputHandler_downBeyondTimeout_onLongPressCalled() {
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down()))
        testContext.advanceTimeBy(100, TimeUnit.MILLISECONDS)
        verify(onLongPress).invoke(any())
    }

    @Test
    fun pointerInputHandler_2DownBeyondTimeout_onLongPressCalled() {
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down(0), down(1)))
        testContext.advanceTimeBy(100, TimeUnit.MILLISECONDS)
        verify(onLongPress).invoke(any())
    }

    @Test
    fun pointerInputHandler_2DownIndependentlyUnderTimeoutButOverlapTimeIsOver_onLongPressCalled() {

        // Arrange

        val down0 = down(0)

        val move0 = down0.moveTo(50L.millisecondsToTimestamp(), 0f, 0f)
        val down1 = down(1, 50L.millisecondsToTimestamp())

        val up0 = move0.up(75L.millisecondsToTimestamp())
        val move1 = down1.moveTo(75L.millisecondsToTimestamp(), 0f, 0f)

        // Act

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            down0
        ))

        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            move0, down1
        ))

        testContext.advanceTimeBy(25, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            up0, move1
        ))

        testContext.advanceTimeBy(25, TimeUnit.MILLISECONDS)

        // Assert

        verify(onLongPress).invoke(any())
    }

    @Test
    fun pointerInputHandler_downMoveNotConsumed_onLongPressCalled() {
        val down = down(0)
        val move = down.moveBy(50.milliseconds, 1f, 1f)

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(move))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)

        verify(onLongPress).invoke(any())
    }

    // Tests that verify correctness of PxPosition value passed to onLongPress

    @Test
    fun pointerInputHandler_down_onLongPressCalledWithDownPosition() {
        val down = down(0, x = 13f, y = 17f)

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down))
        testContext.advanceTimeBy(100, TimeUnit.MILLISECONDS)

        verify(onLongPress).invoke(PxPosition(13.px, 17.px))
    }

    @Test
    fun pointerInputHandler_downMove_onLongPressCalledWithMovePosition() {
        val down = down(0, x = 13f, y = 17f)
        val move = down.moveTo(50L.millisecondsToTimestamp(), -7f, 5f)

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(move))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)

        verify(onLongPress).invoke(PxPosition((-7).px, 5.px))
    }

    @Test
    fun pointerInputHandler_downThenDown_onLongPressCalledWithFirstDownPosition() {
        val down0 = down(0, x = 13f, y = 17f)

        val move0 = down0.moveBy(50.milliseconds, 0f, 0f)
        val down1 = down(1, 50L.millisecondsToTimestamp(), x = 11f, y = 19f)

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down0))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(move0, down1))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)

        verify(onLongPress).invoke(PxPosition(13.px, 17.px))
    }

    @Test
    fun pointerInputHandler_down0ThenDown1ThenUp0_onLongPressCalledWithDown1Position() {
        val down0 = down(0, x = 13f, y = 17f)

        val move0 = down0.moveTo(50L.millisecondsToTimestamp(), 27f, 29f)
        val down1 = down(1, 50L.millisecondsToTimestamp(), x = 11f, y = 19f)

        val up0 = move0.up(75L.millisecondsToTimestamp())
        val move1 = down1.moveBy(25.milliseconds, 0f, 0f)

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down0))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(move0, down1))
        testContext.advanceTimeBy(25, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(up0, move1))
        testContext.advanceTimeBy(25, TimeUnit.MILLISECONDS)

        verify(onLongPress).invoke(PxPosition(11.px, 19.px))
    }

    @Test
    fun pointerInputHandler_down0ThenMove0AndDown1_onLongPressCalledWithMove0Position() {
        val down0 = down(0, x = 13f, y = 17f)

        val move0 = down0.moveTo(50L.millisecondsToTimestamp(), 27f, 29f)
        val down1 = down(1, 50L.millisecondsToTimestamp(), x = 11f, y = 19f)

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down0))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(move0, down1))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)

        verify(onLongPress).invoke(PxPosition(27.px, 29.px))
    }

    @Test
    fun pointerInputHandler_down0Down1Move1Up0_onLongPressCalledWithMove1Position() {
        val down0 = down(0, x = 13f, y = 17f)

        val move0 = down0.moveBy(25.milliseconds, 0f, 0f)
        val down1 = down(1, 25L.millisecondsToTimestamp(), x = 11f, y = 19f)

        val up0 = move0.up(50L.millisecondsToTimestamp())
        val move1 = down1.moveTo(50L.millisecondsToTimestamp(), 27f, 23f)

        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(down0))
        testContext.advanceTimeBy(25, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(move0, down1))
        testContext.advanceTimeBy(25, TimeUnit.MILLISECONDS)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(up0, move1))
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)

        verify(onLongPress).invoke(PxPosition(27.px, 23.px))
    }

    // Tests that verify that consumption behavior

    @Test
    fun pointerInputHandler_1Down_notConsumed() {
        val down0 = down(0)
        val result = mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            down0
        ))
        assertThat(result[0].consumed.downChange).isFalse()
    }

    @Test
    fun pointerInputHandler_1DownThen1Down_notConsumed() {

        // Arrange

        val down0 = down(0, 0L.millisecondsToTimestamp())
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            down0
        ))

        // Act

        testContext.advanceTimeBy(10, TimeUnit.MILLISECONDS)
        val move0 = down0.moveTo(10L.millisecondsToTimestamp(), 0f, 0f)
        val down1 = down(0, 10L.millisecondsToTimestamp())
        val result = mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            move0, down1
        ))

        // Assert

        assertThat(result[0].consumed.downChange).isFalse()
        assertThat(result[1].consumed.downChange).isFalse()
    }

    @Test
    fun pointerInputHandler_1DownUnderTimeUp_upNotConsumed() {

        // Arrange

        val down0 = down(0, 0L.millisecondsToTimestamp())
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            down0
        ))

        // Act

        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        val up0 = down0.up(50L.millisecondsToTimestamp())
        val result = mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            up0
        ))

        // Assert

        assertThat(result[0].consumed.downChange).isFalse()
    }

    @Test
    fun pointerInputHandler_1DownOverTimeUp_upConsumedOnInitialDown() {

        // Arrange

        val down0 = down(0, 0L.millisecondsToTimestamp())
        mRecognizer.pointerInputHandler.invokeOverAllPasses(listOf(
            down0
        ))

        // Act

        testContext.advanceTimeBy(101, TimeUnit.MILLISECONDS)
        val up0 = down0.up(100L.millisecondsToTimestamp())
        val result = mRecognizer.pointerInputHandler.invoke(listOf(
            up0
        ), PointerEventPass.InitialDown)

        // Assert

        assertThat(result[0].consumed.downChange).isTrue()
    }

    @Test
    fun pointerInputHandler_1DownOverTimeMoveConsumedUp_upNotConsumed() {

        // Arrange

        var pointer = down(0, 0L.millisecondsToTimestamp())
        mRecognizer.pointerInputHandler.invokeOverAllPasses(pointer)
        testContext.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        pointer = pointer.moveTo(50L.millisecondsToTimestamp(), 5f).consume(1f)
        mRecognizer.pointerInputHandler.invokeOverAllPasses(pointer)

        // Act

        testContext.advanceTimeBy(51, TimeUnit.MILLISECONDS)
        pointer = pointer.up(100L.millisecondsToTimestamp())
        val result = mRecognizer.pointerInputHandler.invokeOverAllPasses(pointer)

        // Assert

        assertThat(result[0].consumed.downChange).isFalse()
    }
}