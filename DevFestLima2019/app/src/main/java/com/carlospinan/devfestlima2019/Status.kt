package com.carlospinan.devfestlima2019

import androidx.compose.Model
import androidx.compose.frames.ModelList

/**
 * @author Carlos Piñan
 */
sealed class Screen {
    object Home : Screen()
    object Authors : Screen()
    object Sessions : Screen()
}

@Model
object DevFestStatus {
    var currentScreen: Screen = Screen.Home

    val favorites = ModelList<String>()
    val selectedTopics = ModelList<String>()
}

fun navigateTo(destination: Screen) {
    DevFestStatus.currentScreen = destination
}