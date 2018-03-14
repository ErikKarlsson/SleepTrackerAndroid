package net.erikkarlsson.simplesleeptracker.robot

import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.espresso.ViewActions.clickRecyclerViewItemAtPosition

class StatisticsRobot {

    fun clickItem(position: Int): StatisticsRobot {
        clickRecyclerViewItemAtPosition(R.id.recyclerView, position)
        return this
    }

}