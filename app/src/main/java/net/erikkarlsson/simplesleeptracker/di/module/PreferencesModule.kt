package net.erikkarlsson.simplesleeptracker.di.module

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.data.preferences.PreferencesRepository
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import javax.inject.Singleton

@Module
abstract class PreferencesModule {

    @Binds
    abstract fun bindsPreferencesDataSource(preferencesRepository: PreferencesRepository)
            : PreferencesDataSource

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
        fun providesFlowSharedPreferences(sharedPreferences: SharedPreferences) =
                FlowSharedPreferences(sharedPreferences)
    }

}
