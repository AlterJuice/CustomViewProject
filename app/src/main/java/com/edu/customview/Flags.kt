package com.edu.customview

object Flags {
    fun hasFlag(flags: Int, value: Int): Boolean {
        return (flags and value) == value
    }

    fun setFlag(flags: Int, value: Int): Int {
        return (flags or value)
    }

    fun unsetFlag(flags: Int, value: Int): Int {
        return (flags and value.inv())
    }

    fun toggleFlag(flags: Int, value: Int, isChecked: Boolean): Int {
        return if (isChecked) setFlag(flags, value) else unsetFlag(flags, value)
    }
}