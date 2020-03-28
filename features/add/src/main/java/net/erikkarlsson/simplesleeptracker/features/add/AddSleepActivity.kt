package net.erikkarlsson.simplesleeptracker.features.add

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class AddSleepActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = supportFragmentInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, AddSleepFragment.newInstance())
                    .commitNow()
        }
    }

}
