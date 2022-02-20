package com.hobbie.movie.manager.domain.entities

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class YearTest {
    @Test
    fun `should create a valid year`() {
        // when
        val year = Year(2000)

        // then
        Assertions.assertThat(year.value).isEqualTo(2000)
    }

    @Test
    fun `should throw on negative value`() {
        // when
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Year(-10)
        }

        // then
        Assertions.assertThat(exception.message).isEqualTo("Invalid year")
    }

    @Test
    fun `should throw on high value`() {
        // when
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Year(10001)
        }

        // then
        Assertions.assertThat(exception.message).isEqualTo("Invalid year")
    }
}