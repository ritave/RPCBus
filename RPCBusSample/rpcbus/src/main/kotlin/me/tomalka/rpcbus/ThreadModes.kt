package me.tomalka.rpcbus

import me.tomalka.rpcbus.posters.InstantPoster
import me.tomalka.rpcbus.posters.NewThreadPoster

object ThreadModes {
    @JvmStatic
    val Instant = InstantPoster.POSTER_ID
    @JvmStatic
    val NewThread = NewThreadPoster.POSTER_ID
}