package com.example.foodify


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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        networkUtils = NetworkUtils(this)
        showSuccessLogin()
        setupToolbar()
        setUpNavController()
        handleBackButtonForNonTopLevel()
        setupBottomAndDrawerNav()
        setupAppBarConfiguration()
        loadUserImage()
        handleLogout()
        handleSearch()
        checkInitialNetworkState()
        observeNetworkState()
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

    private fun handleNetworkState(connected: Boolean) {

                val allowedFragments = setOf(R.id.bookmarksFragment, R.id.calenderFragment)
                val currentFragmentId = navController.currentDestination?.id

                if (!connected) {
                    binding.toolbar.toolbar.visibility = View.GONE

                    if (currentFragmentId !in allowedFragments) {
                        binding.navHostFragment.visibility = View.GONE
                        showNoInternetDialog()
                    }

                    // Restrict navigation to only the allowed fragments
                    binding.bottomNavigationView.setOnItemSelectedListener { item ->
                        if (item.itemId in allowedFragments) {
                            binding.navHostFragment.visibility = View.VISIBLE
                            binding.toolbar.toolbar.visibility = View.GONE
                            navController.navigate(item.itemId)
                            true
                        } else {
                            showNoInternetDialog()
                            false
                        }
                    }

                } else {
                    binding.navHostFragment.visibility = View.VISIBLE
                    binding.toolbar.toolbar.visibility = View.VISIBLE
                    binding.bottomNavigationView.visibility = View.VISIBLE

                    // Restore normal navigation behavior
                    binding.bottomNavigationView.setOnItemSelectedListener { item ->
                        navController.navigate(item.itemId)
                        true
                    }
                }
    }


    private fun showNoInternetDialog() {
        val alertDialog = SweetAlertDialog(this@MainActivity, SweetAlertDialog.ERROR_TYPE)
        alertDialog.setTitleText("No Internet Connection")
        alertDialog.setContentText("Please try again.")
        alertDialog.setConfirmText("OK")
        alertDialog.setCancelText("Cancel")
        alertDialog.setConfirmClickListener { dialog ->
            if (networkUtils.hasNetworkConnection()) {
                binding.navHostFragment.visibility = View.VISIBLE
                binding.toolbar.toolbar.visibility = View.VISIBLE
                dialog.dismiss()
            } else {
                observeNetworkState()
            }
        }
        alertDialog.setCancelClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        alertDialog.findViewById<Button>(cn.pedant.SweetAlert.R.id.confirm_button)?.apply {
            backgroundTintList = null // Remove default Material background tint
            setPadding(0, 5, 0, 5)
            setTypeface(typeface, Typeface.BOLD) // Make text bold
            setBackgroundResource(R.drawable.custom_button_background) // Apply custo
        }
        alertDialog.findViewById<Button>(cn.pedant.SweetAlert.R.id.cancel_button)?.apply {
            backgroundTintList = null // Remove default Material background tint
            setPadding(0, 5, 0, 5)
            setTypeface(typeface, Typeface.BOLD) // Make text bold
            setBackgroundResource(R.drawable.custom_button_error) // Apply custo
        }
    }


    private fun handleLogout() {

        binding.navigationView.setNavigationItemSelectedListener { item ->
            Log.d("teste", "handleLogout: ${viewModel.getCurrentUser() == null}  + ${viewModel.isUserGuest()}")
            val isGuest = viewModel.getCurrentUser() == null || viewModel.isUserGuest()
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
    private fun loadUserImage() {
        binding.toolbar.userImage.load(viewModel.getCurrentUser()?.photoUrl) {
            placeholder(R.drawable.profile) // Show while loading
            error(R.drawable.profile) // Show if URL is null or fails
            fallback(R.drawable.profile) // Show if URL is explicitly null
        }
        val headerView = binding.navigationView.getHeaderView(0)
        val userImage = headerView.findViewById<ImageView>(R.id.userHeaderImage)
        val userName = headerView.findViewById<TextView>(R.id.profileHeaderName)

        // Initialize Facebook Login button
        if(viewModel.getCurrentUser()==null || viewModel.isUserGuest()){
            userName.text = "Guest"
        }
        userImage.load(viewModel.getCurrentUser()?.photoUrl) {
            placeholder(R.drawable.profile) // Show while loading
            error(R.drawable.profile) // Show if URL is null or fails
            fallback(R.drawable.profile) // Show if URL is explicitly null
        }
    }

    private fun setupAppBarConfiguration() {
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.bookmarksFragment,
                R.id.calenderFragment
            ), // Add your top-level destinations here
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun handleBackButtonForNonTopLevel() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.searchAutoCompleteTextView.apply {
                text.clear()
                clearFocus()
            }
            val isGuest = viewModel.getCurrentUser() == null || viewModel.isUserGuest()
            if (isGuest && (destination.id == R.id.bookmarksFragment || destination.id == R.id.calenderFragment)) {
                navController.popBackStack() // Prevent navigation
                showLoginDialog(this) {
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                binding.bottomNavigationView.post {
                    binding.bottomNavigationView.selectedItemId = R.id.homeFragment
                }
                return@addOnDestinationChangedListener

            }

            when (destination.id) {
                R.id.mealDetailsFragment -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false) //show back button
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.toolbar.toolbar.visibility = View.GONE
                }

                R.id.searchResultFragment -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true) //show back button
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.toolbar.userImageCard.visibility = View.GONE
                    binding.toolbar.toolbar.visibility = View.VISIBLE
                }

                R.id.calenderFragment -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false) //show back button
                    binding.toolbar.searchAutoCompleteTextView.visibility = View.GONE
                    binding.toolbar.toolbar.visibility = View.GONE
                }
                R.id.bookmarksFragment->{
                    supportActionBar?.setDisplayHomeAsUpEnabled(false) //show back button
                    binding.toolbar.searchAutoCompleteTextView.visibility = View.GONE
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