package net.erikkarlsson.simplesleeptracker.users

import com.airbnb.mvrx.*
import com.google.api.services.drive.model.User
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import net.erikkarlsson.simplesleeptracker.MvRxViewModel
import net.erikkarlsson.simplesleeptracker.data.sleep.SleepRepository

data class UsersState(val users: Async<List<User>> = Uninitialized) : MvRxState

class UsersViewModel @AssistedInject constructor(
        @Assisted state: UsersState,
        private val sleepRepository: SleepRepository
) : MvRxViewModel<UsersState>(state) {

    fun fetchUser() {
//        usersService
//            .users()
//            .execute {
//                copy(users = it)
//            }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(state: UsersState): UsersViewModel
    }

    companion object : MvRxViewModelFactory<UsersViewModel, UsersState> {
        override fun create(viewModelContext: ViewModelContext, state: UsersState): UsersViewModel? {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<UsersFragment>()
            return fragment.viewModelFactory.create(state)
        }
    }
}
