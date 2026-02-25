package com.ebookstore.user.di

import com.ebookstore.user.BuildConfig
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkModuleTest {

    @Test
    fun provideRetrofit_normalizesBaseUrlWithTrailingSlash() {
        val retrofit = NetworkModule.provideRetrofit(OkHttpClient(), Gson())
        val expected = BuildConfig.API_BASE_URL.trimEnd('/') + "/"

        assertEquals(expected, retrofit.baseUrl().toString())
    }
}
