package net.erikkarlsson.simplesleeptracker

import androidx.test.ext.junit.rules.activityScenarioRule
import com.facebook.testing.screenshot.Screenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import net.erikkarlsson.simplesleeptracker.di.module.DateTimeModule
import net.erikkarlsson.simplesleeptracker.robot.MainRobot
import net.erikkarlsson.simplesleeptracker.util.TestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class StatisticsTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = activityScenarioRule<MainActivity>()

    val mainRobot = MainRobot()

    @Before
    fun init() {
        hiltRule.inject()
        testUtil.initTest()
    }

    @Inject
    lateinit var testUtil: TestUtil

    @Test
    fun testCompareStatisticsBetweenYears() {
        with(testUtil) {
            // Given current time
            mockDateTimeNow("2018-03-14T22:30:00+01:00")

            // Last year
            insertSleep(fromDate = "2017-03-04T23:30:00+01:00", toDate = "2017-03-05T06:30:00+01:00")
            insertSleep(fromDate = "2017-03-05T23:30:00+01:00", toDate = "2017-03-06T06:55:00+01:00")
            insertSleep(fromDate = "2017-03-06T22:50:00+01:00", toDate = "2017-03-07T06:20:00+01:00")
            insertSleep(fromDate = "2017-03-07T22:50:00+01:00", toDate = "2017-03-08T06:20:00+01:00")
            insertSleep(fromDate = "2017-03-08T22:50:00+01:00", toDate = "2017-03-09T06:20:00+01:00")
            insertSleep(fromDate = "2017-03-09T22:50:00+01:00", toDate = "2017-03-10T06:20:00+01:00")
            insertSleep(fromDate = "2017-03-10T22:50:00+01:00", toDate = "2017-03-11T06:20:00+01:00")

            // Two months ago
            insertSleep(fromDate = "2018-01-04T23:30:00+01:00", toDate = "2018-01-05T06:30:00+01:00")

            // Previous month
            insertSleep(fromDate = "2018-02-04T21:30:00+01:00", toDate = "2018-02-05T06:35:00+01:00")
            insertSleep(fromDate = "2018-02-05T22:30:00+01:00", toDate = "2018-02-06T06:36:00+01:00")

            // Previous week
            insertSleep(fromDate = "2018-03-04T23:30:00+01:00", toDate = "2018-03-05T06:30:00+01:00")
            insertSleep(fromDate = "2018-03-05T23:30:00+01:00", toDate = "2018-03-06T06:55:00+01:00")
            insertSleep(fromDate = "2018-03-06T22:50:00+01:00", toDate = "2018-03-07T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-07T22:50:00+01:00", toDate = "2018-03-08T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-08T22:50:00+01:00", toDate = "2018-03-09T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-09T22:50:00+01:00", toDate = "2018-03-10T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-10T22:50:00+01:00", toDate = "2018-03-11T06:20:00+01:00")

            // Current week
            insertSleep(fromDate = "2018-03-11T22:31:00+01:00", toDate = "2018-03-12T02:30:00+01:00")
            insertSleep(fromDate = "2018-03-12T22:32:00+01:00", toDate = "2018-03-13T07:02:00+01:00")
            insertSleep(fromDate = "2018-03-13T22:33:00+01:00", toDate = "2018-03-14T01:33:00+01:00")
            insertSleep(fromDate = "2018-03-14T21:30:00+01:00", toDate = "2018-03-15T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-15T22:35:00+01:00", toDate = "2018-03-16T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-16T22:36:00+01:00", toDate = "2018-03-17T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-17T22:37:00+01:00", toDate = "2018-03-18T06:20:00+01:00")
        }

        val scenario = activityRule.scenario

        val statisticsRobot = mainRobot.clickStatisticsTab()

        statisticsRobot.selectYearFilter()

        scenario.onActivity { activity ->
            Screenshot.snapActivity(activity).setName("testCompareStatisticsBetweenYears").record()
        }
    }
}
