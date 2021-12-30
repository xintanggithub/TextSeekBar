package com.tson.text.seekbar.listener

import com.tson.text.seekbar.event.Event

interface SeekBarViewOnChangeListener {
    fun touch(percent: Float, eventType: Event)
}