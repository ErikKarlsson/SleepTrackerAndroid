package net.erikkarlsson.simplesleeptracker.details

import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import javax.inject.Inject

class DetailViewModel @Inject constructor(detailComponent: DetailComponent) :
        ElmViewModel<DetailState, DetailMsg, DetailCmd>(detailComponent)