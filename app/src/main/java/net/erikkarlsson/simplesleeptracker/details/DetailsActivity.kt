package net.erikkarlsson.simplesleeptracker.details

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.erikkarlsson.simplesleeptracker.R

const val EXTRA_SLEEP_ID = "EXTRA_SLEEP_ID"

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
    }
}