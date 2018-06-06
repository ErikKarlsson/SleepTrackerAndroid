package net.erikkarlsson.simplesleeptracker.di.module

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.drive.Drive
import dagger.Module
import dagger.Provides

@Module
class SignInModule() {

    @Provides
    fun providesGoogleSignInOptions() =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE)
                .requestEmail()
                .build()

    @Provides
    fun providesGoogleSignInClient(context: Context,
                                   googleSignInOptions: GoogleSignInOptions): GoogleSignInClient =
            GoogleSignIn.getClient(context, googleSignInOptions)

}