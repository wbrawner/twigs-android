package com.wbrawner.budget.common

/**
 * Base interface for an entity repository that provides basic CRUD methods
 *
 * @param T The type of the object supported by this repository
 * @param K The type of the primary identifier for the object supported by this repository
 */
interface Repository<T, K> {
    suspend fun create(newItem: T): T
    suspend fun findAll(): Collection<T>
    suspend fun findById(id: K): T
    suspend fun update(updatedItem: T): T
    suspend fun delete(id: K)
}