package net.erikkarlsson.simplesleeptracker.features.diary

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.livedata.Event
import net.erikkarlsson.simplesleeptracker.core.util.clicksThrottle
import net.erikkarlsson.simplesleeptracker.features.diary.databinding.FragmentDiaryBinding
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

    @Inject @Named("sleepAddedEvents")
    lateinit var sleepAddedEvents: BroadcastChannel<Event<Unit>>

    @Inject
    lateinit var viewModelFactory: DiaryViewModel.Factory

    private var sectionItemDecoration: RecyclerSectionItemDecoration? = null

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val adapter = SleepAdapter { navigateToDetails(it.id) }

    private val viewModel: DiaryViewModel by fragmentViewModel()

    private lateinit var binding: FragmentDiaryBinding

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.addItemDecoration(dividerItemDecoration)

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!isAdded) {
                    return
                }

                if (dy > 0 && binding.floatingActionButton.isVisible) {
                    binding.floatingActionButton.hide()
                } else if (dy < 0 && !binding.floatingActionButton.isVisible) {
                    binding.floatingActionButton.show()
                }
            }
        })

        binding.floatingActionButton.clicksThrottle(compositeDisposable) { navigateToAddSleep() }

        lifecycleScope.launch {
            sleepAddedEvents.consumeEach {
                // Add scroll delay to give RecyclerView time to update.
                Handler().postDelayed({ scrollToTop() }, 100)
            }
        }
    }

    override fun invalidate() = withState(viewModel) { state ->
        render(state)
    }

    private fun render(state: DiaryState) {
        when (state.sleepDiary) {
            is Success -> {
                binding.emptyState.isVisible = !state.isItemsFound
                binding.recyclerView.isVisible = state.isItemsFound

                val diary = state.sleepDiary.invoke()

                adapter.submitList(diary.pagedSleep)

                sectionItemDecoration?.let { sectionItemDecoration ->
                    binding.recyclerView.removeItemDecoration(sectionItemDecoration)
                }

                sectionItemDecoration = sectionItemDecorationFactory.create(diary)

                sectionItemDecoration?.let { sectionItemDecoration ->
                    binding.recyclerView.addItemDecoration(sectionItemDecoration)
                }
            }
            else -> {
                /* Do nothing. */
            }
        }
    }

    private fun scrollToTop() {
        if (isAdded) {
            binding.recyclerView.scrollToPosition(0)
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
