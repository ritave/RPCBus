package me.tomalka.rpcbus.utils

import org.junit.Assert.*
import org.junit.Test

class FifoCacheTest {
    @Test
    fun testOneItem() {
        val firstKey = Any()
        val secondKey = Any()
        val firstValue = Any()
        val secondValue = Any()
        val cache = FifoCache<Any, Any>(1)
        cache.put(firstKey, firstValue)

        assertEquals(cache[firstKey], firstValue)

        cache.put(secondKey, secondValue)

        assertNull(cache[firstKey])
        assertEquals(cache[secondKey], secondValue)
    }

    @Test
    fun testIsActuallyFifo() {
        val cacheSize = 5
        val items = Array<Pair<Any, Any>>(cacheSize + 2) { Pair<Any, Any>(Any(), Any()) }
        val cache = FifoCache<Any, Any>(cacheSize)

        for (i in 0..(cacheSize-1))
            cache.put(items[i].first, items[i].second)
        for (i in 0..(cacheSize-1))
            assertEquals(cache[items[i].first], items[i].second)
        cache.put(items[cacheSize].first, items[cacheSize].second)
        assertNull(cache[items.first().first])
        for (i in 1..cacheSize)
            assertEquals(cache[items[i].first], items[i].second)

        cache.put(items.last().first, items.last().second)
        assertNull(cache[items[0].first])
        assertNull(cache[items[1].first])
        for (i in 2..cacheSize+1)
            assertEquals(cache[items[i].first], items[i].second)
    }

    @Test(expected = AssertionError::class)
    fun testZeroCache() {
        FifoCache<Any, Any>(0)
    }

    @Test(expected = AssertionError::class)
    fun testMinusCache() {
        FifoCache<Any, Any>(-1)
    }

    @Test
    fun testClear() {
        val cacheSize = 5
        val items = Array<Pair<Any, Any>>(cacheSize) { Pair<Any, Any>(Any(), Any()) }
        val cache = FifoCache<Any, Any>(cacheSize)

        for (item in items)
            cache.put(item.first, item.second)
        for (item in items)
            assertEquals(cache[item.first], item.second)
        cache.clear()
        for (item in items)
            assertNull(cache[item.first])
        for (item in items)
            cache.put(item.first, item.second)
        for (item in items)
            assertEquals(cache[item.first], item.second)
    }

    @Test
    fun testIntKeys() {
        val cacheSize = 5
        val firstKey = 2
        val secondKey = 7
        val firstValue = Any()
        val secondValue = Any()
        val cache = FifoCache<Int, Any>(cacheSize)
        cache.put(firstKey, firstValue)
        cache.put(secondKey, secondValue)
        assertEquals(cache[firstKey], firstValue)
        assertEquals(cache[secondKey], secondValue)
    }

    @Test
    fun testReplace() {
        val key = 3
        val firstValue = 123
        val secondValue = 456
        val cache = FifoCache<Int, Int>(3)
        cache.put(key, firstValue)
        assertEquals(cache[key], firstValue)
        cache.put(key, secondValue)
        assertEquals(cache[key], secondValue)
    }
}