package com.globallogic.composesample.presentation.state

/**
 * Events representing user interactions on the create user screen.
 */
sealed class CreateUserEvent {

    /**
     * Update name field
     */
    data class UpdateName(val name: String) : CreateUserEvent()

    /**
     * Update email field
     */
    data class UpdateEmail(val email: String) : CreateUserEvent()

    /**
     * Submit the form to create user
     */
    data object Submit : CreateUserEvent()

    /**
     * Clear the form after successful creation
     */
    data object ClearForm : CreateUserEvent()

    /**
     * Navigate back to user list
     */
    data object NavigateBack : CreateUserEvent()
}

