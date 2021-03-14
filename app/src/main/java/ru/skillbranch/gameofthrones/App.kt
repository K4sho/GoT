package ru.skillbranch.gameofthrones

import android.app.Application
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.room.Room
import ru.skillbranch.gameofthrones.db.AppDatabase

data class HouseUiSettings(
    val name: String,
    @ColorRes val colorPrimary: Int,
    @ColorRes val colorAccent: Int,
    @ColorRes val colorDark: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val coastOfArms: Int
)

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "game_of_thrones_db")
            .build()
    }

    companion object {
        lateinit var database: AppDatabase
        private val houses = listOf(
            HouseUiSettings(
                "Stark",
                R.color.stark_primary,
                R.color.stark_accent,
                R.color.stark_dark,
                R.drawable.stark_icon,
                R.drawable.stark_coast_of_arms
            ),
            HouseUiSettings(
                "Lannister",
                R.color.lannister_primary,
                R.color.lannister_accent,
                R.color.lannister_dark,
                R.drawable.lannister_icon,
                R.drawable.lannister_coast_of_arms
            ),
            HouseUiSettings(
                "Targaryen",
                R.color.targaryen_primary,
                R.color.targaryen_accent,
                R.color.targaryen_dark,
                R.drawable.targaryen_icon,
                R.drawable.targaryen_coast_of_arms
            ),
            HouseUiSettings(
                "Baratheon",
                R.color.baratheon_primary,
                R.color.baratheon_accent,
                R.color.baratheon_dark,
                R.drawable.baratheon_icon,
                R.drawable.baratheon_coast_of_arms
            ),
            HouseUiSettings(
                "Greyjoy",
                R.color.greyjoy_primary,
                R.color.greyjoy_accent,
                R.color.greyjoy_dark,
                R.drawable.greyjoy_icon,
                R.drawable.greyjoy_coast_of_arms
            ),
            HouseUiSettings(
                "Martel",
                R.color.martel_primary,
                R.color.martel_accent,
                R.color.martel_dark,
                R.drawable.martel_icon,
                R.drawable.martel_coast_of_arms
            ),
            HouseUiSettings(
                "Tyrell",
                R.color.tyrel_primary,
                R.color.tyrel_accent,
                R.color.tyrel_dark,
                R.drawable.tyrel_icon,
                R.drawable.tyrel_coast_of_arms
            )
        )

        fun getHouseSettings(name: String): HouseUiSettings  = houses.first { name.contains(it.name, ignoreCase = true) }

        fun getHouseHeader(fullName: String): String  = houses.first { fullName.contains(it.name, ignoreCase = true) }.name
    }
}