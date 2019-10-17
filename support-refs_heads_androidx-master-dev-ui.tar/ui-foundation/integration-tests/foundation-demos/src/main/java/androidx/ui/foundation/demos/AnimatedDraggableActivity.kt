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

package androidx.ui.foundation.demos

import android.app.Activity
import android.os.Bundle
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.foundation.samples.AnchoredDraggableSample
import androidx.ui.foundation.samples.DraggableSample
import androidx.ui.layout.Column
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Wrap

class AnimatedDraggableActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Wrap {
                Column {
                    DraggableSample()
                    HeightSpacer(100.dp)
                    AnchoredDraggableSample()
                }
            }
        }
    }
}