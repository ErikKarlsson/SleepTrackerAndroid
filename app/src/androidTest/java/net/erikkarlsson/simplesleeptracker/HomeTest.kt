package net.erikkarlsson.simplesleeptracker

import androidx.test.ext.junit.rules.activityScenarioRule
import com.facebook.testing.screenshot.Screenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import net.erikkarlsson.simplesleeptracker.di.module.DateTimeModule
import net.erikkarlsson.simplesleeptracker.robot.HomeRobot
import net.erikkarlsson.simplesleeptracker.robot.MainRobot
import net.erikkarlsson.simplesleeptracker.util.TestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@UninstallModules(DateTimeModule::class)
@HiltAndroidTest
class HomeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = activityScenarioRule<MainActivity>()

    @Inject
    lateinit var testUtil: TestUtil

    val mainRobot = MainRobot()
    val homeRobot = HomeRobot()

    @Before
    fun init() {
        hiltRule.inject()
        testUtil.initTest()
    }

    @Test
    fun testHomeTab() {
        val scenario = activityRule.scenario
        scenario.onActivity { activity ->
            Screenshot.snapActivity(activity).setName("testHomeTab").record()
        }
    }

    @Test
    fun testToggleSleepNavigateToDetailsFlow() {
        val scenario = activityRule.scenario

        with(testUtil) {
            // Given is evening
            mockDateTimeNow("2018-03-17T22:30:00+01:00")

            // toggle to sleeping
            homeRobot.clickToggleSleepButton()

            // is morning the following day
            mockDateTimeNow("2018-03-18T06:30:00+01:00")
        }

        homeRobot.clickToggleSleepButton() // toggle to awake

        val diaryRobot = mainRobot.clickDiaryTab()
        diaryRobot.clickItem(0) // When clicking newly added sleep item
                .isShowingDetailsScreen() // Then should navigate to details screen

        scenario.onActivity { activity ->
            Screenshot.snapActivity(activity).setName("testToggleSleepNavigateToDetailsFlow").record()
        }
    }
}
