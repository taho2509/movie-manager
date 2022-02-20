package com.hobbie.movie.manager.domain.use_cases

import com.hobbie.movie.manager.domain.entities.Movie
import com.hobbie.movie.manager.domain.entities.NonEmptyString
import com.hobbie.movie.manager.domain.interfaces.MovieRepository
import com.hobbie.movie.manager.domain.interfaces.MovieService
import org.springframework.stereotype.Service

@Service
class SearchMovie(private val movieProvider: MovieService, private val movieRepository: MovieRepository) {
    fun execute(search: NonEmptyString): List<Movie> {
        val external = movieProvider
            .find(search)
            .distinct()
        val local = movieRepository.find(search)

        return local + removeExistingResultsFromExternal(local, external)
    }

    private fun removeExistingResultsFromExternal(local: List<Movie>, external: List<Movie>): List<Movie> {
        return external.filter { it !in local }
    }
}