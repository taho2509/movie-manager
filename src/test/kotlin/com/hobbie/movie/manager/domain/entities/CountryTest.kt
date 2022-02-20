package com.hobbie.movie.manager.domain.entities

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CountryTest {
    @Test
    fun `should create a valid country`() {
        // when
        val country = Country(NonEmptyString(("Cuba")),null)

        // then
        Assertions.assertThat(country.name.value).isEqualTo("Cuba")
    }

    @Test
    fun `should throw on invalid country`() {
        // when
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Country(NonEmptyString("Fantasiland"), null)
        }

        // then
        Assertions.assertThat(exception.message).isEqualTo("The country Fantasiland is invalid")
    }
}