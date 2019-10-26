package com.carlospinan.devfestlima2019.screens

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.SimpleImage
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.Divider
import androidx.ui.material.surface.Card
import androidx.ui.material.themeTextStyle
import androidx.ui.res.imageResource
import androidx.ui.tooling.preview.Preview
import com.carlospinan.devfestlima2019.data.sessions
import com.carlospinan.devfestlima2019.model.Session

/**
 * @author Carlos Piñan
 */
@Preview
@Composable
fun SessionsScreen() {
    VerticalScroller {
        Column(mainAxisSize = LayoutSize.Expand) {
            for (session in sessions) {
                SessionCard(session)
            }
        }
    }
}

@Composable
fun SessionCard(session: Session) {
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
                            SimpleImage(image = +imageResource(session.author.pictureId))
                        }
                    }
                }
                expanded(1.0f) {
                    Column(
                        modifier = Spacing(8.dp)
                    ) {
                        Text(text = "${session.title}", style = +themeTextStyle { h4 })
                        Text(text = "${session.description}", style = +themeTextStyle { h5 })
                        HeightSpacer(height = 4.dp)
                        Divider()
                        HeightSpacer(height = 16.dp)
                        Text(text = "${session.author.name}", style = +themeTextStyle { h6 })
                    }
                }
            }
        }
    }
}