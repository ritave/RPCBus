package me.tomalka.rpcbus

import me.tomalka.rpcbus.posters.Poster
import me.tomalka.rpcbus.utils.FifoCache
import me.tomalka.rpcbus.utils.getOrPutNew
import me.tomalka.rpcbus.utils.removeIf
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.declaredMemberFunctions
import kotlin.reflect.defaultType
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.javaType

class LocalDispatcher(private val posters: Map<Int, Poster>) {
    companion object {
        private const val CACHE_SIZE = 20
    }

    private val methodCache = FifoCache<KType, MutableList<SubscribeMethod>>(CACHE_SIZE)

    private val eventToSubscriptions = HashMap<KType, MutableList<Subscription>>()
    private val subscriberToEvents = HashMap<Any, List<KType>>()

    fun register(subscriber: Any) {
        assert(!subscriberToEvents.contains(subscriber))

        // A hack to get KClass from instance
        // http://stackoverflow.com/questions/32655216/kotlin-equivalent-of-getclass-for-kclass
        val subscriberClass = subscriber.javaClass.kotlin
        var methods = methodCache.getOrPutNew(subscriberClass.defaultType) { getSubscribeMethods(subscriberClass) }

        val usedEvents = ArrayList<KType>()

        methods.forEach {
            assert(it.threadMode in posters)
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
        eventToSubscriptions[event.javaClass.kotlin.defaultType]?.forEach {
            posters[it.subscribeMethod.threadMode]!!
                    .enqueue { it.subscribeMethod.method.call(it.subscriber, event)}
        }
    }

    fun destroy() {
        posters.forEach { it.value.destroy() }
        methodCache.clear()
    }

    private fun getSubscribeMethods(subscriberClass: KClass<*>): MutableList<SubscribeMethod> {
        val result = ArrayList<SubscribeMethod>();
        for (function in subscriberClass.declaredMemberFunctions) {
            for (annotation in function.annotations) {
                if (annotation is Subscribe) {
                    function.parameters[0].type
                    if (function.parameters.size != 2 || function.parameters[0].type != subscriberClass.defaultType) {
                        throw AssertionError("Can't subscribe a method with more parameters than the event");
                    }
                    val eventType = function.parameters[1].type
                    // TODO: Detect generics and crash

                    result.add(SubscribeMethod(function, annotation.threadMode, eventType))
                    break
                }
            }
        }
        return result
    }
}