package net.erikkarlsson.simplesleeptracker.di.module

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferencesModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context);

    @Provides
    @Singleton
    fun providesRxSharedPreferences(sharedPreferences: SharedPreferences) =
            RxSharedPreferences.create(sharedPreferences);
}