package com.hobbie.movie.manager.interfaces.services.transformers

import com.hobbie.movie.manager.utils.FakerWrapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class MovieTransformerTest{
    private val fake = FakerWrapper()
    private val transformer = MovieTransformer()

    @Test
    fun `should transform from omdb to movie`() {
        val movie = fake.createFakeOMDBMovie()
        // when
        val  result = transformer.toMovie(movie)

        // then
        Assertions.assertThat(result.title.value).isEqualTo(movie.Title)
        Assertions.assertThat(result.year.value).isEqualTo(movie.Year.toShort())
        Assertions.assertThat(result.country?.name?.value).isEqualTo(movie.Country)
        Assertions.assertThat(result.genre?.size).isEqualTo(movie.Genre?.split(", ")?.size)
        Assertions.assertThat(result.poster.url.value).isEqualTo(movie.Poster)
        Assertions.assertThat(result.actors?.size).isEqualTo(movie.Actors?.split(", ")?.size)
        Assertions.assertThat(result.directors?.size).isEqualTo(movie.Director?.split(", ")?.size)
        Assertions.assertThat(result.writers?.size).isEqualTo(movie.Writer?.split(", ")?.size)
        Assertions.assertThat(result.awards?.size).isEqualTo(movie.Awards?.split(". ")?.size)
        Assertions.assertThat(result.language.toString().lowercase()).isEqualTo(movie.Language?.lowercase())
        Assertions.assertThat(result.plot?.value).isEqualTo(movie.Plot)
        Assertions.assertThat(result.production?.value).isEqualTo(movie.Production)
        Assertions.assertThat(movie.Released.toString().contains(result.released?.value.toString())).isEqualTo(true)
        Assertions.assertThat(result.runtime?.value).isEqualTo(movie.Runtime.split(" ")[0].toShort())
    }

    @Test
    fun `should transform from omdb to movie list`() {
        val search = fake.createFakeOMDBSearch()
        // when
        val  result = transformer.toMovieList(search)

        // then
        Assertions.assertThat(result.size).isEqualTo(search.Search.size)
        Assertions.assertThat(result[0].title.value).isEqualTo(search.Search[0].Title)
        Assertions.assertThat(result[0].year.value).isEqualTo(search.Search[0].Year.toShort())
        Assertions.assertThat(result[0].poster.url.value).isEqualTo(search.Search[0].Poster)
    }
}