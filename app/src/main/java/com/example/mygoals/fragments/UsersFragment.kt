package com.example.mygoals.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoals.LoginActivity
import com.example.mygoals.R
import com.example.mygoals.adapter.UserAdapter
import com.example.mygoals.model.Lists
import com.example.mygoals.model.User
import com.example.mygoals.utils.Constants
import com.example.mygoals.utils.Constants.USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.android.synthetic.main.user_list.*
import kotlinx.android.synthetic.main.user_list_layout.*
import kotlinx.android.synthetic.main.user_list_layout.view.*


class UsersFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<User>? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var userList: ArrayList<User>
    private var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =  inflater.inflate(R.layout.user_list_layout, container, false)
        recyclerView = view.findViewById(R.id.recyclerview_message)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        userList = arrayListOf()
        mUsers = ArrayList()

        var recyclerView: RecyclerView? = null
        view?.recyclerview_message?.showShimmer()
        //searchEditText = view.findViewById(R.id.searchUsersET)
        // Inflate the layout for this fragment
        retrieveAllUsers()

/*        view.iv_search_message.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }*/

        return view
    }


    private fun retrieveAllUsers() {


        val refUsers = FirebaseFirestore.getInstance().collection(USER)

        refUsers.get().addOnSuccessListener {
            view?.recyclerview_message?.hideShimmer()
            if (!it.isEmpty) {
                (mUsers as ArrayList<User>).clear()
                for (data in it.documents) {
                        val users: User? = data.toObject<User>(User::class.java)


                    if ((users?.id) != firebaseUserID)
                    {

                        (mUsers as ArrayList<User>).add(users!!)
                        userAdapter = context?.let { it1 -> UserAdapter(it1, mUsers!!) }
                        recyclerView!!.adapter = userAdapter
                    }

                }


            }
        }

    }

}


