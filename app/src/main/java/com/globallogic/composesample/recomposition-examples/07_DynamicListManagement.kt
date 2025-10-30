package com.globallogic.composesample.recompositionexamples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * DYNAMIC LIST MANAGEMENT - EFFICIENT RECOMPOSITION
 *
 * This file demonstrates how to efficiently handle dynamic lists from backend APIs
 * without causing unnecessary recompositions. Covers:
 * - Stable data classes with proper equality
 * - ViewModel state management with StateFlow
 * - Efficient list operations (add, delete, update)
 * - LazyColumn with stable keys
 * - API integration patterns
 * - Real-time updates without full recomposition
 */

// Example 1: Stable Person Data Class
@Immutable
data class Person(
    val id: String,
    val name: String,
    val email: String,
    val age: Int,
    val department: String,
    val isActive: Boolean = true,
    val lastModified: Long = System.currentTimeMillis()
) {
    // Custom equals for stability
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false
        return id == other.id // Only compare by ID for stability
    }

    override fun hashCode(): Int = id.hashCode()
}

// Example 2: Person Update Models (for partial updates)
data class PersonUpdate(
    val name: String? = null,
    val email: String? = null,
    val age: Int? = null,
    val department: String? = null,
    val isActive: Boolean? = null
)

// Example 3: ViewModel for Efficient List Management
class PersonListViewModel : ViewModel() {

    // ✅ GOOD: Use StateFlow for reactive list updates
    private val _persons = MutableStateFlow<List<Person>>(emptyList())
    val persons: StateFlow<List<Person>> = _persons.asStateFlow()

    // ✅ GOOD: Separate loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Simulate initial data load
        loadPersonsFromAPI()
    }

    // Example 4: Efficient Add Operation
    fun addPerson(person: Person) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Simulate API call delay
                kotlinx.coroutines.delay(200)

                // ✅ GOOD: Create new list instead of mutating
                _persons.value = _persons.value + person

                // In real app: call API to persist
                // apiService.createPerson(person)

            } catch (e: Exception) {
                _error.value = "Failed to add person: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Example 5: Efficient Update Operation
    fun updatePerson(personId: String, updates: PersonUpdate) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Simulate API call
                kotlinx.coroutines.delay(150)

                // ✅ GOOD: Immutable update - create new list with updated item
                _persons.value = _persons.value.map { person ->
                    if (person.id == personId) {
                        person.copy(
                            name = updates.name ?: person.name,
                            email = updates.email ?: person.email,
                            age = updates.age ?: person.age,
                            department = updates.department ?: person.department,
                            isActive = updates.isActive ?: person.isActive,
                            lastModified = System.currentTimeMillis()
                        )
                    } else {
                        person
                    }
                }

            } catch (e: Exception) {
                _error.value = "Failed to update person: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Example 6: Efficient Delete Operation
    fun deletePerson(personId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Simulate API call
                kotlinx.coroutines.delay(100)

                // ✅ GOOD: Filter creates new list without the deleted item
                _persons.value = _persons.value.filter { it.id != personId }

            } catch (e: Exception) {
                _error.value = "Failed to delete person: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Example 7: Batch Operations
    fun toggleMultiplePersons(personIds: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true

            // ✅ GOOD: Single update for multiple items
            _persons.value = _persons.value.map { person ->
                if (personIds.contains(person.id)) {
                    person.copy(
                        isActive = !person.isActive,
                        lastModified = System.currentTimeMillis()
                    )
                } else {
                    person
                }
            }

            _isLoading.value = false
        }
    }

    // Example 8: Real-time Updates (WebSocket/SSE simulation)
    fun simulateRealTimeUpdate(updatedPerson: Person) {
        // ✅ GOOD: Update only the specific item
        _persons.value = _persons.value.map { person ->
            if (person.id == updatedPerson.id) updatedPerson else person
        }
    }

    // Example 9: API Integration
    private fun loadPersonsFromAPI() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Simulate API call
                kotlinx.coroutines.delay(500)

                // ✅ GOOD: Replace entire list when loading from API
                _persons.value = generateSamplePersons()

            } catch (e: Exception) {
                _error.value = "Failed to load persons: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Example 10: Refresh Data
    fun refreshPersons() {
        loadPersonsFromAPI()
    }
}

// Example 11: Efficient UI with Stable Keys
@Composable
fun PersonListScreen(viewModel: PersonListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val persons by viewModel.persons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        // Header with stats
        PersonListHeader(
            totalCount = persons.size,
            activeCount = persons.count { it.isActive },
            isLoading = isLoading,
            onRefresh = { viewModel.refreshPersons() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error display
        error?.let { errorMessage ->
            Text(
                text = "Error: $errorMessage",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // ✅ GOOD: LazyColumn with stable keys for efficient recomposition
        LazyColumn {
            items(
                items = persons,
                key = { person -> person.id }  // Stable key prevents unnecessary recompositions
            ) { person ->
                PersonItem(
                    person = person,
                    onUpdate = { updates -> viewModel.updatePerson(person.id, updates) },
                    onDelete = { viewModel.deletePerson(person.id) },
                    onToggle = { viewModel.updatePerson(person.id, PersonUpdate(isActive = !person.isActive)) }
                )
            }
        }

        // Add new person
        AddPersonSection { newPerson ->
            viewModel.addPerson(newPerson)
        }
    }
}

// Example 12: Header with Derived State
@Composable
private fun PersonListHeader(
    totalCount: Int,
    activeCount: Int,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Team Members",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Active: $activeCount / Total: $totalCount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onRefresh,
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Loading..." else "Refresh")
            }
        }
    }
}

