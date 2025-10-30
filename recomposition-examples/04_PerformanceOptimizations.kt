package com.globallogic.composesample.recomposition-examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

/**
 * PERFORMANCE OPTIMIZATION EXAMPLES
 *
 * Shows techniques to minimize unnecessary recompositions and improve performance.
 */

// Example 1: Remember for Expensive Computations
@Composable
fun ExpensiveComputationExample() {
    var data by remember { mutableStateOf(listOf(1, 2, 3)) }

    // ❌ BAD: Expensive work on every recomposition
    // val processedData = processData(data)  // Runs every time!

    // ✅ GOOD: Remember expensive computations
    val processedData = remember(data) {
        println("Computing expensive value...")  // Only prints when data changes
        processData(data)
    }

    Column {
        Text("Data: ${data.joinToString()}")
        Text("Processed: ${processedData.joinToString()}")

        Button(onClick = {
            data = data + (data.size + 1)
        }) {
            Text("Add Data")
        }
    }
}

private fun processData(data: List<Int>): List<Int> {
    Thread.sleep(100) // Simulate expensive computation
    return data.map { it * 2 }
}

// Example 2: Derived State for Computed Properties
@Composable
fun DerivedStateExample(users: List<User>) {
    var searchQuery by remember { mutableStateOf("") }

    // ✅ GOOD: Derived state only recalculates when dependencies change
    val filteredUsers by remember(users, searchQuery) {
        derivedStateOf {
            users.filter { user ->
                user.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // ❌ BAD: Manual computation (recalculates every time)
    val badFilteredUsers = users.filter { user ->
        user.name.contains(searchQuery, ignoreCase = true)
    }

    Column {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search") }
        )

        Text("Derived state results: ${filteredUsers.size}")
        Text("Bad filter results: ${badFilteredUsers.size}")

        LazyColumn {
            items(filteredUsers) { user ->
                Text(user.name)
            }
        }
    }
}

// Example 3: Keys for Dynamic Lists
@Composable
fun KeysExample(users: List<User>) {
    var usersList by remember { mutableStateOf(users) }

    Column {
        // ✅ GOOD: Using stable keys
        LazyColumn {
            items(
                items = usersList,
                key = { user -> user.id }  // Stable identifier
            ) { user ->
                UserCard(user)
            }
        }

        // ❌ BAD: No keys (relies on position)
        LazyColumn {
            items(usersList) { user ->  // No key - position-based
                UserCard(user)
            }
        }

        Button(onClick = {
            // When we add a user in the middle, which approach is better?
            usersList = usersList.toMutableList().apply {
                add(1, User("new-${System.currentTimeMillis()}", "New User"))
            }
        }) {
            Text("Add User in Middle")
        }
    }
}

@Composable
private fun UserCard(user: User) {
    Card {
        Text("User: ${user.name} (ID: ${user.id})")
    }
}

// Example 4: Conditional Composition
@Composable
fun ConditionalCompositionExample(showDetails: Boolean, user: User) {
    Column {
        Text("User: ${user.name}")

        // ✅ GOOD: Conditional composition
        if (showDetails) {
            UserDetails(user)  // Only composed when showDetails is true
        }

        // ❌ BAD: Always compose, just hide
        // UserDetails(user = user, visible = showDetails)
    }
}

@Composable
private fun UserDetails(user: User) {
    Card {
        Column {
            Text("Email: ${user.email}")
            Text("Age: ${user.age}")
        }
    }
}

// Example 5: Remember with Multiple Keys
@Composable
fun RememberWithKeysExample() {
    var userId by remember { mutableStateOf("user1") }
    var refreshTrigger by remember { mutableStateOf(0) }

    // ✅ GOOD: Use keys when computation depends on changing parameters
    val userData = remember(userId, refreshTrigger) {
        fetchUserData(userId)
    }

    Column {
        Text("User Data: $userData")

        Button(onClick = {
            userId = if (userId == "user1") "user2" else "user1"
        }) {
            Text("Switch User")
        }

        Button(onClick = {
            refreshTrigger++  // Force refresh without changing userId
        }) {
            Text("Refresh")
        }
    }
}

private fun fetchUserData(userId: String): String {
    return "Data for $userId (fetched at ${System.currentTimeMillis()})"
}

// Example 6: Debouncing for Performance
@Composable
fun DebouncedSearchExample() {
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }

    // ✅ GOOD: Debounce search to avoid excessive API calls
    LaunchedEffect(searchQuery) {
        delay(300)  // Wait 300ms after user stops typing
        debouncedQuery = searchQuery
    }

    val searchResults = remember(debouncedQuery) {
        performSearch(debouncedQuery)
    }

    Column {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search (debounced)") }
        )

        Text("Debounced query: '$debouncedQuery'")
        Text("Results: ${searchResults.size}")
    }
}

private fun performSearch(query: String): List<String> {
    // Simulate search results
    return if (query.isBlank()) emptyList()
           else listOf("Result 1 for '$query'", "Result 2 for '$query'")
}

// Example 7: Small Composables
@Composable
fun SmallComposablesExample(user: User) {
    // ✅ GOOD: Break into small, focused composables
    UserProfile(user)

    // ❌ BAD: One massive composable
    // MassiveUserProfileComposable(user)
}

@Composable
private fun UserProfile(user: User) {
    Column {
        UserHeader(user)
        UserBody(user)
        UserFooter(user)
    }
}

@Composable
private fun UserHeader(user: User) {
    Text("User: ${user.name}", style = MaterialTheme.typography.headlineSmall)
}

@Composable
private fun UserBody(user: User) {
    Text("Email: ${user.email}")
    Text("Age: ${user.age}")
}

@Composable
private fun UserFooter(user: User) {
    Button(onClick = { /* actions */ }) {
        Text("Contact User")
    }
}

// Data classes for examples
data class User(
    val id: String,
    val name: String,
    val email: String,
    val age: Int = 25
)
