package me.tomalka.rpcbus.posters

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Always posts in a new thread, useful for really long running events, such as network IO
 */
class NewThreadPoster(private val executorService: ExecutorService = Executors.newCachedThreadPool()) : Poster {
    companion object {
        const val POSTER_ID = 1
    }

    override val posterId: Int
        get() = POSTER_ID

    override fun enqueue(callback: () -> Unit) {
        executorService.execute({ callback() })
    }

    override fun destroy() {
        executorService.shutdown()
    }
}