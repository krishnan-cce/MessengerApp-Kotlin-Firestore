package com.example.mygoals.model

import java.util.*

data class Dairy (
    val userId: String = "",
    val dairyId: String = "",
    val header: String = "",
    val date: Date? = null,
    val text: String = "",
    val image: String = "",
    val timestamp: String = ""
        )