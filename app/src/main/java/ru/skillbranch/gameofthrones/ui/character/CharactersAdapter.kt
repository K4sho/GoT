package ru.skillbranch.gameofthrones.ui.character

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_character.view.*
import ru.skillbranch.gameofthrones.HouseUiSettings
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.ui.houses.HousesFragmentDirections
import java.util.*
import kotlin.collections.ArrayList

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CharacterItem>() {
    override fun areItemsTheSame(oldItem: CharacterItem, newItem: CharacterItem): Boolean {
        return (oldItem.id == newItem.id && oldItem.house == newItem.house)
    }

    override fun areContentsTheSame(oldItem: CharacterItem, newItem: CharacterItem): Boolean {
        return (oldItem.name == newItem.name && oldItem.titles == newItem.titles && oldItem.aliases == newItem.aliases)
    }

}

class CharactersAdapter : ListAdapter<CharacterItem, RecyclerView.ViewHolder>(DIFF_CALLBACK), Filterable {
    private lateinit var fullList: List<CharacterItem>
    private lateinit var houseSettings: HouseUiSettings

    fun setData(houseSettings: HouseUiSettings, newCharacters: Collection<CharacterItem>) {
        this.houseSettings = houseSettings
        fullList = newCharacters.let { it.sortedBy { item -> item.name } }
        submitList(fullList)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.view_character, viewGroup, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val options = getItem(position)
        (holder as CharacterViewHolder).bind(options)
    }

    private inner class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var houseIcon: ImageView = itemView.house_icon
        private var characterName: TextView = itemView.character_name
        private var titles: TextView = itemView.titles

        fun bind(character: CharacterItem) {
            houseIcon.setImageResource(this@CharactersAdapter.houseSettings.icon)

            characterName.text =
                if (character.name.isBlank()) itemView.context.getString(R.string.no_information)
                else character.name

            titles.text = if (character.titlesAndAliasesString.isBlank())
                itemView.context.getString(R.string.no_information)
            else
                character.titlesAndAliasesString

            itemView.setOnClickListener {
                val action =
                    HousesFragmentDirections.actionHousesToCharacter(
                        character.id,
                        character.house
                    )
                findNavController(itemView).navigate(action)
            }
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val filteredCharacters = ArrayList<CharacterItem>()
                if (constraint == null || constraint.isEmpty()) {
                    filteredCharacters.addAll(fullList)
                } else {
                    val filterPattern =
                        constraint.toString().toLowerCase(Locale.getDefault()).trim()
                    filteredCharacters.addAll(fullList.filter {
                        it.name.toLowerCase(Locale.getDefault()).contains(filterPattern)
                    })
                }
                val results = FilterResults()
                results.values = filteredCharacters
                return results
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?
            ) {
                submitList(results?.values as List<CharacterItem>?)
            }
        }
    }
}