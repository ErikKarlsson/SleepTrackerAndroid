package net.erikkarlsson.simplesleeptracker.statistics

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotterknife.bindView
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class StatisticsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: StatisticsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(StatisticsViewModel::class.java)
    }

    private val averageSleepText: TextView by bindView(R.id.average_sleep_text)
    private val toggleSleepButton: Button by bindView(R.id.toggle_sleep_button)

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        RxView.clicks(toggleSleepButton).subscribe({ viewModel.dispatch(StatisticsMsg.ToggleSleepMsg) }).addTo(disposables)
        viewModel.state().observe(this, Observer { renderLiveData(it) })
    }

    override fun onStart() {
        super.onStart()
        viewModel.dispatch(StatisticsMsg.InitialIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    fun renderLiveData(state: StatisticsState?) {
        state?.let {
            Timber.d("render %f", state.statistics.avgSleep)
            averageSleepText.setText(String.format("%f : %b : %d", state.statistics.avgSleep, state.isConnectedToInternet, state.count))
        }
    }

}