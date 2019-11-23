package net.erikkarlsson.simplesleeptracker.features.diary

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_diary.*
import net.erikkarlsson.simplesleeptracker.core.livedata.Event
import net.erikkarlsson.simplesleeptracker.core.livedata.EventObserver
import net.erikkarlsson.simplesleeptracker.core.util.clicksThrottle
import net.erikkarlsson.simplesleeptracker.features.diary.recycler.RecyclerSectionItemDecoration
import net.erikkarlsson.simplesleeptracker.features.diary.recycler.RecyclerSectionItemDecorationFactory
import net.erikkarlsson.simplesleeptracker.features.diary.recycler.SimpleDividerItemDecoration
import javax.inject.Inject
import javax.inject.Named

class DiaryFragment : BaseMvRxFragment() {

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var dividerItemDecoration: SimpleDividerItemDecoration

    @Inject
    lateinit var sectionItemDecorationFactory: RecyclerSectionItemDecorationFactory

    @field:[Inject Named("sleepAddedEvents")]
    lateinit var sleepAddedEvents: MutableLiveData<Event<Unit>>

    @Inject
    lateinit var viewModelFactory: DiaryViewModel.Factory

    private var sectionItemDecoration: RecyclerSectionItemDecoration? = null

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val adapter = SleepAdapter { navigateToDetails(it.id) }

    private val viewModel: DiaryViewModel by fragmentViewModel()

    override fun onAttach(context: Context) {
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

        sleepAddedEvents.observe(this, EventObserver {
            // Add scroll delay to give RecyclerView time to update.
            Handler().postDelayed({ scrollToTop() }, 100)
        })
    }

    override fun invalidate() = withState(viewModel) { state ->
        render(state)
    }

    private fun render(state: DiaryState) {
        when (state.sleepDiary) {
            is Success -> {
                emptyState.isVisible = !state.isItemsFound
                recyclerView.isVisible = state.isItemsFound

                val diary = state.sleepDiary.invoke()

                adapter.submitList(diary.pagedSleep)

                sectionItemDecoration?.let { sectionItemDecoration ->
                    recyclerView.removeItemDecoration(sectionItemDecoration)
                }

                sectionItemDecoration = sectionItemDecorationFactory.create(diary)

                sectionItemDecoration?.let { sectionItemDecoration ->
                    recyclerView.addItemDecoration(sectionItemDecoration)
                }
            }
            else -> {
                /* Do nothing. */
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
            findNavController().navigate(action)
        }
    }

    private fun navigateToAddSleep() {
        val action = DiaryFragmentDirections.actionDiaryToAddSleep()
        findNavController().navigate(action)
    }
}
