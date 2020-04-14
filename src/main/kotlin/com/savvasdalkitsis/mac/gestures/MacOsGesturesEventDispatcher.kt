package com.savvasdalkitsis.mac.gestures

import com.savvasdalkitsis.mac.gestures.event.*
import java.awt.Point
import java.awt.Rectangle
import java.awt.Toolkit
import javax.swing.SwingUtilities
import kotlin.math.roundToInt

object MacOsGesturesEventDispatcher {

    private var gestureEventThread: Thread? = null

    @Synchronized
    fun startInSeparateThread() {
        if (MacOsGestures.isSupported && gestureEventThread?.isAlive != true) {
            gestureEventThread = Thread {
                MacOsGesturesNative.init(this::class.java)
                MacOsGesturesNative.start()
            }.apply {
                name = "Gesture Event Thread"
                this.start()
            }
        }
    }

    @Suppress("unused") // called from native code
    @JvmStatic
    fun dispatchMagnifyGesture(mouseX: Double, mouseY: Double, magnification: Double, phase: Int) =
            dispatch(mouseX, mouseY, phase, Magnification(magnification)) { event ->
                magnify(event)
            }

    @Suppress("unused") // called from native code
    @JvmStatic
    fun dispatchRotateGesture(mouseX: Double, mouseY: Double, rotation: Double, phase: Int) =
            dispatch(mouseX, mouseY, phase, Rotation(-Math.toRadians(rotation))) { event ->
                rotate(event)
            }

    @Suppress("unused") // called from native code
    @JvmStatic
    fun dispatchScrollWheelEvent(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double, fromMouse: Boolean, phase: Int) =
            dispatch(mouseX, mouseY, phase, Scroll(deltaX, deltaY, fromMouse)) { event ->
                scroll(event)
            }

    @Suppress("unused") // called from native code
    @JvmStatic
    fun dispatchSmartMagnifyEvent(mouseX: Double, mouseY: Double, phase: Int) =
            dispatch(mouseX, mouseY, phase, SmartMagnify) { event ->
                smartMagnify(event)
            }

    private fun <D> dispatch(
            mouseX: Double, mouseY: Double, phase: Int, data: D, dispatcher: GestureListener.(GestureEvent<D>) -> Unit
    ) = SwingUtilities.invokeLater {
        val remappedMouseX = Toolkit.getDefaultToolkit().screenSize.height - mouseY
        notifyListenersIfNeeded(mouseX, remappedMouseX, Phase.getByCode(phase), data, dispatcher)
    }

    private fun <D> notifyListenersIfNeeded(
            mouseX: Double, mouseY: Double, phase: Phase, data: D, dispatch: GestureListener.(GestureEvent<D>) -> Unit
    ) {
        val mXi = mouseX.roundToInt()
        val mYi = mouseY.roundToInt()
        for ((component, client) in MacOsGesturesUtilities.clients) {
            if (!component.isShowing) continue
            val r = Rectangle(component.locationOnScreen, component.size)
            if (r.contains(mXi, mYi) && client.shouldReceiveEvent) {
                val relP = Point(mXi, mYi)
                SwingUtilities.convertPointFromScreen(relP, component)
                val event = GestureEvent(component, relP.getX(), relP.getY(), mouseX, mouseY, phase, data)
                for (listener in client.listeners) {
                    listener.dispatch(event)
                }
                return
            }
        }
    }
}