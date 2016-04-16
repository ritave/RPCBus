package me.tomalka.rpcbus.engines

import me.tomalka.rpcbus.LocalDispatcher
import me.tomalka.rpcbus.LocalDispatcherTest
import me.tomalka.rpcbus.Subscribe
import me.tomalka.rpcbus.posters.InstantPoster
import me.tomalka.rpcbus.posters.NewThreadPoster
import me.tomalka.rpcbus.posters.Poster
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import tools.SynchronouseExecutorService
import java.util.*

class LoopbackConnectionEngineTest {
    val engine = LoopbackConnectionEngine()
    lateinit var dispatcher : LocalDispatcher

    @Before
    fun setUp() {
        val posters = HashMap<Int, Poster>()
        posters.clear()
        posters.put(InstantPoster.POSTER_ID, InstantPoster())
        posters.put(NewThreadPoster.POSTER_ID, NewThreadPoster(SynchronouseExecutorService()))
        dispatcher = LocalDispatcher(posters)
        engine.install(dispatcher)
    }


    @Test
    fun testDestroy() {
        // Test no throw
        engine.destroy()
    }

    @Test
    fun testOnPost() {
        val testThread = Thread.currentThread()

        val registrant = object {
            var count = 0
            @Subscribe
            fun testSubscribe(e: Any) {
                assertEquals(Thread.currentThread(), testThread)
                count++
            }
        }

        dispatcher.register(registrant)

        engine.onPost(Any())

        assertEquals(registrant.count, 1)
    }

    @Test
    fun testInts() {
        val testThread = Thread.currentThread()

        val registrant = object {
            var count = 0
            @Subscribe
            fun testSubscribe(e: Int) {
                assertEquals(Thread.currentThread(), testThread)
                count++
            }
        }

        dispatcher.register(registrant)

        engine.onPost(3)

        assertEquals(registrant.count, 1)
    }

    @Test
    fun testMismatch() {
        val testThread = Thread.currentThread()

        val registrant = object {
            var count = 0
            @Subscribe
            fun testSubscribe(e: String) {
                assertEquals(Thread.currentThread(), testThread)
                count++
            }
        }

        dispatcher.register(registrant)

        engine.onPost(5)

        assertEquals(registrant.count, 0)
    }

    @Test
    fun testNoRegistrants()
    {
        // Test no throw
        engine.onPost(Any())
        engine.onPost(1)
        engine.onPost("asd")
    }
}