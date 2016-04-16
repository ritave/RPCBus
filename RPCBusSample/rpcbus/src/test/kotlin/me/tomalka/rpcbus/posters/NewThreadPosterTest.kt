package me.tomalka.rpcbus.posters

import me.tomalka.rpcbus.Subscribe
import org.junit.Assert.*
import org.junit.Test
import tools.SynchronouseExecutorService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class NewThreadPosterTest {
    @Test
    fun TestRuns() {
        val poster = NewThreadPoster(SynchronouseExecutorService())
        var actuallyRun = false

        poster.enqueue { actuallyRun = true }
        assertTrue(actuallyRun)
    }

    @Test
    fun TestDifferentThreads() {
        val executorService = Executors.newCachedThreadPool()
        val testThread = Thread.currentThread()
        val poster = NewThreadPoster(executorService)

        var actuallyRun = false

        poster.enqueue {
            assertNotEquals(Thread.currentThread(), testThread)
            synchronized(this) {
                actuallyRun = true
            }
        }
        executorService.shutdown()
        executorService.awaitTermination(10, TimeUnit.SECONDS)
        synchronized(this) {
            assertTrue(actuallyRun)
        }
    }
}