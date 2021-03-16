package ru.skillbranch.gameofthrones.data.remote.res

import ru.skillbranch.gameofthrones.data.local.entities.Character

data class CharacterRes(
    val url: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val titles: List<String> = listOf(),
    val aliases: List<String> = listOf(),
    val father: String,
    val mother: String,
    val spouse: String,
    val allegiances: List<String> = listOf(),
    val books: List<String> = listOf(),
    val povBooks: List<Any> = listOf(),
    val tvSeries: List<String> = listOf(),
    val playedBy: List<String> = listOf()
) : IRes {
    override val id: String
        get() = url.lastSegment()
    lateinit var houseId: String
}

fun CharacterRes.toCharacter() = Character(
    id = id,
    name = name,
    gender = gender,
    culture = culture,
    born = born,
    died = died,
    titles = titles,
    aliases = aliases,
    father = father.lastSegment(),
    mother = mother.lastSegment(),
    spouse = spouse.lastSegment(),
    houseId = houseId
)

interface IRes {
    val id: String
    fun String.lastSegment(divider: String = "/"): String {
        return split(divider).last()
    }
}