package net.erikkarlsson.simplesleeptracker.feature.home.domain

import io.reactivex.Observable
import net.erikkarlsson.simplesleeptracker.domain.FileBackupDataSource
import net.erikkarlsson.simplesleeptracker.domain.entity.Profile
import net.erikkarlsson.simplesleeptracker.domain.task.ObservableTask
import javax.inject.Inject

class GetProfileTask @Inject constructor(private val backupDataSource: FileBackupDataSource)
    : ObservableTask<Profile, ObservableTask.None>
{
    override fun execute(params: ObservableTask.None): Observable<Profile> =
            backupDataSource.getLastBackupTimestamp().map { Profile(it) }

}
