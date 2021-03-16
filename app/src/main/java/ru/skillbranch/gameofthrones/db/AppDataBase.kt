package ru.skillbranch.gameofthrones.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House

@Database(
    entities = [Character::class, House::class],
    views = [CharacterFull::class, CharacterItem::class],
    version = 1
)
@TypeConverters(StringListConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getCharacterDao() : CharacterDao
    abstract fun getHouseDao() : HouseDao

    suspend fun cleanDb() {
        getCharacterDao().cleanTable()
        getHouseDao().cleanTable()
    }

    suspend fun isEmpty() : Boolean {
        return  getCharacterDao().getCount() == 0 &&
                getHouseDao().getCount() == 0
    }
}