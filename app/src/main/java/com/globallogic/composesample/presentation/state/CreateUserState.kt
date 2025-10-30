package com.globallogic.composesample.presentation.state

/**
 * Immutable UI state for the create user screen.
 *
 * This demonstrates how different screens can have their own state
 * data classes while following the same unidirectional data flow pattern.
 */
data class CreateUserState(
    val name: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null
) {
    /**
     * Validates if the form can be submitted
     */
    val canSubmit: Boolean get() =
        name.isNotBlank() &&
        email.isNotBlank() &&
        nameError == null &&
        emailError == null &&
        !isLoading

    /**
     * Validates if name field has errors
     */
    val hasNameError: Boolean get() = nameError != null

    /**
     * Validates if email field has errors
     */
    val hasEmailError: Boolean get() = emailError != null

    /**
     * Shows if there are any field errors
     */
    val hasFieldErrors: Boolean get() = hasNameError || hasEmailError
}

