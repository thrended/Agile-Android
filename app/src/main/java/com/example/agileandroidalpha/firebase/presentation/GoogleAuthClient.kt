package com.example.agileandroidalpha.firebase.presentation

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.Statics.Users.uidFireMap
import com.example.agileandroidalpha.firebase.login.LoginResult
import com.example.agileandroidalpha.firebase.login.UserData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthClient(

    private val context: Context,
    private val oneTap: SignInClient,
) {

    private val auth = Firebase.auth
    private val userRef = Firebase.firestore.collection("users")

    suspend fun signIn(signUp: Boolean = true): IntentSender? {
        val result = try {
            oneTap.beginSignIn(
                buildRequest(signUp)
            ).await()
        } catch(e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): LoginResult {
        val credential = oneTap.getSignInCredentialFromIntent(intent)
        val token = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(token, null)
        Token.token = token.orEmpty()
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            LoginResult(
                data = user?.run {
                    UserData(
                        uid = uid,
                        username = displayName,
                        photoLink = photoUrl?.toString(),
                        email = email,
                        phone = phoneNumber,
                        lastLogin = metadata?.lastSignInTimestamp?: System.currentTimeMillis(),
                        createdTime = metadata?.creationTimestamp?: System.currentTimeMillis(),
                        isEmailVerified = isEmailVerified,
                        isAnonymous = isAnonymous,
                        mfa = multiFactor.enrolledFactors
                    )
                },
                msg = null,
                newUser = addFireUser(user, onComplete = { c ->
                    FireUser(
                        id = c,
                        userId = c,
                        uid = user?.uid,
                        name = user?.displayName,
                        phone = user?.phoneNumber,
                        photo = user?.photoUrl?.toString(),
                        firebaseId = user?.uid,
                        email = user?.email,
                        isVerified = user?.isEmailVerified == true,
                        isDisabled = false,
                    )
                } )
            )
        } catch (e:Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            LoginResult(
                null, e.message
            )
        }
    }

    suspend fun checkUserInDB(
        user: FirebaseUser?,
        onComplete: (Boolean) -> Unit = {}
    ) : Boolean {
        val find = userRef
            .whereEqualTo("uid", user?.uid)
            .limit(1)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    onComplete.invoke(snapshot.isEmpty)
                }
            }
            .await()
        return !find.isEmpty
    }

    suspend fun addFireUser(
        user: FirebaseUser?,
        onComplete: (Long) -> Unit
    ): Boolean {
        if (checkUserInDB(user)) return false
        val countQ = userRef.count()
        countQ.get(AggregateSource.SERVER).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                userRef.add(
                    FireUser(
                        id = snapshot.count,
                        userId = snapshot.count,
                        uid = user?.uid,
                        firebaseId = user?.uid,
                        email = user?.email,
                        isVerified = false,
                        isDisabled = false
                    )
                )
                onComplete.invoke(snapshot.count)
                Log.d("Auth", "Count: ${snapshot.count}")
            } else {
                Log.d("Auth", "Count failed: ", task.exception)
            }

        }.await()
        return true
    }

    suspend fun signOut() {
        val u = auth.currentUser?.displayName ?: auth.currentUser?.email ?: "Unknown Name"
        try {
            uidFireMap[u]?.let{ it.isOnline = false}
            oneTap.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        } finally {
            Toast.makeText(
                context,
                "Successfully signed out of Google Account $u",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun reAuth() {
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        val credential = EmailAuthProvider
            .getCredential("user@example.com", "password1234")
//        val googleCredential = GoogleAuthProvider
//            .getCredential(Token.token)
        // Prompt the user to re-provide their sign-in credentials
        auth.currentUser!!.reauthenticate(credential)
            .addOnCompleteListener { Log.d("Google Re-Auth", "FireUser re-authenticated.") }
    }

    fun getUserData(): UserData? = auth.currentUser?.run {
        UserData(
            uid = uid,
            username = displayName,
            photoLink = photoUrl?.toString(),
            email = email,
            phone = phoneNumber,
            lastLogin = metadata?.lastSignInTimestamp?: System.currentTimeMillis(),
            createdTime = metadata?.creationTimestamp?: System.currentTimeMillis(),
            isEmailVerified = isEmailVerified,
            isAnonymous = isAnonymous,
            mfa = multiFactor.enrolledFactors
        )
    }

    private fun buildRequest(signUp: Boolean): BeginSignInRequest {
        return when(signUp) {
            true -> {
                BeginSignInRequest.Builder()
                    .setGoogleIdTokenRequestOptions(
                        GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(context.getString(R.string.web_client_id))
                            .build()
                    )
                    .build()
            }
            else -> {
                BeginSignInRequest.builder()
                    .setPasswordRequestOptions(
                        BeginSignInRequest.PasswordRequestOptions.builder()
                            .setSupported(true)
                            .build())
                    .setGoogleIdTokenRequestOptions(
                        GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            // Set filterByAuthorizedAccounts = true to avoid duplicated accounts being created
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(context.getString(R.string.web_client_id))
                            .build())
                    .setAutoSelectEnabled(true)
                    .build();
            }
        }
    }

    companion object Token {
        lateinit var token: String
    }
}