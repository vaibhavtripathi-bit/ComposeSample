package com.globallogic.composesample.data.repositories

import com.globallogic.composesample.data.datasources.UserDataSource
import com.globallogic.composesample.data.models.UserDto
import com.globallogic.composesample.domain.entities.User
import com.globallogic.composesample.domain.repositories.UserRepository

/**
 * Implementation of UserRepository using data sources.
 *
 * In Clean Architecture, repository implementations:
 * 1. Belong to the data layer
 * 2. Implement domain repository interfaces
 * 3. Use data sources to access data
 * 4. Transform between data models and domain entities
 * 5. Handle data access logic and error handling
 * 6. Can combine multiple data sources (local + remote)
 */
class UserRepositoryImpl(
    private val userDataSource: UserDataSource
) : UserRepository {

    /**
     * Retrieves all users from the data source and transforms them to domain entities.
     */
    override fun getAllUsers(): List<User> {
        return try {
            // For this demo, we'll use a synchronous approach
            // In a real app, this would typically be a coroutine suspend function
            val userDtos = userDataSource.getAllUsers()
            userDtos.map { it.toDomainEntity() }
        } catch (e: Exception) {
            // In a real app, you might want to log the error and return empty list
            // or throw a custom exception
            emptyList()
        }
    }

    /**
     * Retrieves a user by ID and transforms to domain entity.
     */
    override fun getUserById(userId: String): User? {
        return try {
            val userDto = userDataSource.getUserById(userId)
            userDto?.toDomainEntity()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Saves a user by transforming to DTO, saving, and transforming back.
     */
    override fun saveUser(user: User): User {
        return try {
            val userDto = UserDto.fromDomainEntity(user)
            val savedDto = userDataSource.saveUser(userDto)
            savedDto.toDomainEntity()
        } catch (e: Exception) {
            // In a real app, you might want to throw a custom exception
            // or handle the error more gracefully
            throw RuntimeException("Failed to save user", e)
        }
    }

    /**
     * Deletes a user by ID.
     */
    override fun deleteUser(userId: String): Boolean {
        return try {
            userDataSource.deleteUser(userId)
        } catch (e: Exception) {
            false
        }
    }
}
