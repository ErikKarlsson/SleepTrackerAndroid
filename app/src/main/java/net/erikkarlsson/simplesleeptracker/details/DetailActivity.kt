package net.erikkarlsson.simplesleeptracker.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.erikkarlsson.simplesleeptracker.R

const val INTENT_SLEEP_ID = "INTENT_SLEEP_ID"

fun Context.DetailIntent(id: Int): Intent {
    return Intent(this, DetailActivity::class.java).apply {
        putExtra(INTENT_SLEEP_ID, id)
    }
}

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
    }
}