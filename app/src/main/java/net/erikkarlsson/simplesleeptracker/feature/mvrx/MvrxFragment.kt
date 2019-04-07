package net.erikkarlsson.simplesleeptracker.feature.mvrx

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.*
import io.reactivex.Observable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_mvrx.*
import net.erikkarlsson.simplesleeptracker.R
import timber.log.Timber
import java.util.concurrent.TimeUnit

data class HelloWorldState(val title: String = "Hello MvRx",
                           @PersistState val count: Int = 0,
                           val temperatur: Async<Int> = Uninitialized,
                           val userId: String) : MvRxState {
    val titleWithCount = "$title - $count"

//    constructor(args: HelloWorldArgs): this(userId = args.userId)
}

@Parcelize
data class HelloWorldArgs(val userId: String): Parcelable

class HelloWorldViewModel(initState: HelloWorldState) : MvRxViewModel<HelloWorldState>(initState) {

    fun incrementCount() {
        setState {
            copy(count = count + 1)
        }
    }

    fun fetchTemperatur() = Observable.just(3)
            .delay(2, TimeUnit.SECONDS)
            .execute { copy(temperatur = it) }

    companion object : MvRxViewModelFactory<HelloWorldViewModel, HelloWorldState> {
        override fun initialState(viewModelContext: ViewModelContext): HelloWorldState? {
            val userId = (viewModelContext as FragmentViewModelContext).args<HelloWorldArgs>().userId
            return HelloWorldState(userId = userId)
        }
    }

}

class MvrxFragment : BaseMvRxFragment() {

    private val viewModel: HelloWorldViewModel by fragmentViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mvrx, container, false)
    }

//    fun getUserId() = arguments?.getString(ARG_USER_ID) ?: throw IllegalStateException("You must provide user id")

    private val args: HelloWorldArgs by args()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.subscribe { state ->
            Timber.d("state is $state")
        }
        viewModel.selectSubscribe(HelloWorldState::temperatur) { temperatur ->
            Timber.d("temperatur changed $temperatur")
        }

        viewModel.asyncSubscribe(HelloWorldState::temperatur, onSuccess = {
            Timber.d("temperatur success")
        }, onFail = {
            Timber.d("temperatur fail")
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleView.setOnClickListener {
            viewModel.incrementCount()
            viewModel.fetchTemperatur()
        }
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            titleView.text = when (state.temperatur) {
                Uninitialized -> "Click to load weather"
                is Loading -> "Loading"
                is Success -> "Weather: ${state.temperatur()} degrees, counter ${state.count} userId ${state.userId}"
                is Fail -> "Fail"
            }
        }
    }

    companion object {

        private const val ARG_USER_ID = "user_id"

        @JvmStatic
        fun newInstance(userId: String): MvrxFragment {
            val fragment = MvrxFragment()
            val args = Bundle()
            args.putParcelable(MvRx.KEY_ARG, HelloWorldArgs(userId))
            fragment.arguments = args
            return fragment
        }
    }
}
