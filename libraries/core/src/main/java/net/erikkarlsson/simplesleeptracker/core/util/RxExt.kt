package net.erikkarlsson.simplesleeptracker.core.util

import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.concurrent.TimeUnit

fun View.clicksThrottle(disposables: CompositeDisposable, action: () -> Unit) {
    this.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ action.invoke() })
            .addTo(disposables)
}
