package net.erikkarlsson.simplesleeptracker

import net.erikkarlsson.simplesleeptracker.data.SleepDao
import net.erikkarlsson.simplesleeptracker.di.DaggerTestComponent
import net.erikkarlsson.simplesleeptracker.di.TestComponent
import timber.log.Timber
import javax.inject.Inject

class TestApp : App(), TestableApplication {

    private lateinit var testComponent: TestComponent

    @Inject lateinit var sleepDao: SleepDao

    override fun reInitDependencyInjection() {
        Timber.d("reInitDependencyInjection")
        testComponent = DaggerTestComponent.builder().application(this).build()
        testComponent.inject(this)
    }

    override fun clearDataBetweenTests() {
        sleepDao.deleteAllSleep()
    }

}