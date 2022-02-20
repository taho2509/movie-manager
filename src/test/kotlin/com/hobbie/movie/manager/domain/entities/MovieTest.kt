package com.hobbie.movie.manager.domain.entities

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MovieTest {
    @Test
    fun `should create a valid movie`() {
        // given


        // when
        val movie = Movie(
            NonEmptyString("The Fart & the curious"),
            Year(2022),
            Poster(NonEmptyString("https://poster.com"),null),
            Minutes(120),
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
        )

        // then
        Assertions.assertThat(movie.title.value).isEqualTo("The Fart & the curious")
        Assertions.assertThat(movie.year.value).isEqualTo(2022)
        Assertions.assertThat(movie.poster.url.value).isEqualTo("https://poster.com")
        Assertions.assertThat(movie.runtime?.value).isEqualTo(120)
    }
}