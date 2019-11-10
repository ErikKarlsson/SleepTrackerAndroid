package net.erikkarlsson.simplesleeptracker.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class DateRange(val from: LocalDate, val to: LocalDate) : Parcelable {

    companion object {
        fun empty(): DateRange = DateRange(LocalDate.MIN, LocalDate.MIN)
        fun infinite() = DateRange(LocalDate.parse("1000-01-01"), LocalDate.parse("9999-01-01"))
    }
}
