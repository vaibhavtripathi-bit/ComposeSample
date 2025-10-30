package com.globallogic.composesample.recompositionexamples.dynamiclist.models

import androidx.compose.runtime.Immutable

/**
 * DATA MODELS FOR DYNAMIC LIST MANAGEMENT
 *
 * Stable data classes for efficient recomposition in dynamic lists.
 * These models are designed to work seamlessly with backend APIs.
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
