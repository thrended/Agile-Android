package com.example.agileandroidalpha.feature_board.presentation.chat

import com.example.agileandroidalpha.firebase.firestore.model.ChatMessage
import com.example.agileandroidalpha.firebase.firestore.model.FireUser

data class ChatLogState(
    val roomNo: Int = 1,
    val selectedUser: FireUser? = null,
    val deleted: List<ChatMessage> = emptyList(),
    val messages: List<ChatMessage> = emptyList(),
    val myMessages: List<ChatMessage> = emptyList(),
    val messagesToMe: List<ChatMessage> = emptyList(),
    val messageMap: Map<ChatMessage, FireUser> = emptyMap(),
    val unread: List<ChatMessage> = emptyList(),
)
