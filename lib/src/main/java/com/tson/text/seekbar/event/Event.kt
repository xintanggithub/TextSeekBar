package com.tson.text.seekbar.event

sealed class Event

object Down : Event()
object Move : Event()
object Up : Event()

fun Any.and(block: () -> Unit = {}) = block.invoke()