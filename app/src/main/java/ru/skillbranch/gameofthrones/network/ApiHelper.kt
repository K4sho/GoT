package ru.skillbranch.gameofthrones.network

import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import java.io.IOException
import java.lang.Exception

// Вспомогательная функция. Позволяет вызвать метод запроса в корутине и передать сообщение, которое будет выброшено при ошибке
suspend fun <T: Any> safeApiCall(call: suspend () -> Result<T>, errorMessage: String): Result<T> =
    try {
        call.invoke()
    } catch (e: Exception) {
        Result.Error(IOException(errorMessage, e))
    }

class ApiHelper(private val apiService: ApiService) {
    suspend fun getHouses(pageNumber: Int) = safeApiCall(
        call = { fetchHouses(pageNumber) },
        errorMessage = "Get Houses Error"
    )

    suspend fun getHousesByName(name: String) = safeApiCall(
        call = { fetchHousesByName(name) },
        errorMessage = "Get HousesByName Error"
    )

    suspend fun getCharacter(url: String) = safeApiCall(
        call = { fetchCharacter(url) },
        errorMessage = "Get Error Character"
    )


    /// Метод для запроса списка домов
    private suspend fun fetchHouses(pageNumber: Int): Result<List<HouseRes>> {
        val response = apiService.getHousesAsync(pageNumber, AppConfig.API_PAGE_SIZE)
        if (response.isSuccessful) {
            return response.body()?.let { Result.Success(it) }!!
        }
        return Result.Error(IOException("Error occurred during fetching houses!"))
    }

    /// Метод для запроса дома по имени
    private suspend fun fetchHousesByName(name: String): Result<HouseRes> {
        val response = apiService.getHouseByNameAsync(name)
        if (response.isSuccessful) {
            return response.body()?.let { Result.Success(it.first()) }!!
        }
        return Result.Error(IOException("Error occurred during fetching houses!"))    }

    private suspend fun fetchCharacter(url: String): Result<CharacterRes> {
        val id = url.substringAfterLast("/")
        val response = apiService.getCharacterAsync(id)
        if (response.isSuccessful) {
            return response.body()?.let { Result.Success(it)}!!
        }
        return Result.Error(IOException("Error occurred during fetching houses!"))
    }

}