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

package androidx.ui.core

import android.util.Log
import androidx.ui.engine.geometry.Rect
import androidx.ui.graphics.Color
import androidx.ui.input.EditOperation
import androidx.ui.input.EditProcessor
import androidx.ui.input.FinishComposingTextEditOp
import androidx.ui.input.ImeAction
import androidx.ui.input.KeyboardType
import androidx.ui.input.OffsetMap
import androidx.ui.input.SetSelectionEditOp
import androidx.ui.input.TextInputService
import androidx.ui.input.TransformedText
import androidx.ui.input.VisualTransformation
import androidx.ui.input.identityOffsetMap
import androidx.ui.graphics.Canvas
import androidx.ui.input.INVALID_SESSION
import androidx.ui.input.InputSessionToken
import androidx.ui.input.InputState
import androidx.ui.text.AnnotatedString
import androidx.ui.text.Paragraph
import androidx.ui.text.ParagraphConstraints
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextDelegate
import androidx.ui.text.TextStyle
import androidx.ui.text.font.Font
import androidx.ui.text.style.TextDirectionAlgorithm
import kotlin.math.roundToInt

// -5185306 = 0xFFB0E0E6 = A(0xFF), R(0xB0), G(0xE0), B(0xE6)
internal const val DEFAULT_COMPOSITION_COLOR: Int = -5185306
// TODO(nona): share with Text.DEFAULT_SELECTION_COLOR
internal const val DEFAULT_SELECTION_COLOR: Int = 0x6633B5E5

/**
 * Computed the line height for the empty TextField.
 *
 * The bounding box or x-advance of the empty text is empty, i.e. 0x0 box or 0px advance. However
 * this is not useful for TextField since text field want to reserve some amount of height for
 * accepting touch for starting text input. In Android, uses FontMetrics of the first font in the
 * fallback chain to compute this height, this is because custom font may have different
 * ascender/descender from the default font in Android.
 *
 * Until we have font metrics APIs, use the height of reference text as a workaround.
 */
// TODO(nona): Add FontMetrics API and stop doing this workaround.
private fun computeLineHeightForEmptyText(
    textStyle: TextStyle,
    density: Density,
    resourceLoader: Font.ResourceLoader
): IntPx {
    return Paragraph(
        text = "H", // No meaning: just a reference character.
        style = textStyle,
        paragraphStyle = ParagraphStyle(
            textDirectionAlgorithm = TextDirectionAlgorithm.ContentOrLtr
        ),
        textStyles = listOf(),
        maxLines = 1,
        ellipsis = false,
        density = density,
        resourceLoader = resourceLoader,
        constraints = ParagraphConstraints(width = Float.POSITIVE_INFINITY)
    ).height.roundToInt().ipx
}

