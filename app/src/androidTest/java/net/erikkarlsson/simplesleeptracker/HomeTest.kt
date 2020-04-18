package net.erikkarlsson.simplesleeptracker

import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.facebook.testing.screenshot.Screenshot
import net.erikkarlsson.simplesleeptracker.robot.HomeRobot
import net.erikkarlsson.simplesleeptracker.robot.MainRobot
import net.erikkarlsson.simplesleeptracker.util.TestComponentRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeTest {

    val main = ActivityTestRule(MainActivity::class.java, false, false)
    val component = TestComponentRule()
    val mainRobot = MainRobot()
    val homeRobot = HomeRobot()

    @get:Rule
    var chain: TestRule = RuleChain.outerRule(component).around(main)

    @Test
    fun testHomeTab() {
        val activity = main.launchActivity(null)
        Screenshot.snapActivity(activity).record()
    }

    @Test
    fun testToggleSleepNavigateToDetailsFlow() {
        main.launchActivity(null)

        with(component) {
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

        Screenshot.snapActivity(component.getCurrentActivity()).record()
    }
}
