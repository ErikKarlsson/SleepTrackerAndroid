package net.erikkarlsson.simplesleeptracker.details

import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.elm.Msg
import javax.inject.Inject

class DetailsViewModel @Inject constructor(detailsComponent: DetailsComponent) :
        ElmViewModel<DetailsState, Msg, DetailsCmd>(detailsComponent)