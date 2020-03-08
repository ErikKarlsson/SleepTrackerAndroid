package net.erikkarlsson.simplesleeptracker.di.module

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.tfcporciuncula.flow.FlowSharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.data.preferences.FlowPreferencesRepository
import net.erikkarlsson.simplesleeptracker.data.preferences.RxPreferencesRepository
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSourceFlow
import javax.inject.Singleton

@Module
abstract class PreferencesModule {

    @Binds
    abstract fun bindsPreferencesDataSource(rxPreferencesRepository: RxPreferencesRepository)
            : PreferencesDataSource

    @Binds
    abstract fun bindsPreferencesDataSourceFlow(flowPreferencesRepository: FlowPreferencesRepository)
            : PreferencesDataSourceFlow

    @Module
    companion object {
        @Provides
        @Singleton
        @JvmStatic
        fun providesSharedPreferences(context: Context): SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)

        @Provides
        @Singleton
        @JvmStatic
        fun providesRxSharedPreferences(sharedPreferences: SharedPreferences) =
                RxSharedPreferences.create(sharedPreferences)

        @Provides
        @Singleton
        @JvmStatic
        fun providesFlowSharedPreferences(sharedPreferences: SharedPreferences) =
                FlowSharedPreferences(sharedPreferences)
    }

}
