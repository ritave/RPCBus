package me.tomalka.rpcbus.posters

interface Poster {
    /**
     * An id that uniquely identifies this type of poster
     */
    val posterId: Int

    fun enqueue(callback: () -> Unit)
    fun destroy()
}