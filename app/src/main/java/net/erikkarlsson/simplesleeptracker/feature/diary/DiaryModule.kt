package net.erikkarlsson.simplesleeptracker.feature.diary

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.di.scope.FragmentScope

@Module
class DiaryModule {

    @Provides
    @FragmentScope
    fun providesLinearLayoutManager(context: Context): LinearLayoutManager =
            LinearLayoutManager(context)
}