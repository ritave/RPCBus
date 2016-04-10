package me.tomalka.rpcbus.utils

inline fun <T> MutableList<T>.removeIf(predicate: (T) -> Boolean) {
    var at = 0
    var size = size
    while (at < size) {
        if (predicate(get(at))) {
            removeAt(at)
            size--
        } else
            at++
    }
}

inline fun <K, V> FifoCache<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    val value = get(key)
    return if (value == null) {
        val answer = defaultValue()
        put(key, answer)
        answer
    } else {
        value
    }
}