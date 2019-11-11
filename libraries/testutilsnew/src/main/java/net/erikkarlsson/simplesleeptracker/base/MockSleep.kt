package net.erikkarlsson.simplesleeptracker.base

import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.util.offsetDateTime

fun mockSleep(fromDate: String, toDate: String) =
        Sleep(fromDate = fromDate.offsetDateTime, toDate = toDate.offsetDateTime)
