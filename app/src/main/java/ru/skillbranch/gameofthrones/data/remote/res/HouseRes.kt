
package ru.skillbranch.gameofthrones.data.remote.res

import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.data.local.entities.House

data class HouseRes(
    val url: String,
    val name: String,
    val region: String,
    val coatOfArms: String,
    val words: String,
    val titles: List<String> = listOf(),
    val seats: List<String> = listOf(),
    val currentLord: String,
    val heir: String,
    val overlord: String,
    val founded: String,
    val founder: String,
    val diedOut: String,
    val ancestralWeapons: List<String> = listOf(),
    val cadetBranches: List<Any> = listOf(),
    val swornMembers: List<String> = listOf()
) : IRes {
    override val id: String
        get() = App.getHouseHeader(name)
}

fun HouseRes.toHouse() = House(
    id = id,
    name = name,
    region = region,
    coatOfArms = coatOfArms,
    words = words,
    titles = titles,
    seats = seats,
    currentLord = currentLord.lastSegment(),
    heir = heir,
    overlord = overlord.lastSegment(),
    founded = founded,
    founder = founder.lastSegment(),
    diedOut = diedOut,
    ancestralWeapons = ancestralWeapons
)