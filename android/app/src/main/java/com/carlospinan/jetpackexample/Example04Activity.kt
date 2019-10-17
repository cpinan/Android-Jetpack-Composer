package com.carlospinan.jetpackexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Dp
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.Padding
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.themeTextStyle

@Model
private class MyCustomModel {

    var amount = +state { 0 }

    fun add() {
        amount.value++
    }

    fun reduce() {
        amount.value--
    }
}

@Composable
private fun ExampleScreen(model: MyCustomModel = MyCustomModel()) {
    MaterialTheme(
        children = {
            Center {
                Column {
                    Padding(
                        top = 16.dp,
                        bottom = Dp(16.0f)
                    ) {
                        Text("Tapping Example")

                    }

                    Button(
                        "Add Value",
                        onClick = {
                            model.add()
                        }
                    )

                    Button(
                        "Reduce Value",
                        onClick = {
                            model.reduce()
                        }
                    )

                    Text(
                        "Current value is: ${model.amount.value}",
                        style = +themeTextStyle { h3 }
                    )

                }
            }
        }
    )
}

class Example04Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExampleScreen()
        }
    }

}