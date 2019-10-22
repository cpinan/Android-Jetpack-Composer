package com.carlospinan.jetpackexample.custom

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Row
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.Button
import androidx.ui.material.themeTextStyle

// @source https://www.youtube.com/watch?v=I5zRmCheVVg&t=1069s
@Composable
fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit
) {

    val forward = { onValueChange(value + 1) }
    val back = { onValueChange(value - 1) }
    Row(mainAxisSize = LayoutSize.Expand) {

        Button(text = "-", onClick = if (value > 0) back else null)

        WidthSpacer(8.dp)

        Container(width = 24.dp) {
            Text(text = "$value", style = +themeTextStyle { body1 })
        }

        WidthSpacer(8.dp)

        Button(text = "+", onClick = forward)

    }

}