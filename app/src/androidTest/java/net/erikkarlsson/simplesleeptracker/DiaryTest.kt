package net.erikkarlsson.simplesleeptracker

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import net.erikkarlsson.simplesleeptracker.robot.DiaryRobot
import net.erikkarlsson.simplesleeptracker.robot.MainRobot
import net.erikkarlsson.simplesleeptracker.util.TestComponentRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiaryTest {

    val main = ActivityTestRule(MainActivity::class.java, false, false)
    val component = TestComponentRule()
    val mainRobot = MainRobot()

    lateinit var diaryRobot: DiaryRobot

    @get:Rule
    var chain: TestRule = RuleChain.outerRule(component).around(main)

    @Test
    fun testToggleSleepNavigateToDetailsFlow() {

        with(component) {
            // Given current time
            mockDateTimeNow("2018-03-14T22:30:00+01:00")

            // data for current week
            insertSleep(fromDate = "2018-03-11T22:35:00+01:00", toDate = "2018-03-12T06:30:00+01:00")
            insertSleep(fromDate = "2018-03-12T22:30:00+01:00", toDate = "2018-03-13T07:02:00+01:00")
            insertSleep(fromDate = "2018-03-13T21:20:00+01:00", toDate = "2018-03-14T06:33:00+01:00")
        }

        main.launchActivity(null)
        diaryRobot = mainRobot.clickDiaryTab()
        diaryRobot.clickItem(0) // When clicking newly added sleep item
                .isShowingDetailsScreen() // Then should navigate to details screen

        Thread.sleep(999999)
    }
}