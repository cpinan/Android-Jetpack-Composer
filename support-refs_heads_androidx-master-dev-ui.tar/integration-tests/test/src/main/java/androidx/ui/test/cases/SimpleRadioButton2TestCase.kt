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

package androidx.ui.test.cases

import android.app.Activity
import androidx.ui.core.Density
import androidx.ui.core.Dp
import androidx.ui.core.Px
import androidx.ui.core.PxSize
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.core.withDensity
import androidx.ui.engine.geometry.Offset
import androidx.ui.engine.geometry.Outline
import androidx.ui.engine.geometry.Shape
import androidx.ui.engine.geometry.shift
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.border.Border
import androidx.ui.foundation.shape.border.DrawBorder
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.Path
import androidx.ui.layout.Container

class SimpleRadioButton2TestCase(
    activity: Activity
) : BaseSimpleRadioButtonTestCase(activity) {

    override fun setComposeContent(activity: Activity) = activity.setContent {
        Container(width = 48.dp, height = 48.dp) {
            DrawBorder(CircleShape, Border(Color.Cyan, 1.dp))
            val padding = (48.dp - getInnerSize().value) / 2
            DrawShape(PaddingShape(padding, CircleShape), Color.Cyan)
        }
    }!!
}

private data class PaddingShape(val padding: Dp, val shape: Shape) : Shape {
    override fun createOutline(size: PxSize, density: Density): Outline {
        val twoPaddings = withDensity(density) { (padding * 2).toPx() }
        val sizeMinusPaddings = PxSize(size.width - twoPaddings, size.height - twoPaddings)
        val rawResult = shape.createOutline(sizeMinusPaddings, density)
        return rawResult.offset(twoPaddings / 2)
    }
}

private fun Outline.offset(size: Px): Outline {
    val offset = Offset(size.value, size.value)
    return when (this) {
        is Outline.Rectangle -> Outline.Rectangle(rect.shift(offset))
        is Outline.Rounded -> Outline.Rounded(rrect.shift(offset))
        is Outline.Generic -> Outline.Generic(Path().apply {
            addPath(path)
            shift(offset)
        })
    }
}
