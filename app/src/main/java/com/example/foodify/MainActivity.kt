package com.example.foodify


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.example.foodify.authentication.ui.AuthActivity
import com.example.foodify.databinding.ActivityMainBinding
import com.example.foodify.util.showSuccessSnackBar
import com.musfickjamil.snackify.Snackify
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    //private lateinit var callbackManager: CallbackManager
    private val TAG = "FacebookLogin"

    //private lateinit var buttonFacebookLogin: LoginButton
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModel()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showSuccessLogin()
        setupToolbar()
        setUpNavController()
        handleBackButtonForNonTopLevel()
        setupBottomAndDrawerNav()
        setupAppBarConfiguration()
        binding.userImage.load(viewModel.getCurrentUser()?.photoUrl) {
            placeholder(R.drawable.fav) // Show while loading
            error(R.drawable.fav) // Show if URL is null or fails
            fallback(R.drawable.fav) // Show if URL is explicitly null
        }
        val headerView = binding.navigationView.getHeaderView(0)
        val userImage = headerView.findViewById<ImageView>(R.id.userHeaderImage)
        val userName = headerView.findViewById<TextView>(R.id.profileHeaderName)

        // Initialize Facebook Login button
        val user = viewModel.getCurrentUser()?.displayName ?: "Guest"
        userName.text = user
        userImage.load(viewModel.getCurrentUser()?.photoUrl){
            placeholder(R.drawable.fav) // Show while loading
            error(R.drawable.fav) // Show if URL is null or fails
            fallback(R.drawable.fav) // Show if URL is explicitly null
        }

        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logOut -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    viewModel.logout()
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> {
                    val handled = NavigationUI.onNavDestinationSelected(it, navController)
                    if (handled) binding.drawerLayout.closeDrawer(GravityCompat.START)
                    handled
                }
            }
        }
        Log.d(TAG, "onCreate: $user")
        /*binding.logout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }*/
    }
    private fun setupAppBarConfiguration() {
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.searchFragment,R.id.bookmarksFragment,R.id.profileFragment), // Add your top-level destinations here
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun handleBackButtonForNonTopLevel() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mealDetailsFragment ->{
                    supportActionBar?.setDisplayHomeAsUpEnabled(true) //show back button
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.userImageCard.visibility = View.GONE
                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.userImageCard.visibility = View.VISIBLE
                    binding.bottomNavigationView.visibility = View.VISIBLE

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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.userImageCard.setOnClickListener {
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

    /*
    * buttonFacebookLogin = findViewById(R.id.login_button)
        callbackManager = CallbackManager.Factory.create()
        buttonFacebookLogin.setPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess:${result.accessToken}")
                    handleFacebookAccessToken(result.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "facebook:onError", error)
                }
            },
        )
        *
        * private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.d(TAG, "handleFacebookAccessToken: ${user?.displayName}")
                    Log.d(TAG, "handleFacebookAccessToken: ${user?.photoUrl}")
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    //updateUI(null)
                }
            }
    }
        * */

}