package com.hobbie.movie.manager.application.controllers

import com.hobbie.movie.manager.application.common.LogProducer
import com.hobbie.movie.manager.application.common.logger
import com.hobbie.movie.manager.application.inputs.MovieInput
import com.hobbie.movie.manager.domain.entities.*
import com.hobbie.movie.manager.domain.use_cases.AddNewMovie
import com.hobbie.movie.manager.domain.use_cases.SearchMovie
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/movie")
class MovieController(private val saveUseCase: AddNewMovie, private val searchUseCase: SearchMovie) {
    @GetMapping()
    fun searchMovie(@RequestParam("name") name: String): List<Movie> {
        return searchUseCase.execute(NonEmptyString(name))
    }

    @PostMapping()
    fun saveMovie(@RequestBody movie: MovieInput): Movie {
        return saveUseCase.execute(NonEmptyString(movie.title), movie.year?.let { Year(movie.year) } ?: null )
    }
}