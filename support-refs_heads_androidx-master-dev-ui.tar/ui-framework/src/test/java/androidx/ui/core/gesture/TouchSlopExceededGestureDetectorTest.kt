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

import androidx.ui.core.Direction
import androidx.ui.core.Duration
import androidx.ui.core.PointerEventPass
import androidx.ui.core.consumeDownChange
import androidx.ui.core.ipx
import androidx.ui.core.milliseconds
import androidx.ui.core.millisecondsToTimestamp
import androidx.ui.core.px
import androidx.ui.testutils.consume
import androidx.ui.testutils.down
import androidx.ui.testutils.invokeOverAllPasses
import androidx.ui.testutils.invokeOverPasses
import androidx.ui.testutils.moveBy
import androidx.ui.testutils.moveTo
import androidx.ui.testutils.up
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

// TODO(shepshapard): Write the following tests.
// Verify correct shape of slop area (should it be a square or circle)?
// Test for cases with more than one pointer
// Test for cases where things are reset when last pointer goes up
// Verify all methods called during onPostUp
// Verify default behavior when no callback provided for recognizer or canDrag

// Changing this value will break tests that expect the value to be 10.
private const val TestTouchSlop = 10

@RunWith(JUnit4::class)
class TouchSlopExceededGestureDetectorTest {

    private val onTouchSlopExceeded: () -> Unit = { onTouchSlopExceededCallCount++ }
    private val canDrag: (Direction) -> Boolean = { direction ->
        canDragDirections.add(direction)
        canDragReturn
    }
    private var onTouchSlopExceededCallCount: Int = 0
    private var canDragReturn = false
    private var canDragDirections: MutableList<Direction> = mutableListOf()
    private lateinit var mRecognizer: TouchSlopExceededGestureRecognizer

    @Before
    fun setup() {
        onTouchSlopExceededCallCount = 0
        canDragReturn = true
        canDragDirections.clear()
        mRecognizer =
            TouchSlopExceededGestureRecognizer(TestTouchSlop.ipx)
        mRecognizer.canDrag = canDrag
        mRecognizer.onTouchSlopExceeded = onTouchSlopExceeded
    }

    // Verify the circumstances under which canDrag should not be called.

