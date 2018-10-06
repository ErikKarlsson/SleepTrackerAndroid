package net.erikkarlsson.simplesleeptracker.feature.diary

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.view.isVisible
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_diary.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.base.Event
import net.erikkarlsson.simplesleeptracker.base.EventObserver
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.feature.diary.recycler.RecyclerSectionItemDecoration
import net.erikkarlsson.simplesleeptracker.feature.diary.recycler.RecyclerSectionItemDecorationFactory
import net.erikkarlsson.simplesleeptracker.feature.diary.recycler.SimpleDividerItemDecoration
import net.erikkarlsson.simplesleeptracker.util.clicksThrottle
import javax.inject.Inject
import javax.inject.Named

class DiaryFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var dividerItemDecoration: SimpleDividerItemDecoration

    @Inject
    lateinit var sectionItemDecorationFactory: RecyclerSectionItemDecorationFactory

    @field:[Inject Named("sleepAddedEvents")]
    lateinit var sleepAddedEvents: MutableLiveData<Event<Unit>>

    private var sectionItemDecoration: RecyclerSectionItemDecoration? = null

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val adapter = SleepAdapter { navigateToDetails(it.id) }

    private val viewModel: DiaryViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(DiaryViewModel::class.java)
    }

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

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.addItemDecoration(dividerItemDecoration)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!isAdded) {
                    return
                }

                if (dy > 0 && floatingActionButton.isVisible) {
                    floatingActionButton.hide()
                } else if (dy < 0 && !floatingActionButton.isVisible) {
                    floatingActionButton.show()
                }
            }
        })

        floatingActionButton.clicksThrottle(compositeDisposable) { navigateToAddSleep() }

        viewModel.state().observe(this, Observer(this::render))

        sleepAddedEvents.observe(this, EventObserver {
            // Add scroll delay to give RecyclerView time to update.
            Handler().postDelayed({ scrollToTop() }, 100)
        })
    }

    private fun render(state: DiaryState?) {
        state?.let {
            if (it.isLoading) {
                return
            }

            emptyState.isVisible = it.isEmptySleep
            recyclerView.isVisible = it.isEmptySleep == false

            it.sleepDiary?.let { sleepDiary ->
                adapter.submitList(sleepDiary.pagedSleep)

                sectionItemDecoration?.let { sectionItemDecoration ->
                    recyclerView.removeItemDecoration(sectionItemDecoration)
                }

                sectionItemDecoration = sectionItemDecorationFactory.create(sleepDiary)

                sectionItemDecoration?.let { sectionItemDecoration ->
                    recyclerView.addItemDecoration(sectionItemDecoration)
                }

            }
        }
    }

    private fun scrollToTop() {
        if (isAdded) {
            recyclerView.scrollToPosition(0)
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
