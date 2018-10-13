package net.erikkarlsson.simplesleeptracker.feature.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.jakewharton.rxbinding2.view.clicks
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.logged_in_content.*
import kotlinx.android.synthetic.main.logged_out_content.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.REQUEST_CODE_SIGN_IN
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.domain.entity.UserAccount
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.util.clicksThrottle
import net.erikkarlsson.simplesleeptracker.util.formatTimestamp
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var ctx: Context

    private val disposables = CompositeDisposable()

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state().observe(this, Observer(this::render))

        val account = GoogleSignIn.getLastSignedInAccount(ctx)

        if (account != null) {
            onSignInRestored(account)
        }
    }

    override fun onStart() {
        super.onStart()

        signInButton.clicksThrottle(disposables) { signIn() }
        signOutButton.clicksThrottle(disposables) { signOut() }

        Observable.merge(toggleSleepButton.clicks(), owlImage.clicks())
                .subscribe({ viewModel.dispatch(ToggleSleepClicked) },
                        { Timber.e(it, "Error merging clicks") })
                .addTo(disposables)
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(activity as Activity) {
                    viewModel.dispatch(SignOutComplete)
                }
    }

    private fun render(state: HomeState?) {
        state?.let {
            val imageRes = if (state.isSleeping) R.drawable.owl_asleep else R.drawable.own_awake
            owlImage.setImageResource(imageRes)

            signInButton.isVisible = !it.isLoggedIn
            loggedInContent.isVisible = it.isLoggedIn
            loggedOutContent.isVisible = !it.isLoggedIn

            it.userAccount?.let {
                Glide.with(this)
                        .load(it.photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(photoImage)

                emailText.text = it.email
                displayNameText.text = it.displayName
            }

            it.profile.lastBackupTimestamp.let {
                val backupString = getString(R.string.last_backup)
                val dateString = if (it > 0L) {
                    it.formatTimestamp
                } else {
                    getString(R.string.not_backed_up_yet)
                }
                lastBackupText.text = String.format("%s: %s", backupString, dateString)
            }
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
        viewModel.dispatch(SignInSuccess(userAccount))
    }

    private fun onSignInRestored(account: GoogleSignInAccount) {
        val userAccount = toUserAccount(account)
        viewModel.dispatch(SignInRestored(userAccount))
    }

    private fun toUserAccount(account: GoogleSignInAccount): UserAccount {
        val email = account.email ?: ""
        val displayName = account.displayName ?: ""
        val photoUrl = account.photoUrl?.toString() ?: ""
        val userAccount = UserAccount(email, displayName, photoUrl)
        return userAccount
    }

    private fun onSignInCancelled() {
        viewModel.dispatch(SignInCancelled)
    }

    private fun onSignInFailed() {
        viewModel.dispatch(SignInFailed)
    }
}

class HomeViewModel @Inject constructor(homeComponent: HomeComponent) :
        ElmViewModel<HomeState, HomeMsg, HomeCmd>(homeComponent)
