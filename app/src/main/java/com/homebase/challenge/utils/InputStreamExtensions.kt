package com.homebase.challenge.utils

import java.io.IOException
import java.io.InputStream

fun InputStream.toJsonString() = try {
    val bytes = ByteArray(this.available())
    this.read(bytes, 0, bytes.size)
    String(bytes)
} catch (e: IOException) {
    null
}
