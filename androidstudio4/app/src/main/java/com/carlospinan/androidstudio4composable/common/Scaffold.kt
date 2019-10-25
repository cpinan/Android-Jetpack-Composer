package com.carlospinan.androidstudio4composable.common

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.layout.FlexColumn
import androidx.ui.material.surface.Surface
import androidx.ui.material.themeColor

/**
 * @author Carlos Piñan
 */
@Composable
fun Scaffold(
    appBar: @Composable() () -> Unit,
    children: @Composable() () -> Unit
) {
    FlexColumn {
        inflexible {
            appBar()
        }
        expanded(1.0f) {
            Surface(
                color = +themeColor { background }
            ) {
                children()
            }
        }
    }
}