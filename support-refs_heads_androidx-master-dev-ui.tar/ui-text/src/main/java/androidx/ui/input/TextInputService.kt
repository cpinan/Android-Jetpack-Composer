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

package androidx.ui.input

import androidx.annotation.RestrictTo
import androidx.ui.engine.geometry.Rect

/**
 * The input session token.
 *
 * The positive session token means the input session is alive. The session may be expired though.
 * The zero session token means no session.
 * The negative session token means the input session could not be established with some errors.
 */
typealias InputSessionToken = Int

/**
 * A special session token which represents there is no active input session.
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
const val NO_SESSION: InputSessionToken = 0

/**
 * A special session token which represents the session couldn't be established.
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
const val INVALID_SESSION: InputSessionToken = -1

/**
 * Provide a communication with platform text input service.
 *
 * Open for testing purpose.
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
open class TextInputService(val platformTextInputService: PlatformTextInputService) {

    private var nextSessionToken: Int = 1
    private var currentSessionToken: InputSessionToken = NO_SESSION

    private inline fun ignoreIfExpired(token: InputSessionToken, block: () -> Unit) {
        if (token > 0 && token == currentSessionToken) {
            block()
        }
    }

    /**
     * Start text input session for given client.
     */
    open fun startInput(
        initModel: InputState,
        keyboardType: KeyboardType,
        imeAction: ImeAction,
        onEditCommand: (List<EditOperation>) -> Unit,
        onImeActionPerformed: (ImeAction) -> Unit
    ): InputSessionToken {
        platformTextInputService.startInput(
            initModel,
            keyboardType,
            imeAction,
            onEditCommand,
            onImeActionPerformed)
        currentSessionToken = nextSessionToken++
        return currentSessionToken
    }

    /**
     * Stop text input session.
     */
    open fun stopInput(token: InputSessionToken) = ignoreIfExpired(token) {
        platformTextInputService.stopInput()
    }

    /**
     * Request showing onscreen keyboard
     *
     * There is no guarantee nor callback of the result of this API. The software keyboard or
     * system service may silently ignores this request.
     */
    open fun showSoftwareKeyboard(token: InputSessionToken) = ignoreIfExpired(token) {
        platformTextInputService.showSoftwareKeyboard()
    }

    /*
     * Notify the new editor model to IME.
     */
    open fun onStateUpdated(token: InputSessionToken, model: InputState) = ignoreIfExpired(token) {
        platformTextInputService.onStateUpdated(model)
    }

    /**
     * Notify the focused rectangle to the system.
     */
    open fun notifyFocusedRect(token: InputSessionToken, rect: Rect) = ignoreIfExpired(token) {
        platformTextInputService.notifyFocusedRect(rect)
    }
}

/**
 * An interface for text input service.
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
interface PlatformTextInputService {
    /**
     * Start text input session for given client.
     */
    fun startInput(
        initModel: InputState,
        keyboardType: KeyboardType,
        imeAction: ImeAction,
        onEditCommand: (List<EditOperation>) -> Unit,
        onImeActionPerformed: (ImeAction) -> Unit
    )

    /**
     * Stop text input session.
     */
    fun stopInput()

    /**
     * Request showing onscreen keyboard
     *
     * There is no guarantee nor callback of the result of this API.
     */
    fun showSoftwareKeyboard()

    /*
     * Notify the new editor model to IME.
     */
    fun onStateUpdated(model: InputState)

    /**
     * Notify the focused rectangle to the system.
     */
    fun notifyFocusedRect(rect: Rect)
}