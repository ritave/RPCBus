package me.tomalka.rpcbus

data class Subscription(val subscriber: Any, val subscribeMethod: SubscribeMethod)