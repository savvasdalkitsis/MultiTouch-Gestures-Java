package com.savvasdalkitsis.mac.gestures

import com.savvasdalkitsis.mac.gestures.event.*

interface GestureListener {

    fun magnify(e: GestureEvent<Magnification>)
    fun rotate(e: GestureEvent<Rotation>)
    fun scroll(e: GestureEvent<Scroll>)
    fun smartMagnify(e: GestureEvent<SmartMagnify>)
}