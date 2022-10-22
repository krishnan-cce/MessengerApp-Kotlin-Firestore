package com.example.mygoals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.example.mygoals.fragments.*
import com.example.mygoals.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val onNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        moveToFragment(MessageListFragment())
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.nav_search -> {
                        moveToFragment(UsersFragment())
                        return@OnNavigationItemSelectedListener true
                    }
 /*                   R.id.nav_add_post -> {
                        item.isChecked = false
                        startActivity(Intent(this@MainActivity, AddDairyActivity::class.java))
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.nav_notifications -> {
                       *//* moveToFragment(NotificationsFragment())
                        return@OnNavigationItemSelectedListener true*//*
                    }*/
                    R.id.nav_profile -> {
                        moveToFragment(ProfileFragment())
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        moveToFragment(MessageListFragment())


    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
    fun updateStatus(status: String, lastSeen: Long)
    {
        val ref = FirebaseFirestore.getInstance().collection(Constants.USER).document(FirebaseAuth.getInstance().currentUser?.uid.toString())


        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        hashMap["lastseen"] = lastSeen
        ref.update(hashMap)
    }
    override fun onResume() {
        super.onResume()

        updateStatus("online",System.currentTimeMillis())
    }

    override fun onPause() {
        super.onPause()

        updateStatus("offline",System.currentTimeMillis())
    }
}
