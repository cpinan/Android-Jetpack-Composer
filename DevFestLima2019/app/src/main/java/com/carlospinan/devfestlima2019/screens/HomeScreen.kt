package com.carlospinan.devfestlima2019.screens

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.sp
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.Padding
import androidx.ui.material.Divider
import androidx.ui.material.themeTextStyle
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextAlign

/**
 * @author Carlos Piñan
 */
@Composable
fun HomeScreen() {
    VerticalScroller {
        Padding(padding = 8.dp) {
            Column {
                Text(
                    text = "DevFest Lima 2019",
                    style = +themeTextStyle { h1 },
                    paragraphStyle = ParagraphStyle(
                        textAlign = TextAlign.Center
                    )
                )

                HeightSpacer(height = 8.dp)
                Divider(height = 4.dp)
                HeightSpacer(height = 8.dp)
                Text(
                    text = "GDG DevFest Lima 2019 es un evento organizado por el GDG Lima. Este 2019 reuniremos a los mejores en Android, Cloud, Web UI, Firebase y Artificial Intelligence (AI) en un día lleno de sesiones, networking y exhibiciones. Únete a nuestra celebración de comunidad para comunidad.",
                    style = +themeTextStyle { h5 },
                    paragraphStyle = ParagraphStyle(
                        textAlign = TextAlign.Justify
                    )
                )
                HeightSpacer(height = 16.dp)

                Text(
                    text = "26th Oct 2019   08:00 am to 4:30 pm   CREHANA",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                HeightSpacer(height = 32.dp)

                Text(
                    text = "#GDGLima   #DevFestLima   #GDG   #DevFest  ",
                    style = TextStyle(
                        color = Color.Blue,
                        fontSize = 12.sp
                    )
                )

            }
        }
    }
}