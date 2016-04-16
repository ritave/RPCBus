package me.tomalka.rpcbus_android.posters

import android.os.Handler
import android.os.Looper
import me.tomalka.rpcbus.posters.Poster

class MainThreadPoster : Poster {
    companion object {
        const val POSTER_ID = 3
    }

    private val handler = Handler(Looper.getMainLooper())

    override val posterId: Int
        get() = POSTER_ID

    override fun enqueue(callback: () -> Unit) {
        handler.post(callback)
    }

    override fun destroy() {
    }
}
