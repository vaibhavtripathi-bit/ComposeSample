package com.globallogic.composesample.domain.entities

/**
 * Domain entity representing a User.
 *
 * In Clean Architecture, domain entities are pure business objects
 * that contain no framework-specific code and represent the core
 * business concepts of the application.
 *
 * Domain entities should:
 * - Be immutable (use val instead of var)
 * - Contain business logic related to the entity
 * - Be independent of external frameworks (Android, databases, etc.)
 * - Focus on business rules and invariants
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val isActive: Boolean = true
) {
    /**
     * Business logic method - validates if the user can perform actions
     */
    fun canPerformActions(): Boolean = isActive && email.isNotBlank()

    /**
     * Business logic method - creates a display name
     */
    fun getDisplayName(): String = if (name.isNotBlank()) name else email
}

