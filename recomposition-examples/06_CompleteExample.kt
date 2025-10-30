package com.globallogic.composesample.recomposition-examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Immutable
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * COMPLETE EXAMPLE
 *
 * A practical application that combines all recomposition concepts:
 * - ViewModel state management
 * - Stable data types
 * - Performance optimizations
 * - Proper lifecycle management
 */

// Data Models (Stable and Immutable)
@Immutable
data class TodoItem(
    val id: String,
    val text: String,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// ViewModel (Proper state management)
class TodoViewModel : ViewModel() {
    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    val todos: StateFlow<List<TodoItem>> = _todos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun addTodo(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true

            // Simulate network delay
            kotlinx.coroutines.delay(500)

            val newTodo = TodoItem(
                id = "todo-${System.currentTimeMillis()}",
                text = text.trim()
            )

            _todos.value = _todos.value + newTodo
            _isLoading.value = false
        }
    }

    fun toggleTodo(todoId: String) {
        _todos.value = _todos.value.map { todo ->
            if (todo.id == todoId) {
                todo.copy(completed = !todo.completed)
            } else {
                todo
            }
        }
    }

    fun deleteTodo(todoId: String) {
        _todos.value = _todos.value.filter { it.id != todoId }
    }
}

// Main Screen (Combines everything)
@Composable
fun TodoApp(viewModel: TodoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val todos by viewModel.todos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var newTodoText by remember { mutableStateOf("") }

    // Derived state for performance
    val completedCount by remember(todos) {
        androidx.compose.runtime.derivedStateOf {
            todos.count { it.completed }
        }
    }

    val activeCount by remember(todos) {
        androidx.compose.runtime.derivedStateOf {
            todos.count { !it.completed }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Header with stats (derived state)
        Text(
            text = "Todo App",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Active: $activeCount | Completed: $completedCount")

        Spacer(modifier = Modifier.height(16.dp))

        // Add new todo (controlled input)
        Row {
            TextField(
                value = newTodoText,
                onValueChange = { newTodoText = it },
                label = { Text("New todo") },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    viewModel.addTodo(newTodoText)
                    newTodoText = ""  // Clear after adding
                },
                enabled = newTodoText.isNotBlank() && !isLoading
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading indicator
        if (isLoading) {
            Text("Adding todo...")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Todo list (optimized with keys)
        LazyColumn {
            items(
                items = todos,
                key = { todo -> todo.id }  // Stable key
            ) { todo ->
                TodoItemCard(
                    todo = todo,
                    onToggle = { viewModel.toggleTodo(todo.id) },
                    onDelete = { viewModel.deleteTodo(todo.id) }
                )
            }
        }
    }
}

// Individual todo item (small, focused composable)
@Composable
private fun TodoItemCard(
    todo: TodoItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Completion checkbox
            Button(
                onClick = onToggle,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(if (todo.completed) "✓" else "○")
            }

            // Todo text (with completion styling)
            Text(
                text = todo.text,
                modifier = Modifier.weight(1f),
                style = if (todo.completed) {
                    MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    MaterialTheme.typography.bodyMedium
                }
            )

            // Delete button
            Button(
                onClick = onDelete,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("×")
            }
        }
    }
}

/**
 * WHY THIS EXAMPLE IS OPTIMAL:
 *
 * 1. ✅ STABLE DATA: Using @Immutable TodoItem
 * 2. ✅ STATE MANAGEMENT: Proper ViewModel with StateFlow
 * 3. ✅ PERFORMANCE: Derived state for computed values
 * 4. ✅ KEYS: Stable keys in LazyColumn
 * 5. ✅ SMALL COMPOSABLES: Each component has single responsibility
 * 6. ✅ CONTROLLED INPUT: Text field state properly managed
 * 7. ✅ LOADING STATES: Proper loading indication
 * 8. ✅ IMMUTABLE UPDATES: State updates create new lists
 * 9. ✅ SIDE EFFECTS: Network calls in ViewModel, not composables
 * 10. ✅ RECOMPOSITION SCOPE: Each card only recomposes when its data changes
 *
 * RESULT: Efficient recomposition with minimal unnecessary updates!
 */
