package com.hobbie.movie.manager.application.interceptors

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class Authorization : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val headers = request.getHeader("Authorization")
        /*if(headers.isNullOrBlank()) {
            throw HttpClientErrorException.Unauthorized.create( HttpStatus.UNAUTHORIZED, "statusText",
                HttpHeaders(), "".toByteArray(), null)
        }*/
        return true
    }
}