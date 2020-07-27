package net.erikkarlsson.simplesleeptracker.features.home

import android.annotation.SuppressLint
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import net.erikkarlsson.simplesleeptracker.core.livedata.EventObserver
import net.erikkarlsson.simplesleeptracker.core.util.clicksDebounce
import net.erikkarlsson.simplesleeptracker.core.util.formatHoursMinutes2
import net.erikkarlsson.simplesleeptracker.core.util.formatTimestamp
import net.erikkarlsson.simplesleeptracker.domain.entity.UserAccount
import net.erikkarlsson.simplesleeptracker.features.appwidget.SleepAppWidgetProvider
import net.erikkarlsson.simplesleeptracker.features.home.databinding.FragmentHomeBinding
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

const val REQUEST_CODE_SIGN_IN = 1

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject @Named("homeEvents")
    lateinit var homeEvents: HomeEvents

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeEvents.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                PinWidgetEvent -> pinWidget()
                AddWidgetEvent -> addWidget()
            }
        })

        val account = GoogleSignIn.getLastSignedInAccount(context)

        if (account != null) {
            onSignInRestored(account)
        }

        viewModel.liveData.observe(viewLifecycleOwner, ::render)
    }

    override fun onStart() {
        super.onStart()

        binding.loggedOutContent.signInButton.clicksDebounce { signIn() }
        binding.loggedInContent.signOutButton.clicksDebounce { signOut() }
        binding.widgetBubble.clicksDebounce { viewModel.onBubbleClick() }
        binding.owlImage.setOnClickListener { viewModel.onToggleSleepClick() }
        binding.toggleSleepButton.setOnClickListener { viewModel.onToggleSleepClick() }
    }

    @SuppressLint("NewApi")
    private fun pinWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(requireContext())
        val myProvider = ComponentName(requireContext(), SleepAppWidgetProvider::class.java)

        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            appWidgetManager.requestPinAppWidget(myProvider, null, null)
        }
    }

    private fun addWidget() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())

        builder.setTitle(R.string.add_widget_dialog_title)

        builder.setMessage(getText(R.string.add_widget_instructions))

        builder.setPositiveButton("OK") { _, _ -> }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadWidgetStatus()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(activity as Activity) {
                    viewModel.onSignOutComplete()
                }
    }

    private fun render(state: HomeState) {
        if (state.isLoading) {
            return
        }
        renderOwl(state.isSleeping)
        renderLogin(state.isLoggedIn)
        renderUserAccount(state.userAccount)
        renderBackup(state.lastBackupTimestamp)
        renderWidgetBubble(state.bubbleState, state.sleepDuration)
    }

    private fun renderWidgetBubble(bubbleState: BubbleState, sleepDuration: Float) {
        binding.widgetBubbleText.text = when (bubbleState) {
            BubbleState.SLEEPING_ONBOARDING -> getText(R.string.sleeping_zzz_onboarding)
            BubbleState.SLEEPING -> getText(R.string.sleeping_zzz)
            BubbleState.ADD_WIDGET -> getText(R.string.add_widget)
            BubbleState.PIN_WIDGET -> getText(R.string.pin_widget)
            BubbleState.START_TRACKING -> getText(R.string.remember_to_toggle_sleep)
            BubbleState.MINIMUM_SLEEP -> getText(R.string.sleep_minimum)
            BubbleState.SLEEP_DURATION -> getString(R.string.slept_for, sleepDuration.formatHoursMinutes2)
            BubbleState.EMPTY -> ""
        }

        binding.widgetBubble.isVisible = bubbleState != BubbleState.EMPTY
    }

    private fun renderLogin(loggedIn: Boolean) {
        binding.loggedOutContent.signInButton.isVisible = !loggedIn
        binding.loggedInContent.root.isVisible = loggedIn
        binding.loggedOutContent.root.isVisible = !loggedIn
    }

    private fun renderOwl(isSleeping: Boolean) {
        val imageRes = if (isSleeping) R.drawable.owl_asleep else R.drawable.own_awake
        binding.owlImage.setImageResource(imageRes)

        val textRes = if (isSleeping) {
            R.string.wake_up
        } else {
            R.string.go_to_bed
        }
        binding.toggleSleepButton.setText(textRes)
    }

    private fun renderBackup(timestamp: Long) {
        val backupString = getString(R.string.last_backup)
        val dateString = if (timestamp > 0L) {
            timestamp.formatTimestamp
        } else {
            getString(R.string.not_backed_up_yet)
        }
        binding.loggedInContent.lastBackupText.text = String.format("%s: %s", backupString, dateString)
    }

    private fun renderUserAccount(userAccount: UserAccount?) {
        userAccount?.let {
            Glide.with(this)
                    .load(it.photoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.loggedInContent.photoImage)

            binding.loggedInContent.emailText.text = it.email
            binding.loggedInContent.displayNameText.text = it.displayName
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            if (account == null) {
                Timber.w("Account was null")
                onSignInFailed()
                return
            }

            onSignInSuccess(account)
        } catch (e: ApiException) {
            when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                    Timber.w("Sign in cancelled:failed code=%s", e.statusCode)
                    onSignInCancelled()
                }
                GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
                    Timber.w("Sign in failed:failed code=%s", e.statusCode)
                    onSignInFailed()
                }
                else -> Timber.w("signInResult:failed code=%s", e.statusCode)
            }
        }
    }

    private fun onSignInSuccess(account: GoogleSignInAccount) {
        val userAccount = toUserAccount(account)
        viewModel.onSignInSuccess(userAccount)
    }

    private fun onSignInRestored(account: GoogleSignInAccount) {
        val userAccount = toUserAccount(account)
        viewModel.onSignInRestored(userAccount)
    }

    private fun toUserAccount(account: GoogleSignInAccount): UserAccount {
        val email = account.email ?: ""
        val displayName = account.displayName ?: ""
        val photoUrl = account.photoUrl?.toString() ?: ""
        return UserAccount(email, displayName, photoUrl)
    }

    private fun onSignInCancelled() {
        viewModel.onSignInFailed()
    }

    private fun onSignInFailed() {
        viewModel.onSignInFailed()
    }
}
