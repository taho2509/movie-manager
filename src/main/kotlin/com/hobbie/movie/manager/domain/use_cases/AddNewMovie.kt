package com.hobbie.movie.manager.domain.use_cases

import com.hobbie.movie.manager.domain.entities.Movie
import com.hobbie.movie.manager.domain.entities.NonEmptyString
import com.hobbie.movie.manager.domain.entities.Year
import com.hobbie.movie.manager.domain.interfaces.MovieRepository
import com.hobbie.movie.manager.domain.interfaces.MovieService
import org.springframework.stereotype.Service

@Service
class AddNewMovie(
    private val movieProvider: MovieService,
    private val movieRepository: MovieRepository) {
    fun execute(title: NonEmptyString, year: Year?): Movie {
        val movie = movieProvider.find(title, year)
        movieRepository.save(movie)

        return movie
    }
}