package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val email: String,
    val password: String,
    val name: String,
    val handle: String,
    val bio: String = "",
    val avatarId: Int = 1,
    val customAvatarUri: String? = null,
    val favoriteGenres: String = "Electronic,IDM",
    val memberSince: String = "September 2026",
    val registeredAt: Long = System.currentTimeMillis()
)
