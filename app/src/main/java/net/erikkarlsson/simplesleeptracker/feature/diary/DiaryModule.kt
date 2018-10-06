package net.erikkarlsson.simplesleeptracker.feature.diary

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.Module
import dagger.Provides
import net.erikkarlsson.simplesleeptracker.di.scope.FragmentScope

@Module
class DiaryModule {

    @Provides
    @FragmentScope
    fun providesLinearLayoutManager(context: Context): androidx.recyclerview.widget.LinearLayoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(context)
}