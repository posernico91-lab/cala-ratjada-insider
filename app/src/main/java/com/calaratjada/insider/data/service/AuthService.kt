package com.calaratjada.insider.data.service

import android.content.Context
import android.content.Intent
import com.calaratjada.insider.BuildConfig
import com.calaratjada.insider.data.local.ChatDao
import com.calaratjada.insider.data.model.UserProfile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val chatDao: ChatDao
) {
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestProfile()
        .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
        .build()

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn = _isSignedIn.asStateFlow()

    val profile: Flow<UserProfile?> = chatDao.getProfile()

    init {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        _isSignedIn.value = account != null
    }

    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    suspend fun handleSignInResult(task: Task<GoogleSignInAccount>): Boolean {
        return try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val existing = chatDao.getProfileSync()
                val profile = UserProfile(
                    googleUid = account.id ?: "",
                    email = account.email ?: "",
                    displayName = account.displayName ?: "",
                    nickname = existing?.nickname ?: account.givenName ?: "",
                    profilePicUri = existing?.profilePicUri ?: (account.photoUrl?.toString() ?: ""),
                    coins = existing?.coins ?: 10,
                    isPremium = existing?.isPremium ?: false,
                    premiumUntil = existing?.premiumUntil ?: 0
                )
                chatDao.insertProfile(profile)
                _isSignedIn.value = true
                true
            } else false
        } catch (e: ApiException) {
            false
        }
    }

    suspend fun signOut() {
        googleSignInClient.signOut()
        _isSignedIn.value = false
    }

    suspend fun getOrCreateProfile(): UserProfile {
        val existing = chatDao.getProfileSync()
        if (existing != null) return existing
        val newProfile = UserProfile()
        chatDao.insertProfile(newProfile)
        return newProfile
    }

    suspend fun updateNickname(nickname: String) {
        chatDao.updateNickname(nickname)
    }

    suspend fun updateProfilePic(uri: String) {
        chatDao.updateProfilePic(uri)
    }
}
