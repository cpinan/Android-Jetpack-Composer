package com.carlospinan.jetpackexample.gmail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.core.sp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.SimpleImage
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.imageFromResource
import androidx.ui.layout.*
import androidx.ui.material.DrawerState
import androidx.ui.material.MaterialColors
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ModalDrawerLayout
import androidx.ui.material.surface.Card
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextOverflow
import com.carlospinan.jetpackexample.gmail.mock.MockData
import com.carlospinan.jetpackexample.gmail.mock.items

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Home()
        }
    }

    @Composable
    fun Home() {
        MaterialTheme(
            colors = MaterialColors(
                primary = Color(0xFFD44638)
            )
        ) {
            FlexColumn {
                expanded(1F) {
                    val (state, onStateChange) = +state { DrawerState.Closed }
                    ModalDrawerLayout(
                        drawerState = state,
                        onStateChange = onStateChange,
                        drawerContent = { HomeDrawerContent() },
                        bodyContent = { HomeBodyContent() }
                    )
                }
            }
        }
    }

    @Composable
    fun HomeItem(mock: MockData) {
        var isClicked = +state { false }
        Clickable(
            onClick = {
                isClicked.value = !isClicked.value
            }
        ) {
            Card {
                Padding(16.dp) {
                    FlexRow {
                        inflexible {
                            Container(width = 48.dp, height = 48.dp) {
                                DrawShape(shape = CircleShape, color = mock.color)
                                Text(
                                    "${mock.shortAuthor}",
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.bold
                                    )
                                )
                            }
                        }
                        expanded(2f) {
                            Padding(
                                left = 16.dp
                            ) {
                                Column {
                                    Text(
                                        "${mock.title}", style = TextStyle(
                                            fontSize = 14.sp
                                        )
                                    )
                                    Text(
                                        overflow = TextOverflow.Ellipsis,
                                        softWrap = false,
                                        maxLines = 2,
                                        text = "${mock.content}",
                                        style = TextStyle(
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                        val resource =
                            if (isClicked.value) android.R.drawable.star_on else android.R.drawable.star_off
                        inflexible {
                            Container(width = 16.dp, height = 16.dp) {
                                SimpleImage(
                                    image = imageFromResource(
                                        resources,
                                        resource
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HomeDrawerContent() {

    }

    @Composable
    fun HomeBodyContent() {
        VerticalScroller {
            Column(
                mainAxisSize = LayoutSize.Expand
            ) {
                Card {
                    Padding(
                        left = 16.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ) {
                        FlexRow {
                            inflexible { Text("RECIBIDOS") }
                        }
                    }
                }
                for (mock in items) {
                    HomeItem(mock)
                }
            }
        }

    }

}