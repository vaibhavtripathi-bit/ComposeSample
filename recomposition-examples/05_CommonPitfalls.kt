package com.globallogic.composesample.recomposition-examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * COMMON PITFALLS AND SOLUTIONS
 *
 * Shows common mistakes that cause excessive recomposition and how to fix them.
 * Learn from these examples to avoid performance issues in your apps.
 */

// PITFALL 1: Unstable Collections
@Composable
fun UnstableCollectionPitfall() {
    // ❌ BAD: Mutable collections are unstable
    val users = remember { mutableListOf<User>() }

    LaunchedEffect(Unit) {
        users.addAll(generateUsers())  // This triggers recomposition!
    }

    Column {
        Button(onClick = {
            users.add(User("new-${System.currentTimeMillis()}", "New User"))
        }) {
            Text("Add User (BAD - causes recomposition)")
        }

        Text("Users: ${users.size}")

        // This entire LazyColumn recomposes when users list changes
        LazyColumn {
            items(users) { user ->
                Text("• ${user.name}")
            }
        }
    }
}

// ✅ SOLUTION: Use immutable state
@Composable
fun StableCollectionSolution() {
    // ✅ GOOD: Use immutable state
    var users by remember { mutableStateOf<List<User>>(emptyList()) }

    LaunchedEffect(Unit) {
        users = generateUsers()  // Single assignment, single recomposition
    }

    Column {
        Button(onClick = {
            users = users + User("new-${System.currentTimeMillis()}", "New User")
        }) {
            Text("Add User (GOOD - minimal recomposition)")
        }

        Text("Users: ${users.size}")

        LazyColumn {
            items(users) { user ->
                Text("• ${user.name}")
            }
        }
    }
}

// PITFALL 2: Expensive Computations in Composables
@Composable
fun ExpensiveComputationPitfall() {
    var data by remember { mutableStateOf(listOf(1, 2, 3)) }

    // ❌ BAD: Expensive work on every recomposition
    val processedData = data.map { it * 2 }  // Runs every time!
    val sortedData = data.sorted()            // Runs every time!
    val filteredData = data.filter { it > 1 } // Runs every time!

    Column {
        Text("Original: ${data.joinToString()}")
        Text("Processed: ${processedData.joinToString()}")
        Text("Sorted: ${sortedData.joinToString()}")
        Text("Filtered: ${filteredData.joinToString()}")

        Button(onClick = { data = data + (data.size + 1) }) {
            Text("Add Data")
        }
    }
}

// ✅ SOLUTION: Remember expensive computations
@Composable
fun ExpensiveComputationSolution() {
    var data by remember { mutableStateOf(listOf(1, 2, 3)) }

    // ✅ GOOD: Remember expensive computations
    val processedData = remember(data) { data.map { it * 2 } }
    val sortedData = remember(data) { data.sorted() }
    val filteredData = remember(data) { data.filter { it > 1 } }

    Column {
        Text("Original: ${data.joinToString()}")
        Text("Processed: ${processedData.joinToString()}")
        Text("Sorted: ${sortedData.joinToString()}")
        Text("Filtered: ${filteredData.joinToString()}")

        Button(onClick = { data = data + (data.size + 1) }) {
            Text("Add Data")
        }
    }
}

// PITFALL 3: Incorrect State Updates
@Composable
fun IncorrectStateUpdatePitfall() {
    val state = remember { mutableStateOf(CounterState()) }

    Column {
        Text("Count: ${state.value.count}")

        Button(onClick = {
            // ❌ BAD: Direct mutation of state
            // state.value.count++

            // ❌ BAD: Multiple state updates (causes multiple recompositions)
            state.value = state.value.copy(count = state.value.count + 1)
            // Don't do more updates here - each one triggers recomposition

            // ✅ GOOD: Single state update
            state.value = state.value.copy(count = state.value.count + 1)
        }) {
            Text("Increment (BAD - multiple updates)")
        }
    }
}

// ✅ SOLUTION: Proper state updates
@Composable
fun CorrectStateUpdateSolution() {
    var count by remember { mutableStateOf(0) }

    Column {
        Text("Count: $count")

        Button(onClick = {
            count++  // ✅ GOOD: Single update
        }) {
            Text("Increment (GOOD)")
        }
    }
}

