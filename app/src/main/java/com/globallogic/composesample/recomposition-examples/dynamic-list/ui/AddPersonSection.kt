package com.globallogic.composesample.recompositionexamples.dynamiclist.ui

import androidx.compose.runtime.Composable
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.globallogic.composesample.recompositionexamples.dynamiclist.models.Person

/**
 * ADD PERSON SECTION COMPONENT
 *
 * Form for adding new persons to the list.
 * Manages its own state and validates input.
 */

// Example 14: Add New Person Section
@Composable
fun AddPersonSection(onAddPerson: (Person) -> Unit) {
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
