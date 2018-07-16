package net.erikkarlsson.simplesleeptracker.feature.diary

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.view.isVisible
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_diary.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.util.clicksThrottle
import javax.inject.Inject

class DiaryFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var ctx: Context

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val viewModel: DiaryViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(DiaryViewModel::class.java)
    }

    private val adapter = SleepAdapter { navigateToDetails(it.id) }

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

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && floatingActionButton.isVisible) {
                    floatingActionButton.hide()
                } else if (dy < 0 && !floatingActionButton.isVisible) {
                    floatingActionButton.show()
                }
            }
        })

        floatingActionButton.clicksThrottle(compositeDisposable) { navigateToAddSleep() }

        viewModel.state().observe(this, Observer(this::render))
    }

    private fun render(state: DiaryState?) {
        state?.sleepList?.let {
            adapter.submitList(it)
        }
    }

    private fun navigateToDetails(id: Int?) {
        id?.let {
            val action = DiaryFragmentDirections.actionDiaryToDetail().setSleepId(id)
            NavHostFragment.findNavController(this).navigate(action)
        }
    }

    private fun navigateToAddSleep() {
        val action = DiaryFragmentDirections.actionDiaryToAddSleep()
        NavHostFragment.findNavController(this).navigate(action)
    }
}

class DiaryViewModel @Inject constructor(diaryComponent: DiaryComponent) :
        ElmViewModel<DiaryState, DiaryMsg, DiaryCmd>(diaryComponent)
