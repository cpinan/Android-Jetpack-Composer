package com.carlospinan.jetpackexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.foundation.ColoredRect
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*

class Example08Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BottomDrawer()
            }
        }
    }

    @Composable
    fun StaticDrawer() {
        Row(mainAxisSize = LayoutSize.Expand) {
            StaticDrawer {
                Center {
                    Text("Drawer Content")
                }
            }
            ColoredRect(Color.Black, width = 1.dp)
            Text("Rest of App")
        }
    }

    @Composable
    fun ModalDrawer() {
        val (state, onStateChange) = +state { DrawerState.Closed }
        val appContentText =
            if (state == DrawerState.Closed) ">>> Pull to open >>>" else "<<< Swipe to close <<<"
        ModalDrawerLayout(
            drawerState = state,
            onStateChange = onStateChange,
            drawerContent = { DrawerContent(onStateChange) },
            bodyContent = { DrawerBodyContent(appContentText, onStateChange) }
        )
    }

    @Composable
    fun BottomDrawer() {
        val (state, onStateChange) = +state { DrawerState.Closed }
        val appContentText =
            if (state == DrawerState.Closed) "▲▲▲ Pull to open ▲▲▲" else "▼▼▼ Drag down to close ▼▼▼"
        BottomDrawerLayout(
            drawerState = state,
            onStateChange = onStateChange,
            drawerContent = { DrawerContent(onStateChange) },
            bodyContent = { DrawerBodyContent(appContentText, onStateChange) }
        )
    }

    // UTILS

    @Composable
    private fun DrawerContent(onStateChange: (DrawerState) -> Unit) {
        Container(expanded = true) {
            Column(mainAxisSize = LayoutSize.Expand) {
                Text(text = "Drawer Content")
                HeightSpacer(20.dp)
                Button(
                    text = "Close Drawer",
                    onClick = { onStateChange(DrawerState.Closed) }
                )
            }
        }
    }

    @Composable
    private fun DrawerBodyContent(text: String, onDrawerStateChange: (DrawerState) -> Unit) {
        Center {
            Column(mainAxisSize = LayoutSize.Expand) {
                Text(text = text)
                HeightSpacer(20.dp)
                Button(
                    text = "Click to open",
                    onClick = { onDrawerStateChange(DrawerState.Opened) }
                )
            }
        }
    }

}