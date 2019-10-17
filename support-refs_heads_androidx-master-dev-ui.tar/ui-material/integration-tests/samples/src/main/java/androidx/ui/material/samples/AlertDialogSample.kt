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

package androidx.ui.material.samples

import androidx.annotation.Sampled
import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.material.AlertDialog
import androidx.ui.material.AlertDialogButtonLayout
import androidx.ui.material.Button

@Sampled
@Composable
fun SideBySideAlertDialogSample() {
    val openDialog = +state { true }

    if (openDialog.value) {
        AlertDialog(
            onCloseRequest = {
                // Because we are not setting openDialog.value to false here,
                // the user can close this dialog only via one of the buttons we provide.
            },
            title = {
                Text(text = "Title")
            },
            text = {
                Text("This area typically contains the supportive text" +
                        " which presents the details regarding the Dialog's purpose.")
            },
            confirmButton = {
                Button("Confirm", onClick = {
                    openDialog.value = false
                })
            },
            dismissButton = {
                Button("Dismiss", onClick = {
                    openDialog.value = false
                })
            },
            buttonLayout = AlertDialogButtonLayout.SideBySide
        )
    }
}

@Sampled
@Composable
fun StackedAlertDialogSample() {
    val openDialog = +state { true }

    if (openDialog.value) {
        AlertDialog(
            onCloseRequest = {
                // In this example we allow the dialog to be closed by other actions
                // such as taping outside or pressing the back button.
                openDialog.value = false
            },
            title = {
                Text(text = "Title")
            },
            text = {
                Text("This area typically contains the supportive text" +
                        " which presents the details regarding the Dialog's purpose.")
            },
            confirmButton = {
                Button("Long Confirm Button", onClick = {
                    openDialog.value = false
                })
            },
            dismissButton = {
                Button("Long Dismiss Button", onClick = {
                    openDialog.value = false
                })
            },
            buttonLayout = AlertDialogButtonLayout.Stacked
        )
    }
}