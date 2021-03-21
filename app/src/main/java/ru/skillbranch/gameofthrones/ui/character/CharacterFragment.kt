package ru.skillbranch.gameofthrones.ui.character

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.android.synthetic.main.fragment_character.*
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.HouseUiSettings
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.databinding.FragmentCharacterBinding
import ru.skillbranch.gameofthrones.repositories.RootViewModel

class CharacterFragment : Fragment() {
    private lateinit var binding: FragmentCharacterBinding
    private lateinit var viewModel: RootViewModel
    private lateinit var characterId: String
    private lateinit var houseName: String
    private lateinit var houseSettings: HouseUiSettings

    private val observer =
        Observer<CharacterFull> { character: CharacterFull ->
            binding.character = character

            home.setOnClickListener {
                NavHostFragment.findNavController(this@CharacterFragment).navigateUp()
            }

            toolbar.text = character.name
            character.father?.let {
                group_father.visibility = View.VISIBLE
                father_btn.text = it.name
                father_btn.setBackgroundResource(houseSettings.colorPrimary)
                val action =
                    CharacterFragmentDirections.actionCharacterSelf(
                        it.id, it.house
                    )
                father_btn.setOnClickListener { findNavController().navigate(action) }
            }

            character.mother?.let {
                mother_group.visibility = View.VISIBLE
                mother_btn.text = it.name
                mother_btn.setBackgroundResource(houseSettings.colorPrimary)
                val action =
                    CharacterFragmentDirections.actionCharacterSelf(
                        it.id, it.house
                    )
                mother_btn.setOnClickListener { findNavController().navigate(action) }
            }

            if (character.died.isNotBlank()) {
                Snackbar.make(
                    coordinator,
                    "Died in : ${character.died}",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        characterId = arguments?.getString("characterId")
            ?: throw IllegalStateException("No character for view")
        houseName = arguments?.getString("houseName")
            ?: throw IllegalStateException("No character for view")

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        viewModel = ViewModelProvider(requireActivity()).get(RootViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_character, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        houseSettings = App.getHouseSettings(houseName)

        house_arms.setImageResource(houseSettings.coastOfArms)
        house_arms.setBackgroundResource(houseSettings.colorPrimary)

        val colorAccent = requireContext().getColor(houseSettings.colorAccent)
        listOf(words_label, born_label, titles_label, aliases_label).forEach {
            it.compoundDrawables.firstOrNull()?.colorFilter =
                PorterDuffColorFilter(colorAccent, PorterDuff.Mode.SRC_IN)
        }

        viewModel.getFullCharacter(characterId).observe(requireActivity(), observer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.getFullCharacter(characterId).removeObserver(observer)
    }
}