package com.carlospinan.devfestlima2019

import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.animation.Crossfade
import androidx.ui.layout.FlexColumn
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TabRow
import androidx.ui.material.surface.Surface
import androidx.ui.material.themeColor
import com.carlospinan.devfestlima2019.data.tabs
import com.carlospinan.devfestlima2019.screens.AuthorsScreen
import com.carlospinan.devfestlima2019.screens.HomeScreen
import com.carlospinan.devfestlima2019.screens.SessionsScreen

/**
 * @author Carlos Piñan
 */
val tabState = +state { 0 }

@Composable
fun DevFestApp() {
    MaterialTheme {
        AppContent()
    }
}

@Composable
private fun AppContent() {
    Crossfade(DevFestStatus.currentScreen) { screen ->
        Surface(color = +themeColor { background }) {
            FlexColumn {
                inflexible {
                    TabRow(items = tabs, selectedIndex = tabState.value) { index, title ->
                        DevFestTab(
                            title = title,
                            onClick = {
                                when (index) {
                                    0 -> navigateTo(Screen.Home)
                                    1 -> navigateTo(Screen.Authors)
                                    2 -> navigateTo(Screen.Sessions)
                                }
                                tabState.value = index
                            },
                            selected = (index == tabState.value)
                        )
                    }
                }
                flexible(1.0f) {
                    when (screen) {
                        is Screen.Home -> HomeScreen()
                        is Screen.Sessions -> SessionsScreen()
                        is Screen.Authors -> AuthorsScreen()
                    }
                }
            }
        }
    }
}