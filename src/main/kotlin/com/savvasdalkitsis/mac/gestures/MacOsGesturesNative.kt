package com.savvasdalkitsis.mac.gestures

object MacOsGesturesNative {

    @JvmStatic
    external fun init(dispatcher: Class<out MacOsGesturesEventDispatcher>)

    @JvmStatic
    external fun start()

    @JvmStatic
    external fun stop()
}