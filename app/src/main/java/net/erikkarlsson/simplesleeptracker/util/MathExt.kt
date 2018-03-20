package net.erikkarlsson.simplesleeptracker.util

fun Float.diffPercentage(other: Float): Int {
    return Math.round((this - other) / other * 100)
}



