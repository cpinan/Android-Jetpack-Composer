package com.carlospinan.jetpackexample.sample.rally

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.ColoredRect
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.surface.Card

/**
 * The Alerts card within the Rally Overview screen.
 */
@Composable
fun RallyAlertCard() {
    Card {
        Padding(12.dp) {
            Column {
                Row(
                    mainAxisSize = LayoutSize.Expand,
                    mainAxisAlignment = MainAxisAlignment.SpaceBetween
                ) {
                    Text(text = "Alerts", style = +themeTextStyle { subtitle2 })
                    Button(text = "See All", onClick = { }, style = TextButtonStyle())
                }

                Padding(
                    top = 12.dp,
                    bottom = 12.dp
                ) {
                    Divider(
                        color = +themeColor { background },
                        height = 2.dp
                    )
                }

                FlexRow {
                    expanded(flex = 1.0f) {
                        val text = "Heads up, you've used up 90% of your " +
                                "Shopping budget for this month."
                        Text(
                            style = +themeTextStyle { body1 },
                            text = text
                        )
                    }
                    inflexible {
                        // TODO: Icons still don't work
                        Button(text = "Sort", onClick = { }, style = TextButtonStyle())
                    }
                }
            }
        }
    }
}

/**
 * The Accounts card within the Rally Overview screen.
 */
@Composable
fun RallyAccountsOverviewCard() {
    Card {
        Column(mainAxisSize = LayoutSize.Expand) {
            Padding(12.dp) {
                Text(text = "Accounts", style = +themeTextStyle { body1 })
                Text(text = "$12,132.49", style = +themeTextStyle { h3 })
            }
            Divider(color = rallyGreen, height = 1.dp)
            Padding(12.dp) {
                Column {
                    RallyAccountRow(
                        name = "Checking",
                        number = "1234",
                        amount = "2,215.13",
                        color = Color(0xFF005D57)
                    )

                    RallyDivider()

                    RallyAccountRow(
                        name = "Home Savings",
                        number = "5678",
                        amount = "8,676.88",
                        color = Color(0xFF04B97F)
                    )

                    RallyDivider()

                    RallyAccountRow(
                        name = "Car Savings",
                        number = "9012",
                        amount = "987.48",
                        color = Color(0xFF37EFBA)
                    )

                    RallyDivider()

                    Button(text = "See All", style = TextButtonStyle())
                }
            }
        }
    }
}

/**
 * The Accounts composable used in a separate tab.
 */
@Composable
fun RallyAccountsCard() {
    VerticalScroller {
        Column {
            Padding(16.dp) {
                Stack {
                    aligned(Alignment.Center) {
                        val accountsProportion = listOf(0.595f, 0.045f, 0.095f, 0.195f, 0.045f)
                        val colors = listOf(
                            0xFF1EB980, 0xFF005D57, 0xFF04B97F, 0xFF37EFBA,
                            0xFFFAFFBF
                        ).map { Color(it) }

                        Container(height = 300.dp, expanded = true) {
                            DrawAnimatedCircle(accountsProportion, colors)
                        }

                        Column(crossAxisAlignment = CrossAxisAlignment.Center) {
                            Text(text = "Total", style = +themeTextStyle { body1 })
                            Text(text = "$12,132.49", style = +themeTextStyle { h3 })
                        }
                    }
                }
            }

            HeightSpacer(height = 10.dp)

            Card {
                Padding(12.dp) {
                    Column(mainAxisSize = LayoutSize.Expand) {
                        RallyAccountRow(
                            name = "Checking",
                            number = "1234",
                            amount = "2,215.13",
                            color = Color(0xFF005D57)
                        )
                        RallyDivider()
                        RallyAccountRow(
                            name = "Home Savings",
                            number = "5678",
                            amount = "8,676.88",
                            color = Color(0xFF04B97F)
                        )
                        RallyDivider()
                        RallyAccountRow(
                            name = "Car Savings",
                            number = "9012",
                            amount = "987.48",
                            color = Color(0xFF37EFBA)
                        )
                        RallyDivider()
                        RallyAccountRow(
                            name = "Vacation",
                            number = "3456",
                            amount = "253",
                            color = Color(0xFF005D57)
                        )
                    }
                }
            }
        }
    }
}

