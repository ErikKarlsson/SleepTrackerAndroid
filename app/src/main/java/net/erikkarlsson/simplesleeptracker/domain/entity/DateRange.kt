package net.erikkarlsson.simplesleeptracker.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class DateRange(val from: LocalDate, val to: LocalDate) : Parcelable {

    companion object {
        fun empty(): DateRange = DateRange(LocalDate.MIN, LocalDate.MIN)
    }
}