// PITFALL 4: Side Effects in Composables
@Composable
fun SideEffectPitfall() {
    var data by remember { mutableStateOf(emptyList<String>()) }

    // ❌ BAD: Side effect directly in composable
    val loadedData = loadData()  // This runs on every recomposition!

    Column {
        Text("Data size: ${data.size}")

        Button(onClick = {
            data = loadedData  // This also triggers recomposition
        }) {
            Text("Load Data (BAD)")
        }
    }
}

// ✅ SOLUTION: Use effects for side effects
@Composable
fun SideEffectSolution() {
    var data by remember { mutableStateOf(emptyList<String>()) }

    // ✅ GOOD: Use LaunchedEffect for side effects
    LaunchedEffect(Unit) {
        data = loadData()  // Only runs when key changes
    }

    Column {
        Text("Data size: ${data.size}")

        Button(onClick = {
            // Trigger reload by changing the effect key
        }) {
            Text("Load Data (GOOD)")
        }
    }
}

// PITFALL 5: Over-relying on Recomposition
@Composable
fun OverRecompositionPitfall() {
    var items by remember { mutableStateOf((1..1000).toList()) }

    // ❌ BAD: This entire column recomposes when any item changes
    Column {
        items.forEach { item ->
            var selected by remember { mutableStateOf(false) }

            Button(onClick = { selected = !selected }) {
                Text("$item ${if (selected) "(Selected)" else ""}")
            }
        }
    }
}

// ✅ SOLUTION: Use LazyColumn and proper state management
@Composable
fun OverRecompositionSolution() {
    var items by remember { mutableStateOf((1..1000).toList()) }
    var selectedItems by remember { mutableStateOf(setOf<Int>()) }

    LazyColumn {
        items(items) { item ->
            val isSelected = selectedItems.contains(item)

            Button(onClick = {
                selectedItems = if (isSelected) {
                    selectedItems - item
                } else {
                    selectedItems + item
                }
            }) {
                Text("$item ${if (isSelected) "(Selected)" else ""}")
            }
        }
    }
}

// PITFALL 6: ViewModel State Management
class BadViewModel : ViewModel() {
    // ❌ BAD: Exposing mutable state directly
    val users = mutableStateOf<List<User>>(emptyList())

    fun addUser(user: User) {
        users.value = users.value + user  // This is okay, but not ideal
    }
}

class GoodViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    fun addUser(user: User) {
        _users.value = _users.value + user  // ✅ Better encapsulation
    }
}

// PITFALL 7: Key Management
@Composable
fun KeyManagementPitfall(items: List<Item>) {
    // ❌ BAD: No keys - relies on position
    LazyColumn {
        items(items) { item ->
            ItemCard(item)
        }
    }

    // ❌ BAD: Unstable keys
    LazyColumn {
        items(
            items = items,
            key = { item -> item.name }  // Name might not be unique!
        ) { item ->
            ItemCard(item)
        }
    }
}

// ✅ SOLUTION: Proper key management
@Composable
fun KeyManagementSolution(items: List<Item>) {
    // ✅ GOOD: Stable, unique keys
    LazyColumn {
        items(
            items = items,
            key = { item -> item.id }  // Stable identifier
        ) { item ->
            ItemCard(item)
        }
    }
}

@Composable
private fun ItemCard(item: Item) {
    Card {
        Text("${item.name} (${item.id})")
    }
}

// PITFALL 8: Lifecycle Issues
@Composable
fun LifecyclePitfall() {
    var data by remember { mutableStateOf(emptyList<String>()) }

    // ❌ BAD: Effect that doesn't clean up
    LaunchedEffect(Unit) {
        val job = launchDataLoading()
        // No cleanup when composable leaves composition
    }

    Column {
        Text("Data: ${data.size}")
    }
}

// ✅ SOLUTION: Proper lifecycle management
@Composable
fun LifecycleSolution() {
    var data by remember { mutableStateOf(emptyList<String>()) }

    // ✅ GOOD: Proper cleanup
    DisposableEffect(Unit) {
        val job = launchDataLoading()

        onDispose {
            job.cancel()  // Cleanup when composable leaves composition
        }
    }

    Column {
        Text("Data: ${data.size}")
    }
}

// Data classes
data class CounterState(val count: Int = 0)
data class User(val id: String, val name: String)
data class Item(val id: String, val name: String)

// Mock functions
private fun generateUsers(): List<User> {
    return List(10) { index ->
        User("user-$index", "User $index")
    }
}

private fun loadData(): List<String> {
    return List(5) { "Item $it" }
}

private fun launchDataLoading(): Any {
    // Simulate some async work
    return Object()
}
