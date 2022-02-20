package com.hobbie.movie.manager

import com.hobbie.movie.manager.application.controllers.MovieControllerTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@SpringBootTest
class MovieManagerApplicationTests {

	companion object {
		@Container
		var natsStreaming = GenericContainer(DockerImageName.parse("nats-streaming:latest")).apply {
			withExposedPorts(4222)
			setWaitStrategy(LogMessageWaitStrategy().withRegEx(".*Server is ready.*"))
			start()
		}

		@JvmStatic
		@DynamicPropertySource
		fun properties(registry: DynamicPropertyRegistry) {
			registry.add("nats.url", { "nats://${natsStreaming.getHost()}:${natsStreaming.getFirstMappedPort()}" })
		}
	}

	@Test
	fun contextLoads() {
	}

}