/**
 * A row within the Accounts card in the Rally Overview screen.
 */
@Composable
fun RallyAccountRow(name: String, number: String, amount: String, color: Color) {
    Padding(
        top = 12.dp,
        bottom = 12.dp
    ) {
        FlexRow {
            inflexible {
                AccountIndicator(color = color)
                WidthSpacer(width = 8.dp)
                Column(
                    mainAxisSize = LayoutSize.Expand,
                    crossAxisAlignment = CrossAxisAlignment.Start
                ) {
                    Text(text = name, style = +themeTextStyle { body1 })
                    Text(text = "•••••$number", style = +themeTextStyle { subtitle1 })
                }
            }
            expanded(1.0f) {
                FixedSpacer(width = 0.dp, height = 0.dp)
            }
            inflexible {
                Text(text = "$ $amount", style = +themeTextStyle { h6 })
            }
        }
    }
}

/**
 * A vertical colored line that is used in a [RallyAccountRow] to differentiate accounts.
 */
@Composable
fun AccountIndicator(color: Color) {
    ColoredRect(color = color, width = 4.dp, height = 36.dp)
}

/**
 * The Bills card within the Rally Overview screen.
 */
@Composable
fun RallyBillsOverviewCard() {
    Card {
        Column(mainAxisSize = LayoutSize.Expand) {
            Padding(12.dp) {
                Column {
                    Text(text = "Bills", style = +themeTextStyle { subtitle2 })
                    Text(text = "$1,810.00", style = +themeTextStyle { h3 })
                }
            }

            Divider(color = rallyGreen, height = 1.dp)

            Padding(12.dp) {
                Column {
                    RallyAccountRow(
                        name = "RedPay Credit",
                        number = "Jan 29",
                        amount = "-45.36",
                        color = Color(0xFF005D57)
                    )

                    RallyDivider()

                    RallyAccountRow(
                        name = "Rent",
                        number = "Feb 9",
                        amount = "-1,200.00",
                        color = Color(0xFF04B97F)
                    )

                    RallyDivider()

                    RallyAccountRow(
                        name = "TabFine Credit",
                        number = "Feb 22",
                        amount = "-87.33",
                        color = Color(0xFF37EFBA)
                    )

                    RallyDivider()

                    Button(text = "See All", style = TextButtonStyle())
                }

            }
        }
    }
}

/**
 * The Accounts composable used in a separate tab.
 */
@Composable
fun RallyBillsCard() {
    VerticalScroller {
        Column {
            Padding(16.dp) {
                Stack {
                    aligned(Alignment.Center) {
                        val accountsProportion = listOf(0.65f, 0.25f, 0.03f, 0.05f)
                        val colors = listOf(0xFF1EB980, 0xFF005D57, 0xFF04B97F, 0xFF37EFBA).map {
                            Color(it)
                        }
                        Container(height = 300.dp, expanded = true) {
                            DrawAnimatedCircle(accountsProportion, colors)
                        }
                        Column(crossAxisAlignment = CrossAxisAlignment.Center) {
                            Text(text = "Due", style = +themeTextStyle { body1 })
                            Text(text = "$1,810.00", style = +themeTextStyle { h3 })
                        }
                    }
                }
            }

            HeightSpacer(height = 10.dp)

            Card {
                Padding(16.dp) {
                    Column(mainAxisSize = LayoutSize.Expand) {
                        RallyAccountRow(
                            name = "RedPay Credit",
                            number = "Jan 29",
                            amount = "-45.36",
                            color = Color(0xFF005D57)
                        )
                        RallyDivider()
                        RallyAccountRow(
                            name = "Rent",
                            number = "Feb 9",
                            amount = "-1,200.00",
                            color = Color(0xFF04B97F)
                        )
                        RallyDivider()
                        RallyAccountRow(
                            name = "TabFine Credit",
                            number = "Feb 22",
                            amount = "-87.33",
                            color = Color(0xFF37EFBA)
                        )
                        RallyDivider()
                        RallyAccountRow(
                            name = "ABC Loans",
                            number = "Feb 29",
                            amount = "-400.00",
                            color = Color(0xFF005D57)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RallyDivider() = Divider(color = +themeColor { background }, height = 2.dp)