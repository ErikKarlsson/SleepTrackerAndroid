package net.erikkarlsson.simplesleeptracker.feature.details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import javax.inject.Inject

const val INTENT_SLEEP_ID = "INTENT_SLEEP_ID"

fun Context.DetailIntent(id: Int): Intent {
    return Intent(this, DetailActivity::class.java).apply {
        putExtra(INTENT_SLEEP_ID, id)
    }
}

class DetailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_details)

        viewModel.state().observe(this, Observer { render(it) })
    }

    private fun render(state: DetailState?) {
        state?.let {
        }
    }
}