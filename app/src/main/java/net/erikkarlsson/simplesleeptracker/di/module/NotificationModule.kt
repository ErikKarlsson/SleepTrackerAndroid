package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import net.erikkarlsson.simplesleeptracker.data.SystemNotifications
import net.erikkarlsson.simplesleeptracker.domain.Notifications

@Module
@InstallIn(ApplicationComponent::class)
abstract class NotificationModule {

    @Binds
    abstract fun bindsNotifications(androidNotifications: SystemNotifications): Notifications
}
