package com.savvasdalkitsis.multitouch

import com.savvasdalkitsis.multitouch.event.GestureEvent
import com.savvasdalkitsis.multitouch.event.Phase
import java.awt.Component
import java.awt.Point
import java.awt.Rectangle
import java.util.*
import javax.swing.SwingUtilities
import kotlin.math.roundToInt

object MultiTouchGestureUtilities {

    private val clients = HashMap<Component, MultiTouchClient>()
    private var listenerCount = 0

    @JvmStatic
    fun Component.addGestureListener(listener: GestureListener, receiveEvenIfNotOnTop: Boolean = false) {
        if (listenerCount == 0) {
            EventDispatch.startInSeparateThread()
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
                EventDispatch.stop()
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

    fun <D> notifyListenersIfNeeded(
            mouseX: Double, mouseY: Double, phase: Phase, data: D, dispatch: GestureListener.(GestureEvent<D>) -> Unit
    ) {
        val mXi = mouseX.roundToInt()
        val mYi = mouseY.roundToInt()
        for ((component, client) in clients) {
            if (!component.isShowing) continue
            val r = Rectangle(component.locationOnScreen, component.size)
            if (r.contains(mXi, mYi) && client.shouldReceiveEvent) {
                val relP = Point(mXi, mYi)
                SwingUtilities.convertPointFromScreen(relP, component)
                val event = GestureEvent(
                        component,
                        relP.getX(),
                        relP.getY(),
                        mouseX,
                        mouseY,
                        phase,
                        data
                )
                for (listener in client.listeners) {
                    listener.dispatch(event)
                }
                return
            }
        }
    }
}