package com.savvasdalkitsis.multitouch

import com.savvasdalkitsis.multitouch.event.*

interface GestureListener {

    fun magnify(e: GestureEvent<Magnification>)
    fun rotate(e: GestureEvent<Rotation>)
    fun scroll(e: GestureEvent<Scroll>)
    fun smartMagnify(e: GestureEvent<SmartMagnify>)
}