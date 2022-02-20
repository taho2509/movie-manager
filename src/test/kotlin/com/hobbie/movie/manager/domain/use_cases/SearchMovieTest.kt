package com.hobbie.movie.manager.domain.use_cases

import com.hobbie.movie.manager.domain.entities.*
import com.hobbie.movie.manager.domain.interfaces.MovieRepository
import com.hobbie.movie.manager.domain.interfaces.MovieService
import io.github.serpro69.kfaker.Faker
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class SearchMovieTest {
    private val movieProvider: MovieService = mockk()
    private val movieRepository: MovieRepository = mockk(relaxed = true)
    private val useCase = SearchMovie(movieProvider, movieRepository)
    private val faker = Faker()
    private val movies = List(5) { createFakeMovie() }

    private fun createFakeMovie(): Movie {
        return faker.randomProvider.randomClassInstance {
            typeGenerator { Year(faker.random.nextInt(intRange = 1..10000).toShort()) }
            typeGenerator { Poster(NonEmptyString("https://" + faker.internet.domain()),null) }
            typeGenerator { Minutes(faker.random.nextInt(intRange = 1..200).toShort()) }
            typeGenerator { listOf(Genre.Comedy) }
            typeGenerator { Country(NonEmptyString("Andorra"),null) }
        }
    }

    @Test
    fun `should join local and external results`() {
        // given
        every { movieProvider.find(any()) }.returns(movies.subList(2,5))
        every { movieRepository.find(any()) }.returns(movies.subList(0,2))

        // when
        val result = useCase.execute(NonEmptyString("x"))

        // then
        Assertions.assertThat(result.size).isEqualTo(movies.size)
        Assertions.assertThat(result).isEqualTo(movies)
    }

    @Test
    fun `should filter out duplicates from external`() {
        // given
        every { movieProvider.find(any()) }.returns(movies.subList(2,5) + movies.subList(2,5))
        every { movieRepository.find(any()) }.returns(movies.subList(0,2))

        // when
        val result = useCase.execute(NonEmptyString("x"))

        // then
        Assertions.assertThat(result.size).isEqualTo(movies.size)
        Assertions.assertThat(result).isEqualTo(movies)
    }

    @Test
    fun `should filter out from external overlapping movies with local`() {
        // given
        every { movieProvider.find(any()) }.returns(movies.subList(2,5))
        every { movieRepository.find(any()) }.returns(movies.subList(0,3))

        // when
        val result = useCase.execute(NonEmptyString("x"))

        // then
        Assertions.assertThat(result.size).isEqualTo(movies.size)
        Assertions.assertThat(result).isEqualTo(movies)
    }
}