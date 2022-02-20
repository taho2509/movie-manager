package com.hobbie.movie.manager.application.configs

import com.hobbie.movie.manager.application.interceptors.Authorization
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class InterceptorsConfig:WebMvcConfigurer {
    @Autowired
    private val authorization: Authorization? = null

    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)
        if (authorization != null) {
            registry.addInterceptor(authorization)
        }
    }
}