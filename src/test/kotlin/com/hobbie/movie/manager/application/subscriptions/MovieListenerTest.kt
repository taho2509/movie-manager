package com.hobbie.movie.manager.application.subscriptions

import com.hobbie.movie.manager.application.controllers.MovieControllerTest
import io.nats.streaming.MessageHandler
import io.nats.streaming.Options
import io.nats.streaming.StreamingConnectionFactory
import io.nats.streaming.SubscriptionOptions
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.get
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
internal class MovieListenerTest {

    @Autowired
    lateinit var mockApp: MockMvc

    private val serverClient = MockServerClient(servicecontainer.getHost(), servicecontainer.getServerPort())

    companion object {
        @Container
        var natsStreaming = GenericContainer(DockerImageName.parse("nats-streaming:latest")).apply {
            withExposedPorts(4222)
            setWaitStrategy(LogMessageWaitStrategy().withRegEx(".*Server is ready.*"))
            start()
        }

        @Container
        val dbcontainer = MongoDBContainer("mongo:4.0.2").apply {
            withExposedPorts(27017)
            setWaitStrategy(Wait.forListeningPort())
            start()
        }

        @Container
        val servicecontainer = MockServerContainer(DockerImageName.parse("mockserver/mockserver:mockserver-5.13.2"))


        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.host", dbcontainer::getHost)
            registry.add("spring.data.mongodb.port", dbcontainer::getFirstMappedPort)
            registry.add("omdb.url", servicecontainer::getEndpoint)
            registry.add("nats.url", Supplier { "nats://${natsStreaming.getHost()}:${natsStreaming.getFirstMappedPort()}" })
            registry.add("omdb.apiKey", Supplier { "123456" })
            registry.add("nats.cluster", Supplier { "test-cluster" })
        }
    }

    private val lock = CountDownLatch(1)

    @Test
    fun `should listen for movies and add to mongo`() {
        // given
        serverClient
            .`when`(
                HttpRequest.request()
                    .withMethod("get")
                    .withQueryStringParameter("t", "Harry Potter and the Sorcerer's Stone")
            )
            .respond(
                HttpResponse.response()
                    .withBody(
                        "{\"Title\":\"Harry Potter and the Sorcerer's Stone\",\"Year\":\"2001\",\"Rated\":\"PG\",\"Released\":\"16 Nov 2001\",\"Runtime\":\"152 min\",\"Genre\":\"Adventure, Family, Fantasy\",\"Director\":\"Chris Columbus\",\"Writer\":\"J.K. Rowling, Steve Kloves\",\"Actors\":\"Daniel Radcliffe, Rupert Grint, Richard Harris\",\"Plot\":\"An orphaned boy enrolls in a school of wizardry, where he learns the truth about himself, his family and the terrible evil that haunts the magical world.\",\"Language\":\"English\",\"Country\":\"United Kingdom\",\"Awards\":\"Nominated for 3 Oscars. 17 wins & 68 nominations total\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BNjQ3NWNlNmQtMTE5ZS00MDdmLTlkZjUtZTBlM2UxMGFiMTU3XkEyXkFqcGdeQXVyNjUwNzk3NDc@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.6/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"81%\"},{\"Source\":\"Metacritic\",\"Value\":\"65/100\"}],\"Metascore\":\"65\",\"imdbRating\":\"7.6\",\"imdbVotes\":\"734,590\",\"imdbID\":\"tt0241527\",\"Type\":\"movie\",\"DVD\":\"28 May 2002\",\"BoxOffice\":\"\$318,087,620\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}"
                    )
            )

        serverClient
            .`when`(
                HttpRequest.request()
                    .withMethod("get")
                    .withQueryStringParameter("s", "Harry Potter")
                    .withQueryStringParameter("type", "movie")
            )
            .respond(
                HttpResponse.response()
                    .withBody("{\"Search\":[{\"Title\":\"Harry Potter and the Sorcerer's Stone\",\"Year\":\"2001\",\"imdbID\":\"tt0241527\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BNjQ3NWNlNmQtMTE5ZS00MDdmLTlkZjUtZTBlM2UxMGFiMTU3XkEyXkFqcGdeQXVyNjUwNzk3NDc@._V1_SX300.jpg\"},{\"Title\":\"Harry Potter and the Chamber of Secrets\",\"Year\":\"2002\",\"imdbID\":\"tt0295297\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMTcxODgwMDkxNV5BMl5BanBnXkFtZTYwMDk2MDg3._V1_SX300.jpg\"}],\"totalResults\":\"2\",\"Response\":\"True\"}")
            )


        // when/then
        val cf = StreamingConnectionFactory(
            Options
                .Builder()
                .clientId("test-sender")
                .clusterId("test-cluster")
                .natsUrl(Supplier { "nats://${MovieListenerTest.natsStreaming.getHost()}:${MovieListenerTest.natsStreaming.getFirstMappedPort()}" }.get())
                .build()
        )

        val sc = cf.createConnection()

        sc.publish("movie_viewed", "{\"id\":\"f98a4493-c117-452e-a08a-f1c18e6694b7\",\"source\":\"/movie-reader-csv\",\"specversion\":\"1.0\",\"type\":\"com.movie-reader.movie.viewed\",\"datacontenttype\":\"application/json\",\"subject\":\"movie_viewed\",\"time\":\"2022-02-15T23:11:36-03:00\",\"data\":{\"title\":\"Harry Potter and the Sorcerer's Stone\"}}".toByteArray());

        lateinit var ans: MvcResult
        sc.subscribe("movie_saved", MessageHandler {
            ans = mockApp.get("/api/v1/movie?name=Harry Potter")
                .andReturn()
            lock.countDown()
        }, SubscriptionOptions.Builder().deliverAllAvailable().build())

        lock.await(2000, TimeUnit.MILLISECONDS);
        Assertions.assertThat(ans.response.getContentAsString()).isEqualTo("[{\"title\":{\"value\":\"Harry Potter and the Sorcerer's Stone\"},\"year\":{\"value\":2001},\"poster\":{\"url\":{\"value\":\"https://m.media-amazon.com/images/M/MV5BNjQ3NWNlNmQtMTE5ZS00MDdmLTlkZjUtZTBlM2UxMGFiMTU3XkEyXkFqcGdeQXVyNjUwNzk3NDc@._V1_SX300.jpg\"},\"artist\":null},\"runtime\":{\"value\":152},\"genre\":[\"Adventure\",\"Family\",\"Fantasy\"],\"directors\":[{\"name\":{\"value\":\"Chris\"},\"lastNames\":{\"value\":\"Columbus\"}}],\"writers\":[{\"name\":{\"value\":\"J.K.\"},\"lastNames\":{\"value\":\"Rowling\"}},{\"name\":{\"value\":\"Steve\"},\"lastNames\":{\"value\":\"Kloves\"}}],\"actors\":[{\"name\":{\"value\":\"Daniel\"},\"lastNames\":{\"value\":\"Radcliffe\"}},{\"name\":{\"value\":\"Rupert\"},\"lastNames\":{\"value\":\"Grint\"}},{\"name\":{\"value\":\"Richard\"},\"lastNames\":{\"value\":\"Harris\"}}],\"released\":{\"value\":2001},\"plot\":{\"value\":\"An orphaned boy enrolls in a school of wizardry, where he learns the truth about himself, his family and the terrible evil that haunts the magical world.\"},\"language\":\"ENGLISH\",\"country\":{\"name\":{\"value\":\"United Kingdom\"},\"code\":null},\"awards\":[{\"value\":\"Nominated for 3 Oscars\"},{\"value\":\"17 wins & 68 nominations total\"}],\"production\":{\"value\":\"N/A\"}},{\"title\":{\"value\":\"Harry Potter and the Chamber of Secrets\"},\"year\":{\"value\":2002},\"poster\":{\"url\":{\"value\":\"https://m.media-amazon.com/images/M/MV5BMTcxODgwMDkxNV5BMl5BanBnXkFtZTYwMDk2MDg3._V1_SX300.jpg\"},\"artist\":null},\"runtime\":null,\"genre\":null,\"directors\":null,\"writers\":null,\"actors\":null,\"released\":null,\"plot\":null,\"language\":null,\"country\":null,\"awards\":null,\"production\":null}]")
    }
}