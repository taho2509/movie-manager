package com.hobbie.movie.manager.interfaces.services

import org.springframework.stereotype.Service
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")

@Service
class HttpBaseRequest {
    private val client = HttpClient.newBuilder().build()

    fun get(url: String, params: Map<String,String>): String {
        val urlParams = params.map {(k, v) -> "${(k.utf8())}=${v.utf8()}"}
            .joinToString("&")

        val request = HttpRequest.newBuilder()
            .uri(URI.create("${url}?${urlParams}"))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return if (response.statusCode() in 200..299) {
            response.body().toString()
        } else {
            return "" + response.statusCode()
        }
    }
}