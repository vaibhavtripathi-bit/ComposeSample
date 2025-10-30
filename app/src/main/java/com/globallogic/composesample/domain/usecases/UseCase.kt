package com.globallogic.composesample.domain.usecases

/**
 * Base interface for all use cases in the domain layer.
 *
 * This interface follows the Clean Architecture principle where use cases
 * encapsulate business logic and coordinate between different layers.
 *
 * @param P Input parameter type (use Unit for no parameters)
 * @param R Return type of the use case
 */
interface UseCase<in P, out R> {

    /**
     * Executes the use case with the given parameters.
     *
     * This operator function allows use cases to be called like regular functions,
     * providing a clean and intuitive API for business logic execution.
     *
     * @param params Input parameters for the use case
     * @return Result of the use case execution
     */
    operator fun invoke(params: P): R
}

