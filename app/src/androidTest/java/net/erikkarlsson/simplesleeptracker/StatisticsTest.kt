package net.erikkarlsson.simplesleeptracker

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsActivity
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

    @get:Rule
    var chain: TestRule = RuleChain.outerRule(component).around(main)

    @Test
    fun testLaunch() {
        main.launchActivity(null)
        Assert.assertEquals(1, 1)
    }

    @Test
    fun testLaunch2() {
        main.launchActivity(null)
        Assert.assertEquals(1, 1)
    }
}