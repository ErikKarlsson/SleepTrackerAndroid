package net.erikkarlsson.simplesleeptracker

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.ViewHelpers
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import net.erikkarlsson.simplesleeptracker.dateutil.offsetDateTime
import net.erikkarlsson.simplesleeptracker.di.module.DateTimeModule
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.features.diary.SleepAdapter
import net.erikkarlsson.simplesleeptracker.features.diary.databinding.ItemSleepBinding
import net.erikkarlsson.simplesleeptracker.robot.MainRobot
import net.erikkarlsson.simplesleeptracker.util.TestUtil
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

// TODO (erikkarlsson): Module uninstall across all tests currently not supported by Hilt, duplicate it for now.
@UninstallModules(DateTimeModule::class)
@HiltAndroidTest
class DiaryTest {

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
    fun testDiary() {
        val scenario = activityRule.scenario

        with(testUtil) {
            // Given current time
            mockDateTimeNow("2018-03-14T22:30:00+01:00")

            // data for current week
            insertSleep(fromDate = "2018-03-11T22:35:00+01:00", toDate = "2018-03-12T06:30:00+01:00")
            insertSleep(fromDate = "2018-03-12T22:30:00+01:00", toDate = "2018-03-13T07:02:00+01:00")
            insertSleep(fromDate = "2018-03-13T21:20:00+01:00", toDate = "2018-03-14T06:33:00+01:00")
        }

        mainRobot.clickDiaryTab()

        scenario.onActivity { activity ->
            Screenshot.snapActivity(activity).setName("testDiary").record()
        }
    }

    @Test
    @Ignore
    fun testSleepItem() {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val inflater = LayoutInflater.from(targetContext)
        val view = inflater.inflate(R.layout.item_sleep, null, false)

        view.findViewById<ImageView>(R.id.iconImage).setImageResource(R.drawable.ic_good_sleep)
        view.findViewById<TextView>(R.id.dateText).setText("Ons 14 mars")

        ViewHelpers.setupView(view).setExactWidthDp(300).layout()

        Screenshot.snap(view).record()
    }

    @Test
    fun testSleepViewHolder() {
        val binding = ItemSleepBinding.inflate(testUtil.getInflater(), null, false)

        val viewHolder = SleepAdapter.ViewHolder(binding) {}

        val sleep = Sleep(fromDate = "2018-03-11T22:35:00+01:00".offsetDateTime,
                toDate = "2018-03-12T06:30:00+01:00".offsetDateTime)

        viewHolder.bindSleep(sleep)

        val view = binding.root

        ViewHelpers.setupView(view).setExactWidthDp(300).layout()
        Screenshot.snap(view).record()
    }
}
