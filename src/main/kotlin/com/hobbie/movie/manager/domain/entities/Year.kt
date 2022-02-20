package com.hobbie.movie.manager.domain.entities

data class Year(val value: Short) {
    init {
        require(value in 0..10000) { "Invalid year" }
    }
}
