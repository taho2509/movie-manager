package com.hobbie.movie.manager.domain.entities

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MinutesTest {
    @Test
    fun `should create a minutes object`() {
        // when
        val minutes = Minutes(120)

        // then
        Assertions.assertThat(minutes.value).isEqualTo(120)
    }

    @Test
    fun `should throw on invalid input`() {
        // when
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Minutes(-10)
        }

        // then
        Assertions.assertThat(exception.message).isEqualTo("Minutes can't be negative")
    }
}