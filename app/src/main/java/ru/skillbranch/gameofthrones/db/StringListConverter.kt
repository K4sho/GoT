package ru.skillbranch.gameofthrones.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

internal object StringListConverter {
    @JvmStatic
    @TypeConverter
    fun restoreList(listOfString: String?): List<String?>? {
        return Gson().fromJson(
            listOfString,
            object : TypeToken<List<String?>?>() {}.type
        )
    }

    @JvmStatic
    @TypeConverter
    fun saveList(listOfString: List<String?>?): String? {
        return Gson().toJson(listOfString)
    }
}