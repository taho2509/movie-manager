package com.hobbie.movie.manager.application.inputs

data class MovieViewedEvent(
    val id: String,
    val source: String,
    val specversion: String,
    val type: String,
    val datacontenttype: String,
    val subject: String,
    val time: String,
    val data: MovieInput,
)
