package net.erikkarlsson.simplesleeptracker.core.util

import android.os.SystemClock
import android.view.View

fun View.clicksDebounce(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            when (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) {
                true -> return
                false -> action()
            }

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}
