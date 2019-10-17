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

package androidx.ui.material

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.Dp
import androidx.ui.core.FirstBaseline
import androidx.ui.core.IntPx
import androidx.ui.core.IntPxSize
import androidx.ui.core.LastBaseline
import androidx.ui.core.Layout
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.ipx
import androidx.ui.core.max
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.SimpleImage
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.FlexRow
import androidx.ui.layout.Padding
import androidx.ui.material.ripple.Ripple
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextOverflow

/**
 * Material Design implementation of [list items](https://material.io/components/lists).
 *
 * This component can be used to achieve the list item templates existing in the spec. For example:
 * - one-line items
 * @sample androidx.ui.material.samples.OneLineListItems
 * - two-line items
 * @sample androidx.ui.material.samples.TwoLineListItems
 * - three-line items
 * @sample androidx.ui.material.samples.ThreeLineListItems
 *
 * @param text The primary text of the list item
 * @param icon The leading supporting visual of the list item
 * @param secondaryText The secondary text of the list item
 * @param singleLineSecondaryText Whether the secondary text is single line
 * @param overlineText The text displayed above the primary text
 * @param metaText The meta text to be displayed in the trailing position
 * @param onClick Callback to be invoked when the list item is clicked
 */
@Composable
fun ListItem(
    text: String,
    icon: Image? = null,
    secondaryText: String? = null,
    // TODO(popam): find a way to remove this
    singleLineSecondaryText: Boolean = true,
    overlineText: String? = null,
    metaText: String? = null,
    onClick: (() -> Unit)? = null
) {
    val iconComposable: @Composable() (() -> Unit)? = icon?.let {
        { SimpleImage(it) }
    }
    val textComposable: @Composable() () -> Unit = text.let {
        { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
    }
    val secondaryTextComposable: @Composable() (() -> Unit)? = secondaryText?.let {
        {
            val maxLines = if (!singleLineSecondaryText && overlineText == null) 2 else 1
            Text(it, maxLines = maxLines, overflow = TextOverflow.Ellipsis)
        }
    }
    val overlineTextComposable: @Composable() (() -> Unit)? = overlineText?.let {
        { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
    }
    val metaTextComposable: @Composable() (() -> Unit)? = metaText?.let {
        { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
    }
    ListItem(
        textComposable,
        iconComposable,
        secondaryTextComposable,
        singleLineSecondaryText,
        overlineTextComposable,
        metaTextComposable,
        onClick
    )
}

/**
 * Material Design implementation of [list items](https://material.io/components/lists).
 *
 * This component can be used to achieve the list item templates existing in the spec. For example:
 * - one-line items
 * @sample androidx.ui.material.samples.OneLineListItems
 * - two-line items
 * @sample androidx.ui.material.samples.TwoLineListItems
 * - three-line items
 * @sample androidx.ui.material.samples.ThreeLineListItems
 *
 * @param text The primary text of the list item
 * @param icon The leading supporting visual of the list item
 * @param secondaryText The secondary text of the list item
 * @param singleLineSecondaryText Whether the secondary text is single line
 * @param overlineText The text displayed above the primary text
 * @param trailing The trailing meta text or meta icon of the list item
 * @param onClick Callback to be invoked when the list item is clicked
 */
// TODO(popam): b/137311217
@Suppress("USELESS_CAST")
@Composable
fun ListItem(
    text: @Composable() (() -> Unit),
    icon: @Composable() (() -> Unit)? = null as @Composable() (() -> Unit)?,
    secondaryText: @Composable() (() -> Unit)? = null as @Composable() (() -> Unit)?,
    singleLineSecondaryText: Boolean = true,
    overlineText: @Composable() (() -> Unit)? = null as @Composable() (() -> Unit)?,
    trailing: @Composable() (() -> Unit)? = null as @Composable() (() -> Unit)?,
    onClick: (() -> Unit)? = null
) {
    val styledText = applyTextStyle(PrimaryTextStyle, text)!!
    val styledSecondaryText = applyTextStyle(SecondaryTextStyle, secondaryText)
    val styledOverlineText = applyTextStyle(OverlineTextStyle, overlineText)
    val styledTrailing = applyTextStyle(MetaTextStyle, trailing)

    val item = @Composable {
        if (styledSecondaryText == null && styledOverlineText == null) {
            OneLine.ListItem(icon, styledText, styledTrailing)
        } else if ((styledOverlineText == null && singleLineSecondaryText) ||
            styledSecondaryText == null) {
            TwoLine.ListItem(
                icon,
                styledText,
                styledSecondaryText,
                styledOverlineText,
                styledTrailing
            )
        } else {
            ThreeLine.ListItem(
                icon,
                styledText,
                styledSecondaryText,
                styledOverlineText,
                styledTrailing
            )
        }
    }

    if (onClick != null) {
        val rippleColor = (+themeColor { onSurface }).copy(alpha = RippleOpacity)
        Ripple(bounded = true, color = rippleColor) {
            Clickable(onClick = onClick, children = item)
        }
    } else {
        item()
    }
}

private object OneLine {
    // TODO(popam): support wide icons
    // TODO(popam): convert these to sp
    // List item related constants.
    private val MinHeight = 48.dp
    private val MinHeightWithIcon = 56.dp
    // Icon related constants.
    private val IconMinPaddedWidth = 40.dp
    private val IconLeftPadding = 16.dp
    private val IconVerticalPadding = 8.dp
    // Content related constants.
    private val ContentLeftPadding = 16.dp
    private val ContentRightPadding = 16.dp
    // Trailing related constants.
    private val TrailingRightPadding = 16.dp

    @Composable
    fun ListItem(
        icon: @Composable() (() -> Unit)?,
        text: @Composable() (() -> Unit),
        trailing: @Composable() (() -> Unit)?
    ) {
        val minHeight = if (icon == null) MinHeight else MinHeightWithIcon
        ConstrainedBox(constraints = DpConstraints(minHeight = minHeight)) {
            FlexRow(crossAxisAlignment = CrossAxisAlignment.Center) {
                inflexible {
                    if (icon != null) {
                        Container(
                            alignment = Alignment.CenterLeft,
                            constraints = DpConstraints(
                                minWidth = IconLeftPadding + IconMinPaddedWidth
                            ),
                            padding = EdgeInsets(
                                // TODO(popam): remove left padding for wide icons
                                left = IconLeftPadding,
                                top = IconVerticalPadding,
                                bottom = IconVerticalPadding
                            ),
                            children = icon
                        )
                    }
                }
                expanded(flex = 1f) {
                    Padding(left = ContentLeftPadding, right = ContentRightPadding, children = text)
                }
                inflexible {
                    if (trailing != null) {
                        Container(
                            alignment = Alignment.Center,
                            padding = EdgeInsets(right = TrailingRightPadding),
                            children = trailing
                        )
                    }
                }
            }
        }
    }
}

private object TwoLine {
    // List item related constants.
    private val MinHeight = 64.dp
    private val MinHeightWithIcon = 72.dp
    // Icon related constants.
    private val IconMinPaddedWidth = 40.dp
    private val IconLeftPadding = 16.dp
    private val IconVerticalPadding = 16.dp
    // Content related constants.
    private val ContentLeftPadding = 16.dp
    private val ContentRightPadding = 16.dp
    private val OverlineBaselineOffset = 24.dp
    private val OverlineToPrimaryBaselineOffset = 20.dp
    private val PrimaryBaselineOffsetNoIcon = 28.dp
    private val PrimaryBaselineOffsetWithIcon = 32.dp
    private val PrimaryToSecondaryBaselineOffsetNoIcon = 20.dp
    private val PrimaryToSecondaryBaselineOffsetWithIcon = 20.dp
    // Trailing related constants.
    private val TrailingRightPadding = 16.dp

    @Composable
    fun ListItem(
        icon: @Composable() (() -> Unit)?,
        text: @Composable() (() -> Unit),
        secondaryText: @Composable() (() -> Unit)?,
        overlineText: @Composable() (() -> Unit)?,
        trailing: @Composable() (() -> Unit)?
    ) {
        val minHeight = if (icon == null) MinHeight else MinHeightWithIcon
        ConstrainedBox(constraints = DpConstraints(minHeight = minHeight)) {
            FlexRow(crossAxisAlignment = CrossAxisAlignment.Start) {
                inflexible {
                    if (icon != null) {
                        Container(
                            alignment = Alignment.TopLeft,
                            constraints = DpConstraints(
                                // TODO(popam): remove minHeight with cross axis alignment per child
                                minHeight = minHeight,
                                minWidth = IconLeftPadding + IconMinPaddedWidth
                            ),
                            padding = EdgeInsets(
                                left = IconLeftPadding,
                                top = IconVerticalPadding,
                                bottom = IconVerticalPadding
                            ),
                            children = icon
                        )
                    }
                }
                expanded(flex = 1f) {
                    Padding(left = ContentLeftPadding, right = ContentRightPadding) {
                        if (overlineText != null) {
                            BaselinesOffsetColumn(
                                listOf(OverlineBaselineOffset, OverlineToPrimaryBaselineOffset)
                            ) {
                                overlineText()
                                text()
                            }
                        } else {
                            BaselinesOffsetColumn(
                                listOf(
                                    if (icon != null) {
                                        PrimaryBaselineOffsetWithIcon
                                    } else {
                                        PrimaryBaselineOffsetNoIcon
                                    },
                                    if (icon != null) {
                                        PrimaryToSecondaryBaselineOffsetWithIcon
                                    } else {
                                        PrimaryToSecondaryBaselineOffsetNoIcon
                                    }
                                )
                            ) {
                                text()
                                secondaryText!!()
                            }
                        }
                    }
                }
                inflexible {
                    if (trailing != null) {
                        Padding(right = TrailingRightPadding) {
                            // TODO(popam): find way to center and wrap content without minHeight
                            ConstrainedBox(constraints = DpConstraints(minHeight = minHeight)) {
                                OffsetToBaselineOrCenter(
                                    if (icon != null) {
                                        PrimaryBaselineOffsetWithIcon
                                    } else {
                                        PrimaryBaselineOffsetNoIcon
                                    },
                                    trailing
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private object ThreeLine {
    // List item related constants.
    private val MinHeight = 88.dp
    // Icon related constants.
    private val IconMinPaddedWidth = 40.dp
    private val IconLeftPadding = 16.dp
    private val IconThreeLineVerticalPadding = 16.dp
    // Content related constants.
    private val ContentLeftPadding = 16.dp
    private val ContentRightPadding = 16.dp
    private val ThreeLineBaselineFirstOffset = 28.dp
    private val ThreeLineBaselineSecondOffset = 20.dp
    private val ThreeLineBaselineThirdOffset = 20.dp
    private val ThreeLineTrailingTopPadding = 16.dp
    // Trailing related constants.
    private val TrailingRightPadding = 16.dp

    @Composable
    fun ListItem(
        icon: @Composable() (() -> Unit)?,
        text: @Composable() (() -> Unit),
        secondaryText: @Composable() (() -> Unit),
        overlineText: @Composable() (() -> Unit)?,
        trailing: @Composable() (() -> Unit)?
    ) {
        ConstrainedBox(constraints = DpConstraints(minHeight = MinHeight)) {
            FlexRow(crossAxisAlignment = CrossAxisAlignment.Start) {
                inflexible {
                    if (icon != null) {
                        Container(
                            alignment = Alignment.CenterLeft,
                            constraints = DpConstraints(
                                minWidth = IconLeftPadding + IconMinPaddedWidth
                            ),
                            padding = EdgeInsets(
                                left = IconLeftPadding,
                                top = IconThreeLineVerticalPadding,
                                bottom = IconThreeLineVerticalPadding
                            ),
                            children = icon
                        )
                    }
                }
                expanded(flex = 1f) {
                    Padding(left = ContentLeftPadding, right = ContentRightPadding) {
                        BaselinesOffsetColumn(
                            listOf(
                                ThreeLineBaselineFirstOffset,
                                ThreeLineBaselineSecondOffset,
                                ThreeLineBaselineThirdOffset
                            )
                        ) {
                            if (overlineText != null) overlineText()
                            text()
                            secondaryText()
                        }
                    }
                }
                inflexible {
                    if (trailing != null) {
                        Padding(top = ThreeLineTrailingTopPadding, right = TrailingRightPadding) {
                            OffsetToBaselineOrCenter(
                                ThreeLineBaselineFirstOffset - ThreeLineTrailingTopPadding,
                                trailing
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Layout that expects [Text] children, and positions them with specific offsets between the
 * top of the layout and the first text, as well as the last baseline and first baseline
 * for subsequent pairs of texts.
 */
// TODO(popam): consider making this a layout composable in ui-layout.
@Composable
private fun BaselinesOffsetColumn(offsets: List<Dp>, children: @Composable() () -> Unit) {
    Layout(children) { measurables, constraints ->
        val childConstraints = constraints.copy(minHeight = 0.ipx, maxHeight = IntPx.Infinity)
        val placeables = measurables.map { it.measure(childConstraints) }

        val containerWidth = placeables.fold(0.ipx) { maxWidth, placeable ->
            max(maxWidth, placeable.width)
        }
        val y = Array(placeables.size) { 0.ipx }
        var containerHeight = 0.ipx
        placeables.forEachIndexed { index, placeable ->
            val toPreviousBaseline = if (index > 0) {
                placeables[index - 1].height - placeables[index - 1][LastBaseline]!!
            } else 0.ipx
            val topPadding = max(
                0.ipx,
                offsets[index].toIntPx() - placeable[FirstBaseline]!! - toPreviousBaseline
            )
            y[index] = topPadding + containerHeight
            containerHeight += topPadding + placeable.height
        }

        layout(containerWidth, containerHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable.place(0.ipx, y[index])
            }
        }
    }
}
/**
 * Layout that takes a child and adds the necessary padding such that the first baseline of the
 * child is at a specific offset from the top of the container. If the child does not have
 * a first baseline, the layout will match the minHeight constraint and will center the
 * child.
 */
// TODO(popam): support fallback alignment in AlignmentLineOffset, and use that here.
@Composable
private fun OffsetToBaselineOrCenter(offset: Dp, children: @Composable() () -> Unit) {
    Layout(children) { measurables, constraints ->
        val placeable = measurables[0].measure(constraints.copy(minHeight = 0.ipx))
        val baseline = placeable[FirstBaseline]
        val y: IntPx
        val containerHeight: IntPx
        if (baseline != null) {
            y = offset.toIntPx() - baseline
            containerHeight = max(constraints.minHeight, y + placeable.height)
        } else {
            containerHeight = max(constraints.minHeight, placeable.height)
            y = Alignment.Center
                .align(IntPxSize(0.ipx, containerHeight - placeable.height)).y
        }
        layout(placeable.width, containerHeight) {
            placeable.place(0.ipx, y)
        }
    }
}

private data class ListItemTextStyle(
    val style: MaterialTypography.() -> TextStyle,
    val color: MaterialColors.() -> Color,
    val opacity: Float
)

// TODO(popam): b/137311217
@Suppress("USELESS_CAST")
private fun applyTextStyle(
    textStyle: ListItemTextStyle,
    children: @Composable() (() -> Unit)?
): @Composable() (() -> Unit)? {
    if (children == null) return null as @Composable() (() -> Unit)?
    return {
        val textColor = (+themeColor(textStyle.color)).copy(alpha = textStyle.opacity)
        val appliedTextStyle = (+themeTextStyle(textStyle.style)).copy(color = textColor)
        CurrentTextStyleProvider(appliedTextStyle, children)
    }
}

// Material spec values.
private const val PrimaryTextOpacity = 0.87f
private const val SecondaryTextOpacity = 0.6f
private const val OverlineTextOpacity = 0.87f
private const val MetaTextOpacity = 0.87f
private const val RippleOpacity = 0.16f
private val PrimaryTextStyle = ListItemTextStyle({ subtitle1 }, { onSurface }, PrimaryTextOpacity)
private val SecondaryTextStyle = ListItemTextStyle({ body2 }, { onSurface }, SecondaryTextOpacity)
private val OverlineTextStyle = ListItemTextStyle({ overline }, { onSurface }, OverlineTextOpacity)
private val MetaTextStyle = ListItemTextStyle({ caption }, { onSurface }, MetaTextOpacity)
