package me.tomalka.rpcbus

import me.tomalka.rpcbus.posters.InstantPoster
import me.tomalka.rpcbus.posters.NewThreadPoster

object ThreadModes {
    const val Instant = InstantPoster.POSTER_ID
    const val NewThread = NewThreadPoster.POSTER_ID
}