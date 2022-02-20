package com.hobbie.movie.manager.interfaces.services

data class OMDbSearch(
    val Search: List<OMDbSearchResult>,
    val totalResults: Int,
    val Response: String
)
