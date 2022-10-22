package com.example.mygoals.model

import com.google.firebase.firestore.PropertyName
import java.util.*

data class Chat(
    val image: String = "",
    val sender: String = "",
    val message: String = "",
    val receiver: String = "",
    val isseen: Boolean =  false,
    val url: String = "",
    var messageId: String = "",
    val timestamp: String = "",
    val reactions: Int = 1,
    @get:PropertyName("milliseconds") @set:PropertyName("milliseconds") var milliseconds: Long = 0,


    )
data class Chats(
    val chatParticipants: ArrayList<String>? = null
)
data class Groups(
    val sender: String="",
    val receiver: String=""
)




