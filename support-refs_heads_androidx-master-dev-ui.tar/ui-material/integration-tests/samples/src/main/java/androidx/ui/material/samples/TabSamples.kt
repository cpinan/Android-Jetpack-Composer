/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.ui.material.samples

import androidx.animation.transitionDefinition
import androidx.annotation.Sampled
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.animation.ColorPropKey
import androidx.ui.animation.PxPropKey
import androidx.ui.animation.Transition
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.core.ambientDensity
import androidx.ui.core.dp
import androidx.ui.core.toPx
import androidx.ui.core.withDensity
import androidx.ui.foundation.ColoredRect
import androidx.ui.foundation.selection.MutuallyExclusiveSetItem
import androidx.ui.foundation.shape.border.Border
import androidx.ui.foundation.shape.border.DrawBorder
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.FlexColumn
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Padding
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.material.themeTextStyle
import androidx.ui.graphics.Image

@Sampled
@Composable
fun TextTabs() {
    val state = +state { 0 }
    val titles = listOf("TAB 1", "TAB 2", "TAB 3 WITH LOTS OF TEXT")
    FlexColumn {
        inflexible {
            TabRow(items = titles, selectedIndex = state.value) { index, text ->
                Tab(text = text, selected = state.value == index) { state.value = index }
            }
        }
        flexible(flex = 1f) {
            Center {
                Text(
                    text = "Text tab ${state.value + 1} selected",
                    style = +themeTextStyle { body1 }
                )
            }
        }
    }
}

@Composable
fun IconTabs(image: Image) {
    val state = +state { 0 }
    val icons = listOf(image, image, image)
    FlexColumn {
        inflexible {
            TabRow(items = icons, selectedIndex = state.value) { index, icon ->
                Tab(icon = icon, selected = state.value == index) { state.value = index }
            }
        }
        flexible(flex = 1f) {
            Center {
                Text(
                    text = "Icon tab ${state.value + 1} selected",
                    style = +themeTextStyle { body1 }
                )
            }
        }
    }
}

// TODO: r8 bug preventing us from destructuring data inline
@Composable
fun TextAndIconTabs(image: Image) {
    val state = +state { 0 }
    val titlesAndIcons =
        listOf("TAB 1" to image, "TAB 2" to image, "TAB 3 WITH LOTS OF TEXT" to image)
    FlexColumn {
        inflexible {
            TabRow(items = titlesAndIcons, selectedIndex = state.value) { index, data ->
                val (title, icon) = data
                Tab(text = title, icon = icon, selected = state.value == index) {
                    state.value = index
                }
            }
        }
        flexible(flex = 1f) {
            Center {
                Text(
                    text = "Text and icon tab ${state.value + 1} selected",
                    style = +themeTextStyle { body1 }
                )
            }
        }
    }
}

@Composable
fun ScrollingTextTabs() {
    val state = +state { 0 }
    val titles = listOf(
        "TAB 1",
        "TAB 2",
        "TAB 3 WITH LOTS OF TEXT",
        "TAB 4",
        "TAB 5",
        "TAB 6 WITH LOTS OF TEXT",
        "TAB 7",
        "TAB 8",
        "TAB 9 WITH LOTS OF TEXT",
        "TAB 10"
    )
    FlexColumn {
        inflexible {
            TabRow(items = titles, selectedIndex = state.value, scrollable = true) { index, text ->
                Tab(text = text, selected = state.value == index) { state.value = index }
            }
        }
        flexible(flex = 1f) {
            Center {
                Text(
                    text = "Scrolling text tab ${state.value + 1} selected",
                    style = +themeTextStyle { body1 }
                )
            }
        }
    }
}

@Sampled
@Composable
fun FancyTabs() {
    val state = +state { 0 }
    val titles = listOf("TAB 1", "TAB 2", "TAB 3")
    FlexColumn {
        inflexible {
            TabRow(items = titles, selectedIndex = state.value) { index, title ->
                FancyTab(
                    title = title,
                    onClick = { state.value = index },
                    selected = (index == state.value)
                )
            }
        }
        flexible(flex = 1f) {
            Center {
                Text(
                    text = "Fancy tab ${state.value + 1} selected",
                    style = +themeTextStyle { body1 }
                )
            }
        }
    }
}

@Sampled
@Composable
fun FancyIndicatorTabs() {
    val state = +state { 0 }
    val titles = listOf("TAB 1", "TAB 2", "TAB 3")

    // Reuse the default transition, and provide our custom indicator as its child
    val indicatorContainer = @Composable { tabPositions: List<TabRow.TabPosition> ->
        TabRow.IndicatorContainer(tabPositions = tabPositions, selectedIndex = state.value) {
            FancyIndicator(Color.White)
        }
    }

    FlexColumn {
        inflexible {
            TabRow(
                items = titles,
                selectedIndex = state.value,
                indicatorContainer = indicatorContainer
            ) { index, text ->
                Tab(text = text, selected = state.value == index) { state.value = index }
            }
        }
        flexible(flex = 1f) {
            Center {
                Text(
                    text = "Fancy indicator tab ${state.value + 1} selected",
                    style = +themeTextStyle { body1 }
                )
            }
        }
    }
}

