package com.hobbie.movie.manager.interfaces.repositories

import com.hobbie.movie.manager.domain.entities.*
import com.hobbie.movie.manager.interfaces.repositories.mongo.MongoMovieRepository
import com.hobbie.movie.manager.interfaces.repositories.mongo.MovieDocument
import com.hobbie.movie.manager.interfaces.repositories.mongo.transformers.MongoMovieTransformer
import com.hobbie.movie.manager.utils.FakerWrapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class MongoRepositoryWrapperTest {
    private val fake = FakerWrapper()
    private val repository: MongoMovieRepository = mockk()
    private val transformer: MongoMovieTransformer = mockk()
    private val wrapper = MongoRepositoryWrapper(repository, transformer)

    @Test
    fun `should save transformed movie`() {
        // given
        val movie = fake.createFakeMovie()
        val movieDocument: MovieDocument = fake.randomClass()
        every { transformer.toMongoDocument(movie) } returns movieDocument
        every { repository.save(movieDocument) } returns movieDocument

        // when
        val response = wrapper.save(movie)

        // then
        Assertions.assertThat(response).isTrue
        verify {
            repository.save(movieDocument)
        }
    }

    @Test
    fun `should find movie document and return Movie instance`() {
        // given
        val movie = fake.createFakeMovie()
        val movieDocument: MovieDocument = fake.randomClass()
        every { repository.findOneByTitleAndYear("Title", 2000) } returns movieDocument
        every { transformer.toMovie(movieDocument) } returns movie

        // when
        val response = wrapper.find(NonEmptyString("Title"), Year(2000))

        // then
        Assertions.assertThat(response).isEqualTo(movie)
        verify {
            repository.findOneByTitleAndYear("Title", 2000)
        }
    }

    @Test
    fun `should find list of movie document matching title and return list of movie`() {
        // given
        val movie = fake.createFakeMovie()
        every { transformer.toMovie(any()) } returns movie
        every { repository.findByTitleLike("Title") } returns List(3) { fake.randomClass() }

        // when
        val result = wrapper.find(NonEmptyString("Title"))

        // then
        Assertions.assertThat(result.size).isEqualTo(3)
        Assertions.assertThat(result).isEqualTo(List(3) {movie})
        verify { repository.findByTitleLike("Title") }
    }
}