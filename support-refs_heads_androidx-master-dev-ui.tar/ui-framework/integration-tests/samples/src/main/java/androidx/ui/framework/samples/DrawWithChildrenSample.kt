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

package androidx.ui.framework.samples

import androidx.annotation.Sampled
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Draw
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.toRect
import androidx.ui.engine.geometry.Rect
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.PaintingStyle

@Sampled
@Composable
fun DrawWithChildrenSample() {
    val children = @Composable {
        Text("Hello World")
    }
    val paint = +memo { Paint() }
    Draw(children) { canvas, parentSize ->
        // Draw the highlight behind the text
        paint.color = Color.Yellow
        paint.style = PaintingStyle.fill
        val rect = parentSize.toRect()
        canvas.drawRect(rect, paint)

        // Draw the text
        drawChildren()

        // Draw the border on top of the text
        paint.style = PaintingStyle.stroke
        val strokeWidth = 2.dp.toIntPx().value.toFloat()
        paint.color = Color.Black
        paint.strokeWidth = strokeWidth
        val outlineRect = Rect(
            left = strokeWidth / 2,
            top = strokeWidth / 2,
            right = rect.right - (strokeWidth / 2),
            bottom = rect.bottom - (strokeWidth / 2)
        )
        canvas.drawRect(outlineRect, paint)
    }
}
