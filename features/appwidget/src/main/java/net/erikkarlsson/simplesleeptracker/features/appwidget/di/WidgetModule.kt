package net.erikkarlsson.simplesleeptracker.features.appwidget.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.erikkarlsson.simplesleeptracker.domain.WidgetDataSource
import net.erikkarlsson.simplesleeptracker.features.appwidget.data.WidgetRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class WidgetModule {

    @Binds
    abstract fun bindsWidgetDataSource(widgetRepository: WidgetRepository)
            : WidgetDataSource

}
