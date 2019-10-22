package com.carlospinan.jetpackexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.graphics.Color
import androidx.ui.graphics.imageFromResource
import androidx.ui.layout.Center
import androidx.ui.layout.FlexColumn
import androidx.ui.material.AppBarIcon
import androidx.ui.material.MaterialColors
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar

class Example01Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BobbyApp("This is Bobby World")
        }

    }

    @Composable
    fun BobbyApp(name: String) = MaterialTheme(
        colors = MaterialColors(
            primary = Color.Black
        )
    ) {
        val contextualActions = listOf(
            imageFromResource(resources, android.R.drawable.ic_input_add),
            imageFromResource(resources, android.R.drawable.ic_input_delete)
        )

        val navigationIcon: @Composable() () -> Unit = {
            AppBarIcon(
                imageFromResource(
                    resources,
                    android.R.drawable.ic_menu_more
                )
            ) { /* doSomething()*/ }
        }

        FlexColumn {
            inflexible {
                // Item height will be equal content height
                TopAppBar( // App Bar with title
                    title = { Text("Jetpack Compose Sample") },
                    contextualActions = contextualActions,
                    navigationIcon = navigationIcon
                ) { actionImage ->
                    AppBarIcon(actionImage) {

                    }
                }
            }
            expanded(1F) {
                // occupy whole empty space in the Column
                Center {
                    // Center content
                    Text("BobbyApp $name!") // Text label
                }
            }
        }
    }
}
