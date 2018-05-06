package net.erikkarlsson.simplesleeptracker.feature.details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_details.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.util.formatDateDisplayName
import timber.log.Timber
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
        viewModel.dispatch(LoadDetailIntent(getSleepId()))
    }

    fun getSleepId(): Int {
        return intent.getIntExtra(INTENT_SLEEP_ID, 0)
    }

    private fun render(state: DetailState?) {
        state?.let {
            if (state.sleep != Sleep.empty()) {
                time_asleep.text = state.sleep.fromDate.formatDateDisplayName
            }
            Timber.d("sleep id: %d", state.sleep.id)
        }

    }
}

class DetailViewModel @Inject constructor(detailComponent: DetailComponent) :
        ElmViewModel<DetailState, DetailMsg, DetailCmd>(detailComponent)