package com.agjk.repodepot.view

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.R
import com.agjk.repodepot.model.data.Repos
import com.agjk.repodepot.model.data.Users
import com.agjk.repodepot.view.adapter.MainFragmentAdapter
import com.agjk.repodepot.view.adapter.RepoAdapter
import com.agjk.repodepot.view.adapter.UserAdapter
import com.agjk.repodepot.view.fragment.MainUserRepoFragment
import com.agjk.repodepot.viewmodel.RepoViewModel
import com.agjk.repodepot.viewmodel.RepoViewModelFactory
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

class MainActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var provider = OAuthProvider.newBuilder("github.com")

    private lateinit var mainTextView: TextView
    private lateinit var navigationDrawer: DrawerLayout
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var viewPager: ViewPager2
    private var viewPagePosition = 0

    private val mainFragment = MainUserRepoFragment()

    private val userAdapter = UserAdapter(mutableListOf(),this)
    private val repoAdapter = RepoAdapter(mutableListOf())

    private lateinit var mainFragmentAdapter: MainFragmentAdapter

    private val repoViewModel: RepoViewModel by viewModels(
        factoryProducer = { RepoViewModelFactory }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        DebugLogger("MainActivity onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navDrawerToolbarSetup()
        viewPagerSetup()

        //mainUserRepoFragment = findViewById(R.id.)

        // Testing only
        /*var testList = mutableListOf(
            Users(
                "", "Bladerjam7"),
            Users(
                "", "Johnnyboi")
        )*/

        // When getting the data from the Firebase... create repo then store in userlist
        var dummyRepo = mutableListOf(
            Repos("name", "description", "Kotlin", 7),
            Repos("name2", "description2", "Kotlin", 5),
            Repos("name3", "description3", "Kotlin", 2),
            Repos("name4", "description4", "Kotlin", 6)

        )


        var dummyList = mutableListOf(
            Users("", "bladerjam7", MainUserRepoFragment(), dummyRepo),
            Users("", "geolurez", MainUserRepoFragment(), dummyRepo),
            Users("", "aormsbyeit", MainUserRepoFragment(), dummyRepo),
            Users("", "kamelkhbr", MainUserRepoFragment(), dummyRepo)
        )

        mainFragmentAdapter.addFragmentToList(dummyList[0])
        mainFragmentAdapter.addFragmentToList(dummyList[1])
        mainFragmentAdapter.addFragmentToList(dummyList[2])
        mainFragmentAdapter.addFragmentToList(dummyList[3])



        userAdapter.updateUsers(dummyList)

        // Target specific email with login hint.
        provider.addCustomParameter("login", "george.perez@enhanceit.us")
        // Request read access to a user's email addresses.
        // This must be preconfigured in the app's API permissions.
        val scopes: List<String> = listOf("user", "repo:status")
        provider.scopes = scopes

        //Check if login is pending, sign in if not
        //checkPendingResult()

        //Testing viewmodel methods
        DebugLogger("MainActivity onCreate - saveNewRepos")
        //repoViewModel.getNewRepos("geolurez-eit")
        //repoViewModel.getNewCommits("geolurez-eit","android-kotlin-geo-fences")
        repoViewModel.getStoredReposForUser("geolurez-eit")
            .observe(this, Observer{ DebugLogger("Testing output for repos: $it") })

        repoViewModel.getStoredCommitsForUser(
            "geolurez-eit",
            "android-kotlin-geo-fences"
        ).observe(this, Observer { DebugLogger("Testing output for commits: $it") })

    }

    private fun viewPagerSetup() {

        viewPager = findViewById(R.id.main_view_pager_2)
        mainFragmentAdapter = MainFragmentAdapter(mutableListOf(),this)
        viewPager.adapter = mainFragmentAdapter

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Whenever the user swipes to page position, the text in the recyclerView in the
                // navigation drawer is be colored green on the username selected
                userAdapter.selectedUser(position)
            }
        })
    }

    fun loadViewPagerFragment(i: Int){
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
        if(navigationDrawer.isDrawerOpen(GravityCompat.START)){
            navigationDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }


    }

    private fun startSignIn() {
        firebaseAuth
            .startActivityForSignInWithProvider( /* activity= */this, provider.build())
            .addOnSuccessListener {
                // User is signed in.
                // IdP data available in
                // authResult.getAdditionalUserInfo().getProfile().
                // The OAuth access token can also be retrieved:
                // authResult.getCredential().getAccessToken().
                DebugLogger("startSignIn success")
                DebugLogger(it.credential?.provider.toString())
                mainTextView.text = it.credential?.provider.toString()
            }
            .addOnFailureListener {
                // Handle failure.
                DebugLogger(it.localizedMessage)
            }
    }


    private fun checkPendingResult() {
        val pendingResultTask: Task<AuthResult>? = firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener(
                    OnSuccessListener {
                        // User is signed in.
                        // IdP data available in
                        // authResult.getAdditionalUserInfo().getProfile().
                        // The OAuth access token can also be retrieved:
                        // authResult.getCredential().getAccessToken().
                    })
                .addOnFailureListener {
                    // Handle failure.
                }
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
            startSignIn()
        }

    }

    fun updateRepoList(repo: List<Repos>) {
        mainFragment.updateRepoRecyclerview(repo)
    }

    fun closeNavDrawer() {
        navigationDrawer.closeDrawers()
    }

}