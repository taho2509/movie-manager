package com.hobbie.movie.manager.interfaces.repositories.mongo

interface MongoMovieRepository : MongoBaseRepository<MovieDocument, String> {
    fun findOneByTitleAndYear(title: String, year: Short): MovieDocument
    fun findByTitle(title: String): List<MovieDocument>
    fun findByTitleLike(title: String): List<MovieDocument>
}