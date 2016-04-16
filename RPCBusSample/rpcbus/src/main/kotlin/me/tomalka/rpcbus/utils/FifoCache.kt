package me.tomalka.rpcbus.utils

import java.util.*

class FifoCache<K, V>(val maxSize: Int) {
    private val items = HashMap<K, V>()
    private val deletion_queue = LinkedList<K>()

    init {
        if (maxSize <= 0)
            throw AssertionError("Can't have a cache of <= 0 elements")
    }

    fun clear() {
        items.clear()
        deletion_queue.clear()
    }

    fun put(key: K, value: V) {
        if (deletion_queue.size >= maxSize) {
            items.remove(deletion_queue.first)
            deletion_queue.removeFirst()
        }
        items.put(key, value)
        deletion_queue.addLast(key)
    }

    operator fun get(key: K): V? {
        return items[key]
    }
}