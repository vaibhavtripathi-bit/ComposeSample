package com.globallogic.composesample.domain.usecases

import com.globallogic.composesample.domain.entities.User
import com.globallogic.composesample.domain.repositories.UserRepository

/**
 * Use case for creating a new user.
 *
 * This use case demonstrates:
 * 1. Use cases with input parameters
 * 2. Business logic validation before data operations
 * 3. Error handling and business rule enforcement
 */
class CreateUserUseCase(
    private val userRepository: UserRepository
) : UseCase<CreateUserParams, Result<User>> {

    /**
     * Creates a new user with business logic validation.
     *
     * @param params Parameters containing user creation data
     * @return Result containing the created user or an error
     */
    override fun invoke(params: CreateUserParams): Result<User> {
        return try {
            // Business logic validation
            validateUserData(params)

            // Create the user entity
            val user = User(
                id = generateUserId(), // In real app, this might come from the repository
                name = params.name.trim(),
                email = params.email.trim().lowercase(),
                isActive = true
            )

            // Business rule: check if email is already taken
            // Note: In a real app, you might want a separate method to check by email
            // For this demo, we'll skip this check to avoid complexity
            // val existingUser = userRepository.getUserById(user.email)
            // if (existingUser != null) {
            //     return Result.failure(IllegalArgumentException("Email already exists"))
            // }

            // Save the user
            val savedUser = userRepository.saveUser(user)
            Result.success(savedUser)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateUserData(params: CreateUserParams) {
        require(params.name.isNotBlank()) { "Name cannot be empty" }
        require(params.email.isNotBlank()) { "Email cannot be empty" }
        require(params.email.contains("@")) { "Invalid email format" }
    }

    private fun generateUserId(): String {
        // In a real application, this might be handled by the repository
        // or a dedicated ID generation service
        return "user_${System.currentTimeMillis()}"
    }
}

/**
 * Input parameters for the CreateUserUseCase.
 *
 * This data class represents the business requirements for user creation,
 * containing only the essential fields needed for the business operation.
 */
data class CreateUserParams(
    val name: String,
    val email: String
)
