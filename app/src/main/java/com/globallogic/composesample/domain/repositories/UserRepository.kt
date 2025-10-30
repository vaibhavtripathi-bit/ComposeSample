package com.globallogic.composesample.domain.repositories

import com.globallogic.composesample.domain.entities.User

/**
 * Repository interface for user data operations.
 *
 * In Clean Architecture, repositories:
 * 1. Define the contract for data access
 * 2. Belong to the domain layer (not data layer)
 * 3. Are implemented in the data layer
 * 4. Allow the domain layer to be independent of data sources
 *
 * This follows the Dependency Inversion Principle - the domain layer
 * defines what it needs, and outer layers provide the implementation.
 */
interface UserRepository {

    /**
     * Retrieves all users.
     *
     * @return List of all users
     */
    fun getAllUsers(): List<User>

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The unique identifier of the user
     * @return The user if found, null otherwise
     */
    fun getUserById(userId: String): User?

    /**
     * Saves a user.
     *
     * @param user The user to save
     * @return The saved user (potentially with updated fields like ID)
     */
    fun saveUser(user: User): User

    /**
     * Deletes a user by their ID.
     *
     * @param userId The unique identifier of the user to delete
     * @return True if the user was deleted, false if not found
     */
    fun deleteUser(userId: String): Boolean
}

