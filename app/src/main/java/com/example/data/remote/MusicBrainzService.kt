package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

interface MusicBrainzApi {

    /**
     * Search recordings (tracks/songs) in MusicBrainz database.
     * e.g. query="tag:idm" or query="recording:Bone AND artist:Sonic Youth" or query="electronic"
     */
    @GET("recording")
    suspend fun searchRecordings(
        @Query("query") query: String,
        @Query("fmt") format: String = "json",
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0
    ): MusicBrainzSearchResponse
}

object MusicBrainzClient {
    private const val BASE_URL = "https://musicbrainz.org/ws/2/"
    private val lastRequestTimestamp = AtomicLong(0L)
    private val rateLimitLock = Any()

    private val userAgentAndRateLimitInterceptor = Interceptor { chain ->
        // MusicBrainz rate limits to 1 request per second. Enforce delay across threads.
        synchronized(rateLimitLock) {
            val now = System.currentTimeMillis()
            val timeSinceLast = now - lastRequestTimestamp.get()
            if (timeSinceLast < 1100L) {
                try {
                    Thread.sleep(1100L - timeSinceLast)
                } catch (_: InterruptedException) {}
            }
            lastRequestTimestamp.set(System.currentTimeMillis())
        }

        val original = chain.request()
        val request = original.newBuilder()
            .header("User-Agent", "NoviMusicApp/1.1 ( contact: spidymaadhu2@gmail.com; android-applet )")
            .header("Accept", "application/json")
            .method(original.method, original.body)
            .build()

        var response: Response = chain.proceed(request)

        // Retry once on 503 / 429 rate limit with backoff
        if (response.code == 503 || response.code == 429) {
            response.close()
            try {
                Thread.sleep(1500L)
            } catch (_: InterruptedException) {}
            response = chain.proceed(request)
        }

        response
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(userAgentAndRateLimitInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    val api: MusicBrainzApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MusicBrainzApi::class.java)
    }
}

