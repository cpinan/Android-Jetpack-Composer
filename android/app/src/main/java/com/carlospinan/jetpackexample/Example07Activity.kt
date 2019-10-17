package com.carlospinan.jetpackexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.*
import androidx.ui.foundation.ColoredRect
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme

class Example07Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Center {
                    VerticalScroller {
                        Column {
                            Divider(
                                height = 16.dp,
                                color = Color.Black
                            )
                            ExampleFlexRow()
                            Divider(
                                height = 16.dp,
                                color = Color.Black
                            )
                            ExampleFlexColumn()
                            Divider(
                                height = 16.dp,
                                color = Color.Black
                            )
                            ExampleRow()
                            Divider(
                                height = 16.dp,
                                color = Color.Black
                            )
                            ExampleColumn()
                            Column {
                                listOf("Bobby", "Pedro", "Yani", "Kausha").forEach {
                                    Text(text = it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ExampleFlexRow() {
        FlexRow {
            expanded(flex = 2f) {
                Center {
                    SizedRectangle(color = Color.Red, width = 40.dp, height = 40.dp)
                }
            }
            inflexible {
                SizedRectangle(color = Color.Green, width = 40.dp)
            }
            expanded(flex = 1f) {
                SizedRectangle(color = Color.Blue)
            }
        }
    }

    @Composable
    private fun ExampleFlexColumn() {
        FlexColumn {
            expanded(flex = 2f) {
                Center {
                    SizedRectangle(color = Color(0xFF0000FF), width = 40.dp, height = 40.dp)
                }
            }
            inflexible {
                SizedRectangle(color = Color(0xFFFF0000), height = 40.dp)
            }
            expanded(flex = 1f) {
                SizedRectangle(color = Color(0xFF00FF00))
            }
        }
    }

    @Composable
    private fun ExampleRow() {
        Row(mainAxisSize = LayoutSize.Expand) {
            SizedRectangle(color = Color.Magenta, width = 40.dp, height = 80.dp)
            SizedRectangle(color = Color.Red, width = 40.dp, height = 80.dp)
            SizedRectangle(color = Color.Yellow, width = 40.dp, height = 80.dp)
            SizedRectangle(color = Color.Green, width = 40.dp, height = 80.dp)
        }
    }

    @Composable
    private fun ExampleColumn() {
        Column(mainAxisSize = LayoutSize.Expand) {
            SizedRectangle(color = Color.Magenta, width = 40.dp, height = 80.dp)
            SizedRectangle(color = Color.Red, width = 40.dp, height = 80.dp)
            SizedRectangle(color = Color.Yellow, width = 40.dp, height = 80.dp)
            SizedRectangle(color = Color.Green, width = 40.dp, height = 80.dp)
        }
    }

    @Composable
    private fun SizedRectangle(
        modifier: Modifier = Modifier.None,
        color: Color,
        width: Dp? = null,
        height: Dp? = null
    ) {
        ColoredRect(
            color = color,
            modifier = modifier,
            width = width ?: 200.dp,
            height = height ?: 200.dp
        )
    }

}