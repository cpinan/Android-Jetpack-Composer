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
package androidx.ui.foundation

import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.test.filters.SmallTest
import androidx.ui.core.Draw
import androidx.ui.core.IntPx
import androidx.ui.core.Px
import androidx.ui.core.ipx
import androidx.ui.core.px
import androidx.ui.core.toRect
import androidx.ui.core.withDensity
import androidx.ui.layout.Align
import androidx.ui.layout.Column
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.PaintingStyle
import androidx.test.filters.SdkSuppress
import androidx.ui.core.Alignment
import androidx.ui.core.AndroidComposeView
import androidx.ui.core.setContent
import androidx.ui.graphics.toArgb
import androidx.ui.layout.Row
import androidx.ui.test.android.AndroidComposeTestRule
import androidx.ui.test.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SmallTest
@RunWith(JUnit4::class)
class ScrollerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // TODO(malkov/pavlis) : some tests here require activity access as we need
    // to take screen's bitmap, abstract it better
    val activity
        get() = (composeTestRule as AndroidComposeTestRule).activityTestRule.activity

    val colors = listOf(
        Color(alpha = 0xFF, red = 0xFF, green = 0, blue = 0),
        Color(alpha = 0xFF, red = 0xFF, green = 0xA5, blue = 0),
        Color(alpha = 0xFF, red = 0xFF, green = 0xFF, blue = 0),
        Color(alpha = 0xFF, red = 0xA5, green = 0xFF, blue = 0),
        Color(alpha = 0xFF, red = 0, green = 0xFF, blue = 0),
        Color(alpha = 0xFF, red = 0, green = 0xFF, blue = 0xA5),
        Color(alpha = 0xFF, red = 0, green = 0, blue = 0xFF),
        Color(alpha = 0xFF, red = 0xA5, green = 0, blue = 0xFF)
    )

    var drawLatch = CountDownLatch(1)
    lateinit var handler: Handler

    @Before
    fun setupDrawLatch() {
        drawLatch = CountDownLatch(1)
        composeTestRule.runOnUiThread {
            handler = Handler()
        }
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun verticalScroller_SmallContent() {
        composeVerticalScroller()

        validateVerticalScroller(0, 40)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun verticalScroller_SmallContent_Unscrollable() {
        var max = Px.Infinity

        // latch to wait for a new max to come
        val newMaxLatch = CountDownLatch(1)

        composeVerticalScroller(
            onScrollChanged = { _, newMax ->
                max = newMax
                newMaxLatch.countDown()
            }
        )
        newMaxLatch.await(1, TimeUnit.SECONDS)
        assertTrue(max == 0.px)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun verticalScroller_LargeContent_NoScroll() {
        composeVerticalScroller(height = 30.ipx)

        validateVerticalScroller(0, 30)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun verticalScroller_LargeContent_ScrollToEnd() {
        val scrollerPosition = ScrollerPosition()

        val changeListener = ScrollerChangeListener(scrollerPosition)
        composeVerticalScroller(scrollerPosition, changeListener, height = 30.ipx)

        validateVerticalScroller(0, 30)

        // The 'draw' method will no longer be called because only the position
        // changes during scrolling. Therefore, we should just wait until the draw stage
        // completes and the scrolling will be finished by then.
        val latch = CountDownLatch(1)
        val onDrawListener = object : ViewTreeObserver.OnDrawListener {
            override fun onDraw() {
                latch.countDown()
            }
        }
        composeTestRule.runOnUiThread {
            activity.window.decorView.viewTreeObserver.addOnDrawListener(onDrawListener)
            assertEquals(10.px, changeListener.maxPosition)
            scrollerPosition.value = 10.px
        }
        assertTrue(latch.await(1, TimeUnit.SECONDS))
        composeTestRule.runOnUiThread {
            activity.window.decorView.viewTreeObserver.removeOnDrawListener(onDrawListener)
        }
        validateVerticalScroller(10, 30)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun horizontalScroller_SmallContent() {
        composeHorizontalScroller()

        validateHorizontalScroller(0, 40)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun horizontalScroller_LargeContent_NoScroll() {
        composeHorizontalScroller(width = 30.ipx)

        validateHorizontalScroller(0, 30)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun horizontalScroller_LargeContent_ScrollToEnd() {
        val scrollerPosition = ScrollerPosition()

        val changeListener = ScrollerChangeListener(scrollerPosition)
        composeHorizontalScroller(scrollerPosition, changeListener, width = 30.ipx)

        validateHorizontalScroller(0, 30)

        // The 'draw' method will no longer be called because only the position
        // changes during scrolling. Therefore, we should just wait until the draw stage
        // completes and the scrolling will be finished by then.
        val latch = CountDownLatch(1)
        val onDrawListener = object : ViewTreeObserver.OnDrawListener {
            override fun onDraw() {
                latch.countDown()
            }
        }
        composeTestRule.runOnUiThread {
            activity.window.decorView.viewTreeObserver.addOnDrawListener(onDrawListener)
            assertEquals(10.px, changeListener.maxPosition)
            scrollerPosition.value = 10.px
        }
        assertTrue(latch.await(1, TimeUnit.SECONDS))
        composeTestRule.runOnUiThread {
            activity.window.decorView.viewTreeObserver.removeOnDrawListener(onDrawListener)
        }
        validateHorizontalScroller(10, 30)
    }

    private fun composeVerticalScroller(
        scrollerPosition: ScrollerPosition = ScrollerPosition(),
        onScrollChanged: (position: Px, max: Px) -> Unit = { position, _ ->
            scrollerPosition.value = position
        },
        height: IntPx = 40.ipx
    ) {
        // We assume that the height of the device is more than 45 px
        withDensity(composeTestRule.density) {
            val constraints = DpConstraints.tightConstraints(45.px.toDp(), height.toDp())
            composeTestRule.runOnUiThread {
                activity.setContent {
                    Align(alignment = Alignment.TopLeft) {
                        ConstrainedBox(constraints = constraints) {
                            VerticalScroller(
                                scrollerPosition = scrollerPosition,
                                onScrollPositionChanged = onScrollChanged
                            ) {
                                Column(crossAxisAlignment = CrossAxisAlignment.Start) {
                                    colors.forEach { color ->
                                        Container(
                                            height = 5.px.toDp(),
                                            width = 45.px.toDp()
                                        ) {
                                            Draw { canvas, parentSize ->
                                                val paint = Paint()
                                                paint.color = color
                                                paint.style = PaintingStyle.fill
                                                canvas.drawRect(parentSize.toRect(), paint)
                                            }
                                        }
                                    }
                                }
                            }
                            Draw { _, _ ->
                                drawLatch.countDown()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun composeHorizontalScroller(
        scrollerPosition: ScrollerPosition = ScrollerPosition(),
        onScrollChanged: (position: Px, max: Px) -> Unit = { position, _ ->
            scrollerPosition.value = position
        },
        width: IntPx = 40.ipx
    ) {
        // We assume that the height of the device is more than 45 px
        withDensity(composeTestRule.density) {
            val constraints = DpConstraints.tightConstraints(width.toDp(), 45.px.toDp())
            composeTestRule.runOnUiThread {
                activity.setContent {
                    Align(alignment = Alignment.TopLeft) {
                        ConstrainedBox(constraints = constraints) {
                            HorizontalScroller(
                                scrollerPosition = scrollerPosition,
                                onScrollPositionChanged = onScrollChanged
                            ) {
                                Row(crossAxisAlignment = CrossAxisAlignment.Start) {
                                    colors.forEach { color ->
                                        Container(
                                            width = 5.px.toDp(),
                                            height = 45.px.toDp()
                                        ) {
                                            Draw { canvas, parentSize ->
                                                val paint = Paint()
                                                paint.color = color
                                                paint.style = PaintingStyle.fill
                                                canvas.drawRect(parentSize.toRect(), paint)
                                            }
                                        }
                                    }
                                }
                            }
                            Draw { _, _ ->
                                drawLatch.countDown()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateVerticalScroller(
        offset: Int,
        height: Int,
        width: Int = 45
    ) {
        assertTrue(drawLatch.await(1, TimeUnit.SECONDS))

        val bitmap = waitAndScreenShot()
        assertTrue(bitmap.height >= height)
        assertTrue(bitmap.width >= 45)
        for (y in 0 until height) {
            val colorIndex = (offset + y) / 5
            val expectedColor = colors[colorIndex]

            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                assertEquals(
                    "Expected $expectedColor, but got ${Color(pixel)} at $x, $y",
                    expectedColor.toArgb(), pixel
                )
            }
        }
    }

    private fun validateHorizontalScroller(
        offset: Int,
        width: Int,
        height: Int = 45
    ) {
        assertTrue(drawLatch.await(1, TimeUnit.SECONDS))

        val bitmap = waitAndScreenShot()
        assertTrue(bitmap.height >= 45)
        assertTrue(bitmap.width >= width)
        for (x in 0 until width) {
            val colorIndex = (offset + x) / 5
            val expectedColor = colors[colorIndex]

            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                assertEquals(
                    "Expected $expectedColor, but got ${Color(pixel)} at $x, $y",
                    expectedColor.toArgb(), pixel
                )
            }
        }
    }

    private fun waitAndScreenShot(): Bitmap {
        val view = findAndroidComposeView()
        waitForDraw(view)

        val offset = intArrayOf(0, 0)
        val width = view.width
        val height = view.height

        val dest =
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val srcRect = android.graphics.Rect(0, 0, width, height)
        srcRect.offset(offset[0], offset[1])
        val latch = CountDownLatch(1)
        var copyResult = 0
        val onCopyFinished = object : PixelCopy.OnPixelCopyFinishedListener {
            override fun onPixelCopyFinished(result: Int) {
                copyResult = result
                latch.countDown()
            }
        }
        PixelCopy.request(activity.window, srcRect, dest, onCopyFinished, handler)
        assertTrue(latch.await(1, TimeUnit.SECONDS))
        assertEquals(PixelCopy.SUCCESS, copyResult)
        return dest
    }

    /**
     * Listener that waits for changes to a [ScrollerPosition].
     */
    private class ScrollerChangeListener(val scrollerPosition: ScrollerPosition) :
            (Px, Px) -> Unit {
        private var changeCalls = 0
        private val lock = Object()
        var maxPosition: Px = 0.px

        override fun invoke(position: Px, maxPosition: Px) {
            synchronized(lock) {
                changeCalls++
                lock.notify()
            }
            scrollerPosition.value = position
            this.maxPosition = maxPosition
        }

        fun waitForChange() {
            synchronized(lock) {
                if (changeCalls == 0) {
                    lock.wait(1000)
                    assertNotEquals(0, changeCalls)
                }
                changeCalls--
            }
        }
    }

    // TODO(malkov): ALL below is copypaste from LayoutTest as this test in ui-foundation now

    internal fun findAndroidComposeView(): AndroidComposeView {
        val contentViewGroup = activity.findViewById<ViewGroup>(android.R.id.content)
        return findAndroidComposeView(contentViewGroup)!!
    }

    internal fun findAndroidComposeView(parent: ViewGroup): AndroidComposeView? {
        for (index in 0 until parent.childCount) {
            val child = parent.getChildAt(index)
            if (child is AndroidComposeView) {
                return child
            } else if (child is ViewGroup) {
                val composeView = findAndroidComposeView(child)
                if (composeView != null) {
                    return composeView
                }
            }
        }
        return null
    }

    internal fun waitForDraw(view: View) {
        val viewDrawLatch = CountDownLatch(1)
        val listener = object : ViewTreeObserver.OnDrawListener {
            override fun onDraw() {
                viewDrawLatch.countDown()
            }
        }
        view.post(object : Runnable {
            override fun run() {
                view.viewTreeObserver.addOnDrawListener(listener)
                view.invalidate()
            }
        })
        assertTrue(viewDrawLatch.await(1, TimeUnit.SECONDS))
    }
}
