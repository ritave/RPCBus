package me.tomalka.rpcbus.posters

/**
 * This poster notifies the subscriber on the same thread as post()
 */
class InstantPoster : Poster {
    companion object {
        const val POSTER_ID = 0;
    }

    override val posterId: Int
        get() = POSTER_ID

    override fun enqueue(callback: () -> Unit) {
        callback()
    }

    override fun destroy() {
        // Left blank
    }
}