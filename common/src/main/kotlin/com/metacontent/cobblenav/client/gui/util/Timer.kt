package com.metacontent.cobblenav.client.gui.util

class Timer(private var duration: Float, var loop: Boolean = false) {
    private var timer = duration

    fun tick(delta: Float) {
        if (loop && isOver()) reset()
        if (isOver()) return
        timer -= delta
    }

    fun isOver(): Boolean = timer <= 0f

    fun getProgress(): Float = (1f - timer / duration).coerceIn(0f, 1f)

    fun reset() {
        timer = duration
    }

    fun reset(duration: Float) {
        this.duration = duration
        reset()
    }
}