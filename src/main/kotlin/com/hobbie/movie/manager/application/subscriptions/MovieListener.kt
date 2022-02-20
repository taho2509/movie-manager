package com.hobbie.movie.manager.application.subscriptions

import com.google.gson.Gson
import com.hobbie.movie.manager.application.configs.NatsConfig
import com.hobbie.movie.manager.application.inputs.MovieViewedEvent
import com.hobbie.movie.manager.domain.entities.NonEmptyString
import com.hobbie.movie.manager.domain.use_cases.AddNewMovie
import io.nats.streaming.*
import org.springframework.stereotype.Service


@Service
class MovieListener(private val config: NatsConfig, private val saveUseCase: AddNewMovie, private val gson: Gson) :
    MessageHandler {
    lateinit var sc: StreamingConnection
    init {
        val cf = StreamingConnectionFactory(
            Options
                .Builder()
                .clientId("manager")
                .clusterId(config.cluster)
                .natsUrl(config.url)
                .build()
        )

        sc = cf.createConnection()

        sc.subscribe(config.channel, this, SubscriptionOptions.Builder().deliverAllAvailable().build())
    }

    override fun onMessage(msg: Message) {
        try {
            val event: MovieViewedEvent = gson.fromJson(String(msg.data), MovieViewedEvent::class.java)

            val response = this.saveUseCase.execute(NonEmptyString(event.data.title), null)

            sc.publish("movie_saved", response.toString().toByteArray());
        } catch (ex: Exception) {
            when(ex) {
                is IllegalAccessException, is IndexOutOfBoundsException -> {
                    println(ex)
                }
                else -> throw ex
            }
        }


    }


}