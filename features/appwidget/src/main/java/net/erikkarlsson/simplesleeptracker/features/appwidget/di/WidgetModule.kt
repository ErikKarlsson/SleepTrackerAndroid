package net.erikkarlsson.simplesleeptracker.features.appwidget.di

import dagger.Binds
import dagger.Module
import net.erikkarlsson.simplesleeptracker.domain.WidgetDataSource
import net.erikkarlsson.simplesleeptracker.features.appwidget.data.WidgetRepository

@Module
abstract class WidgetModule {

    @Binds
    abstract fun bindsWidgetDataSource(widgetRepository: WidgetRepository)
            : WidgetDataSource

}
