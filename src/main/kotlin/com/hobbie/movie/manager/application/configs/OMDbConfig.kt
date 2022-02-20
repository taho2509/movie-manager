package com.hobbie.movie.manager.application.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("omdb")
data class OMDbConfig(
    val url: String,
    val apiKey: String,
)
