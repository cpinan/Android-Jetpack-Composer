package com.carlospinan.jetpackexample.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Padding
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import com.carlospinan.jetpackexample.custom.Scaffold
import com.carlospinan.jetpackexample.sample.rally.*

enum class RallyScreenState {
    Overview, Accounts, Bills
}

@Composable
private fun RallyScreenState.body() = when (this) {
    RallyScreenState.Overview -> RallyBody()
    RallyScreenState.Accounts -> RallyAccountsCard()
    RallyScreenState.Bills -> RallyBillsCard()
}

@Composable
fun RallyBody() {
    VerticalScroller {
        Padding(16.dp) {
            Column(mainAxisSize = LayoutSize.Expand) {
                RallyAlertCard()

                HeightSpacer(height = 10.dp)

                RallyAccountsOverviewCard()

                HeightSpacer(height = 10.dp)

                RallyBillsOverviewCard()
            }
        }
    }
}

class RallyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RallyApp()
        }
    }

    @Composable
    fun RallyApp() {
        RallyTheme {
            val allScreens = RallyScreenState.values().toList()
            // https://www.tutorialspoint.com/kotlin/kotlin_delegation.htm
            var currentScreen by +state { RallyScreenState.Overview }
            Scaffold(
                appBar = {
                    TabRow(allScreens, selectedIndex = currentScreen.ordinal) { i, screen ->
                        Tab(text = screen.name, selected = currentScreen.ordinal == i) {
                            currentScreen = screen
                        }
                    }
                }
            ) {
                currentScreen.body()
            }
        }
    }

}