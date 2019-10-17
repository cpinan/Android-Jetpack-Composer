package com.carlospinan.jetpackexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.foundation.SimpleImage
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Image
import androidx.ui.graphics.imageFromResource
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme

class Example06Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SimpleList()
            }
        }
    }

    @Composable
    private fun SimpleList() {
        val icon24 = imageFromResource(resources, android.R.drawable.ic_btn_speak_now)
        val icon40 = imageFromResource(resources, android.R.drawable.ic_media_next)
        val icon56 = imageFromResource(resources, android.R.drawable.ic_dialog_alert)
        Center {
            VerticalScroller {
                Column {
                    OneLineListItems(icon24, icon40, icon56)
                    TwoLineListItems(icon24, icon40)
                    ThreeLineListItems(icon24, icon40)
                }
            }
        }

    }

    @Composable
    fun OneLineListItems(icon24x24: Image, icon40x40: Image, icon56x56: Image) {
        Column {
            ListItem(text = "One line list item with no icon")
            Divider()
            ListItem(text = "One line list item with 24x24 icon", icon = icon24x24)
            Divider()
            ListItem(text = "One line list item with 40x40 icon", icon = icon40x40)
            Divider()
            ListItem(text = "One line list item with 56x56 icon", icon = icon56x56)
            Divider()
            ListItem(text = "One line clickable list item", icon = icon56x56, onClick = {})
            Divider()
            ListItem(
                text = { Text("One line list item with trailing icon") },
                trailing = { SimpleImage(icon24x24) }
            )
            Divider()
            ListItem(
                text = { Text("One line list item") },
                icon = { SimpleImage(icon40x40) },
                trailing = { SimpleImage(icon24x24) }
            )
            Divider()
        }
    }

    @Composable
    fun TwoLineListItems(icon24x24: Image, icon40x40: Image) {
        Column {
            ListItem(text = "Two line list item", secondaryText = "Secondary text")
            Divider()
            ListItem(text = "Two line list item", overlineText = "OVERLINE")
            Divider()
            ListItem(
                text = "Two line list item with 24x24 icon",
                secondaryText = "Secondary text",
                icon = icon24x24
            )
            Divider()
            ListItem(
                text = "Two line list item with 40x40 icon",
                secondaryText = "Secondary text",
                icon = icon40x40
            )
            Divider()
            ListItem(
                text = "Two line list item with 40x40 icon",
                secondaryText = "Secondary text",
                metaText = "meta",
                icon = icon40x40
            )
            Divider()
            ListItem(
                text = { Text("Two line list item") },
                secondaryText = { Text("Secondary text") },
                icon = { SimpleImage(icon40x40) },
                trailing = {
                    // TODO(popam): put checkbox here after b/140292836 is fixed
                    SimpleImage(icon24x24)
                }
            )
            Divider()
        }
    }

    @Composable
    fun ThreeLineListItems(icon24x24: Image, icon40x40: Image) {
        Column {
            ListItem(
                text = "Three line list item",
                secondaryText = "This is a long secondary text for the current list item, displayed" +
                        " on two lines",
                singleLineSecondaryText = false,
                metaText = "meta"
            )
            Divider()
            ListItem(
                text = "Three line list item",
                overlineText = "OVERLINE",
                secondaryText = "Secondary text"
            )
            Divider()
            ListItem(
                text = "Three line list item with 24x24 icon",
                secondaryText = "This is a long secondary text for the current list item, displayed" +
                        " on two lines",
                singleLineSecondaryText = false,
                icon = icon24x24
            )
            Divider()
            ListItem(
                text = { Text("Three line list item with trailing icon") },
                secondaryText = {
                    Text(
                        "This is a long secondary text for the current list" +
                                " item, displayed on two lines"
                    )
                },
                singleLineSecondaryText = false,
                trailing = { SimpleImage(icon40x40) }
            )
            Divider()
            ListItem(
                text = "Three line list item",
                overlineText = "OVERLINE",
                secondaryText = "Secondary text",
                metaText = "meta"
            )
            Divider()
        }
    }

}