    @Test
    fun onPointerInputChanges_down_canDragNotCalled() {
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down())
        assertThat(canDragDirections).isEmpty()
    }

    @Test
    fun onPointerInputChanges_downUp_canDragNotCalled() {
        val down = down(timestamp = 0L.millisecondsToTimestamp())
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down())
        val up = down.up(10L.millisecondsToTimestamp())
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(up)

        assertThat(canDragDirections).isEmpty()
    }

    @Test
    fun onPointerInputChanges_downMoveFullyConsumed_canDragNotCalled() {
        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        val move = down.moveBy(Duration(milliseconds = 10), 3f, 5f).consume(3f, 5f)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        assertThat(canDragDirections).isEmpty()
    }

    // Verify the circumstances under which canDrag should be called.

    @Test
    fun onPointerInputChanges_downMove1Dimension_canDragCalledOnce() {
        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        val move = down.moveBy(Duration(milliseconds = 10), 3f, 0f)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        // Twice because while under touch slop, TouchSlopExceededGestureDetector checks during PostUp and PostDown
        assertThat(canDragDirections).hasSize(2)
    }

    @Test
    fun onPointerInputChanges_downMove2Dimensions_canDragCalledTwice() {
        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        val move = down.moveBy(Duration(milliseconds = 10), 3f, 5f)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        // 4 times because while under touch slop, TouchSlopExceededGestureDetector checks during PostUp and
        // PostDown
        assertThat(canDragDirections).hasSize(4)
    }

    @Test
    fun onPointerInputChanges_downMoveOneDimensionPartiallyConsumed_canDragCalledOnce() {
        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        val move = down.moveBy(Duration(milliseconds = 10), 0f, 5f).consume(0f, 4f)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        // Twice because while under touch slop, DragGestureDetector checks during PostUp and
        // PostDown
        assertThat(canDragDirections).hasSize(2)
    }

    @Test
    fun onPointerInputChanges_downMoveTwoDimensionPartiallyConsumed_canDragCalledTwice() {
        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        val move = down.moveBy(Duration(milliseconds = 10), 3f, 5f).consume(2f, 4f)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        // 4 times because while under touch slop, DragGestureDetector checks during PostUp and
        // PostDown
        assertThat(canDragDirections).hasSize(4)
    }

    @Test
    fun onPointerInputChanges_dragPastTouchSlopOneDimensionAndDrag3MoreTimes_canDragCalledOnce() {
        val justBeyondSlop = (TestTouchSlop + 1).toFloat()

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        var move = down.moveTo(10L.millisecondsToTimestamp(), 0f, justBeyondSlop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)
        repeat(3) {
            move = move.moveBy(Duration(milliseconds = 10), 0f, 1f)
            mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)
        }

        // Once because although DragGestureDetector checks during PostUp and PostDown, slop is
        // surpassed during PostUp, and thus isn't checked again.
        assertThat(canDragDirections).hasSize(1)
    }

    @Test
    fun onPointerInputChanges_downMoveUnderSlop3Times_canDragCalled3Times() {
        val thirdSlop = TestTouchSlop.toFloat() / 3

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        var move = down
        repeat(3) {
            move = move.moveBy(Duration(milliseconds = 10), 0f, thirdSlop)
            mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)
        }

        // 6 times because while under touch slop, DragGestureDetector checks during PostUp and
        // PostDown
        assertThat(canDragDirections).hasSize(6)
    }

    @Test
    fun onPointerInputChanges_moveBeyondSlopThenIntoTouchSlopAreaAndOutAgain_canDragCalledOnce() {
        val beyondTouchSlop = (TestTouchSlop + 1).toFloat()

        var event = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(event)
        // Out of touch slop region
        event = event.moveBy(Duration(milliseconds = 10), 0f, beyondTouchSlop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(event)
        // Back into touch slop region
        event = event.moveBy(Duration(milliseconds = 10), 0f, -beyondTouchSlop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(event)
        // Out of touch slop region again
        event = event.moveBy(Duration(milliseconds = 10), 0f, beyondTouchSlop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(event)

        // Once because although DragGestureDetector checks during PostUp and PostDown, slop is
        // surpassed during PostUp, and thus isn't checked again.
        assertThat(canDragDirections).hasSize(1)
    }

    // Verification of correctness of values passed to canDrag.

    @Test
    fun onPointerInputChanges_canDragCalledWithCorrectDirection() {
        onPointerInputChanges_canDragCalledWithCorrectDirection(
            -1f, 0f, arrayOf(Direction.LEFT)
        )
        onPointerInputChanges_canDragCalledWithCorrectDirection(
            0f, -1f, arrayOf(Direction.UP)
        )
        onPointerInputChanges_canDragCalledWithCorrectDirection(
            1f, 0f, arrayOf(Direction.RIGHT)
        )
        onPointerInputChanges_canDragCalledWithCorrectDirection(
            0f, 1f, arrayOf(Direction.DOWN)
        )
        onPointerInputChanges_canDragCalledWithCorrectDirection(
            -1f, -1f, arrayOf(Direction.LEFT, Direction.UP)
        )
        onPointerInputChanges_canDragCalledWithCorrectDirection(
            -1f, 1f, arrayOf(Direction.LEFT, Direction.DOWN)
        )
        onPointerInputChanges_canDragCalledWithCorrectDirection(
            1f, -1f, arrayOf(Direction.RIGHT, Direction.UP)
        )
        onPointerInputChanges_canDragCalledWithCorrectDirection(
            1f, 1f, arrayOf(Direction.RIGHT, Direction.DOWN)
        )
    }

    private fun onPointerInputChanges_canDragCalledWithCorrectDirection(
        dx: Float,
        dy: Float,
        expectedDirections: Array<Direction>
    ) {
        canDragDirections.clear()
        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        val move = down.moveBy(Duration(milliseconds = 10), dx, dy)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        // Everything here is twice because DragGestureDetector checks during PostUp and PostDown.
        assertThat(canDragDirections).hasSize(expectedDirections.size * 2)
        expectedDirections.forEach { direction ->
            assertThat(canDragDirections.count { it == direction })
                .isEqualTo(2)
        }
    }

    // Verify the circumstances under which onTouchSlopExceeded should not be called.

    // TODO(b/129701831): This test assumes that if a pointer moves by slop in both x and y, we are
    // still under slop even though sqrt(slop^2 + slop^2) > slop.  This may be inaccurate and this
    // test may therefore need to be updated.
    @Test
    fun onPointerInputChanges_downMoveWithinSlop_onTouchSlopExceededNotCalled() {
        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        val move = down.moveBy(
            Duration(milliseconds = 10),
            TestTouchSlop.toFloat(),
            TestTouchSlop.toFloat()
        )
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        assertThat(onTouchSlopExceededCallCount).isEqualTo(0)
    }

    @Test
    fun onPointerInputChanges_moveBeyondSlopInUnsupportedDirection_onTouchSlopExceededNotCalled() {
        val beyondSlop = (TestTouchSlop + 1).toFloat()
        canDragReturn = false

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        val move = down.moveBy(
            Duration(milliseconds = 10),
            beyondSlop,
            beyondSlop
        )
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        assertThat(onTouchSlopExceededCallCount).isEqualTo(0)
    }

    @Test
    fun onPointerInputChanges_moveBeyondSlopButConsumeUnder_onTouchSlopExceededNotCalled() {

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)

        val move = down().moveBy(10.milliseconds, TestTouchSlop + 1f, 0f).consume(dx = 1f)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        // Assert

        assertThat(onTouchSlopExceededCallCount).isEqualTo(0)
    }

    @Test
    fun onPointerInputChanges_moveUnderToPostUpThenModOverInOppDir_onTouchSlopExceededNotCalled() {

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)

        val move = down().moveBy(10.milliseconds, TestTouchSlop.toFloat(), 0f)
        mRecognizer::onPointerInputChanges.invokeOverPasses(
            listOf(move),
            PointerEventPass.InitialDown,
            PointerEventPass.PreUp,
            PointerEventPass.PreDown,
            PointerEventPass.PostUp
        )
        val move2 = move.consume(dx = (TestTouchSlop * 2f + 1))
        mRecognizer::onPointerInputChanges.invokeOverPasses(
            listOf(move2),
            PointerEventPass.PostDown
        )

        // Assert

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    // TODO(b/129701831): This test assumes that if a pointer moves by slop in both x and y, we are
    // still under slop even though sqrt(slop^2 + slop^2) > slop.  This may be inaccurate and this
    // test may therefore need to be updated.
    @Test
    fun onPointerInputChanges_moveAroundWithinSlop_onTouchSlopExceededNotCalled() {
        val slop = TestTouchSlop.toFloat()

        var change = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)

        // Go around the border of the touch slop area

        // To top left
        change = change.moveTo(10L.millisecondsToTimestamp(), -slop, -slop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)
        // To bottom left
        change = change.moveTo(20L.millisecondsToTimestamp(), -slop, slop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)
        // To bottom right
        change = change.moveTo(30L.millisecondsToTimestamp(), slop, slop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)
        // To top right
        change = change.moveTo(40L.millisecondsToTimestamp(), slop, -slop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)

        // Jump from corner to opposite corner and back

        // To bottom left
        change = change.moveTo(50L.millisecondsToTimestamp(), -slop, slop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)
        // To top right
        change = change.moveTo(60L.millisecondsToTimestamp(), slop, -slop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)

        // Move the other diagonal

        // To top left
        change = change.moveTo(70L.millisecondsToTimestamp(), -slop, -slop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)

        // Jump from corner to opposite corner and back

        // To bottom right
        change = change.moveTo(80L.millisecondsToTimestamp(), slop, slop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)
        // To top left
        change = change.moveTo(90L.millisecondsToTimestamp(), -slop, -slop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)

        assertThat(onTouchSlopExceededCallCount).isEqualTo(0)
    }

    // Verify the circumstances under which onTouchSlopExceeded should be called.

    @Test
    fun onPointerInputChanges_movePassedSlop_onTouchSlopExceededCallOnce() {
        val beyondTouchSlop = (TestTouchSlop + 1).toFloat()

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        val move = down.moveBy(
            Duration(milliseconds = 100),
            beyondTouchSlop,
            0f
        )
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_movePassedSlopIn2Events_onTouchSlopExceededCallOnce() {

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        val move = down.moveBy(
            Duration(milliseconds = 100),
            TestTouchSlop.toFloat(),
            0f
        )
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)
        val move2 = down.moveBy(
            Duration(milliseconds = 100),
            1f,
            0f
        )
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move2)

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_passSlopThenInSlopAreaThenOut_onTouchSlopExceededCallOnce() {
        val beyondTouchSlop = (TestTouchSlop + 1).toFloat()

        var event = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(event)
        // Out of touch slop region
        event = event.moveBy(Duration(milliseconds = 10), 0f, beyondTouchSlop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(event)
        // Back into touch slop region
        event = event.moveBy(Duration(milliseconds = 10), 0f, -beyondTouchSlop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(event)
        // Out of touch slop region again
        event = event.moveBy(Duration(milliseconds = 10), 0f, beyondTouchSlop)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(event)

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_downConsumedMovePassedSlop_onTouchSlopExceededCallOnce() {
        val beyondTouchSlop = (TestTouchSlop + 1).toFloat()

        val down = down().consumeDownChange()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)
        val move = down.moveBy(Duration(milliseconds = 100), beyondTouchSlop, 0f)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_beyondInUnsupportThenBeyondInSupport_onTouchSlopExceededCallOnce() {
        val doubleTouchSlop = (TestTouchSlop * 2).toFloat()
        val beyondTouchSlop = (TestTouchSlop + 1).toFloat()

        var change = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)
        canDragReturn = false
        change = change.moveBy(
            Duration(milliseconds = 10),
            0f,
            doubleTouchSlop
        )
        // Sanity check that onTouchSlopExceeded has not been called.
        assertThat(onTouchSlopExceededCallCount).isEqualTo(0)

        canDragReturn = true
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)
        change = change.moveBy(
            Duration(milliseconds = 10),
            0f,
            -beyondTouchSlop
        )
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(change)

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_2PointsMoveInOpposite_onTouchSlopExceededCallOnce() {

        // Arrange

        val beyondTouchSlop = (TestTouchSlop + 1).toFloat()

        var pointer1 = down(1)
        var pointer2 = down(2)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(pointer1, pointer2)

        // Act

        pointer1 = pointer1.moveBy(
            Duration(milliseconds = 100),
            beyondTouchSlop,
            0f
        )
        pointer2 = pointer2.moveBy(
            Duration(milliseconds = 100),
            -beyondTouchSlop,
            0f
        )
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(pointer1, pointer2)

        // Assert

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_3PointsMoveAverage0_onTouchSlopExceededCallOnce() {

        // Arrange

        val beyondTouchSlop = (TestTouchSlop + 1).toFloat()

        val pointers = arrayListOf(down(0), down(1), down(2))
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(pointers)

        // Act

        // These movements average to no movement.
        pointers[0] =
            pointers[0].moveBy(
                Duration(milliseconds = 100),
                beyondTouchSlop * -1,
                beyondTouchSlop * -1
            )
        pointers[1] =
            pointers[1].moveBy(
                Duration(milliseconds = 100),
                beyondTouchSlop * 1,
                beyondTouchSlop * -1
            )
        pointers[2] =
            pointers[2].moveBy(
                Duration(milliseconds = 100),
                0f,
                beyondTouchSlop * 2
            )
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(pointers)

        // Assert

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_5Points1MoveBeyondSlop_onTouchSlopExceededCallOnce() {

        // Arrange

        val beyondTouchSlop = (TestTouchSlop + 1).toFloat()

        val pointers = arrayListOf(down(0), down(1), down(2), down(3), down(4))
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(pointers)

        // Act

        // These movements average to no movement.
        for (i in 0..3) {
            pointers[i] = pointers[i].moveBy(
                Duration(milliseconds = 100),
                0f,
                0f
            )
        }
        pointers[4] =
            pointers[4].moveBy(
                Duration(milliseconds = 100),
                beyondTouchSlop * -1,
                0f
            )
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(pointers)

        // Assert

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_1PointMovesBeyondSlopAndThenManyTimes_onTouchSlopExceededCallOnce() {

        // Arrange

        val beyondTouchSlop = (TestTouchSlop + 1).toFloat()

        var pointer = down(0)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(pointer)

        // Act

        repeat(5) {
            pointer = pointer.moveBy(100.milliseconds, beyondTouchSlop, beyondTouchSlop)
            mRecognizer::onPointerInputChanges.invokeOverAllPasses(pointer)
        }

        // Assert

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_1ModifiedToMoveBeyondSlopBeforePostUp_onTouchSlopExceededCallOnce() {

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)

        val move = down().moveBy(10.milliseconds, 0f, 0f).consume(dx = TestTouchSlop + 1f)
        mRecognizer::onPointerInputChanges.invokeOverPasses(
            listOf(move),
            PointerEventPass.InitialDown,
            PointerEventPass.PreUp,
            PointerEventPass.PreDown,
            PointerEventPass.PostUp
        )

        // Assert

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_1ModedToMoveBeyondSlopBeforePostDown_onTouchSlopExceededCallOnce() {

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)

        val move = down().moveBy(10.milliseconds, 0f, 0f)
        mRecognizer::onPointerInputChanges.invokeOverPasses(
            listOf(move),
            PointerEventPass.InitialDown,
            PointerEventPass.PreUp,
            PointerEventPass.PreDown,
            PointerEventPass.PostUp
        )

        val moveConsumed = move.consume(dx = TestTouchSlop + 1f)
        mRecognizer::onPointerInputChanges.invokeOverPasses(
            listOf(moveConsumed),
            PointerEventPass.PostDown
        )

        // Assert

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_moveUnderToPostUpThenModOverToPostDown_onTouchSlopExceededCallOnce() {

        val halfSlop = TestTouchSlop / 2
        val restOfSlopAndBeyond = TestTouchSlop - halfSlop + 1

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)

        val move = down().moveBy(10.milliseconds, halfSlop.toFloat(), 0f)
        mRecognizer::onPointerInputChanges.invokeOverPasses(
            listOf(move),
            PointerEventPass.InitialDown,
            PointerEventPass.PreUp,
            PointerEventPass.PreDown,
            PointerEventPass.PostUp
        )

        val moveConsumed = move.consume(dx = -restOfSlopAndBeyond.toFloat())
        mRecognizer::onPointerInputChanges.invokeOverPasses(
            listOf(moveConsumed),
            PointerEventPass.PostDown
        )

        // Assert

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    @Test
    fun onPointerInputChanges_moveBeyondSlopAllPassesUpToPostUp_onTouchSlopExceededCallOnce() {

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)

        val move = down().moveBy(10.milliseconds, TestTouchSlop + 1f, 0f)
        mRecognizer::onPointerInputChanges.invokeOverPasses(
            listOf(move),
            PointerEventPass.InitialDown,
            PointerEventPass.PreUp,
            PointerEventPass.PreDown,
            PointerEventPass.PostUp
        )

        // Assert

        assertThat(onTouchSlopExceededCallCount).isEqualTo(1)
    }

    // Verification that TouchSlopExceededGestureDetector does not consume any changes.

    @Test
    fun onPointerInputChanges_1Down_nothingConsumed() {

        val result = mRecognizer::onPointerInputChanges.invokeOverAllPasses(down())

        // Assert

        assertThat(result[0].consumed.downChange).isFalse()
        assertThat(result[0].consumed.positionChange.x).isEqualTo(0.px)
        assertThat(result[0].consumed.positionChange.y).isEqualTo(0.px)
    }

    @Test
    fun onPointerInputChanges_1MoveUnderSlop_nothingConsumed() {

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)

        val move = down().moveBy(10.milliseconds, TestTouchSlop.toFloat(), TestTouchSlop.toFloat())
        val result = mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        // Assert

        assertThat(result[0].consumed.downChange).isFalse()
        assertThat(result[0].consumed.positionChange.x).isEqualTo(0.px)
        assertThat(result[0].consumed.positionChange.y).isEqualTo(0.px)
    }

    @Test
    fun onPointerInputChanges_1MoveUnderSlopThenUp_nothingConsumed() {

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)

        val move = down().moveBy(10.milliseconds, TestTouchSlop.toFloat(), TestTouchSlop.toFloat())
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        val up = move.up(20L.millisecondsToTimestamp())
        val result = mRecognizer::onPointerInputChanges.invokeOverAllPasses(up)

        // Assert

        assertThat(result[0].consumed.downChange).isFalse()
        assertThat(result[0].consumed.positionChange.x).isEqualTo(0.px)
        assertThat(result[0].consumed.positionChange.y).isEqualTo(0.px)
    }

    @Test
    fun onPointerInputChanges_1MoveOverSlop_nothingConsumed() {

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)

        val move = down().moveBy(10.milliseconds, TestTouchSlop + 1f, TestTouchSlop + 1f)
        val result = mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        // Assert

        assertThat(result[0].consumed.downChange).isFalse()
        assertThat(result[0].consumed.positionChange.x).isEqualTo(0.px)
        assertThat(result[0].consumed.positionChange.y).isEqualTo(0.px)
    }

    @Test
    fun onPointerInputChanges_1MoveOverSlopThenUp_nothingConsumed() {

        val down = down()
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)

        val move = down().moveBy(10.milliseconds, TestTouchSlop + 1f, TestTouchSlop + 1f)
        mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

        val up = move.up(20L.millisecondsToTimestamp())
        val result = mRecognizer::onPointerInputChanges.invokeOverAllPasses(up)

        // Assert

        assertThat(result[0].consumed.downChange).isFalse()
        assertThat(result[0].consumed.positionChange.x).isEqualTo(0.px)
        assertThat(result[0].consumed.positionChange.y).isEqualTo(0.px)
    }

    // Verification that TouchSlopExceededGestureDetector resets after up correctly.

    @Test
    fun onPointerInputChanges_MoveBeyondUpDownMoveBeyond_onTouchSlopExceededCalledTwice() {

        repeat(2) {
            val down = down()
            mRecognizer::onPointerInputChanges.invokeOverAllPasses(down)

            val move = down().moveBy(10.milliseconds, TestTouchSlop + 1f, 0f)
            mRecognizer::onPointerInputChanges.invokeOverAllPasses(move)

            val up = move.up(20L.millisecondsToTimestamp())
            mRecognizer::onPointerInputChanges.invokeOverAllPasses(up)
        }

        assertThat(onTouchSlopExceededCallCount).isEqualTo(2)
    }
}