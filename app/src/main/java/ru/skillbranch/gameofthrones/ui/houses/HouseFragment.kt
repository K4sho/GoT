package ru.skillbranch.gameofthrones.ui.houses

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.android.synthetic.main.fragment_house.*
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootViewModel
import ru.skillbranch.gameofthrones.ui.character.CharactersAdapter

class HouseFragment: Fragment() {
    private lateinit var houseName: String
    private val adapter = CharactersAdapter()
    private lateinit var viewModel: RootViewModel

    private val observer =
        Observer<List<CharacterItem>> { items: List<CharacterItem> ->
            val houseSettings = App.getHouseSettings(houseName)
            adapter.setData(houseSettings, items)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)

        viewModel = ViewModelProvider(requireActivity()).get(RootViewModel::class.java)
        houseName = arguments?.getString(ARG_HOUSE_NAME, "") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_house, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        characters.layoutManager = LinearLayoutManager(context)
        characters.itemAnimator = DefaultItemAnimator()
        characters.adapter = adapter
        characters.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        viewModel.getHouseCharacter(houseName).observe(requireActivity(), observer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.getHouseCharacter(houseName).removeObserver(observer)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean  = false

            override fun onQueryTextChange(query: String): Boolean {
                adapter.filter.filter(query)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        private const val ARG_HOUSE_NAME = "house_name"

        @JvmStatic
        fun newInstance(houseName: String): HouseFragment {
            return HouseFragment().apply {
                arguments = Bundle().apply {putString(ARG_HOUSE_NAME, houseName)}
            }
        }
    }
}