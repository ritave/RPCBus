package tools

import java.util.*
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

/**
 * Runs the execution at once in current thread
 */
class SynchronouseExecutorService : AbstractExecutorService() {
    private var terminated: Boolean = false
    private val terminationLock: Any = Any()

    override fun isTerminated() = terminated
    override fun isShutdown() = terminated

    override fun shutdown() {
        synchronized(terminationLock) {
            terminated = true
        }
    }

    override fun shutdownNow(): MutableList<Runnable> {
        shutdown()
        return Collections.emptyList()
    }


    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        shutdown()
        return terminated
    }

    override fun execute(command: Runnable) {
        command.run()
    }
}

