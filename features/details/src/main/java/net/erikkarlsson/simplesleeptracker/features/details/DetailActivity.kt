package net.erikkarlsson.simplesleeptracker.features.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            val sleepId = DetailActivityArgs.fromBundle(checkNotNull(intent.extras)).sleepId

            supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, DetailFragment.newInstance(sleepId))
                    .commitNow()
        }
    }

}
