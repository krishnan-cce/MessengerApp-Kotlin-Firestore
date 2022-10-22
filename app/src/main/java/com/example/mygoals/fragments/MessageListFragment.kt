package com.example.mygoals.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoals.R
import com.example.mygoals.adapter.ChatListAdapter
import com.example.mygoals.adapter.OnlineUserAdapter
import com.example.mygoals.adapter.UserAdapter
import com.example.mygoals.model.*
import com.example.mygoals.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_message_list.*
import kotlinx.android.synthetic.main.fragment_message_list.view.*
import kotlinx.android.synthetic.main.message_list.*
import kotlinx.android.synthetic.main.user_list_layout.view.*


class MessageListFragment : BaseFragment() {

    private var chatlistAdapter: ChatListAdapter? = null
    private var onlineUserAdapter: OnlineUserAdapter? = null
    private var mUsers: ArrayList<User>? = null
    private lateinit var userList: ArrayList<User>
    private var recyclerViewHorizontal: RecyclerView? = null
    private var mGroupId: MutableList<Chats>? = null
    private var recyclerView: RecyclerView? = null
    private var otherUserId: String = ""


    private var mChatLists: MutableList<ChatLists>? = null


    private var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_message_list, container, false)

        view.iv_profile_message.setOnClickListener {
            ProfileFragment()
        }

        val preferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (preferences != null) {
            otherUserId = preferences.getString("otherUserId", "none").toString()
        }

        var recyclerView: RecyclerView? = null
        var recyclerViewHorizontal: RecyclerView? = null

        recyclerView = view.findViewById(R.id.recyclerview_messagelist_messages)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView?.layoutManager = linearLayoutManager


        recyclerViewHorizontal = view.findViewById(R.id.online_users_lisr_rv)
        val horizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewHorizontal?.layoutManager = horizontalLayout
        userList = arrayListOf()
        mUsers = ArrayList()


        mGroupId = ArrayList()
        mChatLists = ArrayList()
        chatlistAdapter = context?.let { ChatListAdapter(it, mChatLists as ArrayList<ChatLists>) }
        onlineUserAdapter = context?.let { OnlineUserAdapter(it, mUsers as ArrayList<User>) }

        recyclerView?.adapter = chatlistAdapter
        recyclerViewHorizontal!!.adapter = onlineUserAdapter
        view?.recyclerview_messagelist_messages?.showShimmer()
        view?.online_users_lisr_rv?.showShimmer()
        retreiveChatlist()
        retrieveAllOnlineUsers()

        countUnreadMessages(view.iv_header_message)



        return view
    }

    private fun retrieveAllOnlineUsers() {

        val refUsers = FirebaseFirestore.getInstance().collection(Constants.USER)

        refUsers.get().addOnSuccessListener {

            if (!it.isEmpty) {
                for (data in it.documents) {
                    view?.online_users_lisr_rv?.hideShimmer()
                    val users: User? = data.toObject<User>(User::class.java)
                    if (users?.status == "online") {
                        mUsers!!.add(users)
                        onlineUserAdapter!!.notifyDataSetChanged()
                    }

                }
            }
        }
    }

    private fun retreiveChatlist() {

        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseFirestore.getInstance().collection("ChatList").document(firebaseUserID)
            .collection("visitors")
            //.orderBy("timestamp")
/*            .addSnapshotListener { value, error ->
                error?.let {
                    //Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                value?.let {
                    if (!value.isEmpty) {

                        for (data in it.documents) {
                            view?.recyclerview_messagelist_messages?.hideShimmer()
                            val lists: ChatLists? = data.toObject<ChatLists>(ChatLists::class.java)
                            mChatLists!!.add(lists!!)
                            chatlistAdapter!!.notifyDataSetChanged()

                        }


                    }
                }
            }
    }*/
        refUsers.get().addOnSuccessListener {
            if (!it.isEmpty) {
                for (data in it.documents) {
                    view?.recyclerview_messagelist_messages?.hideShimmer()
                    val lists: ChatLists? = data.toObject<ChatLists>(ChatLists::class.java)
                    mChatLists!!.add(lists!!)
                    chatlistAdapter!!.notifyDataSetChanged()

                }
            }
        }.addOnFailureListener {

        }
    }



    private fun countUnreadMessages(iv_header_message: TextView){
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        var countUnreadMessages = 0
        val refUnreadMsg =  FirebaseFirestore.getInstance().collection("Chats")
        refUnreadMsg.addSnapshotListener { value, error ->
            error?.let {
                //Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            value?.let {
                if (!value.isEmpty) {

                    for (data in it.documents) {
                        val chat: Chat? = data.toObject<Chat>(Chat::class.java)
                        if (chat?.receiver == firebaseUserID && !chat.isseen){
                            countUnreadMessages += 1
                            iv_header_message.setText("Messages ($countUnreadMessages)")

                        }

                    }


                }
            }

        }
    }


}