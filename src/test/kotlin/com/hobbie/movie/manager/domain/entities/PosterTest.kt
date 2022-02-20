package com.hobbie.movie.manager.domain.entities

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PosterTest {
    @Test
    fun `should throw on invalid url`() {
        // when
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Poster(NonEmptyString("wring"), null)
        }

        // then
        Assertions.assertThat(exception.message).isEqualTo("URL is malformed")
    }

    @Test
    fun `should build a poster`() {
        // when
        val poster = Poster(NonEmptyString("https://url.com"), NonEmptyString(("artist")))

        // then
        Assertions.assertThat(poster.url.value).isEqualTo("https://url.com")
        Assertions.assertThat(poster.artist?.value).isEqualTo("artist")
    }
}