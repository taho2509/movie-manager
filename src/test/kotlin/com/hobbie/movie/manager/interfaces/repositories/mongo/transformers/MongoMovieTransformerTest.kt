package com.hobbie.movie.manager.interfaces.repositories.mongo.transformers

import com.hobbie.movie.manager.interfaces.repositories.mongo.MovieDocument
import com.hobbie.movie.manager.utils.FakerWrapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class MongoMovieTransformerTest {
    private val transformer = MongoMovieTransformer()
    private val fake = FakerWrapper()

    @Test
    fun `should create a MovieDocument from a Movie`() {
        // when
        val movie = fake.createFakeMovie()
        val result = transformer.toMongoDocument(movie)

        // then
        Assertions.assertThat(result.title).isEqualTo(movie.title.value)
        Assertions.assertThat(result.year).isEqualTo(movie.year.value)
        Assertions.assertThat(result.country).isEqualTo(movie.country?.name?.value)
        Assertions.assertThat(result.genres?.size).isEqualTo(movie.genre?.size)
        Assertions.assertThat(result.poster).isEqualTo(movie.poster.url.value)
        Assertions.assertThat(result.actors?.size).isEqualTo(movie.actors?.size)
        Assertions.assertThat(result.writers?.size).isEqualTo(movie.writers?.size)
        Assertions.assertThat(result.directors?.size).isEqualTo(movie.directors?.size)
        Assertions.assertThat(result.awards?.size).isEqualTo(movie.awards?.size)
        Assertions.assertThat(result.language).isEqualTo(movie.language.toString())
        Assertions.assertThat(result.plot).isEqualTo(movie.plot?.value)
        Assertions.assertThat(result.production).isEqualTo(movie.production?.value)
        Assertions.assertThat(result.released).isEqualTo(movie.released?.value)
        Assertions.assertThat(result.runtime).isEqualTo(movie.runtime?.value)
    }

    @Test
    fun `should create a Movie from a MovieDocument`() {
        // when
        val movie = fake.createFakeMovieDocument()
        val result = transformer.toMovie(movie)

        // then
        Assertions.assertThat(result.title.value).isEqualTo(movie.title)
        Assertions.assertThat(result.year.value).isEqualTo(movie.year)
        Assertions.assertThat(result.country?.name?.value).isEqualTo(movie.country)
        Assertions.assertThat(result.genre?.size).isEqualTo(movie.genres?.size)
        Assertions.assertThat(result.poster.url.value).isEqualTo(movie.poster)
        Assertions.assertThat(result.actors?.size).isEqualTo(movie.actors?.size)
        Assertions.assertThat(result.directors?.size).isEqualTo(movie.directors?.size)
        Assertions.assertThat(result.writers?.size).isEqualTo(movie.writers?.size)
        Assertions.assertThat(result.awards?.size).isEqualTo(movie.awards?.size)
        Assertions.assertThat(result.language.toString().lowercase()).isEqualTo(movie.language?.lowercase())
        Assertions.assertThat(result.plot?.value).isEqualTo(movie.plot)
        Assertions.assertThat(result.production?.value).isEqualTo(movie.production)
        Assertions.assertThat(result.released?.value).isEqualTo(movie.released)
        Assertions.assertThat(result.runtime?.value).isEqualTo(movie.runtime)
    }
}