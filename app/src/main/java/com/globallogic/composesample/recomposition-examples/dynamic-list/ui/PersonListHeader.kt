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

/**
 * PERSON LIST HEADER COMPONENT
 *
 * Displays summary statistics and refresh functionality.
 * Uses derived state for efficient updates.
 */

// Example 12: Header with Derived State
@Composable
fun PersonListHeader(
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
