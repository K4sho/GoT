package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*

@Entity(
    tableName = "house",
    indices = [Index("id")]
)
data class House(
    @PrimaryKey
    val id: String,
    val name: String,
    val region: String,
    @ColumnInfo(name = "coat_of_arms")
    val coatOfArms: String,
    val words: String,
    val titles: List<String>,
    val seats: List<String>,
    @ColumnInfo(name = "current_lord")
    val currentLord: String, //rel
    val heir: String, //rel
    val overlord: String,
    val founded: String,
    val founder: String, //rel
    val diedOut: String,
    @ColumnInfo(name = "ancestral_weapons")
    val ancestralWeapons: List<String>
)