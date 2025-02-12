package com.example.foodify.main


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import coil.load
import com.example.foodify.R
import com.example.foodify.authentication.ui.AuthActivity
import com.example.foodify.databinding.ActivityMainBinding
import com.example.foodify.util.NetworkUtils
import com.example.foodify.util.showErrorSnackBar
import com.example.foodify.util.showLoginDialog
import com.example.foodify.util.showSuccessSnackBar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModel()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var networkUtils: NetworkUtils
    private var connectAfterDisconnection = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        networkUtils = NetworkUtils(this)
        showSuccessLogin()
        setupToolbar()
        setUpNavController()
        observeNetworkState()
        checkInitialNetworkState()
        handleBackButtonForNonTopLevel()
        setupBottomAndDrawerNav()
        setupAppBarConfiguration()
        loadUserData()
        handleOnItemClickListener()
        handleOnNavigationItemListener()
        handleSearch()

    }

    private fun handleSearch() {
        binding.toolbar.searchAutoCompleteTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.toolbar.searchAutoCompleteTextView.clearFocus() // Hide keyboard
                true
            } else {
                false
            }
        }
    }

    private fun checkInitialNetworkState() {
        val isConnected = networkUtils.hasNetworkConnection()
        handleNetworkState(isConnected) // Handle initial state
    }

    private fun observeNetworkState() {
        lifecycleScope.launch {
            networkUtils.observeNetworkState().collect { isConnected ->
                handleNetworkState(isConnected) // Handle state changes
            }
        }
    }

    private fun handleOnItemClickListener() {
        val isGuest = viewModel.getCurrentUser() == null || viewModel.isUserGuest()
        val allowedFragments = setOf(R.id.bookmarksFragment, R.id.calenderFragment)
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId in allowedFragments) {
                if (isGuest) {
                    handleGuestPermission()
                    return@setOnItemSelectedListener false
                }
                navController.navigate(item.itemId)
                true
            } else {
                navController.navigate(item.itemId)
                true
            }
        }
    }

    private fun handleNetworkState(connected: Boolean) {
        val isGuest = viewModel.getCurrentUser() == null || viewModel.isUserGuest()

        if (!connected) {
            connectAfterDisconnection = true
            showErrorSnackBar(binding.root, "Please check your internet connection")
            if (isGuest) {
                binding.navHostFragment.visibility = View.GONE
                binding.toolbar.toolbar.visibility = View.GONE
                binding.noInternetText.visibility = View.VISIBLE
                binding.noInternetImage.visibility = View.VISIBLE
                binding.bottomNavigationView.visibility = View.GONE
            } else {
                binding.navHostFragment.visibility = View.GONE
                binding.toolbar.toolbar.visibility = View.GONE
                binding.noInternetText.visibility = View.VISIBLE
                binding.noInternetImage.visibility = View.VISIBLE
                binding.bottomNavigationView.visibility = View.VISIBLE
                binding.bottomNavigationView.setOnItemSelectedListener { item ->
                    if (item.itemId !in setOf(R.id.bookmarksFragment, R.id.calenderFragment)) {
                        showErrorSnackBar(binding.root, "Please check your internet connection")
                        return@setOnItemSelectedListener false
                    }
                    binding.navHostFragment.visibility = View.VISIBLE
                    binding.toolbar.toolbar.visibility = View.GONE
                    binding.noInternetText.visibility = View.GONE
                    binding.noInternetImage.visibility = View.GONE
                    navController.navigate(item.itemId)
                    true
                }
            }
        } else {
            Log.d("zzzzz", "handleNetworkState: $connectAfterDisconnection")
            if(connectAfterDisconnection) {
                showSuccessSnackBar(binding.root, "Connection Restored")
                connectAfterDisconnection = false
            }
            val currentDestinationId = navController.currentDestination?.id
            binding.noInternetText.visibility = View.GONE
            binding.noInternetImage.visibility = View.GONE
            binding.navHostFragment.visibility = View.VISIBLE
            if (currentDestinationId != null && currentDestinationId in setOf(
                    R.id.bookmarksFragment,
                    R.id.calenderFragment,
                    R.id.mealDetailsFragment
                )
            )
                binding.toolbar.toolbar.visibility = View.GONE
            else
                binding.toolbar.toolbar.visibility = View.VISIBLE

            binding.bottomNavigationView.visibility = View.VISIBLE
            handleOnItemClickListener()

        }
    }


    private fun handleOnNavigationItemListener() {
        val isGuest = viewModel.getCurrentUser() == null || viewModel.isUserGuest()
        binding.navigationView.setNavigationItemSelectedListener { item ->
            if (isGuest && (item.itemId == R.id.bookmarksFragment || item.itemId == R.id.calenderFragment)) {
                showErrorSnackBar(binding.root, "Please log in to access this feature")
                false
            } else {
                when (item.itemId) {
                    R.id.logOut -> {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                        viewModel.logout()
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                        finish()
                        true
                    }

                    else -> {
                        val handled = NavigationUI.onNavDestinationSelected(item, navController)
                        if (handled) binding.drawerLayout.closeDrawer(GravityCompat.START)
                        handled
                    }
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadUserData() {
        binding.toolbar.userImage.load(viewModel.getCurrentUser()?.photoUrl) {
            placeholder(R.drawable.person) // Show while loading
            error(R.drawable.person) // Show if URL is null or fails
            fallback(R.drawable.person) // Show if URL is explicitly null
        }
        val headerView = binding.navigationView.getHeaderView(0)
        val userImage = headerView.findViewById<ImageView>(R.id.userHeaderImage)
        val userName = headerView.findViewById<TextView>(R.id.profileHeaderName)
        if (viewModel.getCurrentUser() == null || viewModel.isUserGuest()) {
            userName.text = "Guest"
        } else {
            userName.text = viewModel.getCurrentUser()?.displayName
        }
        userImage.load(viewModel.getCurrentUser()?.photoUrl) {
            placeholder(R.drawable.person) // Show while loading
            error(R.drawable.person) // Show if URL is null or fails
            fallback(R.drawable.person) // Show if URL is explicitly null
        }

    }

    private fun setupAppBarConfiguration() {
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.bookmarksFragment,
                R.id.calenderFragment,
                R.id.mealDetailsFragment,
            ), // Add your top-level destinations here
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun handleBackButtonForNonTopLevel() {
        val isGuest = viewModel.getCurrentUser() == null || viewModel.isUserGuest()
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.searchAutoCompleteTextView.apply {
                text.clear()
                clearFocus()
            }
            when (destination.id) {
                R.id.mealDetailsFragment -> {
                    if (isGuest) {
                        handleGuestPermission()
                        navController.popBackStack()
                        /* binding.bottomNavigationView.post {
                             binding.bottomNavigationView.menu.findItem(R.id.homeFragment)
                                 .setChecked(true)
                         }*/
                        return@addOnDestinationChangedListener
                    }
                    supportActionBar?.setDisplayHomeAsUpEnabled(true) //show back button
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.toolbar.toolbar.visibility = View.GONE
                }

                R.id.searchResultFragment -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true) //show back button
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.toolbar.userImageCard.visibility = View.GONE
                    binding.toolbar.toolbar.visibility = View.VISIBLE
                }

                R.id.bookmarksFragment -> {
                    binding.toolbar.toolbar.visibility = View.GONE
                }

                R.id.calenderFragment -> {
                    binding.toolbar.toolbar.visibility = View.GONE
                }

                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.toolbar.userImageCard.visibility = View.VISIBLE
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.toolbar.searchAutoCompleteTextView.visibility = View.VISIBLE
                    binding.toolbar.toolbar.visibility = View.VISIBLE
                }

            }
        }
    }

    private fun hideToolBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false) //show back button
        binding.toolbar.searchAutoCompleteTextView.visibility = View.GONE
        binding.toolbar.toolbar.visibility = View.GONE

    }

    private fun handleGuestPermission() {
        showLoginDialog(this) {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setUpNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setupBottomAndDrawerNav() {
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.navigationView.setupWithNavController(navController)
    }


    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.toolbar.userImageCard.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun showSuccessLogin() {
        val showSnackBar = intent.getBooleanExtra("SHOW_SNACKBAR", false)
        if (showSnackBar) {
            showSuccessSnackBar(binding.root, "Logged in Successfully!")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}