package me.tomalka.rpcbus

import me.tomalka.rpcbus.posters.Poster
import me.tomalka.rpcbus.utils.FifoCache
import me.tomalka.rpcbus.utils.getOrPut
import me.tomalka.rpcbus.utils.removeIf
import java.util.*
import kotlin.reflect.KClass

class LocalDispatcher(private val posters: Map<Int, Poster>) {
    companion object {
        private const val CACHE_SIZE = 20
    }

    val methodCache = FifoCache<KClass<*>, MutableList<SubscribeMethod>>(CACHE_SIZE)

    val eventToSubscriptions = HashMap<KClass<*>, MutableList<Subscription>>()
    val subscriberToEvents = HashMap<Any, List<KClass<*>>>()

    fun register(subscriber: Any) {
        assert(!subscriberToEvents.contains(subscriber))

        // A hack to get KClass from instance
        // http://stackoverflow.com/questions/32655216/kotlin-equivalent-of-getclass-for-kclass
        val subscriberClass = subscriber.javaClass.kotlin
        var methods = methodCache.getOrPut(subscriberClass) { getSubscribeMethods(subscriberClass) }

        val usedEvents = ArrayList<KClass<*>>()

        methods.forEach {
            assert(it.threadMode < posters.size)
            assert(it.method.parameters.size == 2) // instance + event

            eventToSubscriptions
                    .getOrPut(it.eventType) { ArrayList<Subscription>() }
                    .add(Subscription(subscriber, it))
            usedEvents.add(it.eventType)
        }
        subscriberToEvents.put(subscriber, usedEvents)
    }

    fun unregister(subscriber: Any) {
        assert(subscriberToEvents.containsKey(subscriber))

        val events = subscriberToEvents[subscriber]!!
        events.forEach {
            val subscriptions = eventToSubscriptions[it]!!
            subscriptions.removeIf { it.subscriber == subscriber }
            if (subscriptions.isEmpty())
                eventToSubscriptions.remove(it)
        }
        subscriberToEvents.remove(subscriber)

        assert(subscriberToEvents[subscriberToEvents]?.isNotEmpty() ?: true)
    }

    fun dispatch(event: Any) {
        // TODO: Is it worth doing something when no one listens?
        eventToSubscriptions[event]?.forEach {
            posters[it.subscribeMethod.threadMode]!!
                    .enqueue { it.subscribeMethod.method.call(it.subscriber, event)}
        }
    }

    fun destroy() {
        posters.forEach { it.value.destroy() }
        methodCache.clear()
    }
}

private fun getSubscribeMethods(subscriberClass: KClass<*>): MutableList<SubscribeMethod> {

}