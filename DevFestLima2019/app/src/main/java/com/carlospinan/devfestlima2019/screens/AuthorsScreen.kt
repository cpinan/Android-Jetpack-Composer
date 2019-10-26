package com.carlospinan.devfestlima2019.screens

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.SimpleImage
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.surface.Card
import androidx.ui.material.themeTextStyle
import androidx.ui.res.imageResource
import androidx.ui.tooling.preview.Preview
import com.carlospinan.devfestlima2019.data.authors
import com.carlospinan.devfestlima2019.data.carlosPinan
import com.carlospinan.devfestlima2019.model.Author

/**
 * @author Carlos Piñan
 */
@Composable
fun AuthorsScreen() {
    VerticalScroller {
        Column(mainAxisSize = LayoutSize.Expand) {
            for (author in authors) {
                AuthorCard(author)
            }
        }
    }
}

@Composable
fun AuthorCard(author: Author) {
    Padding(padding = 16.dp) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp
        ) {
            FlexRow(
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                inflexible {
                    Padding(padding = 8.dp) {
                        Container(width = 48.dp, height = 48.dp) {
                            SimpleImage(image = +imageResource(author.pictureId))
                        }
                    }
                }
                expanded(1.0f) {
                    Column(
                        modifier = Spacing(8.dp)
                    ) {
                        Text(text = "${author.name}", style = +themeTextStyle { h3 })
                        Text(text = "${author.company}", style = +themeTextStyle { h4 })
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AuthorTest() {
    AuthorCard(author = carlosPinan)
}