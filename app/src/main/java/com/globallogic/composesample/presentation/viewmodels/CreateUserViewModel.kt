package com.globallogic.composesample.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globallogic.composesample.di.AppModule
import com.globallogic.composesample.domain.usecases.CreateUserUseCase
import com.globallogic.composesample.domain.usecases.CreateUserParams
import com.globallogic.composesample.presentation.state.CreateUserEvent
import com.globallogic.composesample.presentation.state.CreateUserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the create user screen.
 *
 * Demonstrates form handling with validation in Compose:
 * 1. State holds form data and validation errors
 * 2. Events update individual fields
 * 3. Real-time validation as user types
 * 4. Submit event triggers use case execution
 */
class CreateUserViewModel(
    private val createUserUseCase: CreateUserUseCase = AppModule.provideCreateUserUseCase()
) : ViewModel() {

    // Default constructor for ViewModel framework
    constructor() : this(createUserUseCase = AppModule.provideCreateUserUseCase())

    private val _state = MutableStateFlow(CreateUserState())
    val state: StateFlow<CreateUserState> = _state.asStateFlow()

    /**
     * Processes UI events for form interactions.
     */
    fun onEvent(event: CreateUserEvent) {
        when (event) {
            is CreateUserEvent.UpdateName -> updateName(event.name)
            is CreateUserEvent.UpdateEmail -> updateEmail(event.email)
            is CreateUserEvent.Submit -> submitForm()
            is CreateUserEvent.ClearForm -> clearForm()
            else -> {
                // Handle other events if needed
            }
        }
    }

    private fun updateName(name: String) {
        val trimmedName = name.trim()
        _state.value = _state.value.copy(
            name = trimmedName,
            nameError = validateName(trimmedName)
        )
    }

    private fun updateEmail(email: String) {
        val trimmedEmail = email.trim()
        _state.value = _state.value.copy(
            email = trimmedEmail,
            emailError = validateEmail(trimmedEmail)
        )
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            name.length > 50 -> "Name must be less than 50 characters"
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            email.length > 100 -> "Email must be less than 100 characters"
            else -> null
        }
    }

    private fun submitForm() {
        val currentState = _state.value

        // Final validation before submission
        val nameError = validateName(currentState.name)
        val emailError = validateEmail(currentState.email)

        _state.value = currentState.copy(
            nameError = nameError,
            emailError = emailError
        )

        // Only submit if validation passes
        if (nameError == null && emailError == null) {
            performUserCreation()
        }
    }

    private fun performUserCreation() {
        val currentState = _state.value

        _state.value = currentState.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val result = createUserUseCase(
                    CreateUserParams(
                        name = currentState.name,
                        email = currentState.email
                    )
                )

                result.fold(
                    onSuccess = { user ->
                        _state.value = currentState.copy(
                            isLoading = false,
                            isSuccess = true,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _state.value = currentState.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to create user"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = currentState.copy(
                    isLoading = false,
                    error = e.message ?: "Unexpected error occurred"
                )
            }
        }
    }

    private fun clearForm() {
        _state.value = CreateUserState()
    }
}
