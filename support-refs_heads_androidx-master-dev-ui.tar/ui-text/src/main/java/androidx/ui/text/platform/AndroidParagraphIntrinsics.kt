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

package androidx.ui.text.platform

import android.graphics.Paint
import android.text.TextPaint
import androidx.text.LayoutIntrinsics
import androidx.ui.core.Density
import androidx.ui.text.AnnotatedString
import androidx.ui.text.ParagraphIntrinsics
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextStyle

internal class AndroidParagraphIntrinsics(
    val text: String,
    val style: TextStyle,
    val paragraphStyle: ParagraphStyle,
    val textStyles: List<AnnotatedString.Item<TextStyle>>,
    val typefaceAdapter: TypefaceAdapter,
    val density: Density
) : ParagraphIntrinsics {

    internal val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    internal val charSequence: CharSequence

    internal val layoutIntrinsics: LayoutIntrinsics

    override val maxIntrinsicWidth: Float
        get() = layoutIntrinsics.maxIntrinsicWidth

    override val minIntrinsicWidth: Float
        get() = layoutIntrinsics.minIntrinsicWidth

    internal val textDirectionHeuristic = paragraphStyle.textDirectionAlgorithm?.let {
        resolveTextDirectionHeuristics(paragraphStyle.textDirectionAlgorithm)
    } ?: throw IllegalArgumentException(
        "ParagraphStyle.textDirectionAlgorithm should not be null"
    )

    init {
        val notAppliedStyle = textPaint.applyTextStyle(style, typefaceAdapter, density)

        charSequence = createStyledText(
            text = text,
            lineHeight = paragraphStyle.lineHeight,
            textIndent = paragraphStyle.textIndent,
            textStyles = listOf(
                AnnotatedString.Item(
                    notAppliedStyle,
                    0,
                    text.length
                )
            ) + textStyles,
            density = density,
            typefaceAdapter = typefaceAdapter
        )

        layoutIntrinsics = LayoutIntrinsics(charSequence, textPaint, textDirectionHeuristic)
    }
}
