package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import net.erikkarlsson.simplesleeptracker.data.widget.WidgetRepository
import net.erikkarlsson.simplesleeptracker.domain.WidgetDataSource

@Module
abstract class WidgetModule {

    @Binds
    abstract fun bindsWidgetDataSource(widgetRepository: WidgetRepository)
            : WidgetDataSource

}
