package com.savvasdalkitsis.multitouch

import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener

internal class MultiTouchClient(
        private val component: Component,
        private val receiveEvenIfNotOnTop: Boolean
) : MouseMotionListener, MouseListener {

    val listeners: MutableList<GestureListener> = mutableListOf()
    val shouldReceiveEvent get() = isInside || receiveEvenIfNotOnTop

    var isInside = false

    init {
        outside()
        component.addMouseListener(this)
        component.addMouseMotionListener(this)
    }

    fun stopListeningForMouseEvents() = apply {
        outside()
        component.removeMouseListener(this)
        component.removeMouseMotionListener(this)
    }

    override fun mouseDragged(e: MouseEvent) = withinBounds(e)

    override fun mouseMoved(e: MouseEvent) = inside()

    override fun mouseClicked(e: MouseEvent) = inside()

    override fun mousePressed(e: MouseEvent) = inside()

    override fun mouseReleased(e: MouseEvent) = withinBounds(e)

    override fun mouseEntered(e: MouseEvent) = inside()

    override fun mouseExited(e: MouseEvent) = outside()

    private fun inside() { isInside = true }
    private fun outside() { isInside = false }

    private fun withinBounds(e: MouseEvent) {
        isInside = true
        if (e.x < 0 || component.width >= e.x) {
            isInside = false
        }
        if (e.y < 0 || component.height >= e.y) {
            isInside = false
        }
    }
}