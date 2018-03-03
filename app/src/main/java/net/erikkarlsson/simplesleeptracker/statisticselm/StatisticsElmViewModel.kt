package net.erikkarlsson.simplesleeptracker.statisticselm

import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import javax.inject.Inject

class StatisticsElmViewModel @Inject constructor(statisticsComponent: StatisticsComponent) :
        ElmViewModel<StatisticsState, StatisticsMsg, StatisticsCmd>(statisticsComponent)