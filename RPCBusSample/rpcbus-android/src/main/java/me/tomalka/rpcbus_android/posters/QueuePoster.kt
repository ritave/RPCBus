package me.tomalka.rpcbus_android.posters

import me.tomalka.rpcbus.posters.Poster

class QueuePoster : Poster {
    companion object {
        const val POSTER_ID = 2
    }



    override val posterId: Int
        get() = POSTER_ID

    override fun enqueue(callback: () -> Unit) {
        throw UnsupportedOperationException()
    }

    override fun destroy() {
        throw UnsupportedOperationException()
    }
}