package com.carlospinan.devfestlima2019

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.ColoredRect
import androidx.ui.foundation.selection.MutuallyExclusiveSetItem
import androidx.ui.layout.*
import androidx.ui.material.themeColor
import androidx.ui.material.themeTextStyle
import androidx.ui.text.style.TextOverflow

/**
 * @author Carlos Piñan
 */
@Composable
fun DevFestTab(title: String, onClick: () -> Unit, selected: Boolean) {
    MutuallyExclusiveSetItem(selected = selected, onClick = { onClick() }) {
        Container(
            modifier = Spacing(8.dp),
            height = 50.dp,
            padding = EdgeInsets(16.dp)
        ) {
            Column(
                ExpandedHeight,
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                val color = if (selected) +themeColor { secondary } else +themeColor { primary }
                ColoredRect(height = 4.dp, width = 4.dp, color = color)
                Padding(4.dp) {
                    Text(
                        text = title,
                        style = +themeTextStyle { body2 },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}