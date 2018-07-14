package net.erikkarlsson.simplesleeptracker.feature.profile

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_profile.*
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.REQUEST_CODE_SIGN_IN
import net.erikkarlsson.simplesleeptracker.di.ViewModelFactory
import net.erikkarlsson.simplesleeptracker.elm.ElmViewModel
import net.erikkarlsson.simplesleeptracker.util.clicksThrottle
import timber.log.Timber
import javax.inject.Inject


class ProfileFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var ctx: Context

    private val disposables = CompositeDisposable()

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInButton.clicksThrottle(disposables) { signIn() }
        signOutButton.clicksThrottle(disposables) { signOut() }

        viewModel.state().observe(this, Observer(this::render))

        val account = GoogleSignIn.getLastSignedInAccount(ctx)

        if (account != null) {
            onSignInSuccess(account)
        }
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

    private fun render(state: ProfileState?) {
        state?.let {
            signInButton.isVisible = !it.isLoggedIn
            loggedInContent.isVisible = it.isLoggedIn

            it.userAccount?.let {
                Glide.with(this).load(it.photoUrl).into(photoImage);
                emailText.text = it.email
                displayNameText.text = it.displayName
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
            onSignInSuccess(account)
        } catch (e: ApiException) {
            when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                    Timber.w("Sign in cancelled:failed code=" + e.statusCode)
                    onSignInCancelled()
                }
                GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
                    Timber.w("Sign in failed:failed code=" + e.statusCode)
                    onSignInFailed()
                }
                else -> Timber.w("signInResult:failed code=" + e.statusCode)
            }
        }
    }

    private fun onSignInSuccess(account: GoogleSignInAccount) {
        val email = account.email ?: ""
        val displayName = account.displayName ?: ""
        val photoUrl = account.photoUrl?.toString() ?: ""
        val userAccount = UserAccount(email, displayName, photoUrl)
        viewModel.dispatch(SignInSuccess(userAccount))
    }

    private fun onSignInCancelled() {
        viewModel.dispatch(SignInCancelled)
    }

    private fun onSignInFailed() {
        viewModel.dispatch(SignInFailed)
    }
}

class ProfileViewModel @Inject constructor(profileComponent: ProfileComponent) :
        ElmViewModel<ProfileState, ProfileMsg, ProfileCmd>(profileComponent)