internal class TextFieldDelegate {
    companion object {
        /**
         * Process text layout with given constraint.
         *
         * @param textDelegate The text painter
         * @param constraints The layout constraints
         * @return the bounding box size(width and height) of the layout result
         */
        @JvmStatic
        fun layout(textDelegate: TextDelegate, constraints: Constraints): Pair<IntPx, IntPx> {

            // We anyway need to compute layout for preventing NPE during draw which require layout
            // result.
            // TODO(nona): Fix this?
            textDelegate.layout(Constraints.tightConstraintsForWidth(constraints.maxWidth))

            val isEmptyText = textDelegate.text.text.isEmpty()
            val height = if (isEmptyText) {
                computeLineHeightForEmptyText(
                    textStyle = textDelegate.textStyle,
                    density = textDelegate.density,
                    resourceLoader = textDelegate.resourceLoader
                )
            } else {
                textDelegate.height.px.round()
            }
            val width = constraints.maxWidth
            return Pair(width, height)
        }

        /**
         * Draw the text content to the canvas
         *
         * @param canvas The target canvas.
         * @param value The editor state
         * @param offsetMap The offset map
         * @param textDelegate The text painter
         * @param hasFocus true if this composable is focused, otherwise false
         * @param selectionColor The selection color
         */
        @JvmStatic
        fun draw(
            canvas: Canvas,
            value: InputState,
            offsetMap: OffsetMap,
            textDelegate: TextDelegate,
            hasFocus: Boolean,
            selectionColor: Color? = null
        ) {
            value.composition?.let {
                textDelegate.paintBackground(
                    offsetMap.originalToTransformed(it.min),
                    offsetMap.originalToTransformed(it.max),
                    Color(DEFAULT_COMPOSITION_COLOR),
                    canvas
                )
            }
            if (value.selection.collapsed) {
                if (hasFocus) {
                    textDelegate.paintCursor(
                        offsetMap.originalToTransformed(value.selection.min), canvas)
                }
            } else {
                textDelegate.paintBackground(
                    offsetMap.originalToTransformed(value.selection.min),
                    offsetMap.originalToTransformed(value.selection.max),
                    selectionColor ?: Color(DEFAULT_SELECTION_COLOR),
                    canvas
                )
            }
            textDelegate.paint(canvas)
        }

        /**
         * Notify system that focused input area.
         *
         * System is typically scrolled up not to be covered by keyboard.
         *
         * @param value The editor model
         * @param textDelegate The text delegate
         * @param layoutCoordinates The layout coordinates
         * @param textInputService The text input service
         * @param token The current input session token.
         * @param hasFocus True if focus is gained.
         * @param offsetMap The mapper from/to editing buffer to/from visible text.
         */
        @JvmStatic
        fun notifyFocusedRect(
            value: InputState,
            textDelegate: TextDelegate,
            layoutCoordinates: LayoutCoordinates,
            textInputService: TextInputService,
            token: InputSessionToken,
            hasFocus: Boolean,
            offsetMap: OffsetMap
        ) {
            if (!hasFocus) {
                return
            }

            val bbox = if (value.selection.max < value.text.length) {
                textDelegate.getBoundingBox(offsetMap.originalToTransformed(value.selection.max))
            } else if (value.selection.max != 0) {
                textDelegate.getBoundingBox(
                    offsetMap.originalToTransformed(value.selection.max) - 1)
            } else {
                val lineHeightForEmptyText = computeLineHeightForEmptyText(
                    textDelegate.textStyle,
                    textDelegate.density,
                    textDelegate.resourceLoader
                )
                Rect(0f, 0f, 1.0f, lineHeightForEmptyText.value.toFloat())
            }
            val globalLT = layoutCoordinates.localToRoot(PxPosition(bbox.left.px, bbox.top.px))

            textInputService.notifyFocusedRect(
                token,
                Rect.fromLTWH(
                    globalLT.x.value,
                    globalLT.y.value,
                    bbox.width,
                    bbox.height
                )
            )
        }

        /**
         * Called when edit operations are passed from TextInputService
         *
         * @param ops A list of edit operations.
         * @param editProcessor The edit processor
         * @param onValueChange The callback called when the new editor state arrives.
         */
        @JvmStatic
        internal fun onEditCommand(
            ops: List<EditOperation>,
            editProcessor: EditProcessor,
            onValueChange: (InputState) -> Unit
        ) {
            onValueChange(editProcessor.onEditCommands(ops))
        }

        /**
         * Called when onDrag event is fired.
         *
         * @param position The event position in composable coordinate.
         */
        @JvmStatic
        fun onDragAt(position: PxPosition) {
            // TODO(nona): Implement this function
            Log.d("TextFieldDelegate", "onDrag: $position")
        }

        /**
         * Called when onRelease event is fired.
         *
         * @param position The event position in composable coordinate.
         * @param textDelegate The text painter
         * @param editProcessor The edit processor
         * @param offsetMap The offset map
         * @param onValueChange The callback called when the new editor state arrives.
         * @param textInputService The text input service
         * @param token The current input session token.
         * @param hasFocus True if the composable has input focus, otherwise false.
         */
        @JvmStatic
        fun onRelease(
            position: PxPosition,
            textDelegate: TextDelegate,
            editProcessor: EditProcessor,
            offsetMap: OffsetMap,
            onValueChange: (InputState) -> Unit,
            textInputService: TextInputService?,
            token: InputSessionToken,
            hasFocus: Boolean
        ) {
            textInputService?.showSoftwareKeyboard(token)
            if (hasFocus) {
                val offset = offsetMap.transformedToOriginal(
                    textDelegate.getOffsetForPosition(position))
                onEditCommand(
                    listOf(SetSelectionEditOp(offset, offset)),
                    editProcessor,
                    onValueChange)
            }
        }

        /**
         * Called when the composable gained input focus
         *
         * @param textInputService The text input service
         * @param value The editor state
         * @param editProcessor The edit processor
         * @param keyboardType The keyboard type
         * @param onValueChange The callback called when the new editor state arrives.
         * @param onImeActionPerformed The callback called when the editor action arrives.
         */
        @JvmStatic
        fun onFocus(
            textInputService: TextInputService?,
            value: InputState,
            editProcessor: EditProcessor,
            keyboardType: KeyboardType,
            imeAction: ImeAction,
            onValueChange: (InputState) -> Unit,
            onImeActionPerformed: (ImeAction) -> Unit
        ): InputSessionToken {
            return textInputService?.startInput(
                initModel = InputState(value.text, value.selection, value.composition),
                keyboardType = keyboardType,
                imeAction = imeAction,
                onEditCommand = { onEditCommand(it, editProcessor, onValueChange) },
                onImeActionPerformed = onImeActionPerformed) ?: INVALID_SESSION
        }

        /**
         * Called when the composable loses input focus
         *
         * @param textInputService The text input service
         * @param token The current input session token.
         * @param editProcessor The edit processor
         * @param onValueChange The callback called when the new editor state arrives.
         */
        @JvmStatic
        fun onBlur(
            textInputService: TextInputService?,
            token: InputSessionToken,
            editProcessor: EditProcessor,
            onValueChange: (InputState) -> Unit
        ) {
            onEditCommand(listOf(FinishComposingTextEditOp()), editProcessor, onValueChange)
            textInputService?.stopInput(token)
        }

        /**
         * Helper function of applying visual transformation method to the EditorModel.
         *
         * @param value An editor state
         * @param visualTransformation A visual transformation
         */
        @JvmStatic
        fun applyVisualFilter(
            value: InputState,
            visualTransformation: VisualTransformation?
        ): TransformedText {
            val annotatedString = AnnotatedString(value.text)
            return visualTransformation?.filter(annotatedString)
                    ?: TransformedText(annotatedString, identityOffsetMap)
        }
    }
}
