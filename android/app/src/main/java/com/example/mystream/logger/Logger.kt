package com.example.mystream.logger

import android.util.Log
import com.example.mystream.BuildConfig

class Logger(
    private val tag: String,
) {
    fun d(message: String, throwable: Throwable? = null) {
        if (!BuildConfig.DEBUG) return
        Log.d(tag, message, throwable)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (!BuildConfig.DEBUG) return
        Log.e(tag, message, throwable)
    }
}