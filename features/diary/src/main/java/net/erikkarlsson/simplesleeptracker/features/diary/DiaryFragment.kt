package net.erikkarlsson.simplesleeptracker.features.diary

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import net.erikkarlsson.simplesleeptracker.core.livedata.Event
import net.erikkarlsson.simplesleeptracker.core.util.clicksDebounce
import net.erikkarlsson.simplesleeptracker.features.diary.databinding.FragmentDiaryBinding
import net.erikkarlsson.simplesleeptracker.features.diary.recycler.RecyclerSectionItemDecoration
import net.erikkarlsson.simplesleeptracker.features.diary.recycler.RecyclerSectionItemDecorationFactory
import net.erikkarlsson.simplesleeptracker.features.diary.recycler.SimpleDividerItemDecoration
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class DiaryFragment : Fragment() {

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var dividerItemDecoration: SimpleDividerItemDecoration

    @Inject
    lateinit var sectionItemDecorationFactory: RecyclerSectionItemDecorationFactory

    @Inject
    @Named("sleepAddedEvents")
    lateinit var sleepAddedEvents: BroadcastChannel<Event<Unit>>

    private var sectionItemDecoration: RecyclerSectionItemDecoration? = null

    private val adapter = SleepAdapter { navigateToDetails(it.id) }

    private val viewModel: DiaryViewModel by viewModels()

    private lateinit var binding: FragmentDiaryBinding

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

        binding.floatingActionButton.clicksDebounce { navigateToAddSleep() }

        lifecycleScope.launch {
            sleepAddedEvents.consumeEach {
                // Add scroll delay to give RecyclerView time to update.
                Handler().postDelayed({ scrollToTop() }, 100)
            }
        }

        viewModel.liveData.observe(viewLifecycleOwner, ::render)
    }

    private fun render(state: DiaryState) {
        if (state.sleepDiary != null) {
            binding.emptyState.isVisible = !state.isItemsFound
            binding.recyclerView.isVisible = state.isItemsFound

            val diary = state.sleepDiary

            adapter.submitList(diary.pagedSleep)

            sectionItemDecoration?.let { sectionItemDecoration ->
                binding.recyclerView.removeItemDecoration(sectionItemDecoration)
            }

            sectionItemDecoration = sectionItemDecorationFactory.create(diary)

            sectionItemDecoration?.let { sectionItemDecoration ->
                binding.recyclerView.addItemDecoration(sectionItemDecoration)
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
