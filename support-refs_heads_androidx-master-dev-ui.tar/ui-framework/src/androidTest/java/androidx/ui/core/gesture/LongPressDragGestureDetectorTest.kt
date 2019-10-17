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

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.test.rule.ActivityTestRule
import androidx.ui.core.ipx
import androidx.ui.core.setContent
import androidx.ui.framework.test.TestActivity
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import androidx.ui.core.PxPosition
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import androidx.test.filters.LargeTest
import androidx.ui.core.Layout

@LargeTest
@RunWith(JUnit4::class)
class LongPressDragGestureDetectorTest {
    @get:Rule
    val activityTestRule = ActivityTestRule<TestActivity>(TestActivity::class.java)
    private lateinit var longPressDragObserver: LongPressDragObserver
    private lateinit var longPressCountDownLatch: CountDownLatch
    private lateinit var view: View

    @Before
    fun setup() {
        longPressDragObserver = spy(MyLongPressDragObserver {
            longPressCountDownLatch.countDown()
        })

        val activity = activityTestRule.activity
        assertTrue(activity.hasFocusLatch.await(5, TimeUnit.SECONDS))

        val setupLatch = CountDownLatch(2)
        activityTestRule.runOnUiThreadIR {
            activity.setContent {
                LongPressDragGestureDetector(longPressDragObserver) {
                    Layout(
                        measureBlock = { _, _ ->
                            layout(100.ipx, 100.ipx) {
                                setupLatch.countDown()
                            }
                        }, children = {}
                    )
                }
            }

            view = activity.findViewById<ViewGroup>(android.R.id.content)
            setupLatch.countDown()
        }
        assertTrue(setupLatch.await(1000, TimeUnit.SECONDS))
    }

    // Tests that verify conditions under which nothing will be called.

    @Test
    fun ui_downMoveUpBeforeLongPressTimeout_noCallbacksCalled() {

        val down = MotionEvent(
            0,
            MotionEvent.ACTION_DOWN,
            1,
            0,
            arrayOf(PointerProperties(0)),
            arrayOf(PointerCoords(50f, 50f))
        )
        val move = MotionEvent(
            0,
            MotionEvent.ACTION_MOVE,
            1,
            0,
            arrayOf(PointerProperties(0)),
            arrayOf(PointerCoords(51f, 50f))
        )
        val up = MotionEvent(
            0,
            MotionEvent.ACTION_UP,
            1,
            0,
            arrayOf(PointerProperties(0)),
            arrayOf(PointerCoords(51f, 50f))
        )
        activityTestRule.runOnUiThreadIR {
            view.dispatchTouchEvent(down)
            view.dispatchTouchEvent(move)
            view.dispatchTouchEvent(up)
        }

        verifyNoMoreInteractions(longPressDragObserver)
    }

    // Tests that verify conditions under which onLongPress will only be called.

    @Test
    fun ui_downWaitForLongPress_onLongPressCalled() {

        val down = MotionEvent(
            0,
            MotionEvent.ACTION_DOWN,
            1,
            0,
            arrayOf(PointerProperties(0)),
            arrayOf(PointerCoords(50f, 50f))
        )
        waitForLongPress {
            view.dispatchTouchEvent(down)
        }

        verify(longPressDragObserver).onLongPress(any())
        verifyNoMoreInteractions(longPressDragObserver)
    }

    // Tests that verify conditions under which onDragStart and onDrag will be called.

    @Test
    fun ui_downWaitForLongPressMove_onDragStartAndOnDragCalled() {

        // Arrange.
        val down = MotionEvent(
            0,
            MotionEvent.ACTION_DOWN,
            1,
            0,
            arrayOf(PointerProperties(0)),
            arrayOf(PointerCoords(50f, 50f))
        )
        waitForLongPress {
            view.dispatchTouchEvent(down)
        }
        reset(longPressDragObserver)

        // Act.
        val move = MotionEvent(
            0,
            MotionEvent.ACTION_MOVE,
            1,
            0,
            arrayOf(PointerProperties(0)),
            arrayOf(PointerCoords(51f, 50f))
        )
        view.dispatchTouchEvent(move)

        // Assert.
        verify(longPressDragObserver).onDragStart()
        // Twice because DragGestureDetector dispatches onDrag during 2 passes.
        verify(longPressDragObserver, times(2)).onDrag(any())
        verifyNoMoreInteractions(longPressDragObserver)
    }

