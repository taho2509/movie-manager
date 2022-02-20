package com.hobbie.movie.manager.application.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("nats")
data class NatsConfig(
    val cluster: String,
    val url: String,
    val channel: String,
)
