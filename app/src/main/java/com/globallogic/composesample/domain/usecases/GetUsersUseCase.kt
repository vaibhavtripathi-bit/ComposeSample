package com.globallogic.composesample.domain.usecases

import com.globallogic.composesample.domain.entities.User
import com.globallogic.composesample.domain.repositories.UserRepository

/**
 * Use case for retrieving all users.
 *
 * This use case demonstrates the Clean Architecture pattern where:
 * 1. Use cases contain business logic
 * 2. Use cases depend on abstractions (repositories), not concrete implementations
 * 3. Use cases coordinate between different layers
 *
 * In a real application, this might include business rules like:
 * - Filtering users based on permissions
 * - Sorting users by certain criteria
 * - Validating user access rights
 */
class GetUsersUseCase(
    private val userRepository: UserRepository
) : UseCase<Unit, List<User>> {

    /**
     * Retrieves all users from the repository.
     *
     * @param params Unit (no parameters needed)
     * @return List of all users
     */
    override fun invoke(params: Unit): List<User> {
        // Business logic could be added here, such as:
        // - Filtering active users only
        // - Sorting by name or registration date
        // - Applying business rules for data access
        return userRepository.getAllUsers()
    }
}

