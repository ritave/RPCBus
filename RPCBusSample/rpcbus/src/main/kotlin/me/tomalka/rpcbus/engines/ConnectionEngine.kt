package me.tomalka.rpcbus.engines

import me.tomalka.rpcbus.LocalDispatcher

interface ConnectionEngine {
    fun install(localDispatcher: LocalDispatcher)
    fun destroy()

    fun onPost(event: Any)
}