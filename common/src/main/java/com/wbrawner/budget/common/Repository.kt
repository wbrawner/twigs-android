package com.wbrawner.budget.common

import io.reactivex.Single

/**
 * Base interface for an entity repository that provides basic CRUD methods
 *
 * @param T The type of the object supported by this repository
 * @param K The type of the primary identifier for the object supported by this repository
 */
interface Repository<T, K> {
    fun create(newItem: T): Single<T>
    fun findAll(): Single<Collection<T>>
    fun findById(id: K): Single<T>
    fun update(updatedItem: T): Single<T>
    fun delete(id: K): Single<Void>
}