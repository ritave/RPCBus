package me.tomalka.rpcbus

import me.tomalka.rpcbus.posters.InstantPoster
import me.tomalka.rpcbus.posters.NewThreadPoster
import me.tomalka.rpcbus.posters.Poster
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import tools.SynchronouseExecutorService
import java.util.*

class LocalDispatcherTest {
    companion object {
        const val COUNTER_POSTER_ID = 7272;
    }

    val posters = HashMap<Int, Poster>()
    lateinit var disptacher : LocalDispatcher
    lateinit var counterPoster : CounterPoster

    @Before
    fun setUp() {
        posters.clear()
        posters.put(InstantPoster.POSTER_ID, InstantPoster())
        posters.put(NewThreadPoster.POSTER_ID, NewThreadPoster(SynchronouseExecutorService()))
        counterPoster = CounterPoster(COUNTER_POSTER_ID)
        posters.put(COUNTER_POSTER_ID, counterPoster)
        disptacher = LocalDispatcher(posters)
    }

    @Test
    fun testBasicCallback() {
        val counter = AnyCallbackCounter()
        disptacher.register(counter)

        assertEquals(counter.count, 0)
        disptacher.dispatch(Any())
        assertEquals(counter.count, 1)
        disptacher.dispatch(Any())
        assertEquals(counter.count, 2)

        disptacher.unregister(counter)
    }

    @Test
    fun testUnregister() {
        val counter = AnyCallbackCounter()

        disptacher.register(counter)

        disptacher.dispatch(Any())
        assertEquals(counter.count, 1)

        disptacher.unregister(counter)

        disptacher.dispatch(Any())
        assertEquals(counter.count, 1)
    }

    @Test
    fun testDifferentEvents() {
        class TestEvent1
        class TestEvent2

        val counter1 = object  {
            var count: Int = 0

            @Subscribe
            fun testSubscribe(event: TestEvent1) {
                count++
            }
        }

        val counter2 = object  {
            var count: Int = 0
            @Subscribe
            fun testSubscribe(event: TestEvent2) {
                count++
            }
        }

        disptacher.register(counter1)
        disptacher.register(counter2)

        disptacher.dispatch(TestEvent1())
        assertEquals(counter1.count, 1)
        assertEquals(counter2.count, 0)

        disptacher.dispatch(TestEvent2())
        assertEquals(counter1.count, 1)
        assertEquals(counter2.count, 1)


        disptacher.unregister(counter1)
        disptacher.unregister(counter2)

        disptacher.dispatch(TestEvent1())
        assertEquals(counter1.count, 1)
        assertEquals(counter2.count, 1)

    }

    @Test(expected = AssertionError::class)
    fun testWeirdThreadmodeTest() {
        class WeirdThreadMode {
            @Subscribe(threadMode = -1)
            fun testSubscribe(a: Any) {}
        }

        val instance = WeirdThreadMode()

        disptacher.register(instance)
    }

    @Test
    fun testThreadMode() {
        val registrant = object {
            @Subscribe(threadMode = COUNTER_POSTER_ID)
            fun testSubscribe(a: Any) {}
        }

        val control = AnyCallbackCounter()

        disptacher.register(registrant)
        disptacher.register(control)
        disptacher.dispatch(Any())
        disptacher.unregister(control)
        disptacher.unregister(registrant)

        assertEquals(counterPoster.count, 1)
    }

    class CounterPoster(override val posterId: Int) : Poster {
        var count = 0

        override fun destroy() {
        }

        override fun enqueue(callback: () -> Unit) {
            callback()
            count++
        }
    }

    class AnyCallbackCounter {
        var count: Int = 0

        @Subscribe
        fun testSubscribe(event: Any) {
            count++
        }
    }
}