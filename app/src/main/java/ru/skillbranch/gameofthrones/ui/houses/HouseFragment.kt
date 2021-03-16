package ru.skillbranch.gameofthrones.ui.houses

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.skillbranch.gameofthrones.repositories.RootViewModel
import ru.skillbranch.gameofthrones.ui.character.CharactersAdapter

class HouseFragment: Fragment() {
    private lateinit var houseName: String
    private val adapter = CharactersAdapter()
    private lateinit var viewModel: RootViewModel


    // Вспомогательный объект нужен для инстанцирования фрагмента
    companion object {
        private const val ARG_HOUSE_NAME = "house_name"

        @JvmStatic
        fun newInstance(houseName: String): HouseFragment {
            return HouseFragment().apply {
                arguments = Bundle().apply { putString(ARG_HOUSE_NAME, houseName) }
            }
        }
    }
}