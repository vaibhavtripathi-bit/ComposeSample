package com.globallogic.composesample.recompositionexamples.dynamiclist.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globallogic.composesample.recompositionexamples.dynamiclist.models.Person
import com.globallogic.composesample.recompositionexamples.dynamiclist.models.PersonUpdate
import com.globallogic.composesample.recompositionexamples.dynamiclist.viewmodel.PersonListViewModel
import com.globallogic.composesample.recompositionexamples.dynamiclist.ui.PersonListHeader
import com.globallogic.composesample.recompositionexamples.dynamiclist.ui.PersonItem
import com.globallogic.composesample.recompositionexamples.dynamiclist.ui.AddPersonSection

/**
 * MAIN UI SCREEN FOR DYNAMIC LIST MANAGEMENT
 *
 * Efficiently displays and manages a dynamic list of persons with optimal recomposition.
 */

// Example 11: Efficient UI with Stable Keys
@Composable
fun PersonListScreen(viewModel: PersonListViewModel = viewModel()) {
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
