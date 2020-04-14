package com.savvasdalkitsis.multitouch.event

enum class Phase(val code: Int) {

    MOMENTUM(0),
    BEGIN(1),
    CHANGED(2),
    END(3),
    CANCELLED(4),
    OTHER(-1);

    companion object {
        @JvmStatic
        fun getByCode(code: Int): Phase = values()
                .find { it.code == code } ?: OTHER
    }
}