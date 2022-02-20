package com.hobbie.movie.manager.interfaces.services

import com.hobbie.movie.manager.domain.entities.*
import com.hobbie.movie.manager.domain.interfaces.MovieService
import org.springframework.stereotype.Service
import com.google.gson.Gson
import com.hobbie.movie.manager.application.configs.OMDbConfig
import com.hobbie.movie.manager.interfaces.services.transformers.MovieTransformer

@Service
class OMDbService(
    val request: HttpBaseRequest,
    val gson: Gson,
    val transformer: MovieTransformer,
    val config: OMDbConfig,
) : MovieService {

    override fun find(title: NonEmptyString, year: Year?): Movie {
        val params = mapOf("t" to title.value, "apiKey" to config.apiKey)

        val response = request.get(config.url, year?.value?.let { params + ("y" to it.toString()) } ?: params)

        val movieOMDb: OMDbMovie = gson.fromJson(response, OMDbMovie::class.java)

        return transformer.toMovie(movieOMDb)
    }

    override fun find(title: NonEmptyString): List<Movie> {
        val params = mapOf("s" to title.value, "apiKey" to config.apiKey, "type" to "movie")

        val response = request.get(config.url, params)

        val moviesOMDb: OMDbSearch = gson.fromJson(response, OMDbSearch::class.java)

        return transformer.toMovieList(moviesOMDb)
    }
}