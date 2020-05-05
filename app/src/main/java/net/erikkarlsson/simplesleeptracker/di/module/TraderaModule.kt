package net.erikkarlsson.simplesleeptracker.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import net.erikkarlsson.simplesleeptracker.data.draft.DraftContentDao
import net.erikkarlsson.simplesleeptracker.data.draft.DraftImageDao
import net.erikkarlsson.simplesleeptracker.data.draft.ListingDraftDao
import net.erikkarlsson.simplesleeptracker.data.draft.TraderaDatabase
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class TraderaModule {

    companion object {

        @Provides
        @Singleton
        fun provideTraderaDatabase(@ApplicationContext context: Context): TraderaDatabase = TraderaDatabase.getInstance(context)

        @Provides
        @Singleton
        fun provideDraftContentDao(sleepDatabase: TraderaDatabase): DraftContentDao = sleepDatabase.draftDao()

        @Provides
        @Singleton
        fun provideListingDraftDao(sleepDatabase: TraderaDatabase): ListingDraftDao = sleepDatabase.listingDao()

        @Provides
        @Singleton
        fun provideImageDao(sleepDatabase: TraderaDatabase): DraftImageDao = sleepDatabase.imageDao()
    }

}
