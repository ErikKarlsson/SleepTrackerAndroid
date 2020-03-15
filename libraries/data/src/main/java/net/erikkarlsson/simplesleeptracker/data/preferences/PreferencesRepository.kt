package net.erikkarlsson.simplesleeptracker.data.preferences

import com.tfcporciuncula.flow.FlowSharedPreferences
import kotlinx.coroutines.flow.Flow
import net.erikkarlsson.simplesleeptracker.domain.PreferencesDataSource
import javax.inject.Inject

class PreferencesRepository @Inject constructor(private val flowSharedPreferences: FlowSharedPreferences)
    : PreferencesDataSource {

    override fun set(key: String, value: Long) =
             flowSharedPreferences.getLong(key).set(value)

    override fun set(key: String, value: Int) =
            flowSharedPreferences.getInt(key).set(value)

    override fun set(key: String, value: Boolean) =
            flowSharedPreferences.getBoolean(key).set(value)

    override fun getLong(key: String): Flow<Long> =
            flowSharedPreferences.getLong(key, 0).asFlow()

    override fun getInt(key: String): Flow<Int> =
            flowSharedPreferences.getInt(key, 0).asFlow()

    override fun getBoolean(key: String): Flow<Boolean> =
            flowSharedPreferences.getBoolean(key, false).asFlow()

    override fun clear() =
            flowSharedPreferences.clear()
}
