package me.tomalka.rpcbus

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

data class SubscribeMethod(val method: KFunction<*>, val threadMode: Int, val eventType: KClass<*>)