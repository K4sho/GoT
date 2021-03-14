package ru.skillbranch.gameofthrones.repositories

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel

class DataLoadingViewModel(val app: Application) : AndroidViewModel(app) {

    private val repository: RootRepository
    private val isNetworkActive: Boolean
        get() {
            val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork?.isConnected ?: false
        }
}