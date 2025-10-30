package com.globallogic.composesample.data.models

import com.globallogic.composesample.domain.entities.User

/**
 * Data Transfer Object for User data.
 *
 * In Clean Architecture, data models are:
 * 1. Specific to data sources (databases, APIs, etc.)
 * 2. Separate from domain entities
 * 3. Used for data transformation between layers
 * 4. May contain framework-specific annotations or fields
 *
 * This DTO could represent data from a REST API, database, or other data source.
 */
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val isActive: Boolean,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
) {
    /**
     * Maps this DTO to a domain entity.
     *
     * Data layer is responsible for transforming between data models
     * and domain entities, keeping the domain layer pure.
     */
    fun toDomainEntity(): User = User(
        id = id,
        name = name,
        email = email,
        isActive = isActive
    )

    companion object {
        /**
         * Creates a DTO from a domain entity.
         */
        fun fromDomainEntity(user: User): UserDto = UserDto(
            id = user.id,
            name = user.name,
            email = user.email,
            isActive = user.isActive,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}

