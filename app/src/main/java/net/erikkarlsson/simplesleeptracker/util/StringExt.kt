package net.erikkarlsson.simplesleeptracker.util

fun Float.formatHHMM(): String {
    val hours = Math.floor(this.toDouble()).toInt()
    val minutes = Math.floor(((this - hours) * 60).toDouble()).toInt()
    return String.format("%d h %d min", hours, minutes)
}