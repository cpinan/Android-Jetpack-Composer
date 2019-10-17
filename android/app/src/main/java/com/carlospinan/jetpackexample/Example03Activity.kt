package com.carlospinan.jetpackexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Dp
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.Padding
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme

class Example03Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Example03()
        }
    }

    @Composable
    fun Example03() {
        val amount = +state { 0 }
        MaterialTheme(
            children = {
                Center {
                    Column {
                        Padding(
                            top = Dp(16.0f),
                            bottom = Dp(16.0f)
                        ) {
                            Text("Tapping Example")

                        }

                        Button(
                            "Add Value",
                            onClick = {
                                amount.value++
                            }
                        )

                        Button(
                            "Reduce Value",
                            onClick = {
                                amount.value--
                            }
                        )

                        Text(
                            "Current value is: ${amount.value}"
                        )

                    }
                }
            }
        )
    }

}