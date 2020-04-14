package com.savvasdalkitsis.mac.gestures.event

import java.awt.Component

class GestureEvent<D>(
        val source: Component,
        val mouseX: Double,
        val mouseY: Double,
        val absMouseX: Double,
        val absMouseY: Double,
        val phase: Phase,
        val data: D
)