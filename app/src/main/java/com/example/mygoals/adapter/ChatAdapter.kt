package com.example.mygoals.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.example.mygoals.R
import com.example.mygoals.model.Chat
import com.example.mygoals.model.User
import com.example.mygoals.utils.Constants
import com.example.mygoals.utils.GlideLoader


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import jp.wasabeef.glide.transformations.BlurTransformation
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter (
    mContext: Context,
    mChatList: List<Chat>,
    imageUrl: String

) : RecyclerView.Adapter<ChatAdapter.ViewHolder?>() {


    private val mContext: Context
    private val mChatList: List<Chat>
    private val imageUrl: String

    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mChatList = mChatList
        this.mContext = mContext
        this.imageUrl = imageUrl

    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder
    {

        if (position == 1)
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            return ViewHolder(view)
        }
        else
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            return ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profile_image)



        //Picasso.get().load(chat.reactions).into(holder.left_show_reactions)

/*
        getUserImage(holder.iv_right_user!!)
        val reactions = intArrayOf(
            R.drawable.ic_fb_like,
            R.drawable.ic_fb_love,
            R.drawable.ic_fb_laugh,
            R.drawable.ic_fb_wow,
            R.drawable.ic_fb_sad,
            R.drawable.ic_fb_angry
        )
        val config = ReactionsConfigBuilder(mContext)
            .withReactions(reactions)
            .build()
        val popup = ReactionPopup(mContext, config) { position ->

            holder.left_show_reactions!!.visibility = View.VISIBLE
            holder!!.left_show_reactions!!.setImageResource(reactions[position])
            updateFeelingMessageToUser(chat.messageId,reactions[position])
            //holder.left_show_reactions?.setImageResource(ReactionPopup(mContext,chat.reactions))

            true
        }*/
        if (chat.sender == firebaseUser.uid){
           // getUserImage(holder.iv_right_user!!)
            holder.show_text_message!!.setOnClickListener {
                val options = arrayOf<CharSequence>(
                    "View Full Image",
                    "Delete Image",
                    "Cancel"
                )

                var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                builder.setTitle("What do you want?")

                builder.setItems(options, DialogInterface.OnClickListener{
                        dialog, which ->
                    if (which == 0)
                    {

                    }
                    else if (which == 1)
                    {
                        deleteSentMessage(chat.receiver,chat.messageId, holder)
                    }
                })
                builder.show()
            }

        }/*else {
            if (chat.sender != firebaseUser.uid) {
                holder.show_text_message!!.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        popup.onTouch(v!!, event!!)
                        return false
                    }
                })
            }
        }*/
        //images Messages
        if (chat.message == "sent you an image." && chat.image != "")
        {
            //image message - right side
            if (chat.sender == firebaseUser.uid)
            {
                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
/*                Glide.with(mContext).load(chat.image).override(800, 200).fitCenter()
                    .apply(bitmapTransform(BlurTransformation(25, 3)))
                    .into(holder.right_image_view!!)*/

                Picasso.get().load(chat.image).resize(200,200).into(holder.right_image_view)
                holder.sender_date!!.text = chat.timestamp


            }
            //image message - left side
            else if (chat.sender != firebaseUser.uid)
            {
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                //.apply(bitmapTransform(BlurTransformation(25, 3)))
/*                Glide.with(mContext).load(chat.image).override(800, 200).fitCenter()
                    .apply(bitmapTransform(BlurTransformation(25, 3)))
                    .into(holder.left_image_view!!)*/
                Picasso.get().load(chat.image).resize(200,200).into(holder.left_image_view)
                holder.sender_date!!.text = chat.timestamp
            }

        } else
        {

            holder.show_text_message!!.text = chat.message
            holder.sender_date!!.text = chat.timestamp
        }

        if(chat.sender == firebaseUser.uid){
            if (chat.isseen){
                holder.text_seen_right?.visibility = View.VISIBLE
                holder.text_seen_right?.setImageResource(R.drawable.ic_seen)
            }else{
                holder.text_seen_right?.visibility = View.VISIBLE
                holder.text_seen_right?.setImageResource(R.drawable.ic_sent)
            }
        }else{

            if (chat.isseen){
                holder.text_seen?.visibility = View.VISIBLE
                holder.text_seen?.setImageResource(R.drawable.ic_seen)
            }else{
                holder.text_seen?.visibility = View.VISIBLE
                holder.text_seen?.setImageResource(R.drawable.ic_sent)
            }

        }

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profile_image: CircleImageView? = null
        var show_text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: ImageView? = null
        var text_seen_right: ImageView? = null
        var right_image_view: ImageView? = null
        var sender_date: TextView? = null
        //var left_show_reactions: CircleImageView? = null
        //var right_show_reactions: CircleImageView? = null
        var iv_right_user: CircleImageView? = null

        init {
            profile_image = itemView.findViewById(R.id.iv_left_user)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            text_seen_right = itemView.findViewById(R.id.text_seen_right)
            right_image_view = itemView.findViewById(R.id.right_image_view)
            sender_date = itemView.findViewById(R.id.sender_date)

            //left_show_reactions = itemView.findViewById(R.id.left_show_reactions)
            //right_show_reactions = itemView.findViewById(R.id.right_show_reactions)
            iv_right_user = itemView.findViewById(R.id.iv_right_user)

        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if (mChatList[position].sender == firebaseUser.uid)
        {
            1
        }
        else
        {
            0
        }
    }

    private fun deleteSentMessage(userIdVisit: String,messageId: String, holder: ChatAdapter.ViewHolder)
    {
        val reference = FirebaseFirestore.getInstance().collection(Constants.CHATS).document(firebaseUser!!.uid)
            .collection("Groups").document(userIdVisit)
            .collection("Messages").document(messageId)

        println(messageId + "messageId")

        reference.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(holder.itemView.context, "Deleted.", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(holder.itemView.context, "Failed, Not Deleted.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun updateFeelingMessageToUser(messageId: String, reactions: Int) {

        val reference = FirebaseFirestore.getInstance().collection(Constants.CHATS).document(messageId)

        val sdf = SimpleDateFormat("MMMM-dd-yyy")
        val currentDate = sdf.format(Date())

        val HashMap = HashMap<String, Any?>()
        HashMap["reactions"] = reactions

        reference.update(HashMap)

    }

    private fun getUserImage(userImage: CircleImageView){
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseFirestore.getInstance().collection(Constants.USER).document(firebaseUserID)
        refUsers.get().addOnSuccessListener {
            if (it.exists()){
                val user: User? = it.toObject<User>(User::class.java)
                println(user?.image!!)
                Picasso.get().load(user.image).into(userImage)


            }
        }
    }


}
