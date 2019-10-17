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

package androidx.ui.material.demos

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.dp
import androidx.ui.foundation.shape.border.Border
import androidx.ui.foundation.shape.GenericShape
import androidx.ui.graphics.Color
import androidx.ui.layout.FixedSpacer
import androidx.ui.layout.Wrap
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.OutlinedButtonStyle

class CustomShapeActivity : MaterialDemoActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.WHITE))
    }

    @Composable
    override fun materialContent() {
        CustomShapeDemo()
    }
}

@Composable
fun CustomShapeDemo() {
    MaterialTheme {
        Wrap(Alignment.Center) {
            Button(
                onClick = {},
                style = OutlinedButtonStyle(
                    shape = TriangleShape,
                    color = Color.Cyan,
                    border = Border(Color.DarkGray, 1.dp)
                )
            ) {
                FixedSpacer(100.dp, 100.dp)
            }
        }
    }
}

private val TriangleShape = GenericShape { size ->
    moveTo(size.width.value / 2f, 0f)
    lineTo(size.width.value, size.height.value)
    lineTo(0f, size.height.value)
}