    // Tests that verify conditions under which onStop will be called.

    @Test
    fun ui_downWaitForLongPressMoveUp_onDragStopCalled() {

        // Arrange.
        val down = MotionEvent(
            0,
            MotionEvent.ACTION_DOWN,
            1,
            0,
            arrayOf(PointerProperties(0)),
            arrayOf(PointerCoords(50f, 50f))
        )
        waitForLongPress {
            view.dispatchTouchEvent(down)
        }
        val move = MotionEvent(
            0,
            MotionEvent.ACTION_MOVE,
            1,
            0,
            arrayOf(PointerProperties(0)),
            arrayOf(PointerCoords(51f, 50f))
        )
        view.dispatchTouchEvent(move)
        reset(longPressDragObserver)

        // Act.
        val up = MotionEvent(
            0,
            MotionEvent.ACTION_UP,
            1,
            0,
            arrayOf(PointerProperties(0)),
            arrayOf(PointerCoords(51f, 50f))
        )
        view.dispatchTouchEvent(up)

        // Assert.
        verify(longPressDragObserver).onStop(any())
        verifyNoMoreInteractions(longPressDragObserver)
    }

    @Test
    fun ui_downWaitForLongPressUp_onDragStopCalled() {

        // Arrange.
        val down = MotionEvent(
            0,
            MotionEvent.ACTION_DOWN,
            1,
            0,
            arrayOf(PointerProperties(0)),
            arrayOf(PointerCoords(50f, 50f))
        )
        waitForLongPress {
            view.dispatchTouchEvent(down)
        }
        reset(longPressDragObserver)

        // Act.
        val up = MotionEvent(
            0,
            MotionEvent.ACTION_UP,
            1,
            0,
            arrayOf(PointerProperties(0)),
            arrayOf(PointerCoords(51f, 50f))
        )
        view.dispatchTouchEvent(up)

        // Assert.
        verify(longPressDragObserver).onStop(any())
        verifyNoMoreInteractions(longPressDragObserver)
    }

    private fun waitForLongPress(block: () -> Unit) {
        longPressCountDownLatch = CountDownLatch(1)
        activityTestRule.runOnUiThreadIR {
            block()
        }
        assertTrue(longPressCountDownLatch.await(750, TimeUnit.MILLISECONDS))
    }
}

@Suppress("RedundantOverride")
open class MyLongPressDragObserver(val onLongPress: () -> Unit) : LongPressDragObserver {
    override fun onLongPress(pxPosition: PxPosition) {
        onLongPress()
    }

    override fun onDragStart() { }

    override fun onDrag(dragDistance: PxPosition): PxPosition {
        return super.onDrag(dragDistance)
    }

    override fun onStop(velocity: PxPosition) { }
}

// We only need this because IR compiler doesn't like converting lambdas to Runnables
fun ActivityTestRule<*>.runOnUiThreadIR(block: () -> Unit) {
    val runnable: Runnable = object : Runnable {
        override fun run() {
            block()
        }
    }
    runOnUiThread(runnable)
}

private fun MotionEvent(
    eventTime: Int,
    action: Int,
    numPointers: Int,
    actionIndex: Int,
    pointerProperties: Array<MotionEvent.PointerProperties>,
    pointerCoords: Array<MotionEvent.PointerCoords>
) = MotionEvent.obtain(
    0,
    eventTime.toLong(),
    action + (actionIndex shl MotionEvent.ACTION_POINTER_INDEX_SHIFT),
    numPointers,
    pointerProperties,
    pointerCoords,
    0,
    0,
    0f,
    0f,
    0,
    0,
    0,
    0
)

@Suppress("RemoveRedundantQualifierName")
private fun PointerProperties(id: Int) =
    MotionEvent.PointerProperties().apply { this.id = id }

@Suppress("RemoveRedundantQualifierName")
private fun PointerCoords(x: Float, y: Float) =
    MotionEvent.PointerCoords().apply {
        this.x = x
        this.y = y
    }