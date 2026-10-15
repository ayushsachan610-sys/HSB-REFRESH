package com.hsbrefresh.fest.data.network

import android.content.Context
import android.content.SharedPreferences

object ApiConfig {

    private const val PREFS_NAME = "hsb_refresh_api_prefs"
    private const val KEY_BASE_URL = "api_base_url"

    // Default development host for Android Emulator accessing host machine FastAPI server on port 8082
    const val DEFAULT_EMULATOR_URL = "http://10.0.2.2:8082/"
    const val DEFAULT_LOCALHOST_URL = "http://127.0.0.1:8082/"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getBaseUrl(context: Context): String {
        val url = getPrefs(context).getString(KEY_BASE_URL, DEFAULT_EMULATOR_URL) ?: DEFAULT_EMULATOR_URL
        return if (url.endsWith("/")) url else "$url/"
    }

    fun setBaseUrl(context: Context, newUrl: String) {
        val formatted = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        getPrefs(context).edit().putString(KEY_BASE_URL, formatted).apply()
        // Invalidate cached Retrofit client so subsequent requests use the new base URL
        ApiClient.resetClient()
    }
}
