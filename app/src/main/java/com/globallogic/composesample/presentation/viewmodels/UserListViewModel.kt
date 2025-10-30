package com.globallogic.composesample.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globallogic.composesample.di.AppModule
import com.globallogic.composesample.domain.repositories.UserRepository
import com.globallogic.composesample.domain.usecases.CreateUserUseCase
import com.globallogic.composesample.domain.usecases.GetUsersUseCase
import com.globallogic.composesample.presentation.state.UserListEvent
import com.globallogic.composesample.presentation.state.UserListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the user list screen.
 *
 * In Clean Architecture with Jetpack Compose:
 * 1. ViewModel owns screen state (Single Source of Truth)
 * 2. ViewModel processes UI events and updates state
 * 3. ViewModel uses use cases to perform business logic
 * 4. UI observes state changes and triggers events
 * 5. Uses unidirectional data flow pattern
 *
 * ViewModels should:
 * - Expose immutable state
 * - Process events to update state
 * - Use use cases for business logic
 * - Handle coroutines and error states
 * - Be testable and independent of Android framework
 */
class UserListViewModel(
    private val getUsersUseCase: GetUsersUseCase = AppModule.provideGetUsersUseCase(),
    private val createUserUseCase: CreateUserUseCase = AppModule.provideCreateUserUseCase(),
    private val userRepository: UserRepository = AppModule.provideUserRepository()
) : ViewModel() {

    // Default constructor for ViewModel framework
    constructor() : this(
        getUsersUseCase = AppModule.provideGetUsersUseCase(),
        createUserUseCase = AppModule.provideCreateUserUseCase(),
        userRepository = AppModule.provideUserRepository()
    )

    // Backing property for immutable state exposure
    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()

    init {
        // Load users when ViewModel is created
        onEvent(UserListEvent.LoadUsers)
    }

    /**
     * Processes UI events and updates state accordingly.
     *
     * This method implements unidirectional data flow where:
     * 1. UI triggers events
     * 2. ViewModel processes events
     * 3. State is updated
     * 4. UI recomposes based on new state
     */
    fun onEvent(event: UserListEvent) {
        when (event) {
            is UserListEvent.LoadUsers -> loadUsers()
            is UserListEvent.RefreshUsers -> refreshUsers()
            is UserListEvent.DeleteUser -> deleteUser(event.userId)
            is UserListEvent.RetryLoad -> loadUsers()
            else -> {
                // Handle other events if needed
            }
        }
    }

    private fun loadUsers() {
        _state.value = _state.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // Use use case to get users (business logic)
                val users = getUsersUseCase(Unit)

                _state.value = _state.value.copy(
                    users = users,
                    isLoading = false,
                    error = null,
                    isEmpty = users.isEmpty()
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load users: ${e.message}",
                    isEmpty = true
                )
            }
        }
    }

    private fun refreshUsers() {
        // For refresh, we don't show loading state initially
        viewModelScope.launch {
            try {
                val users = getUsersUseCase(Unit)

                _state.value = _state.value.copy(
                    users = users,
                    error = null,
                    isEmpty = users.isEmpty()
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to refresh: ${e.message}"
                )
            }
        }
    }

    private fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                // Use repository directly for delete operation
                // In a real app, you might want a dedicated DeleteUserUseCase
                val deleted = userRepository.deleteUser(userId)

                if (deleted) {
                    // Remove user from current state
                    val currentUsers = _state.value.users.toMutableList()
                    currentUsers.removeAll { it.id == userId }

                    _state.value = _state.value.copy(
                        users = currentUsers,
                        isEmpty = currentUsers.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to delete user: ${e.message}"
                )
            }
        }
    }
}
