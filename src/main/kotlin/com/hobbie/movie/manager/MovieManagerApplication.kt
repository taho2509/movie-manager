package com.hobbie.movie.manager

import com.hobbie.movie.manager.application.configs.NatsConfig
import com.hobbie.movie.manager.application.configs.OMDbConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(OMDbConfig::class, NatsConfig::class)
class MovieManagerApplication

fun main(args: Array<String>) {
	runApplication<MovieManagerApplication>(*args)
}
