package com.hobbie.movie.manager.interfaces.repositories.mongo

data class MovieDocument(
    val title: String,
    val year: Short,
    val poster: String,
    val runtime: Short?,
    val genres: List<String>?,
    val directors: List<String>?,
    val writers: List<String>?,
    val actors: List<String>?,
    val released: Short?,
    val plot: String?,
    val language: String?,
    val country: String?,
    val awards: List<String>?,
    val production: String?,
)
