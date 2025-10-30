package com.globallogic.composesample.presentation.state

/**
 * Events representing user interactions on the user list screen.
 *
 * In unidirectional data flow:
 * 1. UI triggers events (user actions)
 * 2. ViewModel processes events and updates state
 * 3. State changes trigger UI recomposition
 * 4. This creates a predictable, testable flow
 *
 * Events are one-time actions that trigger state changes.
 */
sealed class UserListEvent {

    /**
     * Load users when screen starts or refresh is triggered
     */
    data object LoadUsers : UserListEvent()

    /**
     * Refresh users (pull to refresh)
     */
    data object RefreshUsers : UserListEvent()

    /**
     * Delete a specific user
     */
    data class DeleteUser(val userId: String) : UserListEvent()

    /**
     * Navigate to create user screen
     */
    data object NavigateToCreateUser : UserListEvent()

    /**
     * Retry loading after error
     */
    data object RetryLoad : UserListEvent()
}

