package ru.skillbranch.gameofthrones.ui.main

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.repositories.DataLoadingViewModel

// Являет хостом для нав.фрагмента
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var viewModel: DataLoadingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = DataLoadingViewModel(application)
        savedInstanceState ?: loadData()
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    private val observer: Observer<DataLoadingViewModel.LoadingDataState> =
        Observer {
            when(it) {
                DataLoadingViewModel.LoadingDataState.UNKNOWN -> {
                    viewModel.loadData()
                    navController.navigate(R.id.nav_splash)
                }
                DataLoadingViewModel.LoadingDataState.FINISHED -> {
                    navController.navigate(R.id.splash_to_houses)
                }
                DataLoadingViewModel.LoadingDataState.ERROR -> {
                    val parentLayout: View = findViewById(R.id.nav_host_fragment)
                    Snackbar
                        .make(parentLayout, viewModel.getErrorString(), Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }

    private fun loadData() {
        viewModel.loadingDataState.observe(this, observer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}