package com.wbrawner.twigs.shared

/**
 * Base interface for an entity repository that provides basic CRUD methods
 *
 * @param T The type of the object supported by this repository
 */
interface Repository<T> {
    suspend fun create(newItem: T): T
    suspend fun findAll(): List<T>
    suspend fun findById(id: String): T
    suspend fun update(updatedItem: T): T
    suspend fun delete(id: String)
}

interface Identifiable {
    val id: String?
}
