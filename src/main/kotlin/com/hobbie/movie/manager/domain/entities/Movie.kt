package com.hobbie.movie.manager.domain.entities

data class Movie(
    val title: NonEmptyString,
    val year: Year,
    val poster: Poster,
    val runtime: Minutes?,
    val genre: List<Genre>?,
    val directors: List<Names>?,
    val writers: List<Names>?,
    val actors: List<Names>?,
    val released: Year?,
    val plot: NonEmptyString?,
    val language: Language?,
    val country: Country?,
    val awards: List<NonEmptyString>?,
    val production: NonEmptyString?,
) {
    override infix fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Movie

        return title == other.title && year == other.year
    }
}
