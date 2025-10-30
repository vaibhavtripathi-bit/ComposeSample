package com.globallogic.composesample.recompositionexamples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column

/**
 * BASIC RECOMPOSITION CONCEPTS
 *
 * This file demonstrates the fundamental concepts of recomposition in Jetpack Compose.
 * Keep it simple: State changes → UI updates automatically!
 */

// Example 1: Simple State Change
@Composable
fun SimpleCounter() {
    // This creates a state that triggers recomposition when changed
    var count by remember { mutableStateOf(0) }

    Button(onClick = { count++ }) {
        Text("Clicked $count times")
    }
    // When count changes, this entire composable re-executes
    // The Text shows the new value automatically
}

// Example 2: Multiple State Dependencies
@Composable
fun MultipleStates() {
    var name by remember { mutableStateOf("World") }
    var isVisible by remember { mutableStateOf(true) }

    if (isVisible) {
        Text("Hello $name!")  // Recomposes when name OR isVisible changes
    }

    Button(onClick = { name = "Compose" }) {
        Text("Change Name")
    }

    Button(onClick = { isVisible = !isVisible }) {
        Text("Toggle Visibility")
    }
}

// Example 3: What Recomposes vs What Doesn't
@Composable
fun RecompositionScope() {
    var globalCount by remember { mutableStateOf(0) }
    var localCount by remember { mutableStateOf(0) }

    // This entire Column recomposes when globalCount changes
    Column {
        Text("Global: $globalCount")  // ✅ Recomposes
        Text("Local: $localCount")    // ✅ Also recomposes (same scope)

        Button(onClick = { globalCount++ }) {
            Text("Update Global")
        }

        // This separate composable only recomposes when localCount changes
        LocalCounter(localCount) { localCount = it }
    }
}

@Composable
private fun LocalCounter(count: Int, onCountChange: (Int) -> Unit) {
    // This only recomposes when count parameter changes
    Text("Local Counter: $count")
    Button(onClick = { onCountChange(count + 1) }) {
        Text("Increment Local")
    }
}

// Example 4: Conditional Composition
@Composable
fun ConditionalUI() {
    var showDetails by remember { mutableStateOf(false) }

    Column {
        Text("Main Content")

        if (showDetails) {
            // This only gets composed when showDetails is true
            Text("Extra Details Here!")
        }

        Button(onClick = { showDetails = !showDetails }) {
            Text(if (showDetails) "Hide Details" else "Show Details")
        }
    }
}
