package net.erikkarlsson.simplesleeptracker.robot

import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.espresso.ViewActions

class DiaryRobot {

    fun clickItem(position: Int): DetailsRobot {
        ViewActions.clickRecyclerViewItemAtPosition(R.id.recyclerView, position)
        return DetailsRobot()
    }

}