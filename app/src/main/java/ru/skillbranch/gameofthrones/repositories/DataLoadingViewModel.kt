package ru.skillbranch.gameofthrones.repositories

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skillbranch.gameofthrones.R

class DataLoadingViewModel(val app: Application) : AndroidViewModel(app) {

    private val repository = RootRepository
    private val isNetworkActive: Boolean
        get() {
            val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork?.isConnected ?: false
        }

    enum class LoadingDataState {
        UNKNOWN,
        FINISHED,
        ERROR
    }

    val loadingDataState = MutableLiveData<LoadingDataState>()

    init {
        loadingDataState.value = LoadingDataState.UNKNOWN
    }

    // Загрузка данных из сети
    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.needUpdate()) {
                if (!isNetworkActive) {
                    loadingDataState.postValue(LoadingDataState.ERROR)
                    return@launch
                }
                // Заполняем таблицы в БД
                repository.fillData()
                loadingDataState.postValue(LoadingDataState.FINISHED)
            } else {
                delay(5000)
                loadingDataState.postValue(LoadingDataState.FINISHED)
            }
        }
    }

    fun getErrorString(): String {
        return app.getString(R.string.error_no_network)
    }
}