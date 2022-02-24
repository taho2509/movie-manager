## Movie Manager
![build](https://github.com/taho2509/movie-manager/actions/workflows/build.yml/badge.svg)

Api to look for and save movies. My personal use cases are:

* look for a movie: given the title, search in both OMDb and local db, merge the results, exclude overlapping movies, keep always the local result.
* save a movie: given the title, search for the movie in OMDb and save all the data in local db.

Both functionalities are available as rest endpoints, and the saving is also available as a subscription to a nats queue, used 'batch' processing.
