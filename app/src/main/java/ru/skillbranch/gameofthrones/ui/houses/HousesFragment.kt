package ru.skillbranch.gameofthrones.ui.houses

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_houses.*
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.R


class HousesFragment : Fragment() {
    private lateinit var housesPagerAdapter: HousesPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_houses, container, false)
        val actionBar = view.findViewById(R.id.toolbar) as Toolbar?
        (activity as? AppCompatActivity)?.setSupportActionBar(actionBar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        housesPagerAdapter = HousesPagerAdapter(this)

        viewPager.adapter = housesPagerAdapter
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = getPageTitle(position)
        }.attach()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                val tabView = tab.view as View
                val rect = Rect()
                tabView.getGlobalVisibleRect(rect)
                reveal(position, rect.centerX(), rect.centerY())
            }
        })
    }

    private fun getPageTitle(position: Int): String =
        AppConfig.NEED_HOUSES.map { App.getHouseHeader(it) }[position]

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
        searchView.queryHint = getString(R.string.search_character)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    private fun reveal(position: Int, x: Int, y: Int) {
        val pageTitle = getPageTitle(position)
        val houseSettings = App.getHouseSettings(pageTitle)
        val backgroundColor = requireContext().getColor(houseSettings.colorPrimary)

        revealView.visibility = View.VISIBLE
        val radius = revealView.width.coerceAtLeast(revealView.height) * 1.2f
        val reveal =
            ViewAnimationUtils.createCircularReveal(revealView, x.toInt(), y.toInt(), 0f, radius)

        reveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                backgroundView.setBackgroundColor(backgroundColor)
                revealView.visibility = View.INVISIBLE
            }
        })
        revealView.setBackgroundColor(backgroundColor)
        reveal.start()
    }
}