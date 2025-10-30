package com.globallogic.composesample.recompositionexamples.dynamiclist.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.globallogic.composesample.recompositionexamples.dynamiclist.models.Person
import com.globallogic.composesample.recompositionexamples.dynamiclist.models.PersonUpdate
import com.globallogic.composesample.recompositionexamples.dynamiclist.ui.PersonItem
import com.globallogic.composesample.recompositionexamples.dynamiclist.utils.SampleDataGenerator.generateSamplePersons

/**
 * PERFORMANCE COMPARISON DEMO
 *
 * Shows the difference between using stable keys vs no keys in LazyColumn.
 * Demonstrates the performance impact of proper vs improper recomposition.
 */

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
