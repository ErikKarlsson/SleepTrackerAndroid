package net.erikkarlsson.simplesleeptracker.features.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class DetailActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            val sleepId = DetailActivityArgs.fromBundle(intent.extras).sleepId

            supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, DetailFragment.newInstance(sleepId))
                    .commitNow()
        }
    }

}
