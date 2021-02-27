package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.erikkarlsson.simplesleeptracker.data.SystemNotifications
import net.erikkarlsson.simplesleeptracker.domain.Notifications

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    abstract fun bindsNotifications(androidNotifications: SystemNotifications): Notifications
}