// Example 13: Individual Person Item (Efficient)
@Composable
private fun PersonItem(
    person: Person,
    onUpdate: (PersonUpdate) -> Unit,
    onDelete: () -> Unit,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Person info (only recomposes when person data changes)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = person.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${person.email} • ${person.department}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Age: ${person.age} • ${if (person.isActive) "Active" else "Inactive"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Action buttons
            Row {
                Button(
                    onClick = onToggle,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(if (person.isActive) "Deactivate" else "Activate")
                }

                Button(
                    onClick = onDelete
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

// Example 14: Add New Person Section
@Composable
private fun AddPersonSection(onAddPerson: (Person) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Add New Person",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Form fields
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = department,
                onValueChange = { department = it },
                label = { Text("Department") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && age.isNotBlank() && department.isNotBlank()) {
                        val newPerson = Person(
                            id = "person-${System.currentTimeMillis()}",
                            name = name.trim(),
                            email = email.trim(),
                            age = age.toIntOrNull() ?: 25,
                            department = department.trim()
                        )
                        onAddPerson(newPerson)

                        // Clear form
                        name = ""
                        email = ""
                        age = ""
                        department = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Person")
            }
        }
    }
}

// Example 15: Performance Comparison Demo
@Composable
fun PerformanceComparisonDemo() {
    var useStableKeys by remember { mutableStateOf(true) }
    var persons by remember { mutableStateOf(generateSamplePersons()) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Toggle for demonstration
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Use Stable Keys: ")
            Button(onClick = { useStableKeys = !useStableKeys }) {
                Text(if (useStableKeys) "ON (Efficient)" else "OFF (Inefficient)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (useStableKeys) {
            // ✅ GOOD: Efficient with stable keys
            LazyColumn {
                items(
                    items = persons,
                    key = { person -> person.id }
                ) { person ->
                    PersonItem(
                        person = person,
                        onUpdate = { /* handle update */ },
                        onDelete = { persons = persons.filter { it.id != person.id } },
                        onToggle = {
                            persons = persons.map {
                                if (it.id == person.id) it.copy(isActive = !it.isActive) else it
                            }
                        }
                    )
                }
            }
        } else {
            // ❌ BAD: Inefficient without keys
            LazyColumn {
                items(persons) { person ->  // No key - causes issues
                    PersonItem(
                        person = person,
                        onUpdate = { /* handle update */ },
                        onDelete = { persons = persons.filter { it.id != person.id } },
                        onToggle = {
                            persons = persons.map {
                                if (it.id == person.id) it.copy(isActive = !it.isActive) else it
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Control buttons
        Row {
            Button(
                onClick = {
                    // Add new person
                    val newPerson = Person(
                        id = "person-${System.currentTimeMillis()}",
                        name = "New Person ${persons.size + 1}",
                        email = "new${persons.size + 1}@example.com",
                        age = 20 + (persons.size % 50),
                        department = "Engineering"
                    )
                    persons = persons + newPerson
                }
            ) {
                Text("Add Person")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    // Delete first active person
                    val activePerson = persons.firstOrNull { it.isActive }
                    if (activePerson != null) {
                        persons = persons.filter { it.id != activePerson.id }
                    }
                }
            ) {
                Text("Delete First")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    // Update all active persons
                    persons = persons.map {
                        if (it.isActive) {
                            it.copy(
                                age = it.age + 1,
                                lastModified = System.currentTimeMillis()
                            )
                        } else it
                    }
                }
            ) {
                Text("Age Up Active")
            }
        }
    }
}

// Example 16: Sample Data Generator
private fun generateSamplePersons(): List<Person> {
    val departments = listOf("Engineering", "Marketing", "Sales", "HR", "Finance", "Operations")
    val names = listOf(
        "Alice Johnson", "Bob Smith", "Carol Williams", "David Brown", "Eva Davis",
        "Frank Wilson", "Grace Miller", "Henry Taylor", "Iris Anderson", "Jack Thomas"
    )

    return List(20) { index ->
        Person(
            id = "person-${index + 1}",
            name = names[index % names.size] + if (index >= names.size) " ${index / names.size + 1}" else "",
            email = "person${index + 1}@company.com",
            age = 25 + (index % 40),
            department = departments[index % departments.size],
            isActive = index % 7 != 0, // Most are active
            lastModified = System.currentTimeMillis() - (index * 60000L) // Different timestamps
        )
    }
}

/**
 * KEY PERFORMANCE INSIGHTS:
 *
 * 1. ✅ STABLE KEYS: Using person.id ensures only changed items recompose
 * 2. ✅ IMMUTABLE UPDATES: Creating new lists instead of mutating existing ones
 * 3. ✅ STATEFLOW: Reactive updates without manual state synchronization
 * 4. ✅ BATCH OPERATIONS: Single updates for multiple items
 * 5. ✅ PROPER EQUALITY: Person class only compares by ID for stability
 * 6. ✅ LAZY LOADING: Only visible items are composed
 * 7. ✅ OPTIMIZED RECOMPOSITION: Minimal UI updates when data changes
 *
 * RESULT: Efficient handling of dynamic lists with minimal recompositions!
 */
