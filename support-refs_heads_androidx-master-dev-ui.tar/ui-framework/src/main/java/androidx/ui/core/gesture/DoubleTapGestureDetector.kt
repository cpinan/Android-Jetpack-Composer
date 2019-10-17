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

import androidx.ui.core.PointerEventPass
import androidx.ui.core.PointerInputChange
import androidx.ui.core.changedToDown
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.changedToUp
import androidx.ui.core.CoroutineContextAmbient
import androidx.ui.core.PointerInputWrapper
import androidx.ui.core.PxPosition
import androidx.ui.core.anyPositionChangeConsumed
import androidx.ui.core.changedToUpIgnoreConsumed
import androidx.ui.core.consumeDownChange
import androidx.ui.temputils.delay
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

// TODO(b/138605697): This bug tracks the note below: DoubleTapGestureDetector should use the
//  eventual api that will allow it to temporary block tap.
// TODO(b/138754591): The behavior of this gesture detector needs to be finalized.
// TODO(b/139020678): Probably has shared functionality with other press based detectors.
/**
 * Responds to pointers going up, down within a small duration, and then up again.
 *
 * Note: This is a temporary implementation to unblock dependents.  Once the underlying API that
 * allows double tap to temporarily block tap from firing is complete, this gesture detector will
 * not block tap when the first "up" occurs. It will however block the 2nd up from causing tap to
 * fire.
 *
 * Also, given that this gesture detector is so temporary, opting to not write substantial tests.
 */
@Composable
fun DoubleTapGestureDetector(
    onDoubleTap: (PxPosition) -> Unit,
    children: @Composable() () -> Unit
) {
    val coroutineContext = +ambient(CoroutineContextAmbient)
    val recognizer =
        +memo { DoubleTapGestureRecognizer(coroutineContext) }
    recognizer.onDoubleTap = onDoubleTap

    PointerInputWrapper(pointerInputHandler = recognizer.pointerInputHandler) {
        children()
    }
}

internal class DoubleTapGestureRecognizer(
    coroutineContext: CoroutineContext
) {
    lateinit var onDoubleTap: (PxPosition) -> Unit

    private enum class State {
        Idle, Down, Up, SecondDown, Cancelled
    }

    var doubleTapTimeout = DoubleTapTimeout
    private var state = State.Idle
    private var job: Job? = null

    val pointerInputHandler =
        { changes: List<PointerInputChange>, pass: PointerEventPass ->

            var changesToReturn = changes

            if (pass == PointerEventPass.PostUp) {
                if (state == State.Idle && changesToReturn.all { it.changedToDown() }) {
                    state = State.Down
                } else if (state == State.Down && changesToReturn.all { it.changedToUp() }) {
                    state = State.Up
                    job = delay(doubleTapTimeout, coroutineContext) {
                        state = State.Idle
                    }
                } else if (state == State.Up && changesToReturn.all { it.changedToDown() }) {
                    job?.cancel()
                    state = State.SecondDown
                } else if (state == State.SecondDown && changesToReturn.all { it.changedToUp() }) {
                    changesToReturn = changesToReturn.map { it.consumeDownChange() }
                    state = State.Idle
                    onDoubleTap.invoke(changes[0].previous.position!!)
                } else if (state == State.Cancelled &&
                    changesToReturn.all { it.changedToUpIgnoreConsumed() }
                ) {
                    state = State.Idle
                }
            }

            if (pass == PointerEventPass.PostDown &&
                changesToReturn.any { it.anyPositionChangeConsumed() }
            ) {
                state = State.Cancelled
            }

            changesToReturn
        }
}