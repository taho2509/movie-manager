package com.hobbie.movie.manager.interfaces.repositories.mongo.transformers

import com.hobbie.movie.manager.domain.entities.*
import com.hobbie.movie.manager.interfaces.repositories.mongo.MovieDocument
import org.springframework.stereotype.Service

@Service
class MongoMovieTransformer {
    fun toMongoDocument(movie: Movie): MovieDocument {
        return MovieDocument(
            movie.title.value,
            movie.year.value,
            movie.poster.url.value,
            movie.runtime?.value,
            movie.genre?.map { it.toString() },
            movie.directors?.map { it.name.value + " " + it.lastNames.value },
            movie.writers?.map { it.name.value + " " + it.lastNames.value },
            movie.actors?.map { it.name.value + " " + it.lastNames.value },
            movie.released?.value,
            movie.plot?.value,
            movie.language?.toString(),
            movie.country?.name?.value,
            movie.awards?.map { it.value },
            movie.production?.value,
        )
    }

    fun toMovie(document: MovieDocument): Movie {
        return Movie(NonEmptyString(document.title),
            Year(document.year),
            Poster(NonEmptyString(document.poster), null),
            document.runtime?.let { Minutes(it) },
            document.genres?.map { Genre.valueOf(it) },
            document.directors?.map { it ->
                val names = it.split(" ")
                Names(NonEmptyString(names[0]), NonEmptyString(names[1]))
            },
            document.writers?.map { it ->
                val names = it.split(" ")
                Names(NonEmptyString(names[0]), NonEmptyString(names[1]))
            },
            document.actors?.map { it ->
                val names = it.split(" ")
                Names(NonEmptyString(names[0]), NonEmptyString(names[1]))
            },
            document.released?.let { Year(it) },
            document.plot?.let { NonEmptyString(it) },
            document.language?.let { Language.valueOf(it.uppercase()) } ?: Language.ENGLISH,
            Country(document.country?.let { NonEmptyString(it) } ?: NonEmptyString(("United States")), null),
            document.awards?.map { NonEmptyString(it) },
            document.production?.let { NonEmptyString(it) })
    }
}