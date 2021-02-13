package com.agjk.repodepot.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.agjk.repodepot.DebugLogger
import com.agjk.repodepot.model.data.GitRepo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object DepotRepository {
    private val repoLiveData: MutableLiveData<List<GitRepo.GitRepoItem>> = MutableLiveData()
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    init {
        firebaseDatabase.setPersistenceEnabled(true)
    }

    fun getRepos(): LiveData<List<GitRepo.GitRepoItem>> {
        firebaseDatabase.reference.child("REPOSITORIES")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    DebugLogger("Error ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val repoList = mutableListOf<GitRepo.GitRepoItem>()
                    snapshot.children.forEach {
                        it.getValue(GitRepo.GitRepoItem::class.java)?.let { repo ->
                            repoList.add(repo)
                        }
                    }
                    repoLiveData.value = repoList
                }
            })
        return repoLiveData
    }

    fun postRepo(repo: GitRepo.GitRepoItem) {
        firebaseDatabase.reference.child("REPOSITORIES").child(repo.id.toString()).setValue(repo)
        DebugLogger("Repo:${repo.name} added!")
    }

}