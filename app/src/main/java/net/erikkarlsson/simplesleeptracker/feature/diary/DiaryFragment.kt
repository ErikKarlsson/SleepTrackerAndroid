package net.erikkarlsson.simplesleeptracker.feature.diary

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.os.Handler
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
import net.erikkarlsson.simplesleeptracker.domain.DateTimeProvider
import net.erikkarlsson.simplesleeptracker.domain.entity.SleepDiary
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.util.clicksThrottle
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class DiaryFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var ctx: Context

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    var sectionItemDecoration: RecyclerSectionItemDecoration? = null

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val monthPattern = DateTimeFormatter.ofPattern("MMM")

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
        recyclerView.setItemAnimator(null)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
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
    }

    private fun render(state: DiaryState?) {
        state?.sleepDiary?.let {
            adapter.submitList(it.pagedSleep)

            if (sectionItemDecoration != null) {
                recyclerView.removeItemDecoration(sectionItemDecoration)
            }

            sectionItemDecoration = RecyclerSectionItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_section_header_height),
                                                                  true,
                                                                  getSectionCallback(it))
            recyclerView.addItemDecoration(sectionItemDecoration)

            // TODO (erikkarlsson): Hack to make time for items to be added to list before scrolling.
            // Maybe use one-time event for newly added item?
            Handler().postDelayed({
                                      if (isAdded) {
                                          recyclerView.scrollToPosition(0)
                                      }
                                  }, 200)

        }
    }

    private fun getSectionCallback(sleepDiary: SleepDiary): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {

            val sleepList = sleepDiary.pagedSleep

            override fun isSection(position: Int): Boolean {
                try {
                    return position == 0 || sleepList[position]
                            ?.fromDate?.month != sleepList[position - 1]?.fromDate?.month
                } catch (e: IndexOutOfBoundsException) {
                    return false
                }

            }

            override fun getMonthSectionHeader(position: Int): CharSequence {
                try {
                    val fromDate = sleepList[position]?.fromDate ?: OffsetDateTime.MIN

                    val year = fromDate.year ?: 0
                    val currentYear = dateTimeProvider.now().year
                    val monthString = fromDate.format(monthPattern).toString()

                    return if (year == currentYear) {
                        monthString
                    } else {
                        String.format("%s %d", monthString, year)
                    }
                } catch (e: IndexOutOfBoundsException) {
                    return ""
                }
            }

            override fun getNightsSectionHeader(position: Int): CharSequence {
                try {
                    val fromDate = sleepList[position]?.fromDate

                    if (fromDate == null) {
                        return ""
                    }

                    val year = fromDate.year
                    val month = fromDate.monthValue
                    val sleepCount = sleepDiary.getSleepCount(year, month)

                    return String.format("%s %s",
                                         sleepCount.toString(),
                                         getString(R.string.nights))
                } catch (e: IndexOutOfBoundsException) {
                    return ""
                }
            }
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
