package net.erikkarlsson.simplesleeptracker.core.util

import java.io.File
import java.io.InputStream

fun InputStream.toFile(path: String): File {
    val file = File(path)
    file.outputStream().use { this.copyTo(it) }
    return file
}
