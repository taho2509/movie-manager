package com.hobbie.movie.manager.interfaces.services.transformers

import com.hobbie.movie.manager.domain.entities.*
import com.hobbie.movie.manager.interfaces.services.OMDbMovie
import com.hobbie.movie.manager.interfaces.services.OMDbSearch
import org.springframework.stereotype.Service

@Service
class MovieTransformer {
    fun toMovie(input: OMDbMovie): Movie {
        return Movie(
            NonEmptyString(input.Title),
            Year(input.Year.toShort()),
            Poster(NonEmptyString(if(input.Poster != "N/A") input.Poster else "https://placekitten.com/360/640"), null),
            Minutes(input.Runtime.split(" ")[0].toShort()),
            input.Genre
                ?.split(", ")
                ?.map {
                    Genre.valueOf(
                        it.replace(" ", "")
                    )
                },
            input.Director
                ?.split(", ")
                ?.map { it ->
                    val names = it.split(" ")
                    Names(NonEmptyString(names[0]), NonEmptyString(names[1]))
                },
            input.Writer
                ?.split(", ")
                ?.map { it ->
                    val names = it.split(" ")
                    Names(NonEmptyString(names[0]), NonEmptyString(names[1]))
                },
            input.Actors
                ?.split(", ")
                ?.map { it ->
                    val names = it.split(" ")
                    Names(NonEmptyString(names[0]), NonEmptyString(names[1]))
                },
            input.Released
                ?.split(" ")
                ?.get(2)
                ?.let { Year(it.toShort()) },
            input.Plot?.let { NonEmptyString(it) } ?: null,
            input.Language?.let { Language.valueOf(it.uppercase()) } ?: Language.ENGLISH,
            Country(input.Country?.let { NonEmptyString(it) } ?: NonEmptyString(("United States")), null),
            input.Awards
                ?.split(". ")
                ?.map { NonEmptyString(it) },
            input.Production?.let { NonEmptyString(it) }
        )
    }

    fun toMovieList(input: OMDbSearch): List<Movie> {
        return input.Search.map { it -> Movie(
            NonEmptyString(it.Title),
            Year(it.Year.toShort()),
            Poster(NonEmptyString(if(it.Poster != "N/A") it.Poster else "https://placekitten.com/360/640"), null),
            null,
           null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
        ) }
    }
}