package net.erikkarlsson.simplesleeptracker.features.diary

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class DiaryModule {

    @Provides
    @FragmentScoped
    fun providesLinearLayoutManager(@ActivityContext context: Context): LinearLayoutManager =
            LinearLayoutManager(context)
}
