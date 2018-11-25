package net.erikkarlsson.simplesleeptracker.di.module

import dagger.Binds
import dagger.Module
import net.erikkarlsson.simplesleeptracker.data.SystemNotifications
import net.erikkarlsson.simplesleeptracker.domain.Notifications

@Module
abstract class NotificationModule {

    @Binds
    abstract fun bindsNotifications(androidNotifications: SystemNotifications): Notifications
}
