package ru.skillbranch.gameofthrones.repositories

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.data.remote.res.toCharacter
import ru.skillbranch.gameofthrones.data.remote.res.toHouse
import ru.skillbranch.gameofthrones.network.ApiHelper
import ru.skillbranch.gameofthrones.network.NetworkService
import ru.skillbranch.gameofthrones.network.Result

/// Единственный и общий репозиторий
object RootRepository {
    private val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }

    // SupervisorJob() нужен для того, что бы при краше дочерней корутины не закрывались все остальные
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + handler)
    private val apiHelper = ApiHelper(NetworkService.getJSONApi())

    /// Метод для проверки нужно дли обновить БД. Просто проверим есть ли там данные
    suspend fun needUpdate(): Boolean = App.database.isEmpty()

    /// Метод для заполнения таблиц данными
    suspend fun fillData() {
        val housesFromNetwork = (getNeedHouseWithCharactersInternal(*AppConfig.NEED_HOUSES))
        coroutineScope.launch {
            val houses = housesFromNetwork.map { it.first.toHouse() }
            launch {
                App.database.getHouseDao().insertHouses(houses)
            }
            housesFromNetwork.forEach { houseNet ->
                launch {
                    val characters = houseNet.second.map { it.toCharacter() }
                    App.database.getCharacterDao().insertCharacters(characters)
                }
            }
        }.join()
    }

    suspend fun getCharactersByHouseName(name: String) =
        App.database.getCharacterDao().getCharactersByHouseName(name)

    suspend fun getFullCharacter(id: String) = App.database.getCharacterDao().getFullCharacterInfo(id)

    /**
     * Получение данных о всех домах из сети
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result : (houses : List<HouseRes>) -> Unit) {
        val resultList = mutableListOf<HouseRes>()
        coroutineScope.launch {
            var pageNumber: Int = 1
            while (true) {
                val response = apiHelper.getHouses(pageNumber)
                // Если запрос не удачный - возвращаем управление
                if (response !is Result.Success) {
                    return@launch
                }
                // Если данных по этому дому нет - продолажаем со следующим
                if (response.data.isEmpty()) {
                    break;
                }
                resultList.addAll(response.data)
                pageNumber++
            }
            result(resultList)
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouses(vararg houseNames: String, result : (houses : List<HouseRes>) -> Unit) {
        coroutineScope.launch {
            result(getNeedHousesInternal(*houseNames))
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouseWithCharacters(vararg houseNames: String, result : (houses : List<Pair<HouseRes, List<CharacterRes>>>) -> Unit) {
        coroutineScope.launch {
            result(getNeedHouseWithCharactersInternal(*houseNames))
        }
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertHouses(houses : List<HouseRes>, complete: () -> Unit) {
        val listHouses = mutableListOf<House>()
        houses.forEach{ house -> listHouses.add(house.toHouse())}
        coroutineScope.launch {
            App.database.getHouseDao().insertHouses(listHouses)
            complete()
        }
    }

    /**
     * Запись данных о пересонажах в DB
     * @param Characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertCharacters(Characters : List<CharacterRes>, complete: () -> Unit) {
        val listCharactersHouses = mutableListOf<Character>()
        Characters.forEach { characterRes -> listCharactersHouses.add(characterRes.toCharacter()) }
        coroutineScope.launch {
            App.database.getCharacterDao().insertCharacters(listCharactersHouses)
            complete()
        }
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun dropDb(complete: () -> Unit) {
        coroutineScope.launch {
            App.database.cleanDb()
            complete()
        }
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharactersByHouseName(name : String, result: (characters : List<CharacterItem>) -> Unit) {
        coroutineScope.launch {
            result(App.database.getCharacterDao().getCharactersByHouseName(name))
        }
    }

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharacterFullById(id : String, result: (character : CharacterFull) -> Unit) {
        coroutineScope.launch {
            result(App.database.getCharacterDao().getFullCharacterInfo(id))
        }
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    fun isNeedUpdate(result: (isNeed : Boolean) -> Unit){
        coroutineScope.launch {
            result(App.database.isEmpty())
        }
    }

    /// Метод запрашивает список домов через АПИ по именам
    private suspend fun getNeedHousesInternal(vararg houseNames: String): List<HouseRes> {
        val resultList = mutableListOf<HouseRes>()
        coroutineScope.launch {
            houseNames.forEach { houseName ->
                val response = apiHelper.getHousesByName(houseName)
                if (response is Result.Success) {
                    val netResult = response.data
                    netResult.let {
                        resultList.add(it)
                    }
                }
            }
        }.join()
        return resultList
    }

    /// Метод возвращает список пар - (Дом, список персонажей дома). Получает имена домов
    private suspend fun getNeedHouseWithCharactersInternal(vararg houseNames: String): List<Pair<HouseRes, List<CharacterRes>>> {
        val resultList = mutableListOf<Pair<HouseRes, List<CharacterRes>>>()
        // Получим список домов
        val houses = getNeedHousesInternal(*houseNames)

        coroutineScope.launch {
            houses.forEach { house ->
                val characters = mutableListOf<CharacterRes>()
                resultList.add(house to characters)
                house.swornMembers.forEach { houseMember ->
                    launch {
                        val response = apiHelper.getCharacter(houseMember)
                        if (response is Result.Success) {
                            val netResult = response.data
                            characters.add(netResult.apply { houseId = house.url })
                        }
                    }
                }
            }
        }.join()
        return resultList
    }
}