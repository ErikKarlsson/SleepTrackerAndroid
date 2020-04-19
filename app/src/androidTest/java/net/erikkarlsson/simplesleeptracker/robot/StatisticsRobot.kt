package net.erikkarlsson.simplesleeptracker.robot

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.erikkarlsson.simplesleeptracker.R
import org.hamcrest.Matchers.hasToString
import org.hamcrest.Matchers.startsWith
import java.util.EnumSet.allOf

class StatisticsRobot {

    fun selectYearFilter() {
        onView(withId(R.id.spinner)).perform(click())

        onData(hasToString(startsWith("Year")))
                .perform(click())
    }

}
