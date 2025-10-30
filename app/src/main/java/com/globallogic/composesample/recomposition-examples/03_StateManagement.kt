package com.globallogic.composesample.recompositionexamples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * STATE MANAGEMENT EXAMPLES
 *
 * Shows different ways to manage state and how they affect recomposition.
 * Different state types have different recomposition behaviors.
 */

// Example 1: Basic mutableStateOf
@Composable
fun BasicStateExample() {
    var text by remember { mutableStateOf("") }

    Column {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter text") }
        )
        Text("You typed: $text")  // Recomposes when text changes
    }
}

// Example 2: State Lists
@Composable
fun StateListExample() {
    // ✅ GOOD: Observable state list
    var items by remember { mutableStateOf(listOf<String>()) }

    // ❌ BAD: Mutable list (unstable)
    val mutableItems = remember { mutableListOf<String>() }

    Column {
        Button(onClick = {
            items = items + "Item ${items.size + 1}"  // ✅ Triggers single recomposition
        }) {
            Text("Add to State List")
        }

        Button(onClick = {
            mutableItems.add("Item ${mutableItems.size + 1}")  // ❌ Triggers recomposition on every change
        }) {
            Text("Add to Mutable List")
        }

        Text("State list size: ${items.size}")
        Text("Mutable list size: ${mutableItems.size}")
    }
}

// Example 3: State Maps
@Composable
fun StateMapExample() {
    var userPreferences by remember {
        mutableStateOf(mapOf("theme" to "light", "notifications" to "on"))
    }

    Column {
        Text("Theme: ${userPreferences["theme"]}")
        Text("Notifications: ${userPreferences["notifications"]}")

        Button(onClick = {
            userPreferences = userPreferences + ("theme" to "dark")
        }) {
            Text("Toggle Theme")
        }
    }
}

// Example 4: Complex State Object
data class UserState(
    val name: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@Composable
fun ComplexStateExample() {
    var userState by remember {
        mutableStateOf(UserState())
    }

    Column {
        TextField(
            value = userState.name,
            onValueChange = { userState = userState.copy(name = it) },
            label = { Text("Name") }
        )

        TextField(
            value = userState.email,
            onValueChange = { userState = userState.copy(email = it) },
            label = { Text("Email") }
        )

        if (userState.isLoading) {
            Text("Loading...")
        }

        userState.error?.let { error ->
            Text("Error: $error")
        }

        Button(onClick = {
            userState = userState.copy(isLoading = true, error = null)
            // Simulate loading
            userState = userState.copy(isLoading = false, error = "Failed to save")
        }) {
            Text("Save User")
        }
    }
}

// Example 5: StateFlow (ViewModel pattern)
class UserViewModel {
    private val _users = MutableStateFlow<List<String>>(emptyList())
    val users: StateFlow<List<String>> = _users.asStateFlow()

    fun addUser(name: String) {
        _users.value = _users.value + name
    }
}

@Composable
fun ViewModelStateExample(viewModel: UserViewModel) {
    // ✅ GOOD: Collect StateFlow as Compose State
    val users by viewModel.users.collectAsState()

    Column {
        Text("Users: ${users.joinToString()}")

        Button(onClick = {
            viewModel.addUser("User ${users.size + 1}")
        }) {
            Text("Add User")
        }
    }
}

// Example 6: State Hoisting
@Composable
fun StateHoistingExample() {
    // State hoisted to top level
    var counter by remember { mutableStateOf(0) }

    Column {
        Text("Count: $counter")

        // Pass state and events down
        CounterControls(
            count = counter,
            onIncrement = { counter++ },
            onDecrement = { counter-- }
        )
    }
}

@Composable
private fun CounterControls(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row {
        Button(onClick = onDecrement) {
            Text("-")
        }
        Text("$count")
        Button(onClick = onIncrement) {
            Text("+")
        }
    }
}

// Example 7: Side Effects with State
@Composable
fun SideEffectsWithState() {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<String>>(emptyList()) }

    // ✅ GOOD: Use LaunchedEffect for side effects
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            searchResults = performSearch(searchQuery)
        } else {
            searchResults = emptyList()
        }
    }

    Column {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search") }
        )

        Text("Results: ${searchResults.size}")
        searchResults.forEach { result ->
            Text("• $result")
        }
    }
}

private fun performSearch(query: String): List<String> {
    // Simulate search
    return listOf("Result 1 for '$query'", "Result 2 for '$query'")
}
