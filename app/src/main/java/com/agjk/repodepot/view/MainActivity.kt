package com.agjk.repodepot.view

import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.agjk.repodepot.R
import com.agjk.repodepot.model.DepotRepository
import com.agjk.repodepot.model.data.*
import com.agjk.repodepot.util.DebugLogger
import com.agjk.repodepot.view.adapter.MainFragmentAdapter
import com.agjk.repodepot.view.adapter.UserAdapter
import com.agjk.repodepot.view.fragment.MainUserRepoFragment
import com.agjk.repodepot.view.fragment.SplashScreenFragment
import com.agjk.repodepot.viewmodel.RepoViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    // for splash screen
    private var isFreshLaunch = true
    lateinit var commit: List<Commits>

    private lateinit var navigationDrawer: DrawerLayout
    private lateinit var navMenuButton: ImageButton
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var viewPager: ViewPager2

    private var viewPagePosition = 0
    private lateinit var mainUserRepoFragment: Fragment
    private val splashScreenFragment = SplashScreenFragment()

    var tokenSaved = ""
    private var usersToReturn = mutableListOf<Users>()
    private var allUserRepos = mutableListOf<MutableList<Repos>>()
    private var addedUsers = mutableListOf<String>()


    private var firebaseAuth = FirebaseAuth.getInstance()

    private val userAdapter = UserAdapter(mutableListOf(), this)
    private lateinit var mainFragmentAdapter: MainFragmentAdapter

    private val repoViewModel: RepoViewModel by viewModels()

    // for search bar
    private lateinit var searchManager: SearchManager
    private lateinit var searchView: SearchView
    private lateinit var searchResultsContainer: FragmentContainerView
    private lateinit var loadingSpinner: CircularProgressIndicator
    private lateinit var blankView: View
    private var searchTimer: Timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        DebugLogger("MainActivity onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inSignOut = intent.getBooleanExtra(getString(R.string.SIGNOUT_KEY), false)

        findViewById<MaterialButton>(R.id.log_out_button).setOnClickListener {
            MaterialAlertDialogBuilder(this, R.style.AlertDialog)
                .setTitle(getString(R.string.sign_out))
                .setMessage(getString(R.string.sign_out_message))
                .setPositiveButton(getString(R.string.sign_out_alert_positive),
                    DialogInterface.OnClickListener() { dialog: DialogInterface, _ ->
                        dialog.dismiss()

                        // sign out user
                        Firebase.auth.signOut()

                        // start this activity fresh to unload data and display splash screen
                        startActivity(Intent(this, MainActivity::class.java).also { intent ->
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra(getString(R.string.SIGNOUT_KEY), true)
                        })

                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    })
                .setNegativeButton(getString(R.string.cancel),
                    DialogInterface.OnClickListener() { dialog: DialogInterface, _ ->
                        dialog.dismiss()
                    })
                .show()
        }

        // Show splash on launch
        if (isFreshLaunch) {

//            Bundle bundle = new Bundle();
//            bundle.putString("name", name);
//            bundle.putInt("age", age);
//
//            MyFragment fragment = new MyFragment();
//            fragment.setArguments(bundle);

            val bundle = Bundle().also {
                it.putBoolean(getString(R.string.SIGNOUT_KEY), inSignOut)
            }
            splashScreenFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out,
                    android.R.anim.fade_in, android.R.anim.fade_out
                )
                .add(R.id.splash_fragment_container, splashScreenFragment)
                .addToBackStack(null)
                .commit()

            isFreshLaunch = false
        }

    }

    private fun performUserSearch(stringSearch: String) {
        repoViewModel.searchUsers(stringSearch)
    }

    fun loadMainInBackground() {
//        Log.d("TAG_Q", "load in main background")
        runOnUiThread {
            initFirebase()
            initMainActivity()      // correct order, do not change
            getData(FirebaseAuth.getInstance().currentUser?.displayName.toString())
        }
    }

    fun closeSplash() {
//        Log.d("TAG_Q", "close splash")
        runOnUiThread {
            supportFragmentManager.popBackStack()
        }
    }

    private fun initMainActivity() {
//        Log.d("TAG_D", "init main activity")

        searchViewSetup()
        viewPagerSetup()


        //mainFragmentAdapter.addFragmentToList(userList[0])

        // TODO: Store api call for users into userList
        // TODO: Update userAdapter with userList
        // TODO: Add user to MainFragmentAdapter.addFragmentToList(userList.fragment)

        // TODO: check on Observer
        //Testing viewmodel methods

        val userName: String = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        DebugLogger("Username -----> ${userName}")

    }

    private fun viewPagerSetup() {

        viewPager = findViewById(R.id.main_view_pager_2)
        mainFragmentAdapter = MainFragmentAdapter(mutableListOf(), this)

        //mainFragmentAdapter.addFragmentToList(//)

        viewPager.adapter = mainFragmentAdapter

//        val animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fast_fade_in)
//        viewPager.startAnimation(animFadeIn)
        viewPager.visibility = View.VISIBLE

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

    private fun searchViewSetup() {
        // Search bar
        searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = findViewById(R.id.search_view)
        searchResultsContainer = findViewById(R.id.search_results_fragment_container)
        blankView = findViewById(R.id.blank_view)

        // search view fade in
        val animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fast_fade_in)
        searchView.visibility = View.VISIBLE
        searchView.startAnimation(animFadeIn)

        loadingSpinner = findViewById(R.id.results_loading_spinner)
        loadingSpinner.hide()

        searchView.apply {
            visibility = View.VISIBLE
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    searchResultsContainer.visibility = View.VISIBLE
                    val animFadeScale =
                        AnimationUtils.loadAnimation(context, R.anim.search_page_anim_in)
                    searchResultsContainer.startAnimation(animFadeScale)

                    // TODO: animate this too! :)
                    blankView.visibility = View.GONE
                } else {
                    searchResultsContainer.visibility = View.GONE
                    val animFadeScale =
                        AnimationUtils.loadAnimation(context, R.anim.search_page_anim_out)
                    searchResultsContainer.startAnimation(animFadeScale)

                    blankView.visibility = View.VISIBLE
                    DepotRepository.searchForUsers("")
                }
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { performUserSearch(query) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchTimer.cancel()
                    searchTimer = Timer()
                    loadingSpinner.show()

                    searchTimer.schedule(object : TimerTask() {
                        override fun run() {
                            newText?.let { performUserSearch(newText) }

                            // HACK for timing - maybe okay, maybe not.
                            Thread.sleep(300)

                            runOnUiThread {
                                loadingSpinner.hide()
                            }
                        }
                    }, 1000)

                    return true
                }
            })
        }

        navigationDrawer = findViewById(R.id.drawer_layout)
        userRecyclerView = findViewById(R.id.rv_users)

        userRecyclerView.adapter = userAdapter

        navMenuButton = findViewById(R.id.nav_drawer_menu_button)
        navMenuButton.setOnClickListener {
            if (navigationDrawer.isDrawerOpen(GravityCompat.START)) {
                navigationDrawer.closeDrawer(GravityCompat.START)
            } else {
                navigationDrawer.openDrawer(GravityCompat.START)
            }

            closeSearch()
        }

        // menu button fade in
        navMenuButton.visibility = View.VISIBLE
        navMenuButton.startAnimation(animFadeIn)
    }

    override fun onBackPressed() {
        when {
            searchResultsContainer.visibility == View.VISIBLE -> {
                closeSearch()
            }

            navigationDrawer.isDrawerOpen(GravityCompat.START) ->
                navigationDrawer.closeDrawer(GravityCompat.START)

            else -> super.onBackPressed()
        }
    }

    fun closeSearch() {
        searchView.setQuery("", false)
        searchView.clearFocus()
        searchView.isIconified = true
    }

    fun closeNavDrawer() {
        navigationDrawer.closeDrawers()
    }

    //Data methods for MainActivity

    private fun initFirebase() {
        repoViewModel.getUserPreferences().observe(this, {
            tokenSaved = it.gitHubAccessToken
        })
    }

    /**
     * Gets the repository data for all users in the given user's userlist.
     * @param userName User whose userlist will be populated
     */
    private fun getData(userName: String) {
        // Get the userlist of the given user and observe it
        repoViewModel.getUserList(userName).observe(this, { currentUserList ->
            DebugLogger("userGET SIZE -> ${currentUserList.size}")
            // Check if the userlist is not empty
            if (currentUserList.isNotEmpty()) {
                // For each user in the user list, get their repositories
                currentUserList.forEach { user ->
                    getRepos(user)
                }
            } else
                // If the userlist is empty, add the given user to thee current user's list
                repoViewModel.addUserToList(userName)
        })
    }

    /**
     * Gets the repositories for a given user
     * @param user GitUser whose repositories are being pulled
     */
    private fun getRepos(user: GitUser) {
        // Get the repositories from Firebase for the given user and observe it
        repoViewModel.getStoredReposForUser(user.login.toString(), tokenSaved)
            .observe(this, { gitrepos ->
                // Initialize list of repos; this will be necessary as the observer iterates
                val listToSet = mutableListOf<Repos>()
                var index = 0
                /**
                 *  Check if the current data from the observable is not empty,
                 *  and if the user has not already has been populated with data,
                 *  and if the list of repositories belong to the given user.
                 */
                if(gitrepos.isNotEmpty() && !addedUsers.contains(user.login) && checkReposOwner(gitrepos,user)) {
                    // Add the empty list to the list of repo lists;
                    // there should be a list for every user in the userlist
                    allUserRepos.add(listToSet)
                    // Temporarily store the index of the given user's list of repos
                    index = allUserRepos.indexOf(listToSet)
                    // For every repository in the given user's repository list,
                    // check if the user is the owner.
                    // This is necessary to avoid repos where the given user is a collaborator.
                    for (repo in gitrepos) {
                        if (repo.owner?.login == user.login)
                            // Add the repo to the list
                            allUserRepos[index].add(
                                Repos(
                                    repo.name.toString(),
                                    repo.language.toString(),
                                    repo.stargazers_count.toString(),
                                    repo.html_url.toString(),
                                    repo.description.toString(),
                                    repo.forks_count.toString()
                                )
                            )
                    }
                    // Since the list has been populated, add the given user to the list of
                    // users that have had their repos populated
                    addedUsers.add(user.login.toString())
                    DebugLogger("listToSet SIZE ______> : ${listToSet.size}")
                    // Keep track of the userlist and its size by
                    // check if the given user is already in the userlist being
                    // sent to the fragment displaying it.
                    if (!checkUserListForDupes(usersToReturn, user))
                        // Add the user to the list that will be returned
                        usersToReturn.add(
                            Users(
                                user.avatar_url.toString(),
                                user.login.toString(),
                                MainUserRepoFragment(
                                    allUserRepos[index],
                                    user.avatar_url.toString(),
                                    user.login.toString(),
                                    user.bio.toString()
                                )
                            )
                        )
                    repoViewModel.isMainLoaded.value = true
                }
                // Send list of users to the userAdapter
                userAdapter.updateUsers(usersToReturn)
                // Send list of users to mainFragmentAdapter
                mainFragmentAdapter.addFragmentToList(usersToReturn)
            })
    }

    /**
     *  Keep track of the userlist and its size and
     *  check if the given user is already in the userlist being
     *  sent to the adapter displaying it.
     *  @param list List being checked
     *  @param user User to check if exists in the list
     *  @return True if the user is in the list
     */
    private fun checkUserListForDupes(list: MutableList<Users>, user: GitUser): Boolean {
        list.forEach {
            if (it.username == user.login)
                return true
        }
        return false
    }

    /**
     * Checks if the list of repositories belong to the given user
     * @param list List of repositories to check
     * @param user User to compare repo owners against
     * @return True if given user is owner of any of the repos in the list
     */
    private fun checkReposOwner(list: List<GitRepo.GitRepoItem>, user: GitUser): Boolean {
        list.forEach {
            if (it.owner?.login == user.login)
                return true
        }
        return false
    }

    /*  override fun passDataToDetailsFragment() {
          //val bundle= Bundle()
          //bundle.putString("message”,repoUrl)
          val transaction = this.supportFragmentManager.beginTransaction()
          val detailsFragment= UserDetailsFragment(commit)
          //fragmentB.arguments = bundle
          transaction.replace(R.id.splash_fragment_container, detailsFragment)
          transaction.commit()

      }*/

    fun popDetails() {
        supportFragmentManager.popBackStack()
    }
}