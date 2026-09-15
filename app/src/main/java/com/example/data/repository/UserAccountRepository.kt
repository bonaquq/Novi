package com.example.data.repository

import com.example.data.local.UserAccountDao
import com.example.data.local.UserAccountEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class AuthResult {
    data class Success(val account: UserAccountEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class UserAccountRepository(private val userAccountDao: UserAccountDao) {

    suspend fun initializeDefaultAccountsIfNeeded() = withContext(Dispatchers.IO) {
        try {
            userAccountDao.deleteDemoAccount()
        } catch (_: Exception) {}
    }

    suspend fun registerAccount(
        name: String,
        email: String,
        password: String,
        handle: String,
        genres: List<String>,
        customAvatarUri: String? = null
    ): AuthResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return@withContext AuthResult.Error("Please enter a valid email address.")
        }
        if (password.length < 4) {
            return@withContext AuthResult.Error("Password must be at least 4 characters long.")
        }
        val existing = userAccountDao.getAccountByEmail(cleanEmail)
        if (existing != null) {
            return@withContext AuthResult.Error("An account with this email is already registered. Please log in.")
        }

        val cleanName = name.trim().ifBlank { cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() } }
        val cleanHandle = if (handle.isNotBlank()) {
            if (handle.startsWith("@")) handle.trim() else "@${handle.trim()}"
        } else {
            "@" + cleanName.lowercase().replace(" ", "").replace(".", "")
        }

        val newAccount = UserAccountEntity(
            email = cleanEmail,
            password = password,
            name = cleanName,
            handle = cleanHandle,
            bio = if (genres.isNotEmpty()) "Loving ${genres.joinToString(", ")}" else "Music lover & sound explorer",
            avatarId = 1,
            customAvatarUri = customAvatarUri,
            favoriteGenres = genres.joinToString(","),
            memberSince = "September 2026",
            registeredAt = System.currentTimeMillis()
        )
        userAccountDao.insertAccount(newAccount)
        AuthResult.Success(newAccount)
    }

    suspend fun login(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) {
            return@withContext AuthResult.Error("Please enter your email address.")
        }
        if (password.isBlank()) {
            return@withContext AuthResult.Error("Please enter your password.")
        }

        val account = userAccountDao.getAccountByEmail(cleanEmail)
        if (account == null) {
            return@withContext AuthResult.Error("No account found with this email. Please register first in the Sign Up tab.")
        }

        if (account.password != password) {
            return@withContext AuthResult.Error("Incorrect password. Please verify your credentials.")
        }

        AuthResult.Success(account)
    }

    suspend fun updateProfile(
        email: String,
        name: String,
        handle: String,
        bio: String,
        avatarId: Int,
        customAvatarUri: String?
    ) = withContext(Dispatchers.IO) {
        userAccountDao.updateProfileFields(
            email = email.trim().lowercase(),
            name = name,
            handle = handle,
            bio = bio,
            avatarId = avatarId,
            customAvatarUri = customAvatarUri
        )
    }

    suspend fun getAccount(email: String): UserAccountEntity? = withContext(Dispatchers.IO) {
        userAccountDao.getAccountByEmail(email.trim().lowercase())
    }
}
