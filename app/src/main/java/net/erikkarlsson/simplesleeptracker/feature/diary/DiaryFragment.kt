package net.erikkarlsson.simplesleeptracker.feature.diary

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.google.common.collect.ImmutableList
import com.jakewharton.rxrelay2.PublishRelay
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_diary.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.domain.entity.Sleep
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.feature.statistics.SleepAdapter
import net.erikkarlsson.simplesleeptracker.util.scanMap
import timber.log.Timber
import javax.inject.Inject

class DiaryFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var ctx: Context

    private val viewModel: DiaryViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(DiaryViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

    private val adapter = SleepAdapter { navigateToDetails(it.id) }
    private val sleepListRelay: PublishRelay<ImmutableList<Sleep>> = PublishRelay.create()

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(ctx)
        recyclerView.adapter = adapter

        viewModel.state().observe(this, Observer { render(it) })
    }

    override fun onStart() {
        super.onStart()

        sleepListRelay.scanMap(ImmutableList.of(), { old, new ->
            val callback = SleepAdapter.DiffCallback(old, new)
            val result = DiffUtil.calculateDiff(callback)
            Pair(new, result)
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ adapter.swapData(it.first, it.second) },
                           { Timber.e(it, "Error calculating diff result") })
                .addTo(disposables)
    }

    override fun onStop() {
        super.onStop()
        disposables.dispose()
    }

    private fun render(state: DiaryState?) {
        state?.let {
            sleepListRelay.accept(state.sleepList)
        }
    }

    private fun navigateToDetails(id: Int?) {
        id?.let {
            val action = DiaryFragmentDirections.actionDiaryToDetail().setSleepId(id)
            NavHostFragment.findNavController(this).navigate(action)
        }
    }
}

class DiaryViewModel @Inject constructor(diaryComponent: DiaryComponent) :
        ElmViewModel<DiaryState, DiaryMsg, DiaryCmd>(diaryComponent)
