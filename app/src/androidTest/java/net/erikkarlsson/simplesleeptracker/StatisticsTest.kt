package net.erikkarlsson.simplesleeptracker

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import net.erikkarlsson.simplesleeptracker.domain.Sleep
import net.erikkarlsson.simplesleeptracker.robot.StatisticsRobot
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsActivity
import net.erikkarlsson.simplesleeptracker.util.TestComponentRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.threeten.bp.OffsetDateTime

@RunWith(AndroidJUnit4::class)
class StatisticsTest {

    val component = TestComponentRule()

    val main = ActivityTestRule(StatisticsActivity::class.java, false, false)

    val robot = StatisticsRobot()

    @get:Rule
    var chain: TestRule = RuleChain.outerRule(component).around(main)

    @Test
    fun testLaunch() {
        component.insertSleep(Sleep(fromDate = OffsetDateTime.now().minusWeeks(1), toDate = OffsetDateTime.now().minusWeeks(1).plusHours(7)))
        component.insertSleep(Sleep(fromDate = OffsetDateTime.now().minusWeeks(1), toDate = OffsetDateTime.now().minusWeeks(1).plusHours(8)))
        component.insertSleep(Sleep(fromDate = OffsetDateTime.now().minusWeeks(1), toDate = OffsetDateTime.now().minusWeeks(1).plusHours(7)))

        component.insertSleep(Sleep(fromDate = OffsetDateTime.now(), toDate = OffsetDateTime.now().plusHours(8)))
        component.insertSleep(Sleep(fromDate = OffsetDateTime.now(), toDate = OffsetDateTime.now().plusHours(9)))
        component.insertSleep(Sleep(fromDate = OffsetDateTime.now(), toDate = OffsetDateTime.now().plusHours(8)))

        main.launchActivity(null)

        robot.clickItem(2)

        Assert.assertEquals(1, 1)

    }

    @Test
    fun testLaunch2() {
        main.launchActivity(null)
        Assert.assertEquals(1, 1)
    }
}