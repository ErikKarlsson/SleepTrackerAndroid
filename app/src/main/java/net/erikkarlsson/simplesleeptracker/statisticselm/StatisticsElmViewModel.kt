package net.erikkarlsson.simplesleeptracker.statisticselm

import cz.inventi.elmdroid.ElmViewModel
import javax.inject.Inject

class StatisticsElmViewModel @Inject constructor(statisticsComponent: StatisticsComponent) :
        ElmViewModel<StatisticsState, StatisticsMsg, StatisticsCmd>(statisticsComponent)