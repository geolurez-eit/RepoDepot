package com.agjk.repodepot.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Repos
import com.agjk.repodepot.model.data.Users
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.adapter.MainFragmentAdapter
import com.agjk.repodepot.view.adapter.UserAdapter
import com.agjk.repodepot.view.fragment.MainUserRepoFragment
import com.agjk.repodepot.view.fragment.SplashScreenFragment
import com.agjk.repodepot.viewmodel.RepoViewModel
import com.agjk.repodepot.viewmodel.RepoViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private var isFreshLaunch = true

    private lateinit var navigationDrawer: DrawerLayout
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var viewPager: ViewPager2
    private var viewPagePosition = 0
    private lateinit var mainUserRepoFragment: Fragment
    private var tokenSaved = ""

    private val userAdapter = UserAdapter(mutableListOf(), this)
    private lateinit var mainFragmentAdapter: MainFragmentAdapter

    private val repoViewModel: RepoViewModel by viewModels(
        factoryProducer = { RepoViewModelFactory }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        DebugLogger("MainActivity onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<MaterialButton>(R.id.log_out_button).setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.sign_out_alert))
                .setMessage(getString(R.string.sign_out_message))
                .setPositiveButton(getString(R.string.sign_out_alert_positive),
                    DialogInterface.OnClickListener() { dialog: DialogInterface, _ ->
                        dialog.dismiss()

                        // sign out user
                        Firebase.auth.signOut()

                        // start this activity fresh to unload data and display splash screen
                        startActivity(Intent(this, MainActivity::class.java).also { intent ->
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        })

                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    })
                .setNegativeButton(getString(R.string.cancel),
                    DialogInterface.OnClickListener() {dialog: DialogInterface, _ ->
                        dialog.dismiss()
                    })
                .show()
        }

        // Show splash on launch
        if (isFreshLaunch) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out,
                    android.R.anim.fade_in, android.R.anim.fade_out
                )
                .add(R.id.splash_fragment_container, SplashScreenFragment())
                .addToBackStack(null)
                .commit()

            isFreshLaunch = false
        }
    }

    fun closeSplash() {
        runOnUiThread {
            supportFragmentManager.popBackStack()
            initMainActivity()
        }
    }

    private fun initMainActivity() {
        navDrawerToolbarSetup()
        viewPagerSetup()

        val repoList: List<Repos> = listOf(
            Repos("John//repo/barber", "Kotlin", 7),
            Repos("kamel//repoDepo",  "Kotlin", 5),
            Repos("Netherland/github",  "Kotlin", 2),
            Repos("hubgit/netherland",  "Kotlin", 6),
            Repos("hubgit/netherland",  "Kotlin", 6),
            Repos("hubgit/netherland",  "Kotlin", 6),
            Repos("hubgit/netherland",  "Kotlin", 6),
            Repos("hubgit/netherland",  "Kotlin", 6),
            Repos("hubgit/netherland",  "Kotlin", 6),
            Repos("hubgit/netherland",  "Kotlin", 6),
            Repos("hubgit/netherland",  "Kotlin", 6),
            Repos("hubgit/netherland",  "Kotlin", 6),
            Repos("hubgit/netherland",  "Kotlin", 6))


        val userList: List<Users> = listOf(
            Users("", "bladerjam7", MainUserRepoFragment(repoList)),
            Users("", "george21", MainUserRepoFragment(repoList)),
            Users("", "AdamLiving", MainUserRepoFragment(repoList)),
            Users("", "JohnCena", MainUserRepoFragment(repoList))
        )

        mainFragmentAdapter.addFragmentToList(userList[0])
        userAdapter.updateUsers(userList)

        // TODO: Store api call for users into userList
        // TODO: Update userAdapter with userList
        // TODO: Add user to MainFragmentAdapter.addFragmentToList(userList.fragment)

        // TODO: check on Observer
        //Testing viewmodel methods
        DebugLogger("MainActivity onCreate - saveNewRepos")
        /*//repoViewModel.getNewRepos("geolurez-eit")
        //repoViewModel.getNewCommits("geolurez-eit","android-kotlin-geo-fences")*/

        val userName: String = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        DebugLogger("Username -----> ${userName}")
        DebugLogger("Token -----> ${tokenSaved}")

        repoViewModel.getStoredPrivateReposForUser("bladerjam7", tokenSaved).observe( this, Observer {
            DebugLogger("Repo size -------> ${it}")
        })

        /*repoViewModel.getStoredCommitsForUser(
            "geolurez-eit",
            "android-kotlin-geo-fences"
        ).observe(this, Observer{ DebugLogger("Testing output for commits: $it") })*/
    }

    private fun viewPagerSetup() {

        viewPager = findViewById(R.id.main_view_pager_2)
        mainFragmentAdapter = MainFragmentAdapter(mutableListOf(), this)

        //mainFragmentAdapter.addFragmentToList(//)

        viewPager.adapter = mainFragmentAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Whenever the user swipes to page position, the text in the recyclerView in the
                // navigation drawer is be colored green on the username selected
                userAdapter.selectedUser(position)
            }
        })
    }

    fun loadViewPagerFragment(i: Int) {
        viewPager.currentItem = i
    }

    private fun navDrawerToolbarSetup() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationDrawer = findViewById(R.id.drawer_layout)
        userRecyclerView = findViewById(R.id.rv_users)

        userRecyclerView.adapter = userAdapter

        // Toggle is used to attach the toolbar and navigation drawer
        val toggle = ActionBarDrawerToggle(
            this,
            navigationDrawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        navigationDrawer.addDrawerListener(toggle)
        toggle.syncState()  // Menu button default animation when drawer is open and closed
    }

    override fun onBackPressed() {
        if (navigationDrawer.isDrawerOpen(GravityCompat.START)) {
            navigationDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun closeNavDrawer() {
        navigationDrawer.closeDrawers()
    }

    fun saveToken(tokenSaved: String) {
        this.tokenSaved = tokenSaved
    }

    /* fun saveUsername(input: String){
         username = input
     }*/
}