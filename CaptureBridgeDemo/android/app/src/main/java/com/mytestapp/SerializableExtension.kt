package com.mytestapp

import java.io.Serializable
import java.util.*

/**
 * Helper function to convert a extract a typed array from a [Serializable] without warnings
 */
inline fun <reified T> Serializable.asTypedArray(): Array<T> {
    this as Array<*>
    return Arrays.copyOf(this, this.size, Array<T>::class.java)
}