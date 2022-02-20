package com.hobbie.movie.manager.utils

import com.hobbie.movie.manager.domain.entities.*
import com.hobbie.movie.manager.interfaces.repositories.mongo.MovieDocument
import com.hobbie.movie.manager.interfaces.services.OMDbMovie
import com.hobbie.movie.manager.interfaces.services.OMDbSearch
import com.hobbie.movie.manager.interfaces.services.OMDbSearchResult
import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.fakerConfig

class FakerWrapper {
    private val faker = Faker(fakerConfig {
        randomSeed = 100
    })

    fun createFakeMovie(): Movie {
        return faker.randomProvider.randomClassInstance {
            typeGenerator { Year(faker.random.nextInt(intRange = 1..10000).toShort()) }
            typeGenerator { Poster(NonEmptyString("https://" + faker.internet.domain()), null) }
            typeGenerator { Minutes(faker.random.nextInt(intRange = 1..200).toShort()) }
            typeGenerator { Country(NonEmptyString("Andorra"), null) }
            typeGenerator { listOf(Names(NonEmptyString(faker.name.firstName()),NonEmptyString(faker.name.lastName()))) }
            namedParameterGenerator("title") { NonEmptyString(faker.movie.title()) }
            namedParameterGenerator("genre") { listOf(Genre.Comedy) }
            namedParameterGenerator("awards") { listOf(NonEmptyString(faker.quote.famousLastWords())) }
        }
    }

    fun createFakeMovieDocument(): MovieDocument {
        return MovieDocument(
            faker.movie.title(),
            faker.random.nextInt(intRange = 1000 .. 2000).toShort(),
            "https://" + faker.internet.domain(),
            faker.random.nextInt(intRange = 5 .. 300).toShort(),
            listOf("Drama"),
            listOf(faker.name.name(),faker.name.name()),
            listOf(faker.name.name(),faker.name.name()),
            listOf(faker.name.name(),faker.name.name()),
            faker.random.nextInt(intRange = 1000 .. 2000).toShort(),
            faker.quote.yoda(),
            "Spanish",
            faker.address.country(),
            listOf(faker.name.name(),faker.name.name()),
            faker.quote.mostInterestingManInTheWorld()
        )
    }

    fun createFakeOMDBMovie(): OMDbMovie {
        return OMDbMovie(
            faker.movie.title(),
            faker.random.nextInt(intRange = 1000 .. 2000).toString(),
            faker.idNumber.toString(),
            "movie",
            "https://" + faker.internet.domain(),
            faker.random.nextInt(intRange = 5 .. 300).toString() + " min",
            "PG-13",
            "09 Jul " + faker.random.nextInt(intRange = 1000 .. 2000).toString(),
            "Drama, Comedy",
            faker.name.firstName() + " " + faker.name.lastName(),
            faker.name.firstName() + " " + faker.name.lastName(),
            faker.name.firstName() + " " + faker.name.lastName(),
            faker.quote.yoda(),
            "Spanish",
            "Spain",
            faker.quote.mostInterestingManInTheWorld(),
            faker.quote.mostInterestingManInTheWorld(),
            faker.quote.mostInterestingManInTheWorld(),
            faker.quote.mostInterestingManInTheWorld(),
            faker.quote.mostInterestingManInTheWorld(),
            faker.quote.mostInterestingManInTheWorld(),
            faker.quote.mostInterestingManInTheWorld(),
            faker.quote.mostInterestingManInTheWorld(),
            "True"
        )
    }

    fun createFakeOMDBSearch(): OMDbSearch {
        return OMDbSearch(
            listOf(OMDbSearchResult(
                faker.movie.title(),
                faker.random.nextInt(intRange = 1000 .. 2000).toString(),
                faker.idNumber.toString(),
                "movie",
                "https://" + faker.internet.domain(),
            )),
            1,
            "True"
        )
    }

    inline fun <reified T> randomClass(): T {
        return Faker().randomProvider.randomClassInstance()
    }
}