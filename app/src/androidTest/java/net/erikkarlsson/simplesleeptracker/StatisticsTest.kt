package net.erikkarlsson.simplesleeptracker

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import net.erikkarlsson.simplesleeptracker.robot.StatisticsRobot
import net.erikkarlsson.simplesleeptracker.util.TestComponentRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatisticsTest {

    val main = ActivityTestRule(MainActivity::class.java, false, false)
    val component = TestComponentRule()
    val robot = StatisticsRobot()

    @get:Rule
    var chain: TestRule = RuleChain.outerRule(component).around(main)

    @Test
    fun testToggleSleepNavigateToDetailsFlow() {
        main.launchActivity(null)

        with(component) {
            // Given is evening
            mockDateTimeNow("2018-03-17T22:30:00+01:00")

            // toggle to sleeping
            robot.clickToggleSleepButton()

            // is morning the following day
            mockDateTimeNow("2018-03-18T06:30:00+01:00")
        }

        robot.clickToggleSleepButton() // toggle to awake
            .clickItem(0) // When clicking newly added sleep item
            .isShowingDetailsScreen() // Then should navigate to details screen
    }

    @Test
    fun testCompareStatisticsBetweenWeeks() {
        with(component) {
            // Given current time
            mockDateTimeNow("2018-03-14T22:30:00+01:00")

            // data for previous week
            insertSleep(fromDate = "2018-03-04T23:30:00+01:00", toDate = "2018-03-05T06:30:00+01:00")
            insertSleep(fromDate = "2018-03-05T23:30:00+01:00", toDate = "2018-03-06T06:55:00+01:00")
            insertSleep(fromDate = "2018-03-06T22:50:00+01:00", toDate = "2018-03-07T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-07T22:50:00+01:00", toDate = "2018-03-08T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-08T22:50:00+01:00", toDate = "2018-03-09T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-09T22:50:00+01:00", toDate = "2018-03-10T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-10T22:50:00+01:00", toDate = "2018-03-11T06:20:00+01:00")

            // data for current week
            insertSleep(fromDate = "2018-03-11T22:31:00+01:00", toDate = "2018-03-12T02:30:00+01:00")
            insertSleep(fromDate = "2018-03-12T22:32:00+01:00", toDate = "2018-03-13T07:02:00+01:00")
            insertSleep(fromDate = "2018-03-13T22:33:00+01:00", toDate = "2018-03-14T01:33:00+01:00")
            insertSleep(fromDate = "2018-03-14T21:30:00+01:00", toDate = "2018-03-15T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-15T22:35:00+01:00", toDate = "2018-03-16T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-16T22:36:00+01:00", toDate = "2018-03-17T06:20:00+01:00")
            insertSleep(fromDate = "2018-03-17T22:37:00+01:00", toDate = "2018-03-18T06:20:00+01:00")
        }

        main.launchActivity(null)

        Thread.sleep(99999999)

        // TODO (erikkarlsson): Verify statistics are rendered in view
    }
}