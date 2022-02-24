package com.hobbie.movie.manager.application.controllers

import com.hobbie.movie.manager.application.subscriptions.MovieListenerTest
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import org.testcontainers.utility.DockerImageName
import java.util.function.Supplier

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
internal class MovieControllerTest {
    @Autowired
    lateinit var mockApp: MockMvc

    private val mapper = ObjectMapper()
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
        val servicecontainer = MockServerContainer(DockerImageName.parse("mockserver/mockserver:mockserver-5.12.0"))

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.host", dbcontainer::getHost)
            registry.add("spring.data.mongodb.port", dbcontainer::getFirstMappedPort)
            registry.add("omdb.url", servicecontainer::getEndpoint)
            registry.add("omdb.apiKey", Supplier { "123456" })
            registry.add("nats.url", Supplier { "nats://${natsStreaming.getHost()}:${natsStreaming.getFirstMappedPort()}" })
            registry.add("nats.cluster", Supplier { "test-cluster" })
        }
    }

    @Test
    fun `should search movies matching title in omdb`() {
        // given
        serverClient
            .`when`(
                HttpRequest.request()
                    .withMethod("get")
                    .withQueryStringParameter("s", "Star Wars")
                    .withQueryStringParameter("type", "movie")
            )
            .respond(
                HttpResponse.response()
                    .withBody("{\"Search\":[{\"Title\":\"Star Wars: The Magic & the Mystery\",\"Year\":\"1997\",\"imdbID\":\"tt0477094\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BNzFjZjg4MDktYzk5Zi00MjUwLWJmOGQtMTcyMTY3YjEyNjIzXkEyXkFqcGdeQXVyNzMxMzYyOTI@._V1_SX300.jpg\"}],\"totalResults\":\"1\",\"Response\":\"True\"}")
            )

        // when/then
        mockApp.get("/api/v1/movie?name=Star Wars")
            .andExpect {
                status { isOk() }
                content { string("[{\"title\":{\"value\":\"Star Wars: The Magic & the Mystery\"},\"year\":{\"value\":1997},\"poster\":{\"url\":{\"value\":\"https://m.media-amazon.com/images/M/MV5BNzFjZjg4MDktYzk5Zi00MjUwLWJmOGQtMTcyMTY3YjEyNjIzXkEyXkFqcGdeQXVyNzMxMzYyOTI@._V1_SX300.jpg\"},\"artist\":null},\"runtime\":null,\"genre\":null,\"directors\":null,\"writers\":null,\"actors\":null,\"released\":null,\"plot\":null,\"language\":null,\"country\":null,\"awards\":null,\"production\":null}]") }
            }

    }

    @Test
    fun `should add movie to mongo`() {
        // given
        serverClient
            .`when`(
                HttpRequest.request()
                    .withMethod("get")
                    .withQueryStringParameter("t", "Pirates of the Caribbean: The Curse of the Black Pearl")
                    .withQueryStringParameter("y", "2003")
            )
            .respond(
                HttpResponse.response()
                    .withBody(
                        "{\"Title\":\"Pirates of the Caribbean: The Curse of the Black Pearl\",\"Year\":\"2003\",\"Rated\":\"PG-13\",\"Released\":\"09 Jul 2003\",\"Runtime\":\"143 min\",\"Genre\":\"Action, Adventure, Fantasy\",\"Director\":\"Gore Verbinski\",\"Writer\":\"Ted Elliott, Terry Rossio, Stuart Beattie\",\"Actors\":\"Johnny Depp, Geoffrey Rush, Orlando Bloom\",\"Plot\":\"Blacksmith Will Turner teams up with eccentric pirate \\\"Captain\\\" Jack Sparrow to save his love, the governor's daughter, from Jack's former pirate allies, who are now undead.\",\"Language\":\"English\",\"Country\":\"United States\",\"Awards\":\"Nominated for 5 Oscars. 38 wins & 104 nominations total\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BNGYyZGM5MGMtYTY2Ni00M2Y1LWIzNjQtYWUzM2VlNGVhMDNhXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"8.0/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"79%\"},{\"Source\":\"Metacritic\",\"Value\":\"63/100\"}],\"Metascore\":\"63\",\"imdbRating\":\"8.0\",\"imdbVotes\":\"1,060,290\",\"imdbID\":\"tt0325980\",\"Type\":\"movie\",\"DVD\":\"25 Jan 2005\",\"BoxOffice\":\"\$305,413,918\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}"
                    )
            )

        serverClient
            .`when`(
                HttpRequest.request()
                    .withMethod("get")
                    .withQueryStringParameter("s", "Pirates")
                    .withQueryStringParameter("type", "movie")
            )
            .respond(
                HttpResponse.response()
                    .withBody("{\"Search\":[{\"Title\":\"Pirates of the Caribbean: The Curse of the Black Pearl\",\"Year\":\"2003\",\"imdbID\":\"tt0325980\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BNGYyZGM5MGMtYTY2Ni00M2Y1LWIzNjQtYWUzM2VlNGVhMDNhXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg\"},{\"Title\":\"Pirates of the Caribbean: At World's End\",\"Year\":\"2007\",\"imdbID\":\"tt0449088\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMjIyNjkxNzEyMl5BMl5BanBnXkFtZTYwMjc3MDE3._V1_SX300.jpg\"}],\"totalResults\":\"2\",\"Response\":\"True\"}")
            )


        // when/then
        mockApp.post("/api/v1/movie") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(
                mapOf(
                    "title" to "Pirates of the Caribbean: The Curse of the Black Pearl",
                    "year" to 2003
                )
            )
        }
            .andExpect {

                status { isOk() }
                content {
                    string(
                        "{\"title\":{\"value\":\"Pirates of the Caribbean: The Curse of the Black Pearl\"},\"year\":{\"value\":2003},\"poster\":{\"url\":{\"value\":\"https://m.media-amazon.com/images/M/MV5BNGYyZGM5MGMtYTY2Ni00M2Y1LWIzNjQtYWUzM2VlNGVhMDNhXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg\"},\"artist\":null},\"runtime\":{\"value\":143},\"genre\":[\"Action\",\"Adventure\",\"Fantasy\"],\"directors\":[{\"name\":{\"value\":\"Gore\"},\"lastNames\":{\"value\":\"Verbinski\"}}],\"writers\":[{\"name\":{\"value\":\"Ted\"},\"lastNames\":{\"value\":\"Elliott\"}},{\"name\":{\"value\":\"Terry\"},\"lastNames\":{\"value\":\"Rossio\"}},{\"name\":{\"value\":\"Stuart\"},\"lastNames\":{\"value\":\"Beattie\"}}],\"actors\":[{\"name\":{\"value\":\"Johnny\"},\"lastNames\":{\"value\":\"Depp\"}},{\"name\":{\"value\":\"Geoffrey\"},\"lastNames\":{\"value\":\"Rush\"}},{\"name\":{\"value\":\"Orlando\"},\"lastNames\":{\"value\":\"Bloom\"}}],\"released\":{\"value\":2003},\"plot\":{\"value\":\"Blacksmith Will Turner teams up with eccentric pirate \\\"Captain\\\" Jack Sparrow to save his love, the governor's daughter, from Jack's former pirate allies, who are now undead.\"},\"language\":\"ENGLISH\",\"country\":{\"name\":{\"value\":\"United States\"},\"code\":null},\"awards\":[{\"value\":\"Nominated for 5 Oscars\"},{\"value\":\"38 wins & 104 nominations total\"}],\"production\":{\"value\":\"N/A\"}}"
                    )
                }
            }

        mockApp.get("/api/v1/movie?name=Pirates")
            .andExpect {
                status { isOk() }
                content {
                    string(
                        "[{\"title\":{\"value\":\"Pirates of the Caribbean: The Curse of the Black Pearl\"},\"year\":{\"value\":2003},\"poster\":{\"url\":{\"value\":\"https://m.media-amazon.com/images/M/MV5BNGYyZGM5MGMtYTY2Ni00M2Y1LWIzNjQtYWUzM2VlNGVhMDNhXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg\"},\"artist\":null},\"runtime\":{\"value\":143},\"genre\":[\"Action\",\"Adventure\",\"Fantasy\"],\"directors\":[{\"name\":{\"value\":\"Gore\"},\"lastNames\":{\"value\":\"Verbinski\"}}],\"writers\":[{\"name\":{\"value\":\"Ted\"},\"lastNames\":{\"value\":\"Elliott\"}},{\"name\":{\"value\":\"Terry\"},\"lastNames\":{\"value\":\"Rossio\"}},{\"name\":{\"value\":\"Stuart\"},\"lastNames\":{\"value\":\"Beattie\"}}],\"actors\":[{\"name\":{\"value\":\"Johnny\"},\"lastNames\":{\"value\":\"Depp\"}},{\"name\":{\"value\":\"Geoffrey\"},\"lastNames\":{\"value\":\"Rush\"}},{\"name\":{\"value\":\"Orlando\"},\"lastNames\":{\"value\":\"Bloom\"}}],\"released\":{\"value\":2003},\"plot\":{\"value\":\"Blacksmith Will Turner teams up with eccentric pirate \\\"Captain\\\" Jack Sparrow to save his love, the governor's daughter, from Jack's former pirate allies, who are now undead.\"},\"language\":\"ENGLISH\",\"country\":{\"name\":{\"value\":\"United States\"},\"code\":null},\"awards\":[{\"value\":\"Nominated for 5 Oscars\"},{\"value\":\"38 wins & 104 nominations total\"}],\"production\":{\"value\":\"N/A\"}},{\"title\":{\"value\":\"Pirates of the Caribbean: At World's End\"},\"year\":{\"value\":2007},\"poster\":{\"url\":{\"value\":\"https://m.media-amazon.com/images/M/MV5BMjIyNjkxNzEyMl5BMl5BanBnXkFtZTYwMjc3MDE3._V1_SX300.jpg\"},\"artist\":null},\"runtime\":null,\"genre\":null,\"directors\":null,\"writers\":null,\"actors\":null,\"released\":null,\"plot\":null,\"language\":null,\"country\":null,\"awards\":null,\"production\":null}]"
                    )
                }
            }
    }
}