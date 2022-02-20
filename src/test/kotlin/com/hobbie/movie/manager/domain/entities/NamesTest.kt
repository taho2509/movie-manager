package com.hobbie.movie.manager.domain.entities

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class NamesTest {
    @Test
    fun `should create a valid names object`() {
        // when
        val names = Names(NonEmptyString("John"),NonEmptyString("Doe"))

        // then
        Assertions.assertThat(names.name.value).isEqualTo("John")
        Assertions.assertThat(names.lastNames.value).isEqualTo("Doe")
    }
}