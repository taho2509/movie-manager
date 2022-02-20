package com.hobbie.movie.manager.interfaces.services

data class OMDbMovie(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Type: String,
    val Poster: String,
    val Runtime: String,
    val Rated: String?,
    val Released: String?,
    val Genre: String?,
    val Director: String?,
    val Writer: String?,
    val Actors: String?,
    val Plot: String?,
    val Language: String?,
    val Country: String?,
    val Awards: String?,
    val Metascore: String?,
    val imdbRating: String?,
    val imdbVotes: String?,
    val DVD: String?,
    val BoxOffice: String?,
    val Production: String?,
    val Website: String?,
    val Response: String
)
