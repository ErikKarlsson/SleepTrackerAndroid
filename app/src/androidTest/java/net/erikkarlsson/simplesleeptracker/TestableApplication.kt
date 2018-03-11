package net.erikkarlsson.simplesleeptracker

interface TestableApplication {
    fun reInitDependencyInjection()
    fun clearDataBetweenTests()
}