package com.hobbie.movie.manager.domain.entities

data class Minutes(val value: Short) {
    init {
        require(value >= 0) { "Minutes can't be negative" }
    }
}
