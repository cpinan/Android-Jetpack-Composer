package com.carlospinan.jetpackexample

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.ui.core.setContent
import androidx.ui.graphics.Color
import androidx.ui.layout.FlexColumn
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Row
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.LinearProgressIndicator
import androidx.ui.material.MaterialTheme

class Example05Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ProgressIndicatorDemo()
            }
        }
    }

}


@Model
private class ProgressState {
    var progress = +state { 0.0f }
    var cycle = +state { 0 }

    fun generateColor(): Color {
        return when (cycle.value) {
            1 -> Color.Red
            2 -> Color.Green
            3 -> Color.Blue
            // unused
            else -> Color.Black
        }
    }

    fun start() {
        handler.postDelayed(updateProgress, 400)
    }

    fun stop() {
        handler.removeCallbacks(updateProgress)
    }

    val handler = Handler()
    val updateProgress: Runnable = object : Runnable {
        override fun run() {
            if (progress.value == 1f) {
                cycle.value++
                if (cycle.value > 3) {
                    cycle.value = 1
                }
                progress.value = 0f
            } else {
                progress.value += 0.25f
            }
            handler.postDelayed(this, 400)
        }
    }
}

@Composable
private fun ProgressIndicatorDemo(state: ProgressState = ProgressState()) {

    +onActive { state.start() }
    +onDispose { state.stop() }

    FlexColumn {
        expanded(flex = 1f) {
            Row(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.SpaceEvenly
            ) {
                // Determinate indicators
                LinearProgressIndicator(progress = state.progress.value)
                CircularProgressIndicator(progress = state.progress.value)
            }
            Row(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.SpaceEvenly
            ) {
                // Fancy colours!
                LinearProgressIndicator(
                    progress = (state.progress.value),
                    color = state.generateColor()
                )
                CircularProgressIndicator(
                    progress = (state.progress.value),
                    color = state.generateColor()
                )
            }
            Row(
                mainAxisSize = LayoutSize.Expand,
                mainAxisAlignment = MainAxisAlignment.SpaceEvenly
            ) {
                // Indeterminate indicators
                LinearProgressIndicator()
                CircularProgressIndicator()
            }
        }
    }
}