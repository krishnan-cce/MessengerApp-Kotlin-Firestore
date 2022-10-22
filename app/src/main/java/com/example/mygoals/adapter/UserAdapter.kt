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


class UserAdapter(
    mContext: Context,
    mUsers: List<User>,

    ) : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {

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
            LayoutInflater.from(mContext).inflate(R.layout.user_list, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user: User = mUsers[i]
        holder.userNameTxt.text = user.username
        //Picasso.get().load(user.image).placeholder(R.drawable.profile).into(holder.profileImageView)
        if (user.image == ""){
            Picasso.get().load(R.drawable.profile).placeholder(R.drawable.profile).into(holder.profileImageView)
        }else{
            Picasso.get().load(user.image).placeholder(R.drawable.profile).into(holder.profileImageView)
        }



        if (user.status == "online") {

            holder.tv_last_seen.visibility = View.VISIBLE
            holder.tv_last_seen.setTextColor(color)
            holder.iv_online.visibility = View.VISIBLE
            holder.iv_online.setImageResource(R.drawable.online)
            holder.tv_last_seen.setText("Active Now")
        } else {
            holder.iv_online.visibility = View.GONE
            holder.tv_last_seen.visibility = View.VISIBLE
            holder.tv_last_seen.setTextColor(colorRed)
            holder.tv_last_seen.setText(DateUtils.getRelativeTimeSpanString(user.lastseen))
        }

        holder.itemView.setOnClickListener(View.OnClickListener {

            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.id)
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UsersFragment()).commit()
        })

        holder.profileImageView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0) {
                    //retreiveChatlists(firebaseUser!!.uid,user.id)
                    //newChat(firebaseUser!!.uid, user.id)

                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.id)
                    mContext.startActivity(intent)
                }
                if (position == 1) {
                    val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

                    editor.putString("profileId", user.id)

                    editor.apply()

                    (mContext as FragmentActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment()).commit()
                }
            })
            builder.show()
        }

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var Et_mob_search: TextView
        var iv_online: ImageView
        var tv_last_seen: TextView

        init {
            userNameTxt = itemView.findViewById(R.id.tv_username_message_list)
            profileImageView = itemView.findViewById(R.id.iv_image_message_list)
            Et_mob_search = itemView.findViewById(R.id.tv_text_message_list)
            iv_online = itemView.findViewById(R.id.iv_online)
            tv_last_seen = itemView.findViewById(R.id.tv_last_seen)


        }
    }

    private fun retreiveChatlists(currentUserId: String,vistId : String){

        val refUser = FirebaseFirestore.getInstance().collection("ChatList")
            .document(currentUserId).collection("visitors").document(vistId)
        val refMap = HashMap<String, Any?>()
        refMap["id"] = refUser.id
        refMap["uid"] = vistId
        refMap["timestamp"] = System.currentTimeMillis()
        //refMap["SenderId"] = firebaseUserID                               // p1 sends chatlist
        refUser.set(refMap)

        val refVisitor = FirebaseFirestore.getInstance().collection("ChatList")
            .document(vistId).collection("visitors").document(currentUserId)
        val refMapV = HashMap<String, Any?>()                            //p2 recives chatlist
        refMapV["id"] = refVisitor.id
        refMapV["uid"] = currentUserId
        refMap["timestamp"] = System.currentTimeMillis()
        //refMapV["SenderId"] = firebaseUserID
        refVisitor.set(refMapV)
    }


    }

