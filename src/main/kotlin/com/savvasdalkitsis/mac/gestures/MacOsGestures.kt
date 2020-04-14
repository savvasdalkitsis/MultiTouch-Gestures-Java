package com.savvasdalkitsis.mac.gestures

import java.io.File.createTempFile
import java.util.logging.Level
import java.util.logging.Logger

object MacOsGestures {

    @get:JvmStatic
    var isSupported: Boolean = false
        private set

    private val log = Logger.getGlobal()

    init {
        if (isMacOS()) {
            try {
                loadLibrary()
                Runtime.getRuntime().addShutdownHook(Thread {
                    log.info("In shutdownhook. Stopping Event Tap.")
                    MacOsGesturesNative.stop()
                })
                isSupported = true
            } catch (e: Throwable) {
                log.log(Level.WARNING, "Could not load multitouch gesture library", e)
            }
        } else {
            log.warning("[MULTITOUCH GESTURES] Only Mac OS X is supported at the moment.")
        }
    }

    private fun isMacOS() = System.getProperty("os.name").contains("mac os x", ignoreCase = true)

    private fun loadLibrary() {
        val file = createTempFile("libmtg_mac", ".dylib")
        javaClass.getResource("libmtg_mac.dylib").openStream().copyTo(file.outputStream(), 1024)
        System.load(file.absolutePath)
    }
}