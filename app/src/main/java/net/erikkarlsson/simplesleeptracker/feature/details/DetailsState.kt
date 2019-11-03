package net.erikkarlsson.simplesleeptracker.feature.details

import android.os.Parcelable
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import kotlinx.android.parcel.Parcelize
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

@Parcelize
data class DetailsArgs(val sleepId: Int) : Parcelable

data class DetailsState(
        val sleepId: Int,
        val sleep: Async<Sleep> = Uninitialized,
        val isDeleted: Boolean = false
) : MvRxState {
    constructor(args: DetailsArgs) : this(sleepId = args.sleepId)

    val hoursSlept: Float get() = sleep.invoke()?.hours ?: 0f

    companion object {
        fun empty() = DetailsState(0, Uninitialized, false)
    }
}

