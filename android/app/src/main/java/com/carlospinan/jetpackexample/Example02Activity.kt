package com.carlospinan.jetpackexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.Sp
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.text.TextStyle

class Example02Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExampleScreen()
        }
    }

    @Composable
    fun ExampleScreen() {
        MaterialTheme {
            Text(
                text = "Example 02 Activity",
                style = TextStyle(
                    color = Color.Magenta,
                    fontSize = Sp(16.0f)
                )
            )
        }
    }

}