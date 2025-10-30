package com.globallogic.composesample.recompositionexamples.dynamiclist.utils

import com.globallogic.composesample.recompositionexamples.dynamiclist.models.Person

/**
 * SAMPLE DATA GENERATOR
 *
 * Provides sample data for testing dynamic list management.
 * In real applications, this would come from backend APIs.
 */

// Example 16: Sample Data Generator
fun generateSamplePersons(): List<Person> {
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
