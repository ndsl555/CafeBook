package com.example.cafebook
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.savedstate.SavedState
import com.example.cafebook.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity :
    AppCompatActivity(),
    NavController.OnDestinationChangedListener,
    View.OnClickListener,
    CoroutineScope by MainScope() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        val bottomNavView: BottomNavigationView = findViewById(R.id.mainBottomNavigationView)
        val navHostFragment = supportFragmentManager.findNavHostFragment(R.id.navHostView)
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_home_graph)

        bottomNavView.setOnItemSelectedListener {
            Log.d("FragmentCheck", "BottomNavigationView item clicked: ${it.itemId}")

            when (it.itemId) {
                R.id.nav_search -> {
                    Log.d("FragmentCheck", "SFragment navigationPocketFragment")
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationSearchFragment())
                }

                R.id.nav_map -> {
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationNearFragment())
                }

                R.id.nav_barcode -> {
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationBarCodeFragment())
                }

                R.id.nav_pocket -> {
                    Log.d("FragmentCheck", "PocketFragment navigationPocketFragment")

                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationPocketFragment())
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: SavedState?,
    ) {
        TODO("Not yet implemented")
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}

private fun FragmentManager.findNavHostFragment(
    @IdRes id: Int,
) = findFragmentById(id) as NavHostFragment
