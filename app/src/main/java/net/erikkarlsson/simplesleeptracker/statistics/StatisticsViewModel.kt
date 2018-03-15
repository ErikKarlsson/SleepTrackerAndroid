package net.erikkarlsson.simplesleeptracker.statistics

import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import javax.inject.Inject

class StatisticsViewModel @Inject constructor(statisticsComponent: StatisticsComponent) :
        ElmViewModel<StatisticsState, StatisticsMsg, StatisticsCmd>(statisticsComponent)