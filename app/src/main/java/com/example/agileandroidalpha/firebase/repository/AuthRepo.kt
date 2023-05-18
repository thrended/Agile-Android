package com.example.agileandroidalpha.firebase.repository

import android.util.Log
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepo @Inject constructor(
) {
    private val db = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userRef = Firebase.firestore.collection("users")
    val user: FirebaseUser? = Firebase.auth.currentUser
    fun isLoggedIn(): Boolean = user != null
    fun isAnon(): Boolean = isLoggedIn() && (user!!.isAnonymous)
//            || (user.email.isNullOrBlank() && user.phoneNumber.isNullOrBlank() &&
//            user.displayName.isNullOrBlank()) )
    fun getUID(): String = user?.uid.orEmpty()


    suspend fun registerUserWithEmail(
        email: String,
        password: String,
        onComplete: (Boolean) -> Unit
    ) : Boolean
    {
        var x = false
        if(email.isNotBlank() && password.isNotBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            onComplete.invoke(it.isSuccessful)
                            x = it.isSuccessful
                        }.await()
                } catch(e: Exception) {
                    print("Error registering user.")
                } finally {
                    val countQ = userRef.count()
                    countQ.get(AggregateSource.SERVER).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val snapshot = task.result
                            userRef.add(
                                FireUser(
                                    id = snapshot.count,
                                    userId = snapshot.count,
                                    uid = auth.uid,
                                    firebaseId = auth.uid,
                                    email = email,
                                    isVerified = false,
                                    isDisabled = false
                                )
                            )
                            Log.d("Auth", "Count: ${snapshot.count}")
                        } else {
                            Log.d("Auth", "Count failed: ", task.exception)
                        }

                    }
                }
            }
        }
        return x
    }

    private suspend fun addUserToFirestore() {
        auth.currentUser?.apply {
            val user = toUser()
            db.collection("users").document(uid).set(user).await()
        }
    }

    fun FirebaseUser.toUser() = mapOf(
        "name" to displayName,
        "email" to email,
        "photo" to photoUrl?.toString(),
        "created" to serverTimestamp()
    )

    suspend fun loginWithEmailAndPassword(
        email: String,
        password: String,
        onComplete: (Boolean) -> Unit
    ) : Boolean
    {
        var x = false
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        onComplete.invoke(it.isSuccessful)
                        Log.d("login", it.isSuccessful.toString())
                        x = it.isSuccessful
                    }.await()
            } catch(e: Exception) {
                print("Error logging in.")
            }
        }
        return x
    }

    fun signInAnonymously() {
        // [START sign-in_anonymously]
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ANON, "signInAnonymously:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ANON, "signInAnonymously:failure", task.exception)
                }
            }
        // [END sign-in_anonymously]
    }

    fun signOut() = CoroutineScope(Dispatchers.IO).launch {
        try {
          if(auth.currentUser != null) {
              auth.signOut()
          }
        } catch(e: Exception) {
            Log.d("auth", "Failed to sign out")
        }

    }

    fun verifyUserEmail() {

        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User verification email sent.")
                }
            }
    }

    fun verifyUserEmailInApp() {

        user!!.sendEmailVerification(actionCodeSettings {
            url = "https://agile-android.firebaseapp.com/?email=" + auth.currentUser?.email
            handleCodeInApp = true
            dynamicLinkDomain = "agile-android.firebaseapp.page.link"
        })
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User verification email sent.")
                }
                else {
                    Log.d(TAG, "An error occurred trying to send a user verification email.")
                }
            }
    }

    fun updateUserProfile(profileUpdates: UserProfileChangeRequest) {

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            }
    }

    fun changePassword(newPassword: String) {

        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                }
            }
    }

    fun resetPassword(emailAddress: String = user!!.email!!) {
        Firebase.auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent to $emailAddress.")
                }
            }
    }

    fun deleteUser() {
//        deleteUserData()
        user!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User account deleted.")
                    auth.signOut()
                }

            }
    }

    fun reAuth() {
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        val credential = EmailAuthProvider
            .getCredential(user!!.email!!, "password1234")
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
            .addOnCompleteListener { Log.d(TAG, "FireUser re-authenticated.") }
    }

    private suspend fun deleteUserData() {
        try {
            val action = userRef.document(matchUID())
                .delete().await()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to delete user data.")
            }
        }
    }

    private suspend fun matchUID(uid: String = auth.uid.orEmpty()): String  {
        val query = userRef
            .whereEqualTo("uid", uid)
            .limit(1)
            .get()
            .await()
        var x = ""
        if(!query.isEmpty) {
            query.documents.forEach { dc ->
                val obj = dc.toObject<FireUser>()
                obj?.let {
                    x = obj.uid?: "invalid"
                }
            }
        }
        return x
    }

    companion object {
        const val TAG = "AuthRepo"
        const val ANON = "Anonymous Login"
    }

}