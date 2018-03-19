package net.erikkarlsson.simplesleeptracker.robot

import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.espresso.ViewAssertions.shouldDisplayView

class DetailsRobot {
    fun isShowingDetailsScreen() {
        shouldDisplayView(R.id.details_text)
    }
}