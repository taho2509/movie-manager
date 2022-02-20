package com.hobbie.movie.manager.application.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

interface LogProducer

inline fun <reified T : LogProducer> T.logger(): Logger =
    LoggerFactory.getLogger(getClassForLogging(T::class.java).name + " w/interface")

fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> {
    return javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
}