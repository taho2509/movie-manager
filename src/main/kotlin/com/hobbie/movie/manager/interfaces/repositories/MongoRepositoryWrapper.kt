package com.hobbie.movie.manager.interfaces.repositories

import com.hobbie.movie.manager.domain.entities.Movie
import com.hobbie.movie.manager.domain.entities.NonEmptyString
import com.hobbie.movie.manager.domain.entities.Year
import com.hobbie.movie.manager.domain.interfaces.MovieRepository
import com.hobbie.movie.manager.interfaces.repositories.mongo.MongoMovieRepository
import com.hobbie.movie.manager.interfaces.repositories.mongo.transformers.MongoMovieTransformer
import org.springframework.stereotype.Repository

@Repository
class MongoRepositoryWrapper(
    val repository: MongoMovieRepository,
    val transformer: MongoMovieTransformer,
):MovieRepository {
    override fun save(movie: Movie): Boolean {
        val document = transformer.toMongoDocument(movie)
        repository.save(document)
        return true
    }

    override fun find(title: NonEmptyString, year: Year): Movie {
        val document = repository.findOneByTitleAndYear(title.value, year.value)
        return transformer.toMovie(document)
    }

    override fun find(title: NonEmptyString): List<Movie> {
        val documents = repository.findByTitleLike(title.value)
        return documents.map { transformer.toMovie(it) }
    }
}