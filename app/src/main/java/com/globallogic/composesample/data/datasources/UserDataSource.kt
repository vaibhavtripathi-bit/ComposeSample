package com.globallogic.composesample.data.datasources

import com.globallogic.composesample.data.models.UserDto
import androidx.core.content.edit

/**
 * Data source interface for user data operations.
 *
 * In Clean Architecture, data sources:
 * 1. Handle the actual data retrieval/storage
 * 2. Are framework-specific (Room, Retrofit, SharedPreferences, etc.)
 * 3. Are abstracted behind interfaces for testability
 * 4. Can be local (database, cache) or remote (API)
 */
interface UserDataSource {

    /**
     * Retrieves all users from the data source.
     */
    fun getAllUsers(): List<UserDto>

    /**
     * Retrieves a user by ID.
     */
    fun getUserById(userId: String): UserDto?

    /**
     * Saves a user to the data source.
     */
    fun saveUser(userDto: UserDto): UserDto

    /**
     * Deletes a user by ID.
     */
    fun deleteUser(userId: String): Boolean
}

/**
 * SharedPreferences implementation of UserDataSource for persistent storage.
 *
 * This implementation uses SharedPreferences to persist user data across app sessions.
 * In a real application, you might use:
 * - Room database for local storage
 * - Retrofit API client for remote data
 * - File storage for caching
 */
class SharedPreferencesUserDataSource(
    private val sharedPreferences: android.content.SharedPreferences
) : UserDataSource {

    companion object {
        private const val PREFS_NAME = "user_data"
        private const val KEY_USERS = "users"
        private const val SEPARATOR = "|||" // Separator for user fields
        private const val FIELD_SEPARATOR = ":::" // Separator within user data
    }

    init {
        // Add some sample data if no data exists
        if (getAllUsers().isEmpty()) {
            val sampleUsers = listOf(
                UserDto(
                    id = "user_1",
                    name = "John Doe",
                    email = "john.doe@example.com",
                    isActive = true,
                    createdAt = System.currentTimeMillis()
                ),
                UserDto(
                    id = "user_2",
                    name = "Jane Smith",
                    email = "jane.smith@example.com",
                    isActive = true,
                    createdAt = System.currentTimeMillis()
                ),
                UserDto(
                    id = "user_3",
                    name = "Bob Johnson",
                    email = "bob.johnson@example.com",
                    isActive = false,
                    createdAt = System.currentTimeMillis()
                )
            )
            sampleUsers.forEach { saveUser(it) }
        }
    }

    override fun getAllUsers(): List<UserDto> {
        val usersString = sharedPreferences.getString(KEY_USERS, "") ?: ""
        if (usersString.isEmpty()) return emptyList()

        return usersString.split(SEPARATOR).mapNotNull { userString ->
            if (userString.isEmpty()) return@mapNotNull null

            try {
                val parts = userString.split(FIELD_SEPARATOR)
                if (parts.size >= 5) {
                    UserDto(
                        id = parts[0],
                        name = parts[1],
                        email = parts[2],
                        isActive = parts[3].toBoolean(),
                        createdAt = parts[4].toLongOrNull() ?: System.currentTimeMillis(),
                        updatedAt = parts[5].toLongOrNull() ?: System.currentTimeMillis()
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun getUserById(userId: String): UserDto? {
        return getAllUsers().find { it.id == userId }
    }

    override fun saveUser(userDto: UserDto): UserDto {
        val updatedDto = userDto.copy(updatedAt = System.currentTimeMillis())
        val currentUsers = getAllUsers().toMutableList()

        // Remove existing user with same ID
        currentUsers.removeAll { it.id == updatedDto.id }

        // Add the updated user
        currentUsers.add(updatedDto)

        // Save to SharedPreferences
        saveUsersToPrefs(currentUsers)

        return updatedDto
    }

    override fun deleteUser(userId: String): Boolean {
        val currentUsers = getAllUsers().toMutableList()
        val removed = currentUsers.removeAll { it.id == userId }

        if (removed) {
            saveUsersToPrefs(currentUsers)
        }

        return removed
    }

    private fun saveUsersToPrefs(users: List<UserDto>) {
        val usersString = users.joinToString(SEPARATOR) { user ->
            "${user.id}$FIELD_SEPARATOR${user.name}$FIELD_SEPARATOR${user.email}$FIELD_SEPARATOR${user.isActive}$FIELD_SEPARATOR${user.createdAt}$FIELD_SEPARATOR${user.updatedAt}"
        }

        sharedPreferences.edit {
            putString(KEY_USERS, usersString)
        }
    }
}
