package net.erikkarlsson.simplesleeptracker.di.module

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import net.erikkarlsson.simplesleeptracker.features.backup.BackupSleepWorker
import net.erikkarlsson.simplesleeptracker.features.backup.ChildWorkerFactory
import net.erikkarlsson.simplesleeptracker.features.backup.RestoreSleepWorker
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@Module
abstract class WorkerBindingModule {

    @Binds
    @IntoMap
    @WorkerKey(RestoreSleepWorker::class)
    internal abstract fun bindRestoreSleepWorker(factory: RestoreSleepWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(BackupSleepWorker::class)
    internal abstract fun bindBackupSleepWorker(factory: BackupSleepWorker.Factory): ChildWorkerFactory
}

class MyWorkerFactory @Inject constructor(
        private val workerFactories: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {
    override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
    ): ListenableWorker? {
        val foundEntry =
                workerFactories.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key) }
        val factoryProvider = foundEntry?.value
                ?: throw IllegalArgumentException("unknown worker class name: $workerClassName")
        return factoryProvider.get().create(appContext, workerParameters)
    }
}
