package ru.skillbranch.gameofthrones.db

import androidx.room.*
import ru.skillbranch.gameofthrones.data.local.entities.House

@Dao
abstract class HouseDao {
    // Заполнить таблицу списком домов
    @Transaction
    open suspend fun insertHouses(houses: List<House>) {
        houses.forEach { insertHouse (it) }
    }

    // Вставить в таблицу дом
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertHouse(house: House): Long

    // Удалить таблицу дом
    @Query("DELETE FROM house")
    abstract suspend fun cleanTable()

    // Получить название дома
    @Query("SELECT words FROM house WHERE id = :id")
    abstract suspend fun getHouseWords(id: String): String

    // Получить кол-во домой
    @Query("SELECT COUNT(id) FROM house WHERE id > 0")
    abstract suspend fun getCount(): Int

}