package ru.skillbranch.gameofthrones.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

interface ApiService {
    @GET("houses")
    suspend fun getHousesAsync(@Query(value = "page")page: Int, @Query(value = "pageSize")pageSize: Int): Response<List<HouseRes>>

    @GET("houses")
    suspend fun getHouseByNameAsync(@Query(value = "name")name: String): Response<List<HouseRes>>

    @GET("characters/{id}")
    suspend fun getCharacterAsync(@Path("id")id: String): Response<CharacterRes>
}