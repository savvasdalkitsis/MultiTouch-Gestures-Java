package com.savvasdalkitsis.mac.gestures

import java.awt.Component
import java.util.*

object MacOsGesturesUtilities {

    internal val clients = HashMap<Component, MultiTouchClient>()
    private var listenerCount = 0

    @JvmStatic
    @JvmOverloads
    fun Component.addGestureListener(listener: GestureListener, receiveEvenIfNotOnTop: Boolean = false) {
        if (listenerCount == 0) {
            MacOsGesturesEventDispatcher.startInSeparateThread()
        }
        val client = clients.getOrPut(this) {
            MultiTouchClient(this, receiveEvenIfNotOnTop)
        }
        client.listeners += listener
        listenerCount++
    }

    @JvmStatic
    fun Component.removeGestureListener(listener: GestureListener): Boolean {
        val client = clients[this] ?: return false
        val listeners = client.listeners
        if (listeners.remove(listener)) {
            if (listeners.isEmpty()) {
                client.stopListeningForMouseEvents()
                clients.remove(this)
            }
            listenerCount--
            if (listenerCount == 0) {
                MacOsGesturesNative.stop()
            }
            return true
        }
        return false
    }

    @JvmStatic
    fun Component.removeAllGestureListeners(): Int {
        var removed = 0
        clients[this]?.let { client ->
            for (i in client.listeners.size - 1 downTo 0) {
                if (removeGestureListener(client.listeners[i]))
                    removed++
            }
        }
        return removed
    }
}