@Sampled
@Composable
fun FancyIndicatorContainerTabs() {
    val state = +state { 0 }
    val titles = listOf("TAB 1", "TAB 2", "TAB 3")

    val indicatorContainer = @Composable { tabPositions: List<TabRow.TabPosition> ->
        FancyIndicatorContainer(tabPositions = tabPositions, selectedIndex = state.value)
    }

    FlexColumn {
        inflexible {
            TabRow(
                items = titles,
                selectedIndex = state.value,
                indicatorContainer = indicatorContainer
            ) { index, text ->
                Tab(text = text, selected = state.value == index) { state.value = index }
            }
        }
        flexible(flex = 1f) {
            Center {
                Text(
                    text = "Fancy transition tab ${state.value + 1} selected",
                    style = +themeTextStyle { body1 }
                )
            }
        }
    }
}

@Composable
fun ScrollingFancyIndicatorContainerTabs() {
    val state = +state { 0 }
    val titles = listOf(
        "TAB 1",
        "TAB 2",
        "TAB 3 WITH LOTS OF TEXT",
        "TAB 4",
        "TAB 5",
        "TAB 6 WITH LOTS OF TEXT",
        "TAB 7",
        "TAB 8",
        "TAB 9 WITH LOTS OF TEXT",
        "TAB 10"
    )
    val indicatorContainer = @Composable { tabPositions: List<TabRow.TabPosition> ->
        FancyIndicatorContainer(tabPositions = tabPositions, selectedIndex = state.value)
    }

    FlexColumn {
        inflexible {
            TabRow(
                items = titles,
                selectedIndex = state.value,
                indicatorContainer = indicatorContainer,
                scrollable = true
            ) { index, text ->
                Tab(text = text, selected = state.value == index) { state.value = index }
            }
        }
        flexible(flex = 1f) {
            Center {
                Text(
                    text = "Scrolling fancy transition tab ${state.value + 1} selected",
                    style = +themeTextStyle { body1 }
                )
            }
        }
    }
}

// TODO: make this use our base tab when it's exposed and available to use
@Sampled
@Composable
fun FancyTab(title: String, onClick: () -> Unit, selected: Boolean) {
    MutuallyExclusiveSetItem(selected = selected, onClick = { onClick() }) {
        Container(height = 50.dp, padding = EdgeInsets(10.dp)) {
            Column(
                mainAxisSize = LayoutSize.Expand,
                crossAxisAlignment = CrossAxisAlignment.Center
            ) {
                val color = if (selected) Color.Red else Color.Gray
                ColoredRect(height = 10.dp, width = 10.dp, color = color)
                Padding(5.dp) {
                    Text(text = title, style = +themeTextStyle { body1 })
                }
            }
        }
    }
}

@Sampled
@Composable
fun FancyIndicator(color: Color) {
    // Draws a rounded rectangular with border around the Tab, with a 5.dp padding from the edges
    // Color is passed in as a parameter [color]
    Padding(5.dp) {
        Container(expanded = true) {
            DrawBorder(RoundedCornerShape(5.dp), Border(color, 2.dp))
        }
    }
}

@Sampled
@Composable
fun FancyIndicatorContainer(tabPositions: List<TabRow.TabPosition>, selectedIndex: Int) {
    val indicatorStart = +memo { PxPropKey() }
    val indicatorEnd = +memo { PxPropKey() }
    val indicatorColor = +memo { ColorPropKey() }

    val colors = listOf(Color.Yellow, Color.Red, Color.Green)
    val transitionDefinition =
        +memo(tabPositions) {
            transitionDefinition {
                tabPositions.forEachIndexed { index, position ->
                    state(index) {
                        this[indicatorStart] = position.left.toPx()
                        this[indicatorEnd] = position.right.toPx()
                        this[indicatorColor] = colors[index % colors.size]
                    }
                }
                repeat(tabPositions.size) { from ->
                    repeat(tabPositions.size) { to ->
                        if (from != to) {
                            transition(fromState = from, toState = to) {
                                // Handle directionality here, if we are moving to the right, we
                                // want the right side of the indicator to move faster, if we are
                                // moving to the left, we want the left side to move faster.
                                val startStiffness = if (from < to) 50f else 1000f
                                val endStiffness = if (from < to) 1000f else 50f
                                indicatorStart using physics {
                                    dampingRatio = 1f
                                    stiffness = startStiffness
                                }
                                indicatorEnd using physics {
                                    dampingRatio = 1f
                                    stiffness = endStiffness
                                }
                            }
                        }
                    }
                }
            }
        }

    // Fill up the entire TabRow with this container, and place children at the left so we can use
    // Padding to set the 'offset'
    Container(expanded = true, alignment = Alignment.BottomLeft) {
        Transition(transitionDefinition, selectedIndex) { state ->
            val offset = withDensity(+ambientDensity()) { state[indicatorStart].toDp() }
            val width = withDensity(+ambientDensity()) {
                (state[indicatorEnd] - state[indicatorStart]).toDp()
            }
            Padding(left = offset) {
                Container(width = width) {
                    // Pass the current color to the indicator
                    FancyIndicator(state[indicatorColor])
                }
            }
        }
    }
}
