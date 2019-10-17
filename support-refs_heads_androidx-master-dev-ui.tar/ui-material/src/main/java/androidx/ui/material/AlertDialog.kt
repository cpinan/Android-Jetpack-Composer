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

package androidx.ui.material

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.dp
import androidx.ui.foundation.Dialog
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Row
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.surface.Surface

/**
 * Alert dialog is a [Dialog] which interrupts the user with urgent information, details or actions.
 *
 * There are two different layouts for showing the buttons inside the Alert dialog provided by
 * [AlertDialogButtonLayout].
 *
 * Sample of dialog with side by side buttons:
 *
 * @sample androidx.ui.material.samples.SideBySideAlertDialogSample
 *
 * Sample of dialog with stacked buttons:
 *
 * @sample androidx.ui.material.samples.StackedAlertDialogSample
 *
 * @param onCloseRequest Executes when the user tries to dismiss the Dialog by clicking outside
 * or pressing the back button.
 * @param title The title of the Dialog which should specify the purpose of the Dialog. The title
 * is not mandatory, because there may be sufficient information inside the [text].
 * @param text The text which presents the details regarding
 * the Dialog's purpose.
 * @param confirmButton A button which is meant to confirm a proposed action, thus resolving
 * what triggered the dialog.
 * @param dismissButton A button which is meant to dismiss the dialog.
 * @param buttonLayout An enum which specifies how the buttons are positioned inside the dialog:
 * SideBySide or Stacked.
 *
 */
// TODO(b/137311217): type inference for nullable lambdas currently doesn't work
@Suppress("USELESS_CAST")
@Composable
fun AlertDialog(
    onCloseRequest: () -> Unit,
    title: (@Composable() () -> Unit)? = null as @Composable() (() -> Unit)?,
    text: (@Composable() () -> Unit),
    confirmButton: (@Composable() () -> Unit),
    dismissButton: (@Composable() () -> Unit)? = null as @Composable() (() -> Unit)?,
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide
) {
    // TODO: Find a cleaner way to pass the properties of the MaterialTheme
    val currentColors = +ambient(Colors)
    val currentTypography = +ambient(Typography)
    Dialog(onCloseRequest = onCloseRequest) {
        MaterialTheme(colors = currentColors, typography = currentTypography) {
            Surface(shape = AlertDialogShape) {
                Container(width = AlertDialogWidth) {
                    Column(crossAxisAlignment = CrossAxisAlignment.Start) {
                        if (title != null) {
                            Container(
                                alignment = Alignment.CenterLeft,
                                padding = TitlePadding
                            ) {
                                val textStyle = +themeTextStyle { h6 }
                                CurrentTextStyleProvider(textStyle) {
                                    title()
                                }
                            }
                        } else {
                            // TODO(b/138924683): Temporary until padding for the Text's
                            //  baseline
                            HeightSpacer(NoTitleExtraHeight)
                        }

                        Container(alignment = Alignment.CenterLeft, padding = TextPadding) {
                            val textStyle = +themeTextStyle { body1 }
                            CurrentTextStyleProvider(textStyle) {
                                text()
                            }
                        }
                        HeightSpacer(height = TextToButtonsHeight)
                        AlertDialogButtonLayout(
                            confirmButton = confirmButton,
                            dismissButton = dismissButton,
                            buttonLayout = buttonLayout
                        )
                    }
                }
            }
        }
    }
}

// TODO(b/138925106): Add Auto mode when the flow layout is implemented
/**
 * An enum which specifies how the buttons are positioned inside the [AlertDialog]:
 *
 * [SideBySide] - positions the dismiss button on the left side of the confirm button.
 * [Stacked] - positions the dismiss button below the confirm button.
 */
enum class AlertDialogButtonLayout {
    SideBySide,
    Stacked
}

@Composable
private fun AlertDialogButtonLayout(
    confirmButton: @Composable() () -> Unit,
    dismissButton: @Composable() (() -> Unit)?,
    buttonLayout: AlertDialogButtonLayout
) {
    Container(padding = ButtonsPadding, alignment = Alignment.CenterRight, expanded = true) {
        if (buttonLayout == AlertDialogButtonLayout.SideBySide) {
            Row(mainAxisAlignment = MainAxisAlignment.End) {
                if (dismissButton != null) {
                    dismissButton()
                    WidthSpacer(ButtonsWidthSpace)
                }

                confirmButton()
            }
        } else {
            Column(mainAxisSize = LayoutSize.Expand) {
                confirmButton()

                if (dismissButton != null) {
                    HeightSpacer(ButtonsHeightSpace)
                    dismissButton()
                }
            }
        }
    }
}

private val AlertDialogWidth = 280.dp
private val ButtonsPadding = EdgeInsets(left = 0.dp, top = 8.dp, right = 8.dp, bottom = 8.dp)
private val ButtonsWidthSpace = 8.dp
private val ButtonsHeightSpace = 12.dp
// TODO(b/138924683): Top padding should be actually be a distance between the Text baseline and
//  the Title baseline
private val TextPadding = EdgeInsets(left = 24.dp, top = 20.dp, right = 24.dp, bottom = 0.dp)
// TODO(b/138924683): Top padding should be actually be relative to the Text baseline
private val TitlePadding = EdgeInsets(left = 24.dp, top = 24.dp, right = 24.dp, bottom = 0.dp)
// The height difference of the padding between a Dialog with a title and one without a title
private val NoTitleExtraHeight = 2.dp
private val TextToButtonsHeight = 28.dp
// TODO: The corner radius should be customizable
private val AlertDialogShape = RoundedCornerShape(4.dp)