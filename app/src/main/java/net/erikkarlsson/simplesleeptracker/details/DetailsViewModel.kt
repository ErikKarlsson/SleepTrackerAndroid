package net.erikkarlsson.simplesleeptracker.details

import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsCmd
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsComponent
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsMsg
import net.erikkarlsson.simplesleeptracker.statistics.StatisticsState
import javax.inject.Inject

class DetailsViewModel @Inject constructor(statisticsComponent: StatisticsComponent) :
        ElmViewModel<StatisticsState, StatisticsMsg, StatisticsCmd>(statisticsComponent)