package me.tomalka.rpcbus_android

import me.tomalka.rpcbus.engines.ConnectionEngine
import me.tomalka.rpcbus.engines.LoopbackConnectionEngine
import me.tomalka.rpcbus_android.posters.MainThreadPoster
import me.tomalka.rpcbus_android.posters.QueuePoster

@JvmOverloads
fun androidRpcBuilder(connectionEngine: ConnectionEngine = LoopbackConnectionEngine()) : RpcBus.Builder {
    return RpcBus
            .Builder(connectionEngine)
            .addPoster(QueuePoster())
            .addPoster(MainThreadPoster())
}

