package com.hobbie.movie.manager.interfaces.repositories.mongo

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository

@NoRepositoryBean
interface MongoBaseRepository<T, ID>: Repository<T, ID> {
    fun findById(id: ID): T?
    fun <S : T?> save(entity: S): S
}