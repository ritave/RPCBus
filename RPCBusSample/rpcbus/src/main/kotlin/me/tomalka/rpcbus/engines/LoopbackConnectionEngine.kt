package me.tomalka.rpcbus.engines

import me.tomalka.rpcbus.LocalDispatcher

/**
 * A connection engine that posts all events only in current application
 */
class LoopbackConnectionEngine : ConnectionEngine {
    private lateinit var dispatcher: LocalDispatcher

    override fun install(localDispatcher: LocalDispatcher) {
        dispatcher = localDispatcher
    }

    override fun destroy() {
    }

    override fun onPost(event: Any) {
        dispatcher.dispatch(event)
    }
}