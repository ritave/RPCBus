import me.tomalka.rpcbus.LocalDispatcher
import me.tomalka.rpcbus.engines.ConnectionEngine
import me.tomalka.rpcbus.engines.LoopbackConnectionEngine
import me.tomalka.rpcbus.posters.InstantPoster
import me.tomalka.rpcbus.posters.NewThreadPoster
import me.tomalka.rpcbus.posters.Poster
import java.util.*
import java.util.concurrent.Executors

class RpcBus(
        private val connectionEngine: ConnectionEngine,
        private val localDispatcher: LocalDispatcher) {

    private var wasDestroyed = false

    fun register(listener: Any) {
        localDispatcher.register(listener)
    }

    fun unregister(listener: Any) {
        localDispatcher.unregister(listener)
    }

    fun post(event: Any) {
        connectionEngine.onPost(event)
    }

    /**
     * Useful in case when you want to terminate external connections
     */
    fun destroy() {
        if (wasDestroyed)
            throw AssertionError("Double RpcBus destroy")

        wasDestroyed = true
        localDispatcher.destroy()
        connectionEngine.destroy()
    }

    init {
        connectionEngine.install(localDispatcher)
    }

    class Builder @JvmOverloads constructor(private var connectionEngine: ConnectionEngine = LoopbackConnectionEngine()) {
        private val posters = HashMap<Int, Poster>()

        fun addPoster(poster: Poster) : Builder {
            if (poster.posterId in posters)
                throw AssertionError("Duplicate poster ids")

            posters.put(poster.posterId, poster)
            return this
        }

        fun build(): RpcBus {
            return RpcBus(connectionEngine, LocalDispatcher(posters))
        }

        init {
            addPoster(InstantPoster())
            addPoster(NewThreadPoster())
        }
    }
}