package net.erikkarlsson.simplesleeptracker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import net.erikkarlsson.simplesleeptracker.data.SleepRepository
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sleepRepository : SleepRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
