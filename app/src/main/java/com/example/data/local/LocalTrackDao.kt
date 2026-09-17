package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<LocalTrackEntity>)

    @Query("SELECT * FROM local_tracks ORDER BY title ASC")
    fun getAllTracks(): Flow<List<LocalTrackEntity>>

    @Query("SELECT * FROM local_tracks WHERE title LIKE '%' || :q || '%' OR artist LIKE '%' || :q || '%' OR album LIKE '%' || :q || '%'")
    fun searchTracks(q: String): Flow<List<LocalTrackEntity>>

    @Query("DELETE FROM local_tracks")
    suspend fun deleteAll()
}
