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

package androidx.ui.test

import androidx.compose.Composable
import androidx.ui.core.Density
import androidx.ui.core.DensityScope
import androidx.ui.core.Dp
import androidx.ui.core.IntPx
import androidx.ui.core.OnChildPositioned
import androidx.ui.core.PxSize
import androidx.ui.core.dp
import androidx.ui.core.round
import androidx.ui.core.withDensity
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.Wrap

/**
 * Constant to emulate very big but finite constraints
 */
val BigTestConstraints = DpConstraints(maxWidth = 5000.dp, maxHeight = 5000.dp)

/**
 * Set content as with [ComposeTestRule.setContent], but return sizes of this content
 *
 * @param parentConstraints desired parent constraints for content
 * @param performSetContent lambda that should be performed to set content.
 * Defaults to [ComposeTestRule.setContent] and usually don't need to be changed
 * @param children content to set
 */
fun ComposeTestRule.setContentAndGetPixelSize(
    parentConstraints: DpConstraints = BigTestConstraints,
    // TODO : figure out better way to make it flexible
    performSetContent: (@Composable() () -> Unit) -> Unit = { setContent(it) },
    children: @Composable() () -> Unit
): PxSize {
    var realSize: PxSize? = null
    performSetContent {
        Wrap {
            ConstrainedBox(constraints = parentConstraints) {
                OnChildPositioned(onPositioned = { coordinates ->
                    realSize = coordinates.size
                }) {
                    children()
                }
            }
        }
    }
    return realSize!!
}

/**
 * Set content as with [ComposeTestRule.setContent], but return [CollectedSizes] to assert
 * width and height of this content
 *
 * @param parentConstraints desired parent constraints for content
 * @param children content to set
 */
fun ComposeTestRule.setContentAndCollectSizes(
    parentConstraints: DpConstraints = BigTestConstraints,
    children: @Composable() () -> Unit
): CollectedSizes {
    val size = setContentAndGetPixelSize(parentConstraints, { setContent(it) }, children)
    return CollectedSizes(size, density)
}

/**
 * Small utility class to provide convenient assertion for width and height for some [PxSize].
 * It also provides [DensityScope] while asserting.
 *
 * @see ComposeTestRule.setContentAndCollectSizes
 */
class CollectedSizes(private val size: PxSize, private val density: Density) {
    fun assertHeightEqualsTo(expectedHeight: Dp) =
        assertHeightEqualsTo { expectedHeight.toIntPx() }

    fun assertWidthEqualsTo(expectedWidth: Dp): CollectedSizes =
        assertWidthEqualsTo { expectedWidth.toIntPx() }

    fun assertIsSquareWithSize(expectedSize: Dp) = assertIsSquareWithSize { expectedSize.toIntPx() }

    fun assertWidthEqualsTo(expectedWidthPx: DensityScope.() -> IntPx): CollectedSizes {
        val widthPx = withDensity(density) {
            expectedWidthPx()
        }
        assertSize(size.width.round(), widthPx)
        return this
    }

    fun assertHeightEqualsTo(expectedHeightPx: DensityScope.() -> IntPx): CollectedSizes {
        val heightPx = withDensity(density) {
            expectedHeightPx()
        }
        assertSize(size.height.round(), heightPx)
        return this
    }

    fun assertIsSquareWithSize(expectedSquarePx: DensityScope.() -> IntPx): CollectedSizes {
        val squarePx = withDensity(density) {
            expectedSquarePx()
        }
        assertSize(size.width.round(), squarePx)
        assertSize(size.height.round(), squarePx)
        return this
    }
}

private fun assertSize(actual: IntPx, expected: IntPx) {
    if (actual != expected) {
        throw AssertionError("Found size: $actual pixels.\nExpected size $expected pixels")
    }
}