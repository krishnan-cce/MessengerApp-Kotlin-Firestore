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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoals.MessageChatActivity
import com.example.mygoals.R
import com.example.mygoals.fragments.ProfileFragment
import com.example.mygoals.model.*
import com.example.mygoals.utils.Constants
import com.example.mygoals.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView


class ChatListAdapter(
    mContext: Context,
    mGroupList: ArrayList<ChatLists>,

    ) : RecyclerView.Adapter<ChatListAdapter.ViewHolder?>() {

    private val mContext: Context
    private val mGroupList: List<ChatLists>
    var lastMsg: String = ""
    init {
        this.mGroupList = mGroupList
        this.mContext = mContext

    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_list, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mGroupList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: ChatLists = mGroupList[position]
        retriveLastMessage(user.uid,holder.tv_text_message_list,holder.tv_date_message_list,holder.tv_notify_lastMsg)
        //holder.tv_date_message_list.text = user.uid
        //recieverInfo(holder.tv_username_message_list,holder.iv_image_message_list,holder.iv_online_messagelist,user.groupid)
        publisherInfo(holder.tv_username_message_list,user.uid,holder.iv_image_message_list,holder.iv_online_messagelist)

        holder.iv_image_message_list.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0)
                {

                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.uid)
                    mContext.startActivity(intent)
                }
                if (position == 1)
                {
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
        var tv_username_message_list: TextView
        var iv_image_message_list: CircleImageView
        var tv_text_message_list: TextView
        var iv_online_messagelist: ImageView
        var tv_notify_lastMsg: TextView
        var tv_date_message_list: TextView
        init {
            tv_username_message_list = itemView.findViewById(R.id.tv_username_mess_list)
            iv_image_message_list = itemView.findViewById(R.id.iv_image_mess_list)
            tv_text_message_list = itemView.findViewById(R.id.tv_text_mess_list)
            iv_online_messagelist =  itemView.findViewById(R.id.iv_online_messlist)
            tv_notify_lastMsg =  itemView.findViewById(R.id.tv_notify_lastMsg)
            tv_date_message_list = itemView.findViewById(R.id.tv_date_mess_list)


        }
    }
    private fun publisherInfo(receiverName: TextView, publisher: String,receiverImage: CircleImageView
    ,receiverStatus: ImageView)
    {

        val usersRef = FirebaseFirestore.getInstance().collection(Constants.USER).document(publisher)

        usersRef.get().addOnSuccessListener{documentSnapshot ->

            val user: User? = documentSnapshot.toObject<User>(User::class.java)
            receiverName.text = user!!.username
            GlideLoader(mContext).loadUserPicture(user?.image!!, receiverImage)
            if (user.status == "online"){
                receiverStatus.visibility = View.VISIBLE
                receiverStatus.setImageResource(R.drawable.online)
            }else{
                receiverStatus.visibility = View.VISIBLE
                receiverStatus.setImageResource(R.drawable.offline)
            }
        }.addOnFailureListener {
            /* Toast.makeText(mContext, it.toString(), Toast.LENGTH_LONG).show()*/

        }
    }

    private fun retriveLastMessage(chatUserId: String?, lastMessageTxt: TextView,lastMessageDate: TextView,
    notifyMessageCount:TextView){
        lastMsg = "defaultMsg"
        var countUnreadMessages = 0

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refrence = FirebaseFirestore.getInstance().collection(Constants.CHATS)
            .orderBy("milliseconds")

        refrence.addSnapshotListener { value, error ->
            error?.let {
                //Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            value?.let {
                if (!value.isEmpty) {

                    for (data in it.documents) {
                        val chat: Chat? = data.toObject<Chat>(Chat::class.java)
                        if (firebaseUser!=null && chat!=null)
                        {
                            if (chat.receiver == firebaseUser!!.uid  &&
                                chat.sender == chatUserId  ||
                                chat.receiver == chatUserId  &&
                                chat.sender == firebaseUser!!.uid)
                            {
                                if (chat.receiver == firebaseUser!!.uid ){
                                    lastMsg =  chat.message!!
                                    if (!chat.isseen){
                                        countUnreadMessages += 1
                                        notifyMessageCount.visibility = View.VISIBLE
                                        notifyMessageCount.text = "Unread ($countUnreadMessages)"
                                    }
                                }else {
                                    lastMsg = "You: " +chat.message!!
                                }

                                lastMessageDate.setText(DateUtils.getRelativeTimeSpanString(chat.milliseconds))
                            }
                        }
                    }
                    when(lastMsg)
                    {
                        "defaultMsg" -> lastMessageTxt.text = "No Message"
                        "sent you an image." -> lastMessageTxt.text = "image sent."

                        else -> lastMessageTxt.text = lastMsg
                    }
                    lastMsg = "defaultMsg"

                }
            }

        }
    }

}
