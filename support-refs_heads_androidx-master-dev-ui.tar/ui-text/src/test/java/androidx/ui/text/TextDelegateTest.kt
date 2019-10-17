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

package androidx.ui.text

import androidx.ui.core.Density
import androidx.ui.core.LayoutDirection
import androidx.ui.graphics.Canvas
import androidx.ui.text.font.Font
import androidx.ui.text.style.TextDirectionAlgorithm
import androidx.ui.text.style.TextOverflow
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TextDelegateTest() {
    private val density = Density(density = 1f)
    private val resourceLoader = mock<Font.ResourceLoader>()

    @Test
    fun `constructor with default values`() {
        val textDelegate = TextDelegate(
            text = AnnotatedString(text = ""),
            density = density,
            resourceLoader = resourceLoader,
            layoutDirection = LayoutDirection.Ltr
        )

        assertThat(textDelegate.maxLines).isNull()
        assertThat(textDelegate.overflow).isEqualTo(TextOverflow.Clip)
    }

    @Test
    fun `constructor with customized text(TextSpan)`() {
        val text = AnnotatedString("Hello")
        val textDelegate = TextDelegate(
            text = text,
            density = density,
            resourceLoader = resourceLoader,
            layoutDirection = LayoutDirection.Ltr
        )

        assertThat(textDelegate.text).isEqualTo(text)
    }

    @Test
    fun `constructor with customized maxLines`() {
        val maxLines = 8

        val textDelegate = TextDelegate(
            text = AnnotatedString(text = ""),
            maxLines = maxLines,
            density = density,
            resourceLoader = resourceLoader,
            layoutDirection = LayoutDirection.Ltr
        )

        assertThat(textDelegate.maxLines).isEqualTo(maxLines)
    }

    @Test
    fun `constructor with customized overflow`() {
        val overflow = TextOverflow.Ellipsis

        val textDelegate = TextDelegate(
            text = AnnotatedString(text = ""),
            overflow = overflow,
            density = density,
            resourceLoader = resourceLoader,
            layoutDirection = LayoutDirection.Ltr
        )

        assertThat(textDelegate.overflow).isEqualTo(overflow)
    }

    @Test(expected = AssertionError::class)
    fun `minIntrinsicWidth without layout assertion should fail`() {
        val textDelegate = TextDelegate(
            text = AnnotatedString(text = ""),
            density = density,
            resourceLoader = resourceLoader,
            layoutDirection = LayoutDirection.Ltr)

        textDelegate.minIntrinsicWidth
    }

    @Test(expected = AssertionError::class)
    fun `maxIntrinsicWidth without layout assertion should fail`() {
        val textDelegate = TextDelegate(
            text = AnnotatedString(text = ""),
            density = density,
            resourceLoader = resourceLoader,
            layoutDirection = LayoutDirection.Ltr)

        textDelegate.maxIntrinsicWidth
    }

    @Test(expected = AssertionError::class)
    fun `width without layout assertion should fail`() {
        val textDelegate = TextDelegate(
            text = AnnotatedString(text = ""),
            density = density,
            resourceLoader = resourceLoader,
            layoutDirection = LayoutDirection.Ltr)

        textDelegate.width
    }

    @Test(expected = AssertionError::class)
    fun `height without layout assertion should fail`() {
        val textDelegate = TextDelegate(
            text = AnnotatedString(text = ""),
            density = density,
            resourceLoader = resourceLoader,
            layoutDirection = LayoutDirection.Ltr)

        textDelegate.height
    }

    @Test(expected = AssertionError::class)
    fun `paint without layout assertion should fail`() {
        val textDelegate = TextDelegate(
            text = AnnotatedString(text = ""),
            density = density,
            resourceLoader = resourceLoader,
            layoutDirection = LayoutDirection.Ltr)
        val canvas = mock<Canvas>()

        textDelegate.paint(canvas)
    }

    @Test
    fun resolveTextDirectionHeuristics_null() {
        assertThat(
            resolveTextDirectionAlgorithm(
                LayoutDirection.Ltr,
                null)
        ).isEqualTo(TextDirectionAlgorithm.ContentOrLtr)

        assertThat(
            resolveTextDirectionAlgorithm(
                LayoutDirection.Rtl,
                null)
        ).isEqualTo(TextDirectionAlgorithm.ContentOrRtl)
    }

    @Test
    fun resolveTextDirectionHeuristics_DefaultLtr() {
        assertThat(
            resolveTextDirectionAlgorithm(
                LayoutDirection.Ltr,
                TextDirectionAlgorithm.ContentOrLtr
            )
        ).isEqualTo(TextDirectionAlgorithm.ContentOrLtr)

        assertThat(
            resolveTextDirectionAlgorithm(LayoutDirection.Rtl,
                TextDirectionAlgorithm.ContentOrLtr
            )
        ).isEqualTo(TextDirectionAlgorithm.ContentOrLtr)
    }

    @Test
    fun resolveTextDirectionHeuristics_DefaultRtl() {
        assertThat(
            resolveTextDirectionAlgorithm(
                LayoutDirection.Ltr,
                TextDirectionAlgorithm.ContentOrRtl
            )
        ).isEqualTo(TextDirectionAlgorithm.ContentOrRtl)

        assertThat(
            resolveTextDirectionAlgorithm(
                LayoutDirection.Rtl,
                TextDirectionAlgorithm.ContentOrRtl
            )
        ).isEqualTo(TextDirectionAlgorithm.ContentOrRtl)
    }

    @Test
    fun resolveTextDirectionHeuristics_Ltr() {
        assertThat(
            resolveTextDirectionAlgorithm(
                LayoutDirection.Ltr,
                TextDirectionAlgorithm.ForceLtr
            )
        ).isEqualTo(TextDirectionAlgorithm.ForceLtr)

        assertThat(
            resolveTextDirectionAlgorithm(
                LayoutDirection.Rtl,
                TextDirectionAlgorithm.ForceLtr
            )
        ).isEqualTo(TextDirectionAlgorithm.ForceLtr)
    }

    @Test
    fun resolveTextDirectionHeuristics_Rtl() {
        assertThat(
            resolveTextDirectionAlgorithm(
                LayoutDirection.Ltr,
                TextDirectionAlgorithm.ForceRtl
            )
        ).isEqualTo(TextDirectionAlgorithm.ForceRtl)

        assertThat(
            resolveTextDirectionAlgorithm(
                LayoutDirection.Rtl,
                TextDirectionAlgorithm.ForceRtl
            )
        ).isEqualTo(TextDirectionAlgorithm.ForceRtl)
    }
}
