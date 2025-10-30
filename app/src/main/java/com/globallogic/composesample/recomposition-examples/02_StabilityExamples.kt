package com.globallogic.composesample.recompositionexamples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * STABILITY EXAMPLES
 *
 * Demonstrates how data stability affects recomposition behavior.
 * Stable data = better performance, fewer unnecessary recompositions.
 */

// Example 1: Stable vs Unstable Data Classes
// ✅ STABLE: Data classes are stable by default
data class StableUser(
    val id: String,
    val name: String,
    val email: String
)

// ❌ UNSTABLE: Classes without proper equals() are unstable
class UnstableUser(
    val id: String,
    val name: String,
    val email: String
) {
    // No equals() or hashCode() - unstable!
}

// ✅ STABLE: Explicitly marked as stable
@Stable
class ExplicitlyStableUser(
    val id: String,
    val name: String,
    val email: String
)

// ✅ IMMUTABLE: Strongest stability guarantee
@Immutable
data class ImmutableUser(
    val id: String,
    val name: String,
    val email: String
)

@Composable
fun StabilityDemo() {
    var stableUser by remember {
        mutableStateOf(StableUser("1", "John", "john@example.com"))
    }
    var unstableUser by remember {
        mutableStateOf(UnstableUser("1", "John", "john@example.com"))
    }

    Column {
        // This composable will only recompose when the actual data changes
        StableUserCard(stableUser)
        Button(onClick = {
            stableUser = stableUser.copy(name = "Jane")
        }) {
            Text("Update Stable User")
        }

        // This composable recomposes on EVERY state change (inefficient!)
        UnstableUserCard(unstableUser)
        Button(onClick = {
            unstableUser = UnstableUser("1", "Jane", "jane@example.com")
        }) {
            Text("Update Unstable User")
        }
    }
}

@Composable
private fun StableUserCard(user: StableUser) {
    Card {
        Text("Stable User: ${user.name} (${user.email})")
    }
}

@Composable
private fun UnstableUserCard(user: UnstableUser) {
    Card {
        Text("Unstable User: ${user.name} (${user.email})")
    }
}

// Example 2: Stable vs Unstable Collections
@Composable
fun CollectionStabilityDemo() {
    // ✅ STABLE: Immutable list
    var stableUsers by remember {
        mutableStateOf(listOf(StableUser("1", "Alice", "alice@example.com")))
    }

    // ❌ UNSTABLE: Mutable list
    val unstableUsers = remember { mutableListOf(StableUser("1", "Bob", "bob@example.com")) }

    Column {
        Button(onClick = {
            stableUsers = stableUsers + StableUser("2", "Charlie", "charlie@example.com")
        }) {
            Text("Add to Stable List (Recomposes once)")
        }

        Button(onClick = {
            unstableUsers.add(StableUser("2", "David", "david@example.com"))
            // This will trigger recomposition even though we just want to add an item
        }) {
            Text("Add to Unstable List (Recomposes always)")
        }

        Text("Stable users: ${stableUsers.size}")
        Text("Unstable users: ${unstableUsers.size}")
    }
}

// Example 3: Keys for Stable Identity
@Composable
fun KeysDemo(users: List<StableUser>) {
    Column {
        // ✅ GOOD: Using stable keys in LazyColumn
        LazyColumn {
            items(
                items = users,
                key = { user -> user.id }  // Stable key - only this item recomposes when changed
            ) { user ->
                Text("User: ${user.name} (ID: ${user.id})")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ❌ BAD: No keys, relies on position (causes issues when items are reordered)
        LazyColumn {
            items(users) { user ->  // No key - position-based, causes issues with insertions/deletions
                Text("User: ${user.name}")
            }
        }
    }
}

// Example 4: Computed Properties with Stability
@Composable
fun ComputedPropertiesDemo(user: StableUser) {
    // ✅ GOOD: Stable computed property
    val displayName = remember(user.name, user.email) {
        "${user.name} (${user.email})"
    }

    // ❌ BAD: Unstable computed property
    val unstableDisplay = remember {
        "${user.name} (${user.email})"  // Recomposes every time!
    }

    Column {
        Text("Stable: $displayName")
        Text("Unstable: $unstableDisplay")
    }
}
