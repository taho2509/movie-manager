package com.hobbie.movie.manager.domain.interfaces

import com.hobbie.movie.manager.domain.entities.Movie
import com.hobbie.movie.manager.domain.entities.NonEmptyString
import com.hobbie.movie.manager.domain.entities.Year

interface MovieRepository  {
    fun find(title: NonEmptyString, year: Year): Movie
    fun find(title: NonEmptyString): List<Movie>
    fun save(movie: Movie): Boolean
}