package com.globallogic.composesample.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Performance and Best Practices Examples in Jetpack Compose.
 *
 * This file demonstrates key performance optimizations and best practices:
 * 1. Expensive computations with remember
 * 2. Stable keys in LazyColumn
 * 3. Derived state optimization
 * 4. Modifier efficiency
 * 5. Composition avoidance
 */

/**
 * Example 1: Expensive computation optimization with remember.
 *
 * ❌ BEFORE: Expensive work on every recomposition
 * ✅ AFTER: Remember expensive computations
 */
@Composable
fun ExpensiveComputationExample() {
    // ❌ Bad: This runs on every recomposition
    // val expensiveData = computeExpensiveValue()

    // ✅ Good: Remember expensive computations
    val expensiveData = remember {
        println("Computing expensive value...") // Only prints once
        computeExpensiveValue()
    }

    Text("Result: $expensiveData")
}

/**
 * Example 2: LazyColumn with stable keys for better performance.
 */
@Composable
fun LazyColumnWithKeysExample(items: List<UserItem>) {
    LazyColumn {
        // ✅ Good: Stable keys improve performance and animations
        items(
            items = items,
            key = { item -> item.id } // Stable identifier
        ) { item ->
            UserItemCard(item)
        }

        // ❌ Bad: No keys, relies on position (can cause issues with insertions/deletions)
        // items(items) { item -> UserItemCard(item) }
    }
}

/**
 * Example 3: Derived state for computed properties.
 */
@Composable
fun DerivedStateExample(user: User) {
    // ✅ Good: Derived state for computed properties
    val displayName = remember(user.firstName, user.lastName) {
        "${user.firstName} ${user.lastName}".trim()
    }

    // ✅ Good: Complex derived state with multiple dependencies
    val isValidUser = remember(user.name, user.email, user.age) {
        user.name.isNotBlank() &&
        user.email.contains("@") &&
        user.age >= 18
    }

    Text("User: $displayName (Valid: $isValidUser)")
}

/**
 * Example 4: Efficient modifier usage.
 */
@Composable
fun ModifierEfficiencyExample() {
    Card(
        modifier = Modifier
            // ✅ Good: Chain modifiers efficiently
            .fillMaxWidth()
            .padding(16.dp)
            // ❌ Bad: Multiple padding calls (creates multiple modifiers)
            // .padding(8.dp).padding(8.dp)
    ) {
        Text("Efficient modifiers")
    }
}

/**
 * Example 5: Composition avoidance with conditional rendering.
 */
@Composable
fun ConditionalCompositionExample(
    showDetails: Boolean,
    user: User
) {
    Column {
        Text("User: ${user.name}")

        // ✅ Good: Conditional composition
        if (showDetails) {
            UserDetailsCard(user)
        }

        // ❌ Bad: Always compose, just hide
        // UserDetails(user = user, visible = showDetails)
    }
}

/**
 * Example 6: Remember with keys for dynamic data.
 */
@Composable
fun RememberWithKeysExample(userId: String) {
    // ✅ Good: Use keys when computation depends on changing parameters
    val userData = remember(userId) {
        fetchUserData(userId)
    }

    // ✅ Good: Multiple keys for complex dependencies
    val currentTime = System.currentTimeMillis()
    val userStats = remember(userId, currentTime) {
        computeUserStats(userId, currentTime)
    }

    Text("User data: $userData")
}

/**
 * Example 7: Side-effect management.
 */
@Composable
fun SideEffectExample() {
    // ✅ Good: Use LaunchedEffect for one-time side effects
    LaunchedEffect(Unit) {
        println("Component entered composition")
    }

    // ✅ Good: Use DisposableEffect for cleanup
    DisposableEffect(Unit) {
        val resource = acquireResource()
        onDispose {
            resource.release()
        }
    }
}

/**
 * Example 8: State hoisting for reusability.
 */
@Composable
fun StateHoistingExample() {
    // State is hoisted to parent
    var counter by remember { mutableStateOf(0) }

    Column {
        Text("Count: $counter")

        // Pass state and events to child
        CounterButtons(
            count = counter,
            onIncrement = { counter++ },
            onDecrement = { counter-- }
        )
    }
}

@Composable
private fun CounterButtons(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row {
        Button(onClick = onDecrement) {
            Text("-")
        }
        Spacer(Modifier.width(8.dp))
        Text("$count")
        Spacer(Modifier.width(8.dp))
        Button(onClick = onIncrement) {
            Text("+")
        }
    }
}

/**
 * Example 9: Preview composables for development.
 */
@Preview(name = "User Card", showBackground = true)
@Composable
private fun UserCardPreview() {
    UserItemCard(
        UserItem(
            id = "1",
            name = "John Doe",
            email = "john@example.com"
        )
    )
}

/**
 * Example 10: Performance-optimized list items.
 */
@Composable
private fun UserItemCard(user: UserItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * User details card for conditional composition example.
 */
@Composable
private fun UserDetailsCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Email: ${user.email}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Age: ${user.age}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Sample data classes for examples
data class User(
    val name: String,
    val email: String,
    val age: Int,
    val firstName: String = name.split(" ").firstOrNull() ?: "",
    val lastName: String = name.split(" ").lastOrNull() ?: ""
)

data class UserItem(
    val id: String,
    val name: String,
    val email: String
)

// Mock functions for examples
private fun computeExpensiveValue(): String {
    Thread.sleep(100) // Simulate expensive work
    return "Expensive Result"
}

private fun fetchUserData(userId: String): String {
    return "User data for $userId"
}

private fun computeUserStats(userId: String, time: Long): String {
    return "Stats for $userId at $time"
}

private fun acquireResource(): Any = Object()

private fun Any.release() {
    println("Resource released")
}
