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
import androidx.ui.core.PxPosition
import androidx.ui.core.anyPositionChangeConsumed
import androidx.ui.core.changedToDown
import androidx.ui.core.consumeDownChange
import androidx.ui.engine.geometry.Offset
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.PointerInputWrapper
import androidx.ui.core.changedToUpIgnoreConsumed

/**
 * This gesture detector has callbacks for when a press gesture starts and ends for the purposes of
 * displaying visual feedback for those two states.
 *
 * More specifically:
 * - It will call [onStart] if the first pointer down it receives during the
 * [PointerEventPass.PostUp] pass is not consumed.
 * - It will call [onStop] if [onStart] has been called and the last [PointerInputChange] it
 * receives during the [PointerEventPass.PostUp] pass has an up change, consumed or not, indicating
 * the press gesture indication should end.
 * - It will call [onCancel] if movement has been consumed by the time of the
 * [PointerEventPass.PostDown] pass, indicating that the press gesture indication should end because
 * something moved.
 *
 * This gesture detector always consumes the down change during the [PointerEventPass.PostUp] pass.
 */
// TODO(b/139020678): Probably has shared functionality with other press based detectors.
@Composable
fun PressIndicatorGestureDetector(
    onStart: ((PxPosition) -> Unit)? = null,
    onStop: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    children: @Composable() () -> Unit
) {
    val recognizer = +memo { PressIndicatorGestureRecognizer() }
    recognizer.onStart = onStart
    recognizer.onStop = onStop
    recognizer.onCancel = onCancel

    PointerInputWrapper(pointerInputHandler = recognizer.pointerInputHandler) {
        children()
    }
}

internal class PressIndicatorGestureRecognizer {
    /**
     * Called if the first pointer's down change was not consumed by the time this gesture
     * recognizer receives it in the [PointerEventPass.PostUp] pass.
     *
     * This callback should be used to indicate that the press state should be shown.  An [Offset]
     * is provided to indicate where the first pointer made contact with this gesrure detector.
     */
    var onStart: ((PxPosition) -> Unit)? = null

    /**
     * Called if onStart was attempted to be called (it may have been null), no pointer movement
     * was consumed, and the last pointer went up (consumed or not).
     *
     * This should be used for removing visual feedback that indicates that the press has ended with
     * a completed press released gesture.
     */
    var onStop: (() -> Unit)? = null

    /**
     * Called if onStart was attempted to be called (it may have been null), and pointer movement
     * was consumed by the time of the [PointerEventPass.PostDown] reaches this gesture detector.
     *
     * This should be used for removing visual feedback that indicates that the press gesture was
     * cancelled.
     */
    var onCancel: (() -> Unit)? = null

    private var started = false

    val pointerInputHandler =
        { changes: List<PointerInputChange>, pass: PointerEventPass ->

            var internalChanges = changes

            if (pass == PointerEventPass.InitialDown && started) {
                internalChanges = internalChanges.map {
                    if (it.changedToDown()) {
                        it.consumeDownChange()
                    } else {
                        it
                    }
                }
            }

            if (pass == PointerEventPass.PostUp) {

                if (!started && internalChanges.all { it.changedToDown() }) {
                    // If we have not yet started and all of the changes changed to down, we are
                    // starting.
                    started = true
                    onStart?.invoke(internalChanges.first().current.position!!)
                    internalChanges = internalChanges.map { it.consumeDownChange() }
                } else if (started) {
                    if (internalChanges.all { it.changedToUpIgnoreConsumed() }) {
                        // If we have started and all of the changes changed to up, we are stopping.
                        started = false
                        onStop?.invoke()
                    }
                }

                if (started) {
                    internalChanges = internalChanges.map { it.consumeDownChange() }
                }
            }

            if (pass == PointerEventPass.PostDown &&
                started &&
                internalChanges
                    .any { it.anyPositionChangeConsumed() }
            ) {
                // On the final pass, if we have started and any of the changes had consumed
                // position changes, we cancel.
                started = false
                onCancel?.invoke()
            }

            internalChanges
        }
}