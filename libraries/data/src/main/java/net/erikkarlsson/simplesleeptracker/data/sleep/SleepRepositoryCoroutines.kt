package net.erikkarlsson.simplesleeptracker.data.sleep

import androidx.paging.PagedList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import net.erikkarlsson.simplesleeptracker.data.FlowPagedListBuilder
import net.erikkarlsson.simplesleeptracker.data.entity.SleepEntity
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSourceCoroutines
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import javax.inject.Inject

@ExperimentalCoroutinesApi
class SleepRepositoryCoroutines @Inject constructor(private val sleepDao: SleepDao,
                                                    private val sleepMapper: SleepMapper) : SleepDataSourceCoroutines {

    override fun getCount(): Flow<Int> =
            sleepDao.getSleepCountFlow().distinctUntilChanged()

    override fun getSleepPaged(): Flow<PagedList<Sleep>> {
        val dataSource = sleepDao.getSleepFactory()
                .map { sleepMapper.mapFromEntity(it) }

        val config = PagedList.Config.Builder()
                .setPageSize(50)
                .build()

        return FlowPagedListBuilder(dataSource, config).buildFlow()
    }

    override suspend fun getSleep(id: Int): Flow<Sleep> =
            sleepDao.getSleepFlow(id)
                    .map {
                        if (it.isEmpty()) Sleep.empty()
                        else sleepMapper.mapFromEntity(it[0])
                    }

    override suspend fun getCurrent(): Sleep {
        val sleepEntity = sleepDao.getCurrentSleepCoroutines();
        return when (sleepEntity == null) {
            true -> Sleep.empty()
            false -> sleepMapper.mapFromEntity(sleepEntity)
        }
    }

    override fun getCurrentFlow(): Flow<Sleep> =
        sleepDao.getCurrentSleepFlow()
                .map {
                    when (it.isEmpty()) {
                        true -> Sleep.empty()
                        false -> sleepMapper.mapFromEntity(it[0])
                    }
                }

    override suspend fun update(updatedSleep: Sleep): Int {
        val sleepEntity = sleepMapper.mapToEntity(updatedSleep)
        return sleepDao.updateSleepCoroutine(sleepEntity)
    }

    override suspend fun delete(sleep: Sleep) =
        sleepDao.deleteSleepCoroutines(sleepMapper.mapToEntity(sleep))

    override suspend fun deleteAll() =
        sleepDao.deleteAllSleepCoroutines()

    override suspend fun insert(newSleep: Sleep): Long {
        val sleepEntity = sleepMapper.mapToEntity(newSleep)
        return sleepDao.insertSleepSuspend(sleepEntity)
    }

    private fun firstOrEmpty(sleepList: List<SleepEntity>): SleepEntity =
            sleepList.firstOrNull() ?: SleepEntity.empty()
}
