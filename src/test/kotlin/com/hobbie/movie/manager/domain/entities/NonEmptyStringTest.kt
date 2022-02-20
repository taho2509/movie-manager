package com.hobbie.movie.manager.domain.entities

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


internal class NonEmptyStringTest {
    @Test
    fun `should throw on empty string`() {
        // when
        val exception = assertThrows(IllegalArgumentException::class.java) {
            NonEmptyString("")
        }

        Assertions.assertThat(exception.message).isEqualTo("String can't be empty")
    }

    @Test
    fun `should get a valid string`() {
        // when
        val s = NonEmptyString("valid")

        // then
        Assertions.assertThat(s.value).isEqualTo("valid")
    }
}