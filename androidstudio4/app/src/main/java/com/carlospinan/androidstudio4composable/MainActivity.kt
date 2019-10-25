package com.carlospinan.androidstudio4composable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.core.sp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.SimpleImage
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextAlign
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import com.carlospinan.androidstudio4composable.common.Scaffold

val starModel = Star()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ComposableFlutterScreen()
            }
        }
    }
}

// Based on https://flutter.dev/docs/development/ui/layout/tutorial
@Composable
fun ComposableFlutterScreen() {
    MaterialTheme(
        colors = MaterialColors(),
        typography = MaterialTypography()
    ) {
        Scaffold(appBar = { AppBarHomeScreen() }) {
            HomeScreen()
        }
    }
}

@Composable
fun AppBarHomeScreen() {
    TopAppBar(
        title = { Text("@Composable layout demo") }
    )
}

@Composable
fun HomeScreen() {
    VerticalScroller {
        Column {
            HeaderSection()
            TitleSection()
            SectionButtons()
            TextExplanationSection()
        }
    }
}

@Preview
@Composable
fun HeaderSection() {
    Container(height = 280.dp, expanded = true) {
        SimpleImage(image = +imageResource(R.drawable.lake))
    }
}

@Preview
@Composable
fun TitleSection() {
    FlexRow(
        modifier = Spacing(
            top = 32.dp,
            left = 32.dp,
            right = 32.dp,
            bottom = 8.dp
        )
    ) {
        expanded(0.8f) {
            Column(
                crossAxisSize = LayoutSize.Expand
            ) {
                Text(
                    text = "Oeschinen Lake Campground",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Kandersteg, Switzerland",
                    style = TextStyle(
                        color = Color.Gray
                    )
                )
            }
        }
        inflexible {
            Clickable(
                onClick = {
                    starModel.enabled = !starModel.enabled
                    if (starModel.enabled) {
                        starModel.count++
                    } else {
                        starModel.count--
                    }
                }
            ) {
                Row(
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    SimpleImage(
                        image = if (starModel.enabled) +imageResource(android.R.drawable.star_on) else +imageResource(
                            android.R.drawable.star_off
                        ),
                        tint = Color.Red
                    )
                    Text(text = "${starModel.count}")
                }
            }
        }
    }
}

@Composable
fun HomeButton(
    image: Image,
    text: String
) {
    val dialogState = +state { false }
    HomeButtonDialog("$text", dialogState)
    Clickable(
        onClick = {
            dialogState.value = true
        }
    ) {
        Column(
            mainAxisAlignment = MainAxisAlignment.Center,
            crossAxisAlignment = CrossAxisAlignment.Center
        ) {
            Container(
                height = 64.dp,
                width = 64.dp
            ) {
                SimpleImage(image = image)
            }
            Padding(padding = 8.dp) {
                Text(
                    text = "${text.toUpperCase()}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W400
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun SectionButtons() {
    Row(
        modifier = Spacing(16.dp),
        mainAxisSize = LayoutSize.Expand,
        mainAxisAlignment = MainAxisAlignment.SpaceEvenly
    ) {
        HomeButton(image = +imageResource(android.R.drawable.ic_menu_call), text = "Call")
        HomeButton(image = +imageResource(android.R.drawable.ic_menu_mapmode), text = "Route")
        HomeButton(image = +imageResource(android.R.drawable.ic_menu_share), text = "Share")
    }
}

@Preview
@Composable
fun TextExplanationSection() {
    Padding(padding = 16.dp) {
        Text(
            text = +stringResource(R.string.explanation),
            overflow = TextOverflow.Fade,
            maxLines = 20,
            paragraphStyle = ParagraphStyle(
                textAlign = TextAlign.Justify
            )
        )
    }
}

@Composable
fun HomeButtonDialog(
    text: String,
    openDialog: State<Boolean>
) {
    if (openDialog.value) {
        AlertDialog(
            onCloseRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "$text")
            },
            text = {
                Text("Description for $text should be placed here.")
            },
            confirmButton = {
                Button(+stringResource(R.string.accept), onClick = {
                    openDialog.value = false
                })
            },
            dismissButton = {
                Button(+stringResource(R.string.dismiss), onClick = {
                    openDialog.value = false
                })
            },
            buttonLayout = AlertDialogButtonLayout.Stacked
        )
    }
}

@Model
data class Star(
    var count: Int = 0,
    var enabled: Boolean = false
)
