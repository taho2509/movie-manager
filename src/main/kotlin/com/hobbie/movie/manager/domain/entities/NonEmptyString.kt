package com.hobbie.movie.manager.domain.entities

data class NonEmptyString(val value: String) {
    init {
        require(value.isNotBlank()) { "String can't be empty" }
    }
}