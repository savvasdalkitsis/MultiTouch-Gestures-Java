package com.savvasdalkitsis.mac.gestures

import com.savvasdalkitsis.mac.gestures.event.*

abstract class GestureAdapter : GestureListener {

    override fun magnify(e: GestureEvent<Magnification>) {}
    override fun rotate(e: GestureEvent<Rotation>) {}
    override fun scroll(e: GestureEvent<Scroll>) {}
    override fun smartMagnify(e: GestureEvent<SmartMagnify>) {}
}