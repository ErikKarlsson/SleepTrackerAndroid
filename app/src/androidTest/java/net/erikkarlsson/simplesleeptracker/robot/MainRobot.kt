package net.erikkarlsson.simplesleeptracker.robot

import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.espresso.ViewActions.clickViewWithId

class MainRobot {

    fun clickDiaryTab(): DiaryRobot {
        clickViewWithId(R.id.diary)
        return DiaryRobot()
    }

}