package net.erikkarlsson.simplesleeptracker.core.util

import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <VM : ViewModel> viewModelProviderFactoryOf(
    crossinline f: () -> VM
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = f() as T
}

inline fun <reified T : ViewModel> FragmentActivity.assistedViewModel(
        crossinline viewModelProducer: (SavedStateHandle) -> T
) = viewModels<T> {
    object : AbstractSavedStateViewModelFactory(this, intent.extras) {
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle) =
                viewModelProducer(handle) as T
    }
}

inline fun <reified T : ViewModel> Fragment.assistedViewModel(
        crossinline viewModelProducer: (SavedStateHandle) -> T
) = viewModels<T> {
    object : AbstractSavedStateViewModelFactory(this, arguments) {
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle) =
                viewModelProducer(handle) as T
    }
}

inline fun <reified T : ViewModel> Fragment.assistedActivityViewModel(
        crossinline viewModelProducer: (SavedStateHandle) -> T
) = activityViewModels<T> {
    object : AbstractSavedStateViewModelFactory(this, arguments) {
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle) =
                viewModelProducer(handle) as T
    }
}