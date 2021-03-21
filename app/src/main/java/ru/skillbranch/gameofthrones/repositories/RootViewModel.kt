package ru.skillbranch.gameofthrones.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem

class RootViewModel : ViewModel() {
    private val repository = RootRepository

    /**
     * // Метод отдает лайвдату на которую можно подписаться. Возвращает полную список персонажей дома
     */
    fun getHouseCharacter(houseName: String): LiveData<List<CharacterItem>> {
        val result = MutableLiveData<List<CharacterItem>>()
        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(repository.getCharactersByHouseName(houseName))
        }
        return result
    }

    /**
     * Метод отдает лайвдату на которую можно подписаться. Возвращает полную информацию о персонаже
     */
    fun getFullCharacter(id: String): LiveData<CharacterFull> {
        val result = MutableLiveData<CharacterFull>()
        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(repository.getFullCharacter(id))
        }
        return result
    }
}