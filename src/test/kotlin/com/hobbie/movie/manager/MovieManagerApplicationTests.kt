package com.hobbie.movie.manager

import com.hobbie.movie.manager.application.controllers.MovieControllerTest
import com.hobbie.movie.manager.application.subscriptions.MovieListenerTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import java.util.function.Supplier

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
			registry.add("spring.data.mongodb.host", Supplier { "localhost" })
			registry.add("spring.data.mongodb.port", Supplier { "27017" })
			registry.add("omdb.apiKey", Supplier { "123456" })
			registry.add("nats.cluster", Supplier { "test-cluster" })
			registry.add("nats.url", { "nats://${natsStreaming.getHost()}:${natsStreaming.getFirstMappedPort()}" })
		}
	}

	@Test
	fun contextLoads() {
	}

}
