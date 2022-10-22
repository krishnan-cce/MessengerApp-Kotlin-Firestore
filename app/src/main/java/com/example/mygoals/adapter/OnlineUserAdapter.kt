package com.example.mygoals.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoals.MessageChatActivity
import com.example.mygoals.R
import com.example.mygoals.fragments.MessageListFragment
import com.example.mygoals.fragments.ProfileFragment
import com.example.mygoals.fragments.UsersFragment
import com.example.mygoals.model.Chat
import com.example.mygoals.model.Chats
import com.example.mygoals.model.Groups
import com.example.mygoals.model.User
import com.example.mygoals.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class OnlineUserAdapter(
    mContext: Context,
    mUsers: List<User>,

    ) : RecyclerView.Adapter<OnlineUserAdapter.ViewHolder?>() {

    private val mContext: Context
    private val mUsers: List<User>
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val color = ContextCompat.getColor(mContext, R.color.successGreen)
    val colorRed = ContextCompat.getColor(mContext, R.color.errorRed)

    init {
        this.mUsers = mUsers
        this.mContext = mContext

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.list_online_users, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user: User = mUsers[i]

        //if (user.status == "online"){
            holder.tv_online_user_name.text = user.username


            holder.iv_online_status.visibility = View.VISIBLE
            holder.iv_online_status.setImageResource(R.drawable.online)
       // }
        if (user.image == ""){
            Picasso.get().load(R.drawable.profile).placeholder(R.drawable.profile).into(holder.iv_online_user_img)
        }else{
            Picasso.get().load(user.image).placeholder(R.drawable.profile).into(holder.iv_online_user_img)
        }

        holder.iv_online_user_img.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("profileId", user.id)

            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        }

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_online_user_name: TextView
        var iv_online_user_img: CircleImageView
        var iv_online_status: ImageView

        init {
            tv_online_user_name = itemView.findViewById(R.id.tv_online_user_name)
            iv_online_user_img = itemView.findViewById(R.id.iv_online_user_img)
            iv_online_status = itemView.findViewById(R.id.iv_online_status)
        }
    }


    }

