package net.erikkarlsson.simplesleeptracker

import net.erikkarlsson.simplesleeptracker.domain.Sleep

interface TestableApplication {
    fun reInitDependencyInjection()
    fun clearDataBetweenTests()
    fun insertSleep(sleep: Sleep)
}