package net.erikkarlsson.simplesleeptracker.robot

import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.espresso.ViewActions

class HomeRobot {

    fun clickToggleSleepButton(): HomeRobot {
        ViewActions.clickViewWithId(R.id.toggleSleepButton)
        return this
    }
}