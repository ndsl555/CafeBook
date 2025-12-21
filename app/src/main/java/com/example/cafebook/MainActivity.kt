package com.example.cafebook
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.cafebook.Utils.NetworkUtils
import com.example.cafebook.Utils.NetworkUtils.getNetworkType
import com.example.cafebook.Utils.NetworkUtils.isNetworkConnected
import com.example.cafebook.databinding.ActivityMainBinding
import com.example.cafebook.ui.NoNetworkDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity :
    AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkNetworkStatus()
        observeNetworkState()
        initView()
    }

    private fun observeNetworkState() {
        lifecycleScope.launch {
            NetworkUtils.networkState.collect { isConnected ->
                if (isConnected) {
                    // 網路恢复
                    showNetworkRestored()
                } else {
                    // 網路斷開
                    showNetworkLost()
                }
            }
        }
    }

    private fun showNetworkRestored() {
        // 显示網路恢复提示
        Toast.makeText(this, "網路已恢复", Toast.LENGTH_SHORT).show()
    }

    private fun showNetworkLost() {
        // 显示網路斷開提示
        showNoWifiDialog()
        Toast.makeText(this, "網路已斷開", Toast.LENGTH_SHORT).show()
    }

    private fun checkNetworkStatus() {
        if (isNetworkConnected(baseContext)) {
            val networkType = getNetworkType(baseContext)
            println("網路已連接 - $networkType")
        } else {
            showNoWifiDialog()
            Toast.makeText(this, "網路未連接", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNoWifiDialog() {
        NoNetworkDialogFragment().show(supportFragmentManager, "NoNetworkDialogFragment")
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
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationSearchFragment())
                }

                R.id.nav_map -> {
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationNearFragment())
                }

                R.id.nav_barcode -> {
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationBarCodeFragment())
                }

                R.id.nav_pocket -> {
                    navController.navigate(NavHomeGraphDirections.actionGlobalToNavigationPocketFragment())
                }
            }
            return@setOnItemSelectedListener true
        }
    }
}

private fun FragmentManager.findNavHostFragment(
    @IdRes id: Int,
) = findFragmentById(id) as NavHostFragment
