package com.hobbie.movie.manager.interfaces.services

import com.google.gson.Gson
import com.hobbie.movie.manager.application.configs.OMDbConfig
import com.hobbie.movie.manager.domain.entities.NonEmptyString
import com.hobbie.movie.manager.domain.entities.Year
import com.hobbie.movie.manager.interfaces.services.transformers.MovieTransformer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class OMDbServiceTest {
    private val httpBaseRequest: HttpBaseRequest = mockk()
    private val trasformer = MovieTransformer()

    private val omDbService = OMDbService(httpBaseRequest, Gson(),trasformer, OMDbConfig("url", "1234"))
    @Test
    fun `should get and transform a movie by title and year`() {
        // given
        every { httpBaseRequest.get("url", any()) } returns "{\"Title\":\"Pirates of the Caribbean: The Curse of the Black Pearl\",\"Year\":\"2003\",\"Rated\":\"PG-13\",\"Released\":\"09 Jul 2003\",\"Runtime\":\"143 min\",\"Genre\":\"Action, Adventure, Fantasy\",\"Director\":\"Gore Verbinski\",\"Writer\":\"Ted Elliott, Terry Rossio, Stuart Beattie\",\"Actors\":\"Johnny Depp, Geoffrey Rush, Orlando Bloom\",\"Plot\":\"Blacksmith Will Turner teams up with eccentric pirate \\\"Captain\\\" Jack Sparrow to save his love, the governor's daughter, from Jack's former pirate allies, who are now undead.\",\"Language\":\"English\",\"Country\":\"United States\",\"Awards\":\"Nominated for 5 Oscars. 38 wins & 104 nominations total\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BNGYyZGM5MGMtYTY2Ni00M2Y1LWIzNjQtYWUzM2VlNGVhMDNhXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"8.0/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"79%\"},{\"Source\":\"Metacritic\",\"Value\":\"63/100\"}],\"Metascore\":\"63\",\"imdbRating\":\"8.0\",\"imdbVotes\":\"1,060,290\",\"imdbID\":\"tt0325980\",\"Type\":\"movie\",\"DVD\":\"25 Jan 2005\",\"BoxOffice\":\"\$305,413,918\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}"

        // when
        val result = omDbService.find(NonEmptyString("Pirate"), Year(2000))

        // then
        Assertions.assertThat(result.title.value).isEqualTo("Pirates of the Caribbean: The Curse of the Black Pearl")
        verify { httpBaseRequest.get("url",mapOf("t" to "Pirate", "apiKey" to "1234", "y" to "2000")) }
    }
    
    @Test
    fun `should get and transform a movie list`() {
        // given
        every { httpBaseRequest.get("url", any()) } returns "{\"Search\":[{\"Title\":\"Pirates of the Caribbean: The Curse of the Black Pearl\",\"Year\":\"2003\",\"imdbID\":\"tt0325980\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BNGYyZGM5MGMtYTY2Ni00M2Y1LWIzNjQtYWUzM2VlNGVhMDNhXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg\"},{\"Title\":\"An Epic at Sea: The Making of 'Pirates of the Caribbean: The Curse of the Black Pearl'\",\"Year\":\"2003\",\"imdbID\":\"tt0395141\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMTM2OTEwNTExNF5BMl5BanBnXkFtZTcwNjM3OTMyMQ@@._V1_SX300.jpg\"},{\"Title\":\"Pirates of the Caribbean: The Curse of the Black Pearl\",\"Year\":\"2003\",\"imdbID\":\"tt5793632\",\"Type\":\"game\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BZjFkMTdiMDctMDZkMi00MWJkLTkzYzgtNGFiYjEyZjM0NDc2XkEyXkFqcGdeQXVyMTk5NDI0MA@@._V1_SX300.jpg\"},{\"Title\":\"Fly on the Set of 'Pirates of the Caribbean: The Curse of the Black Pearl'\",\"Year\":\"2003\",\"imdbID\":\"tt5954154\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMGE5OGQ2YWUtNmY5MC00YzhiLWJhMmItOWVjZWRjNTAxMGY5XkEyXkFqcGdeQXVyODE2NDgwMzM@._V1_SX300.jpg\"}],\"totalResults\":\"4\",\"Response\":\"True\"}"
        
        // when
        val result = omDbService.find(NonEmptyString("Pirates"))
        
        // then
        Assertions.assertThat(result.size).isEqualTo(4)
        verify { httpBaseRequest.get("url",mapOf("s" to "Pirates", "apiKey" to "1234", "type" to "movie")) }
    }
}