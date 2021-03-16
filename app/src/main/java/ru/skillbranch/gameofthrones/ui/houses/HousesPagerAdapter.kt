package ru.skillbranch.gameofthrones.ui.houses

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.AppConfig

class HousesPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val housesNames = AppConfig.NEED_HOUSES.map { App.getHouseHeader(it)}

    override fun getItemCount(): Int =
        housesNames.size

    override fun createFragment(position: Int): Fragment =
        HouseFragment.newInstance(housesNames[position])
}