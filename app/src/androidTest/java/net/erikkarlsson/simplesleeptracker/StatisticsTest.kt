package net.erikkarlsson.simplesleeptracker

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.robot.StatisticsRobot
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsActivity
import net.erikkarlsson.simplesleeptracker.util.TestComponentRule
import net.erikkarlsson.simplesleeptracker.util.offsetDateTime
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatisticsTest {

    val component = TestComponentRule()

    val main = ActivityTestRule(StatisticsActivity::class.java, false, false)

    val robot = StatisticsRobot()

    @get:Rule
    var chain: TestRule = RuleChain.outerRule(component).around(main)

    @Test
    fun testLaunch() {
        component.insertSleep(Sleep(fromDate = "2018-03-07T21:30:00+01:00".offsetDateTime, toDate = "2018-03-08T08:30:00+01:00".offsetDateTime))
        component.insertSleep(Sleep(fromDate = "2018-03-08T20:30:00+01:00".offsetDateTime, toDate = "2018-03-09T08:55:00+01:00".offsetDateTime))
        component.insertSleep(Sleep(fromDate = "2018-03-09T22:50:00+01:00".offsetDateTime, toDate = "2018-03-10T07:20:00+01:00".offsetDateTime))
        component.insertSleep(Sleep(fromDate = "2018-03-14T22:35:00+01:00".offsetDateTime, toDate = "2018-03-15T09:30:00+01:00".offsetDateTime))
        component.insertSleep(Sleep(fromDate = "2018-03-15T19:30:00+01:00".offsetDateTime, toDate = "2018-03-16T10:02:00+01:00".offsetDateTime))
        component.insertSleep(Sleep(fromDate = "2018-03-16T23:20:00+01:00".offsetDateTime, toDate = "2018-03-17T09:33:00+01:00".offsetDateTime))

        main.launchActivity(null)

        component.mockDateTimeNow("2018-03-17T22:30:00+01:00".offsetDateTime)
        robot.clickToggleSleepButton()
        component.mockDateTimeNow("2018-03-18T06:30:00+01:00".offsetDateTime)
        robot.clickToggleSleepButton()

        Thread.sleep(9999999)

        robot.clickItem(2)
    }

    @Test
    fun testLaunch2() {
        main.launchActivity(null)
        Assert.assertEquals(1, 1)
    }
}