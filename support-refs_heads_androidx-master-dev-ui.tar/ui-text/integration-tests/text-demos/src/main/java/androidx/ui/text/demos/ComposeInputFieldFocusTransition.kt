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

package androidx.ui.text.demos

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.FocusManagerAmbient
import androidx.ui.core.TextField
import androidx.ui.core.sp
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.graphics.Color
import androidx.ui.input.EditorStyle
import androidx.ui.input.ImeAction
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutSize
import androidx.ui.text.TextStyle

@Composable
fun TextFieldFocusTransition() {
    VerticalScroller {
        Column(
            mainAxisSize = LayoutSize.Expand,
            crossAxisAlignment = CrossAxisAlignment.Start
        ) {
            TextFieldWithFocusId("Focus 1", "Focus 2")
            TextFieldWithFocusId("Focus 2", "Focus 3")
            TextFieldWithFocusId("Focus 3", "Focus 4")
            TextFieldWithFocusId("Focus 4", "Focus 5")
            TextFieldWithFocusId("Focus 5", "Focus 6")
            TextFieldWithFocusId("Focus 6", "Focus 1")
        }
    }
}

@Composable
private fun TextFieldWithFocusId(focusID: String, nextFocus: String) {
    val focusManager = +ambient(FocusManagerAmbient)
    val state = +state { "Focus ID: $focusID" }
    val focused = +state { false }
    val color = if (focused.value) {
        Color.Red
    } else {
        Color.Black
    }
    TextField(
        value = state.value,
        editorStyle = EditorStyle(textStyle = TextStyle(color = color, fontSize = 32.sp)),
        onValueChange = {
            state.value = it
        },
        onFocus = { focused.value = true },
        onBlur = { focused.value = false },
        imeAction = ImeAction.Next,
        focusIdentifier = focusID,
        onImeActionPerformed = {
            if (it == ImeAction.Next)
                focusManager.requestFocusById(nextFocus)
        }
    )
}
