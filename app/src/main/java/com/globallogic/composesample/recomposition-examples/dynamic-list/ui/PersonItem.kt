package com.globallogic.composesample.recompositionexamples.dynamiclist.ui

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.globallogic.composesample.recompositionexamples.dynamiclist.models.Person
import com.globallogic.composesample.recompositionexamples.dynamiclist.models.PersonUpdate

/**
 * INDIVIDUAL PERSON ITEM COMPONENT
 *
 * Efficiently displays a single person with actions.
 * Only recomposes when person data actually changes.
 */

// Example 13: Individual Person Item (Efficient)
@Composable
fun PersonItem(
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
