package me.tomalka.rpcbus

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType

data class SubscribeMethod(val method: KFunction<*>, val threadMode: Int, val eventType: KType)