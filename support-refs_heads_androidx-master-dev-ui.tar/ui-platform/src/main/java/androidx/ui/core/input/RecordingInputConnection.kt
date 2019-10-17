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

package androidx.ui.core.input

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.CompletionInfo
import android.view.inputmethod.CorrectionInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputContentInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.VisibleForTesting
import androidx.ui.input.BackspaceKeyEditOp
import androidx.ui.input.CommitTextEditOp
import androidx.ui.input.DeleteSurroundingTextEditOp
import androidx.ui.input.DeleteSurroundingTextInCodePointsEditOp
import androidx.ui.input.EditOperation
import androidx.ui.input.FinishComposingTextEditOp
import androidx.ui.input.ImeAction
import androidx.ui.input.InputEventListener
import androidx.ui.input.InputState
import androidx.ui.input.MoveCursorEditOp
import androidx.ui.input.SetComposingRegionEditOp
import androidx.ui.input.SetComposingTextEditOp
import androidx.ui.input.SetSelectionEditOp

private val DEBUG = false
private val TAG = "RecordingIC"

internal class RecordingInputConnection(
    /**
     * The initial input state
     */
    initState: InputState,

    /**
     * An input event listener.
     */
    val eventListener: InputEventListener
) : InputConnection {

    // The depth of the batch session. 0 means no session.
    private var batchDepth: Int = 0

    // The input state.
    @VisibleForTesting
    internal var inputState: InputState = initState
        set(value) {
            if (DEBUG) { Log.d(TAG, "New InputState has set: $value -> $inputState") }
            field = value
        }

    /**
     * The token to be used for reporting updateExtractedText API.
     *
     * 0 if no token was specified from IME.
     */
    private var currentExtractedTextRequestToken = 0

    /**
     * True if IME requested extracted text monitor mode.
     *
     * If extracted text monitor mode is ON, need to call updateExtractedText API whenever the text
     * is changed.
     */
    private var extractedTextMonitorMode = false

    /**
     * Updates the input state and tells it to the IME.
     *
     * This function may emits updateSelection and updateExtractedText to notify IMEs that the text
     * contents has changed if needed.
     */
    fun updateInputState(state: InputState, imm: InputMethodManager, view: View) {
        val prev = inputState
        val next = state
        inputState = next

        if (prev == next) {
            return
        }

        if (extractedTextMonitorMode) {
            imm.updateExtractedText(view, currentExtractedTextRequestToken, next.toExtractedText())
        }

        // The candidateStart and candidateEnd is composition start and composition end in
        // updateSelection API. Need to pass -1 if there is no composition.
        val candidateStart = next.composition?.min ?: -1
        val candidateEnd = next.composition?.max ?: -1
        if (DEBUG) {
            Log.d(TAG, "updateSelection(" +
                        "selection = (${next.selection.min},${next.selection.max}), " +
                        "compoairion = ($candidateStart, $candidateEnd)")
        }
        imm.updateSelection(view, next.selection.min, next.selection.max,
            candidateStart, candidateEnd)
    }

    // The recoding editing ops.
    private val editOps = mutableListOf<EditOperation>()

    // Add edit op to internal list with wrapping batch edit.
    private fun addEditOpWithBatch(editOp: EditOperation) {
        beginBatchEdit()
        try {
            editOps.add(editOp)
        } finally {
            endBatchEdit()
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // Callbacks for text editing session
    // /////////////////////////////////////////////////////////////////////////////////////////////

    override fun beginBatchEdit(): Boolean {
        if (DEBUG) { Log.d(TAG, "beginBatchEdit()") }
        batchDepth++
        return true
    }

    override fun endBatchEdit(): Boolean {
        if (DEBUG) { Log.d(TAG, "endBatchEdit()") }
        batchDepth--
        if (batchDepth == 0 && editOps.isNotEmpty()) {
            eventListener.onEditOperations(editOps.toList())
            editOps.clear()
        }
        return batchDepth > 0
    }

    override fun closeConnection() {
        if (DEBUG) { Log.d(TAG, "closeConnection()") }
        editOps.clear()
        batchDepth = 0
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // Callbacks for text editing
    // /////////////////////////////////////////////////////////////////////////////////////////////

    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
        if (DEBUG) { Log.d(TAG, "commitText(\"$text\", $newCursorPosition)") }
        addEditOpWithBatch(CommitTextEditOp(text.toString(), newCursorPosition))
        return true
    }

    override fun setComposingRegion(start: Int, end: Int): Boolean {
        if (DEBUG) { Log.d(TAG, "setComposingRegion($start, $end)") }
        addEditOpWithBatch(SetComposingRegionEditOp(start, end))
        return true
    }

    override fun setComposingText(text: CharSequence?, newCursorPosition: Int): Boolean {
        if (DEBUG) { Log.d(TAG, "setComposingText(\"$text\", $newCursorPosition)") }
        addEditOpWithBatch(SetComposingTextEditOp(text.toString(), newCursorPosition))
        return true
    }

    override fun deleteSurroundingTextInCodePoints(beforeLength: Int, afterLength: Int): Boolean {
        if (DEBUG) { Log.d(TAG, "deleteSurroundingTextInCodePoints($beforeLength, $afterLength)") }
        addEditOpWithBatch(DeleteSurroundingTextInCodePointsEditOp(beforeLength, afterLength))
        return true
    }

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
        if (DEBUG) { Log.d(TAG, "deleteSurroundingText($beforeLength, $afterLength)") }
        addEditOpWithBatch(DeleteSurroundingTextEditOp(beforeLength, afterLength))
        return true
    }

    override fun setSelection(start: Int, end: Int): Boolean {
        if (DEBUG) { Log.d(TAG, "setSelection($start, $end)") }
        addEditOpWithBatch(SetSelectionEditOp(start, end))
        return true
    }

    override fun finishComposingText(): Boolean {
        if (DEBUG) { Log.d(TAG, "finishComposingText()") }
        addEditOpWithBatch(FinishComposingTextEditOp())
        return true
    }

    override fun sendKeyEvent(event: KeyEvent): Boolean {
        if (DEBUG) { Log.d(TAG, "sendKeyEvent($event)") }
        if (event.action != KeyEvent.ACTION_DOWN) {
            return true // Only interested in KEY_DOWN event.
        }

        val op = when (event.keyCode) {
            KeyEvent.KEYCODE_DEL -> BackspaceKeyEditOp()
            KeyEvent.KEYCODE_DPAD_LEFT -> MoveCursorEditOp(-1)
            KeyEvent.KEYCODE_DPAD_RIGHT -> MoveCursorEditOp(1)
            else -> CommitTextEditOp(String(Character.toChars(event.getUnicodeChar())), 1)
        }

        addEditOpWithBatch(op)
        return true
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // Callbacks for retrieving editing buffers
    // /////////////////////////////////////////////////////////////////////////////////////////////

    override fun getTextBeforeCursor(maxChars: Int, flags: Int): CharSequence {
        if (DEBUG) { Log.d(TAG, "getTextBeforeCursor($maxChars, $flags)") }
        return inputState.getTextBeforeSelection(maxChars)
    }

    override fun getTextAfterCursor(maxChars: Int, flags: Int): CharSequence {
        if (DEBUG) { Log.d(TAG, "getTextAfterCursor($maxChars, $flags)") }
        return inputState.getTextAfterSelection(maxChars)
    }

    override fun getSelectedText(flags: Int): CharSequence {
        if (DEBUG) { Log.d(TAG, "getSelectedText($flags)") }
        return inputState.getSelectedText()
    }

    override fun requestCursorUpdates(cursorUpdateMode: Int): Boolean {
        if (DEBUG) { Log.d(TAG, "requestCursorUpdates($cursorUpdateMode)") }
        TODO("not implemented")
    }

    override fun getExtractedText(request: ExtractedTextRequest?, flags: Int): ExtractedText {
        if (DEBUG) { Log.d(TAG, "getExtractedText($request, $flags)") }
        extractedTextMonitorMode = (flags and InputConnection.GET_EXTRACTED_TEXT_MONITOR) != 0
        if (extractedTextMonitorMode) {
            currentExtractedTextRequestToken = request?.token ?: 0
        }
        return inputState.toExtractedText()
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // Editor action and Key events.
    // /////////////////////////////////////////////////////////////////////////////////////////////

    override fun performContextMenuAction(id: Int): Boolean {
        if (DEBUG) { Log.d(TAG, "performContextMenuAction($id)") }
        TODO("not implemented")
    }

    override fun performEditorAction(editorAction: Int): Boolean {
        if (DEBUG) { Log.d(TAG, "performEditorAction($editorAction)") }
        val imeAction = when (editorAction) {
            EditorInfo.IME_ACTION_UNSPECIFIED -> ImeAction.Unspecified
            EditorInfo.IME_ACTION_DONE -> ImeAction.Done
            EditorInfo.IME_ACTION_SEND -> ImeAction.Send
            EditorInfo.IME_ACTION_SEARCH -> ImeAction.Search
            EditorInfo.IME_ACTION_PREVIOUS -> ImeAction.Previous
            EditorInfo.IME_ACTION_NEXT -> ImeAction.Next
            EditorInfo.IME_ACTION_GO -> ImeAction.Go
            else -> {
                Log.w(TAG, "IME sends unsupported Editor Action: $editorAction")
                ImeAction.Unspecified
            }
        }
        eventListener.onImeAction(imeAction)
        return true
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // Unsupported callbacks
    // /////////////////////////////////////////////////////////////////////////////////////////////

    override fun commitCompletion(text: CompletionInfo?): Boolean {
        if (DEBUG) { Log.d(TAG, "commitCompletion(${text?.text})") }
        // We don't support this callback.
        // The API documents says this should return if the input connection is no longer valid, but
        // The Chromium implementation already returning false, so assuming it is safe to return
        // false if not supported.
        // see https://cs.chromium.org/chromium/src/content/public/android/java/src/org/chromium/content/browser/input/ThreadedInputConnection.java
        return false
    }

    override fun commitCorrection(correctionInfo: CorrectionInfo?): Boolean {
        if (DEBUG) { Log.d(TAG, "commitCorrection($correctionInfo)") }
        // We don't support this callback.
        // The API documents says this should return if the input connection is no longer valid, but
        // The Chromium implementation already returning false, so assuming it is safe to return
        // false if not supported.
        // see https://cs.chromium.org/chromium/src/content/public/android/java/src/org/chromium/content/browser/input/ThreadedInputConnection.java
        return false
    }

    override fun getHandler(): Handler? {
        if (DEBUG) { Log.d(TAG, "getHandler()") }
        return null // Returns null means using default Handler
    }

    override fun clearMetaKeyStates(states: Int): Boolean {
        if (DEBUG) { Log.d(TAG, "clearMetaKeyStates($states)") }
        // We don't support this callback.
        // The API documents says this should return if the input connection is no longer valid, but
        // The Chromium implementation already returning false, so assuming it is safe to return
        // false if not supported.
        // see https://cs.chromium.org/chromium/src/content/public/android/java/src/org/chromium/content/browser/input/ThreadedInputConnection.java
        return false
    }

    override fun reportFullscreenMode(enabled: Boolean): Boolean {
        if (DEBUG) { Log.d(TAG, "reportFullscreenMode($enabled)") }
        return false // This value is ignored according to the API docs.
    }

    override fun getCursorCapsMode(reqModes: Int): Int {
        if (DEBUG) { Log.d(TAG, "getCursorCapsMode($reqModes)") }
        return TextUtils.getCapsMode(inputState.text, inputState.selection.min, reqModes)
    }

    override fun performPrivateCommand(action: String?, data: Bundle?): Boolean {
        if (DEBUG) { Log.d(TAG, "performPrivateCommand($action, $data)") }
        return true // API doc says we should return true even if we didn't understand the command.
    }

    override fun commitContent(
        inputContentInfo: InputContentInfo,
        flags: Int,
        opts: Bundle?
    ): Boolean {
        if (DEBUG) { Log.d(TAG, "commitContent($inputContentInfo, $flags, $opts)") }
        return false // We don't accept any contents.
    }
}