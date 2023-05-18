package com.example.agileandroidalpha.feature_board.presentation.chat

import com.example.agileandroidalpha.firebase.firestore.model.ChatMessage
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDateTime
import kotlin.random.Random

data class ChatState(
    val iDGen: Long = Random.nextLong(10000 * 10000),
    var markForUpdate: Boolean = false,
    val msg: String = "",
    val sig: String = "",
    val editMsg: String = "",
    val extraTxt: String? = null,
    val replyToMsg: ChatMessage? = null,
    val replyTo: FireUser? = null,
    val replying: Boolean = false,
    val edited: Boolean = false,
    val auth: FirebaseUser? = null,
    val user: FireUser? = null,
    val sender: String = "Anonymous",
    val receiver: String = "",
    val recipients: List<String> = emptyList(),
    val availUsers: List<FireUser> = emptyList(),
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val readBy: List<String> = emptyList(),
    val expireDate: Long = -999999,
    val expires: Boolean = false,
    val hideName: Boolean = false,
    val isUnread: Boolean = true,
    val isScheduled: Boolean = false,
    val isRecurring: Boolean = false,
    val isReply: Boolean = false,
    val isPrivate: Boolean = false,
    val isPinned: Boolean = false,
    val isHidden: Boolean = false,
    val isFlagged: Boolean = false,
    val isDeleted: Boolean = false,
    val isAnnouncement: Boolean = false,
    val blocked: List<String> = emptyList()
)
