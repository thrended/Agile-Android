package com.example.agileandroidalpha.firebase.firestore.model

import kotlin.math.min

data class ChatMessage(
    val id: Long = 0,
    val title: String = "",
    val msg: String = "",
    val signature: String = "",
    val extraTxt: String = "",
    val sender: String = "",
    val senderPic: String = "",
    val senderUid: String? = null,
    val receiver: String? = null,
    val receiverPic: String? = null,
    val receiverUid: String? = null,
    val recipients: List<String> = emptyList(),
    val recipientsPic: List<String> = emptyList(),
    val recipientsUid: List<String> = emptyList(),
    val replyTxt: String = "",
    val timestamp: String = "",
    val timestampAbs: Long = Long.MIN_VALUE,
    val readBy: List<String> = emptyList(),
    val expireDate: Long = -999999,
    val expires: Boolean = false,
    val hideName: Boolean = false,
    val timer: Long = -1,
    val sent: Boolean = false,
    @field:JvmField val isAnnouncement: Boolean = false,
    @field:JvmField var isDeleted: Boolean = false,
    @field:JvmField val isEdited: Boolean = false,
    @field:JvmField val isFlagged: Boolean = false,
    @field:JvmField val isHidden: Boolean = false,
    @field:JvmField val isPermanent: Boolean = false,
    @field:JvmField val isPinned: Boolean = false,
    @field:JvmField val isPrivate: Boolean = false,
    @field:JvmField val isRecurring: Boolean = false,
    @field:JvmField val isReply: Boolean = false,
    @field:JvmField val isScheduled: Boolean = false,
    @field:JvmField val isSecret: Boolean = false,
    @field:JvmField val isUnread: Boolean = true,
) {
    fun matchesSearchQuery(que: String): Boolean {
        if (que.isBlank()) return false
        val matching = listOfNotNull(
            "$id", title, msg, signature, sender, sender.split(" ").first(), senderUid,
            receiver, receiver?.split(" ")?.first(), receiverUid, extraTxt, replyTxt,
            //recipients.any(), recipientsUid.any(), readBy.any(),
            title.split(" ").first(),
            msg.split(" ").first(),
            sender.substring(0, min(5, sender.length)),
            receiver?.let { it.substring(0, min(5, it.length)) },
            "ann=$isAnnouncement", "del=$isDeleted", "flag=$isFlagged", "hide=$isHidden", "perm=$isPermanent",
            "pin=$isPinned", "pri=$isPrivate", "reply=$isReply", "sch=$isScheduled","????=$isSecret",
            "rec=$isRecurring", "unr=$isUnread", "sent=$sent", "exp=$expires", "anon=$hideName"
        )
        return matching.any {
            it.contains(que, ignoreCase = true)
        }
    }
}