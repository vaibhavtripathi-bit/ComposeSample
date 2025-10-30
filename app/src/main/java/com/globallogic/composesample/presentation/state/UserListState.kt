package com.globallogic.composesample.presentation.state

import com.globallogic.composesample.domain.entities.User

/**
 * Immutable UI state for the user list screen.
 *
 * In Jetpack Compose with unidirectional data flow:
 * 1. State is immutable (data classes)
 * 2. State represents the entire UI state at any point in time
 * 3. ViewModel owns and modifies state
 * 4. UI observes state and triggers events
 * 5. State changes trigger recomposition
 *
 * This follows the Single Source of Truth pattern where the ViewModel
 * is the single source of truth for screen state.
 */
data class UserListState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false
) {
    /**
     * Computed property to show/hide loading indicator
     */
    val showLoading: Boolean get() = isLoading && users.isEmpty()

    /**
     * Computed property to show/hide error message
     */
    val showError: Boolean get() = error != null

    /**
     * Computed property to show/hide empty state
     */
    val showEmpty: Boolean get() = isEmpty && !isLoading && error == null

    /**
     * Computed property to show/hide user list
     */
    val showUsers: Boolean get() = users.isNotEmpty() && !isLoading && error == null
}

