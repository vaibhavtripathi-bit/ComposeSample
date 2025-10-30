package com.globallogic.composesample.recompositionexamples.dynamiclist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globallogic.composesample.recompositionexamples.dynamiclist.models.Person
import com.globallogic.composesample.recompositionexamples.dynamiclist.models.PersonUpdate
import com.globallogic.composesample.recompositionexamples.dynamiclist.utils.SampleDataGenerator.generateSamplePersons
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * VIEWMODEL FOR EFFICIENT LIST MANAGEMENT
 *
 * Manages dynamic person list with optimal recomposition patterns.
 * Demonstrates StateFlow usage, efficient operations, and API integration.
 */

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
                delay(200)

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
                delay(150)

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
                delay(100)

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
                delay(500)

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
