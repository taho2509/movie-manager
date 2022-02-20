package com.hobbie.movie.manager.interfaces.services

data class OMDbSearchResult(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Type: String,
    val Poster: String
)
