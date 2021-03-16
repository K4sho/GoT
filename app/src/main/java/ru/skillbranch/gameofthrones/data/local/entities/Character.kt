package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*
import ru.skillbranch.gameofthrones.db.StringListConverter

@Entity(
    tableName = "character",
    foreignKeys = [ForeignKey(
        entity = House::class,
        parentColumns = ["id"],
        childColumns = ["house_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("id"), Index("house_id")]
)
data class Character(
    @PrimaryKey
    val id: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    @TypeConverters(StringListConverter::class)
    val titles: List<String> = listOf(),
    @TypeConverters(StringListConverter::class)
    val aliases: List<String> = listOf(),
    val father: String, //rel
    val mother: String, //rel
    val spouse: String, //rel
    @ColumnInfo(name = "house_id")
    val houseId: String//rel
)

@DatabaseView("SELECT id, name, house_id as house, titles, aliases FROM character")
data class CharacterItem(
    val id: String,
    val house: String, //rel
    val name: String,
    val titles: List<String>,
    val aliases: List<String>
) : CharacterStrings {
    override val titlesString: String
        get() = titles.convertToString()

    override val aliasesString: String
        get() = aliases.convertToString()

    override val titlesAndAliasesString: String
        get() {
            if (titles.isNullOrEmpty() && aliases.isNullOrEmpty()) {
                return ""
            }
            return if (titles.isNullOrEmpty()) {
                aliases.convertToString()
            } else {
                titles.convertToString() + " " + aliases.convertToString()
            }
        }
}

@DatabaseView(
    "SELECT character.id as id, character.name as name, character.born, " +
            "character.died, character.titles, character.aliases, character.house_id as house, house.words, " +
            "father.id as father_id, father.name AS father_name, father.house_id AS father_house, " +
            "mother.id as mother_id, mother.name AS mother_name, mother.house_id AS mother_house " +
            "FROM character " +
            "LEFT JOIN character AS father ON character.father = father.id " +
            "LEFT JOIN character AS mother ON character.mother = mother.id " +
            "JOIN house ON character.house_id = house.id"
)
data class CharacterFull(
    val id: String,
    val name: String,
    val words: String,
    val born: String,
    val died: String,
    val titles: List<String>,
    val aliases: List<String>,
    val house: String, //rel
    @Embedded(prefix = "father_")
    val father: RelativeCharacter?,
    @Embedded(prefix = "mother_")
    val mother: RelativeCharacter?
) : CharacterStrings {
    override val titlesString: String
        get() = titles.convertToString("\n")

    override val aliasesString: String
        get() = aliases.convertToString("\n")

    override val titlesAndAliasesString: String
        get() = ""
}

interface CharacterStrings {
    val titlesString: String
    val aliasesString: String
    val titlesAndAliasesString: String

    fun List<String>.convertToString(separator: String = " • "): String {
        return joinToString(separator).trim()
    }
}

data class RelativeCharacter(
    val id: String,
    val name: String,
    val house: String //rel
)