package com.carlospinan.jetpackexample.custom

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.layout.FlexColumn
import androidx.ui.material.surface.Surface
import androidx.ui.material.themeColor


@Composable
fun Scaffold(appBar: @Composable() () -> Unit, children: @Composable() () -> Unit) {
    FlexColumn {
        inflexible {
            appBar()
        }
        expanded(flex = 1.0f) {
            Surface(color = +themeColor { background }) {
                children()
            }
        }
    }
}