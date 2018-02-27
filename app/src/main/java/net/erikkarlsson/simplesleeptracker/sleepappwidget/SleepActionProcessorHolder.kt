package net.erikkarlsson.simplesleeptracker.sleepappwidget

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import net.erikkarlsson.simplesleeptracker.sleepappwidget.processor.LoadCurrentSleep
import net.erikkarlsson.simplesleeptracker.sleepappwidget.processor.ToggleSleep
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepActionProcessorHolder @Inject constructor(
        private val loadCurrentSleep: LoadCurrentSleep,
        private val toggleSleep: ToggleSleep) {

    internal val actionProcessor =
            ObservableTransformer<WidgetAction, WidgetResult> { actions ->
                actions.publish { shared ->
                    Observable.merge(
                            shared.ofType(WidgetAction.LoadCurrentSleepAction::class.java).compose(loadCurrentSleep.processor),
                            shared.ofType(WidgetAction.ToggleSleepAction::class.java).compose(toggleSleep.processor))
                }
            }
}