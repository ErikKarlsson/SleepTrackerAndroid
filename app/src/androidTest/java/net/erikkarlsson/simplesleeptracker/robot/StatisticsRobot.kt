package net.erikkarlsson.simplesleeptracker.robot

import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.espresso.ViewActions.clickRecyclerViewItemAtPosition
import net.erikkarlsson.simplesleeptracker.espresso.ViewActions.clickViewWithId

class StatisticsRobot {

    fun clickItem(position: Int): DetailsRobot {
        clickRecyclerViewItemAtPosition(R.id.recyclerView, position)
        return DetailsRobot()
    }

    fun clickToggleSleepButton(): StatisticsRobot {
        clickViewWithId(R.id.toggleSleepButton)
        return this
    }

}