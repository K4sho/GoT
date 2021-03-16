package ru.skillbranch.gameofthrones.db

import androidx.room.*
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.Character

@Dao
abstract class CharacterDao {
    // Вспомогательный метод для вставки в таблицу переданного списка персонажей
    @Transaction
    open suspend fun insertCharacters(characters: List<Character>) {
        characters.forEach {insertCharacters(it) }
    }

    // Метод для вставки в таблицу Персонажа
    // REPLACE для того что бы обновить запись, если такая уже есть
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCharacters(characters: Character): Long

    // Удаление таблицы персонажей
    @Query("DELETE FROM character")
    abstract suspend fun cleanTable()

    // Получить список персонажей по id дома
    @Transaction
    @Query("SELECT * FROM CharacterItem WHERE house = :house_id")
    abstract suspend fun getCharactersByHouseName(house_id: String): List<CharacterItem>

    // Получить персонажа по его id
    @Query("SELECT * FROM character WHERE id = :id")
    abstract suspend fun getCharacterById(id: String): Character

    // Получить информацию о персонаже по id
    @Transaction
    @Query("SELECT * FROM CharacterFull WHERE id = :id")
    abstract suspend fun getFullCharacterInfo(id: String): CharacterFull

    // Получить количество персонажей
    @Query("SELECT COUNT(id) FROM character WHERE id > 0")
    abstract suspend fun getCount(): Int

}