package com.example.agileandroidalpha.firebase.firestore

import com.example.agileandroidalpha.firebase.firestore.model.FireUser

data class PrivateMessage(
    val id: Long = 0,
    val title: String = "",
    val body: String = "",
    val signature: String = "",
    val extraTxt: String? = null,
    val sender: FireUser,
    val recipient: FireUser?,
    val additionalRecipients: List<FireUser> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val timestampAbs: Long = Long.MIN_VALUE,
    val secret: Boolean = false,
)
