package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts")
    fun getAllAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getAccountByEmail(email: String): UserAccountEntity?

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun getAccountCount(): Int

    @Query("DELETE FROM user_accounts WHERE LOWER(email) = 'audrey.v@novimusic.io'")
    suspend fun deleteDemoAccount()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: UserAccountEntity)

    @Update
    suspend fun updateAccount(account: UserAccountEntity)

    @Query("UPDATE user_accounts SET name = :name, handle = :handle, bio = :bio, avatarId = :avatarId, customAvatarUri = :customAvatarUri WHERE LOWER(email) = LOWER(:email)")
    suspend fun updateProfileFields(
        email: String,
        name: String,
        handle: String,
        bio: String,
        avatarId: Int,
        customAvatarUri: String?
    )
}
