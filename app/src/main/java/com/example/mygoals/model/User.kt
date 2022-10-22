package com.example.mygoals.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList


@Parcelize

data class User(
        val id: String = "",
        val username: String = "",
        val email: String = "",
        val image: String = "",
        val mobile: String = "+91123654789",
        val bio: String = "Hi am using Unitto!",
        val category:String ="Kotlin",
        val status: String = "",
        @get:PropertyName("lastseen") @set:PropertyName("lastseen") var lastseen: Long = 0,
        val cover:String = ""
     // val userchats: ArrayList<String>? = null
    ) : Parcelable
data class Lists(
        val id: String = ""
)
data class ChatLists(
        val id: String = "",
        val uid: String = "",
        @get:PropertyName("lastseen") @set:PropertyName("lastseen") var lastseen: Long = 0
)
data class Cover(
        var id: String = "",
        var cover: String = "",
        @get:PropertyName("lastseen") @set:PropertyName("lastseen") var lastseen: Long = 0
        )


data class Post(
        val postid: String = "",
        val postimage: String = "",
        val publisher: String = "",
        val description: String = "",

)

data class Comment(
        val publisher: String = "",
        val timestamp: Date? = null,
        val comment: String = "",

        )
data class Notification(
        val userid: String="",
        val text: String="",
        val postid: String="",
        val ispost: Boolean = false
)