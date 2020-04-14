package com.savvasdalkitsis.mac.gestures

import com.savvasdalkitsis.mac.gestures.MacOsGesturesUtilities.notifyListenersIfNeeded
import com.savvasdalkitsis.mac.gestures.event.*
import com.savvasdalkitsis.mac.gestures.event.Phase.Companion.getByCode
import java.awt.Toolkit
import java.io.File.createTempFile
import java.lang.Math.toRadians
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.SwingUtilities

object MacOsGestures {

    @get:JvmStatic
    var isSupported: Boolean = false
        private set

    private var gestureEventThread: Thread? = null
    private val log = Logger.getGlobal()

    init {
        if (isMacOS()) {
            try {
                loadLibrary()
                Runtime.getRuntime().addShutdownHook(Thread {
                    log.info("In shutdownhook. Stopping Event Tap.")
                    stop()
                })
                isSupported = true
            } catch (e: Throwable) {
                log.log(Level.WARNING, "Could not load multitouch gesture library", e)
            }
        } else {
            log.warning("[MULTITOUCH GESTURES] Only Mac OS X is supported at the moment.")
        }
    }

    @Synchronized
    fun startInSeparateThread() {
        if (isSupported && gestureEventThread?.isAlive != true) {
            gestureEventThread = Thread {
                init()
                start()
            }.apply {
                name = "Gesture Event Thread"
                this.start()
            }
        }
    }

    @JvmStatic
    external fun init()

    @JvmStatic
    external fun start()

    @JvmStatic
    external fun stop()

    @Suppress("unused") // called from native code
    @JvmStatic
    fun dispatchMagnifyGesture(mouseX: Double, mouseY: Double, magnification: Double, phase: Int) =
            dispatch(mouseX, mouseY, phase, Magnification(magnification)) { event ->
                magnify(event)
            }

    @Suppress("unused") // called from native code
    @JvmStatic
    fun dispatchRotateGesture(mouseX: Double, mouseY: Double, rotation: Double, phase: Int) =
            dispatch(mouseX, mouseY, phase, Rotation(-toRadians(rotation))) { event ->
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
        notifyListenersIfNeeded(mouseX, remappedMouseX, getByCode(phase), data, dispatcher)
    }

    private fun isMacOS() = System.getProperty("os.name").contains("mac os x", ignoreCase = true)

    private fun loadLibrary() {
        val file = createTempFile("libmtg_mac", ".dylib")
        javaClass.getResource("libmtg_mac.dylib").openStream().copyTo(file.outputStream(), 1024)
        System.load(file.absolutePath)
    }
}