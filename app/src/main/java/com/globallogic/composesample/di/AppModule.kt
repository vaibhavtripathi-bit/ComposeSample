package com.globallogic.composesample.di

import android.content.Context
import android.content.SharedPreferences
import com.globallogic.composesample.data.datasources.SharedPreferencesUserDataSource
import com.globallogic.composesample.data.datasources.UserDataSource
import com.globallogic.composesample.data.repositories.UserRepositoryImpl
import com.globallogic.composesample.domain.repositories.UserRepository
import com.globallogic.composesample.domain.usecases.CreateUserUseCase
import com.globallogic.composesample.domain.usecases.GetUsersUseCase

/**
 * Dependency injection module for the application.
 *
 * In a real application, you would use a DI framework like:
 * - Dagger Hilt
 * - Koin
 * - Kodein
 *
 * For this demo, we'll use simple factory functions to create dependencies.
 * This demonstrates dependency inversion - high-level modules don't depend
 * on low-level modules, but both depend on abstractions.
 */
object AppModule {

    private lateinit var appContext: Context

    /**
     * Initialize the AppModule with application context.
     * This should be called from Application.onCreate()
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Provides SharedPreferences instance.
     */
    private fun provideSharedPreferences(): SharedPreferences {
        return appContext.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    }

    /**
     * Provides UserDataSource implementation with persistent storage.
     */
    fun provideUserDataSource(): UserDataSource {
        return SharedPreferencesUserDataSource(provideSharedPreferences())
    }

    /**
     * Provides UserRepository implementation.
     */
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl(provideUserDataSource())
    }

    /**
     * Provides GetUsersUseCase.
     */
    fun provideGetUsersUseCase(): GetUsersUseCase {
        return GetUsersUseCase(provideUserRepository())
    }

    /**
     * Provides CreateUserUseCase.
     */
    fun provideCreateUserUseCase(): CreateUserUseCase {
        return CreateUserUseCase(provideUserRepository())
    }
}
