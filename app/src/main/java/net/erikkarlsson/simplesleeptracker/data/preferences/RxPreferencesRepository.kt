package net.erikkarlsson.simplesleeptracker.data.preferences

import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import javax.inject.Inject

class RxPreferencesRepository @Inject constructor(private val rxSharedPreferences: RxSharedPreferences)
    : PreferencesDataSource {
    override fun set(key: String, value: Long) =
            rxSharedPreferences.getLong(key).set(value)

    override fun set(key: String, value: Int) =
            rxSharedPreferences.getInteger(key).set(value)

    override fun getLong(key: String): Observable<Long> =
            rxSharedPreferences.getLong(key, 0).asObservable()

    override fun getInt(key: String): Observable<Int> =
            rxSharedPreferences.getInteger(key, 0).asObservable()

    override fun clear() =
            rxSharedPreferences.clear()
}
