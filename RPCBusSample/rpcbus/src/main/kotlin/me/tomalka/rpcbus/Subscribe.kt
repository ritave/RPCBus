package me.tomalka.rpcbus

import me.tomalka.rpcbus.posters.InstantPoster

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Subscribe(val threadMode: Int = InstantPoster.POSTER_ID)