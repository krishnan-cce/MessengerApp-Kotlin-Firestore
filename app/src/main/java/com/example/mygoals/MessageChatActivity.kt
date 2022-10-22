package com.example.mygoals

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.mygoals.adapter.ChatAdapter
import com.example.mygoals.fragments.MessageListFragment
import com.example.mygoals.model.Chat
import com.example.mygoals.model.User
import com.example.mygoals.utils.Constants
import com.example.mygoals.utils.Constants.CHATS

import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_add_dairy.*
import kotlinx.android.synthetic.main.activity_message_chat.*
import okhttp3.internal.cache.DiskLruCache
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap



class MessageChatActivity : BaseActivity() {
    var userIdVisit: String = ""   //reciver id


    var chatsAdapter: ChatAdapter? = null
    var mChatList: List<Chat>? = null
    lateinit var recycler_view_chats: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        recycler_view_chats = findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycler_view_chats.layoutManager = linearLayoutManager


        refreshChats()

        back_btn_msg.setOnClickListener {
            onBackPressed()
        }

        attact_image_file_btn.setOnClickListener {

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }



        send_message_btn.setOnClickListener {
            val message = text_message.text.toString()
            if (message == "") {
                Toast.makeText(
                    this@MessageChatActivity,
                    "Please write a message, first...", Toast.LENGTH_LONG
                ).show()
            } else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            text_message.setText("")
        }
    }


    fun refreshChats() {

        val dataRef =  FirebaseFirestore.getInstance().collection(Constants.USER).document(userIdVisit)
        dataRef.addSnapshotListener(object : EventListener<DocumentSnapshot?> {
            @SuppressLint("ResourceAsColor")
            override fun onEvent(
                @Nullable snapshot: DocumentSnapshot?,
                @Nullable e: FirebaseFirestoreException?
            ) {
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return
                }
                if (snapshot != null && snapshot.exists()) {
                    val user: User? = snapshot.toObject<User>(User::class.java)
                    if (user!!.status == "online"){
                        lastseen_mchat.visibility = View.VISIBLE
                        lastseen_mchat.setTextColor(R.color.successGreen)
                        lastseen_mchat.text =  "Online"
                    }else{
                        lastseen_mchat.visibility = View.VISIBLE

                        lastseen_mchat.text = "Active " + DateUtils.getRelativeTimeSpanString(user.lastseen)

                    }
                    username_mchat.text = user!!.username
                    Picasso.get().load(user.image).into(profile_image_mchat)
                   // GlideLoader(this).loadUserPicture(user.image!!, profile_image_mchat)
                    retrieveMessages(firebaseUser!!.uid, userIdVisit, user.image)
                } else {


                }
            }
        })

    }


    private fun retrieveMessages(senderId: String, receiverId: String, receiverImageUrl: String) {
        mChatList = ArrayList()

        val docRef = FirebaseFirestore.getInstance().collection(Constants.CHATS)
            .orderBy("milliseconds")
        docRef.addSnapshotListener { value, error ->
            error?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            value?.let {
                if (!value.isEmpty) {
                    (mChatList as ArrayList<Chat>).clear()
                    for (data in it.documents) {
                        val chat: Chat? = data.toObject<Chat>(Chat::class.java)
                        if (chat!!.receiver == senderId && chat.sender == receiverId
                            || chat.receiver == receiverId && chat.sender == senderId
                        ) {
                            chatsAdapter = ChatAdapter(this, (mChatList as ArrayList<Chat>), receiverImageUrl)
                            (mChatList as ArrayList<Chat>).add(chat)
                        }

                        recycler_view_chats.adapter = chatsAdapter
                    }

                }
            }

        }
    }


    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {

        retreiveChatlists(senderId,receiverId!!)
        val reference = FirebaseFirestore.getInstance().collection(CHATS).document()
                val sdf = SimpleDateFormat("h:mm a")
                val currentDate = sdf.format(Date())
                val HashMap = HashMap<String, Any?>()
                HashMap["image"] = ""
                HashMap["sender"] = senderId
                HashMap["message"] = message
                HashMap["receiver"] = receiverId
                HashMap["isseen"] = false
                HashMap["url"] = ""
                HashMap["messageId"] = reference.id
        HashMap["timestamp"] = currentDate
        HashMap["reactions"] = 1
        HashMap["milliseconds"] = System.currentTimeMillis()

        reference.set(HashMap)


    }

    private fun sendMessageToUserTest(senderId: String, receiverId: String?, message: String,image: String?) {
        retreiveChatlists(senderId,receiverId!!)
        val reference = FirebaseFirestore.getInstance().collection(CHATS).document()
                val sdf = SimpleDateFormat("h:mm a")
                val currentDate = sdf.format(Date())
                val HashMap = HashMap<String, Any?>()
                HashMap["image"] = image
                HashMap["sender"] = senderId
                HashMap["message"] = message
                HashMap["receiver"] = receiverId
                HashMap["isseen"] = false
                HashMap["url"] = ""
                HashMap["messageId"] = reference.id
                HashMap["timestamp"] = currentDate
                HashMap["reactions"] = 1
                HashMap["milliseconds"] = System.currentTimeMillis()

        reference.set(HashMap)


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.data != null) {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("image is uploading, please wait....")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseFirestore.getInstance().collection(CHATS).document()

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(
                    this,
                    fileUri
                )
            )
            val filePath = storageReference.child("$sRef.jpg")



            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressBar.dismiss()

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    sendMessageToUserTest(firebaseUser!!.uid,userIdVisit,"sent you an image.",url)

                }
            }

        }
    }
    private fun retriveLastMessage(otherUserId: String?){

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refrence = FirebaseFirestore.getInstance().collection(Constants.CHATS)
          //  .orderBy("milliseconds")

        refrence.addSnapshotListener { value, error ->
            error?.let {
                //Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            value?.let {
                if (!value.isEmpty) {

                    for (data in it.documents) {
                        val chat: Chat? = data.toObject<Chat>(Chat::class.java)
                     /*   if (firebaseUser!=null && chat!=null)
                        {*/
                            if (
                                chat?.receiver == firebaseUser?.uid  && chat?.sender == otherUserId)

                            {

                                val reference = FirebaseFirestore.getInstance().collection("Chats")
                                val updateDoc = reference.document(chat!!.messageId)
                                val hashMap = HashMap<String, Any>()
                                hashMap["isseen"] = true
                                updateDoc.update(hashMap)
                            }

                        //}
                    }


                }
            }

        }
    }
    private fun seen(userId: String){
        val reference = FirebaseFirestore.getInstance().collection("Chats")
        val updateDoc = reference.document()
        val hashMap = HashMap<String, Any>()
        hashMap["isseen"] = true
        updateDoc.update(hashMap)
    }


    private fun seenMessage(userId: String) {
        val reference = FirebaseFirestore.getInstance().collection("Chats")
        val updateDoc = reference.whereEqualTo("receiver",firebaseUser!!.uid)
            .whereEqualTo("sender",userId)

        val hashMap = HashMap<String, Any>()
        hashMap["isseen"] = true


        updateDoc.addSnapshotListener { value, error ->
            error?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            value?.let {
                if (!value.isEmpty) {

                    for (data in it.documents) {
                        reference.document().update(hashMap)

                    }

                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        retriveLastMessage(userIdVisit)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        MessageListFragment()
    }



}




