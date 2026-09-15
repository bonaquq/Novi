package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MusicBrainzSearchResponse(
    @Json(name = "created") val created: String? = null,
    @Json(name = "count") val count: Int? = null,
    @Json(name = "offset") val offset: Int? = null,
    @Json(name = "recordings") val recordings: List<RecordingDto>? = null
)

@JsonClass(generateAdapter = true)
data class RecordingDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "length") val length: Long? = null,
    @Json(name = "video") val video: Boolean? = null,
    @Json(name = "artist-credit") val artistCredit: List<ArtistCreditDto>? = null,
    @Json(name = "releases") val releases: List<ReleaseDto>? = null,
    @Json(name = "tags") val tags: List<TagDto>? = null,
    @Json(name = "genres") val genres: List<TagDto>? = null,
    @Json(name = "disambiguation") val disambiguation: String? = null,
    @Json(name = "first-release-date") val firstReleaseDate: String? = null
)

@JsonClass(generateAdapter = true)
data class ArtistCreditDto(
    @Json(name = "name") val name: String,
    @Json(name = "artist") val artist: ArtistDto? = null,
    @Json(name = "joinphrase") val joinphrase: String? = null
)

@JsonClass(generateAdapter = true)
data class ArtistDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "sort-name") val sortName: String? = null,
    @Json(name = "disambiguation") val disambiguation: String? = null
)

@JsonClass(generateAdapter = true)
data class ReleaseDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "status") val status: String? = null,
    @Json(name = "date") val date: String? = null,
    @Json(name = "country") val country: String? = null
)

@JsonClass(generateAdapter = true)
data class TagDto(
    @Json(name = "count") val count: Int? = null,
    @Json(name = "name") val name: String
)
