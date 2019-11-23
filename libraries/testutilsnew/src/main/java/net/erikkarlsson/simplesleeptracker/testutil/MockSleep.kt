package net.erikkarlsson.simplesleeptracker.testutil

import net.easypark.dateutil.offsetDateTime
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep

fun mockSleep(fromDate: String, toDate: String) =
        Sleep(fromDate = fromDate.offsetDateTime, toDate = toDate.offsetDateTime)
