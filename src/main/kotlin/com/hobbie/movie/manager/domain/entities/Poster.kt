package com.hobbie.movie.manager.domain.entities

data class Poster(val url: NonEmptyString, val artist: NonEmptyString?) {
    init {
        require(url.value.startsWith("https://")) { "URL is malformed" }
    }
}
