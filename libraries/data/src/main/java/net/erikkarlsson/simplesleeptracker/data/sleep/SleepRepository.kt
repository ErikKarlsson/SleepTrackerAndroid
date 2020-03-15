package net.erikkarlsson.simplesleeptracker.data.sleep

import androidx.paging.PagedList
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import net.erikkarlsson.simplesleeptracker.data.FlowPagedListBuilder
import net.erikkarlsson.simplesleeptracker.data.entity.SleepEntity
import net.erikkarlsson.simplesleeptracker.domain.SleepDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import javax.inject.Inject

@ExperimentalCoroutinesApi
class SleepRepository @Inject constructor(private val sleepDao: SleepDao,
                                          private val sleepMapper: SleepMapper) : SleepDataSource {

    override fun getCountFlow(): Flow<Int> =
            sleepDao.getSleepCountFlow().distinctUntilChanged()

    override fun getSleepPaged(): Flow<PagedList<Sleep>> {
        val dataSource = sleepDao.getSleepFactory()
                .map { sleepMapper.mapFromEntity(it) }

        val config = PagedList.Config.Builder()
                .setPageSize(50)
                .build()

        return FlowPagedListBuilder(dataSource, config).buildFlow()
    }

    override fun getSleepListFlow(): Flow<ImmutableList<Sleep>> =
            sleepDao.getSleepFlow()
                    .mapLatest { sleepList ->
                        val sleepListBuilder = ImmutableList.Builder<Sleep>()

                        sleepList.map { sleepMapper.mapFromEntity(it) }
                                .onEach { sleepListBuilder.add(it) }

                        sleepListBuilder.build()
                    }

    override fun getSleepFlow(id: Int): Flow<Sleep> =
            sleepDao.getSleepFlow(id)
                    .map {
                        if (it.isEmpty()) Sleep.empty()
                        else sleepMapper.mapFromEntity(it[0])
                    }

    override suspend fun getCurrent(): Sleep {
        val sleepEntity = sleepDao.getCurrentSleep();
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
        return sleepDao.updateSleep(sleepEntity)
    }

    override suspend fun delete(sleep: Sleep) =
            sleepDao.deleteSleep(sleepMapper.mapToEntity(sleep))

    override suspend fun deleteAll() =
            sleepDao.deleteAllSleep()

    override suspend fun insert(newSleep: Sleep): Long {
        val sleepEntity = sleepMapper.mapToEntity(newSleep)
        return sleepDao.insertSleep(sleepEntity)
    }

    override suspend fun insertAll(sleepList: ImmutableList<Sleep>) {
        val sleepListBuilder = ImmutableList.Builder<SleepEntity>()
        for (sleep in sleepList) {
            sleepListBuilder.add(sleepMapper.mapToEntity(sleep))
        }
        sleepDao.insertAll(sleepListBuilder.build())
    }
}
