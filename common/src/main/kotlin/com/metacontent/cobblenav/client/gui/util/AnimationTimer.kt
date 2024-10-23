package com.metacontent.cobblenav.client.gui.util

class AnimationTimer(private var duration: Float) {
    private var timer = duration

    fun tick(delta: Float) {
        if (isOver()) return
        timer -= delta
    }

    fun isOver(): Boolean {
        return timer <= 0f
    }

    fun getProgress(): Float {
        var progress = 1f - timer / duration
        if (progress > 1f) {
            progress = 1f
        }
        return progress
    }

    fun reset() {
        timer = duration
    }

    fun reset(duration: Float) {
        this.duration = duration
        reset()
    }
}