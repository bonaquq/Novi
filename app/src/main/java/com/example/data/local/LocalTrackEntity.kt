package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_tracks")
data class LocalTrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val durationMs: Long,
    val filePath: String,
    val mimeType: String,
    val sizeBytes: Long,
    val dateAdded: Long,
    val artworkUri: String?
)
