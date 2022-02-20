package com.hobbie.movie.manager.domain.use_cases

import com.hobbie.movie.manager.domain.entities.*
import com.hobbie.movie.manager.domain.interfaces.MovieRepository
import com.hobbie.movie.manager.domain.interfaces.MovieService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class AddNewMovieTest {
    private val movieProvider: MovieService = mockk()
    private val movieRepository: MovieRepository = mockk(relaxed = true)
    private val useCase = AddNewMovie(movieProvider, movieRepository)

    @Test
    fun `should get the movie and save it`() {
        // given
        val title = NonEmptyString("Title")
        val year = Year(2000)
        val movie = Movie(
            title,
            year,
            Poster(NonEmptyString("https://url.com"),null),
            Minutes(124),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
        )
        every { movieProvider.find(title,year) } returns movie

        // when
        useCase.execute(title, year)
        
        // then
        verify { movieProvider.find(title, year) }
        verify { movieRepository.save(movie) }
    }
}