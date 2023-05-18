package com.example.agileandroidalpha.feature_board.presentation.chat

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.blocked
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.currentUser
import com.example.agileandroidalpha.firebase.firestore.Statics.Deleted.deletedMessages
import com.example.agileandroidalpha.firebase.firestore.model.ChatMessage
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.repository.AuthRepo
import com.example.agileandroidalpha.firebase.repository.FirestoreRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val auth: AuthRepo,
    private val repo: FirestoreRepository
) : ViewModel() {

    private val chatRef = Firebase.firestore.collection("chats")
    private val userRef = Firebase.firestore.collection("users")

    private fun formatDT(dt: LocalDateTime = LocalDateTime.now()): String = run {
        DateTimeFormatter
            .ofPattern("EEEE, MMM dd yyyy hh:mm a")
            .format(dt)
    }?: "00:00"

    private fun getTymDiff(t0: LocalDateTime, tf: LocalDateTime): Long {    // MS
        val days = Period.between(t0.toLocalDate(), tf.toLocalDate())
        val time = tf.toLocalTime().toSecondOfDay() - t0.toLocalTime().toSecondOfDay()
        val seconds = days.days * 24 * 60 * 60 + time
        return seconds * 1000L
    }

    private val _state = MutableStateFlow(ChatState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(
            5000L,
            //500L,
        ),
        _state.value
    )

    private val _log = MutableStateFlow(ChatLogState())
    val log = _log.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(
            5000L,
            //500L,
        ),
        _log.value
    )

    private val _roomNo = MutableStateFlow(1)
    val roomNo = _roomNo.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(
            5000L,
            //500L,
        ),
        _roomNo.value
    )

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var deletedMsg: ChatMessage? = null

    private val me = Firebase.auth.currentUser

    init {
        _roomNo.value = 1
        refresh()
//        viewModelScope.launch {
//            repo.getAllUsers { fireUsers, fireUser ->
//                _state.value = state.value.copy(
//                    availUsers = fireUsers.orEmpty(),
//                    user = fireUser,
//                    auth = me,
//                    sender = fireUser?.name?: fireUser?.email?: me?.displayName?: me?.email?: "Anonymous",
//                    blocked = fireUser?.blockedUsers.orEmpty()
//                )
//                blocked = fireUser?.blockedUsers.orEmpty().toMutableList()
//            }
//            repo.getAllChats { messages, map ->
//                _log.value = log.value.copy(
//                    messages = messages.orEmpty(),
//                    myMessages = messages.orEmpty().filter { m -> m.sender == state.value.sender },
//                    messagesToMe = messages.orEmpty().filter { m -> m.sender != state.value.sender },
//                    messageMap = map.orEmpty(),
//                    unread = messages.orEmpty().filter { m -> !m.readBy.contains(state.value.user?.name) }
//                )
//
//            }
//        }
    }

    fun refresh() {
        when(roomNo.value) {
            1 -> {                                          // PUBLIC CHAT
                viewModelScope.launch {
                    repo.getAllUsers { fireUsers, fireUser ->
                        blocked = fireUser?.blockedUsers.orEmpty().toMutableList()
//                        val filtered = fireUsers.orEmpty().apply {
//                            if(blocked.isNotEmpty()) {
//                                filter { u -> !blocked.contains(u.uid) }
//                            }
//                        }
                        _state.value = state.value.copy(
                            availUsers = fireUsers.orEmpty(),
                            user = fireUser,
                            auth = me,
                            sender = fireUser?.name?: fireUser?.email?: me?.displayName?: me?.email?: "Anonymous",
                            blocked = fireUser?.blockedUsers.orEmpty()
                        )
                    }
                    repo.getAllChats { messages, map ->
                        val filtered = messages.orEmpty().filter { m -> !m.isAnnouncement && !m.isPrivate }.apply {
                            if(blocked.isNotEmpty()) {
                                filter { m -> !blocked.contains(m.senderUid) }
                            }
                            sortedByDescending { m -> m.isFlagged }
                            sortedByDescending { m -> m.isPinned }
                        }
                        val fMap = map.orEmpty().filter { m -> !m.key.isAnnouncement && !m.key.isPrivate }
                        _log.value = log.value.copy(
                            messages = filtered,
                            deleted = filtered.filter { m -> m.isDeleted },
                            myMessages = filtered.filter { m -> m.sender == state.value.sender },
                            messagesToMe = filtered.filter { m -> m.sender != state.value.sender },
                            messageMap = fMap.orEmpty(),
                            unread = filtered.filter { m -> !m.readBy.contains(state.value.user?.name) }
                        )
                    }
                }
            }
            2 -> {                                          // PRIVATE CHAT
                viewModelScope.launch {
                    repo.getAllUsers { fireUsers, fireUser ->
                        blocked = fireUser?.blockedUsers.orEmpty().toMutableList()
                        val filtered = fireUsers.orEmpty().apply {
                            if(blocked.isNotEmpty()) {
                                filter { u -> !blocked.contains(u.uid) }
                            }
                        }
                        _state.value = state.value.copy(
                            availUsers = filtered,
                            user = fireUser,
                            auth = me,
                            sender = fireUser?.name?: fireUser?.email?: me?.displayName?: me?.email?: "Anonymous",
                            blocked = fireUser?.blockedUsers.orEmpty()
                        )
                    }
                    repo.getAllChats { messages, map ->
                        val filtered = messages.orEmpty().filter { m -> m.isPrivate }.apply {
                            if(log.value.selectedUser != null) {
                                filter {
                                    m -> m.senderUid == log.value.selectedUser!!.uid ||
                                        m.receiverUid == log.value.selectedUser!!.uid ||
                                        m.recipientsUid.contains(log.value.selectedUser!!.uid)
                                }
                            }
                        }
                        val fMap = map.orEmpty().filter { m -> m.key.isPrivate }
                        _log.value = log.value.copy(
                            messages = filtered,
                            deleted = filtered.filter { m -> m.isDeleted },
                            myMessages = filtered.filter { m -> m.sender == state.value.sender },
                            messagesToMe = filtered.filter { m -> m.sender != state.value.sender },
                            messageMap = fMap.orEmpty(),
                            unread = filtered.filter { m -> !m.readBy.contains(state.value.user?.name) }
                        )
                    }
                }
            }
            3 -> {                                          // ANNOUNCEMENTS
                viewModelScope.launch {
                    repo.getAllUsers { fireUsers, fireUser ->
                        _state.value = state.value.copy(
                            availUsers = fireUsers.orEmpty(),
                            user = fireUser,
                            auth = me,
                            sender = fireUser?.name?: fireUser?.email?: me?.displayName?: me?.email?: "Anonymous",
                            blocked = fireUser?.blockedUsers.orEmpty()
                        )
                        blocked = fireUser?.blockedUsers.orEmpty().toMutableList()
                    }
                    repo.getAnnouncements { messages, map ->
                        val sorted = messages.orEmpty().sortedByDescending { m -> m.sender == currentUser?.name }
                        val fMap = map.orEmpty().filter { m -> m.key.isAnnouncement }
                        _log.value = log.value.copy(
                            messages = sorted,
                            deleted = sorted.filter { m -> m.isDeleted },
                            myMessages = sorted.filter { m -> m.sender == state.value.sender },
                            messagesToMe = sorted.filter { m -> m.sender != state.value.sender },
                            messageMap = fMap,
                            unread = sorted.filter { m -> !m.readBy.contains(state.value.user?.name) }
                        )
                    }
                }
            }
            else -> {}
        }
        _state.value.markForUpdate = true
    }

    fun refreshUpd() {
        when(roomNo.value) {
            1 -> {                                          // PUBLIC CHAT
                viewModelScope.launch {
                    repo.getAllUsers { fireUsers, fireUser ->
                        blocked = fireUser?.blockedUsers.orEmpty().toMutableList()
//                        val filtered = fireUsers.orEmpty().apply {
//                            if(blocked.isNotEmpty()) {
//                                filter { u -> !blocked.contains(u.uid) }
//                            }
//                        }
                        _state.value = state.value.copy(
                            availUsers = fireUsers.orEmpty(),
                            user = fireUser,
                            auth = me,
                            sender = fireUser?.name?: fireUser?.email?: me?.displayName?: me?.email?: "Anonymous",
                            blocked = fireUser?.blockedUsers.orEmpty()
                        )
                    }
                    repo.getChatUpdates{ messages, deleted, map, ur ->
                        val filtered = messages.orEmpty().filter { m -> !m.isAnnouncement && !m.isPrivate }.apply {
                            if(blocked.isNotEmpty()) {
                                filter { m -> !blocked.contains(m.senderUid) }
                            }
                            sortedByDescending { m -> m.isFlagged }
                            sortedByDescending { m -> m.isPinned }
                        }
                        val fMap = map.orEmpty().filter { m -> !m.key.isAnnouncement && !m.key.isPrivate }
                        _log.value = log.value.copy(
                            messages = filtered,
                            deleted = deleted.orEmpty().filter { m -> !m.isAnnouncement && !m.isPrivate },
                            myMessages = filtered.filter { m -> m.sender == state.value.sender },
                            messagesToMe = filtered.filter { m -> m.sender != state.value.sender },
                            messageMap = fMap.orEmpty(),
                            unread = filtered.filter { m -> !m.readBy.contains(state.value.user?.name) }
                        )
                    }
                }
            }
            2 -> {                                          // PRIVATE CHAT
                viewModelScope.launch {
                    repo.getAllUsers { fireUsers, fireUser ->
                        blocked = fireUser?.blockedUsers.orEmpty().toMutableList()
                        val filtered = fireUsers.orEmpty().apply {
                            if(blocked.isNotEmpty()) {
                                filter { u -> !blocked.contains(u.uid) }
                            }
                        }
                        _state.value = state.value.copy(
                            availUsers = filtered,
                            user = fireUser,
                            auth = me,
                            sender = fireUser?.name?: fireUser?.email?: me?.displayName?: me?.email?: "Anonymous",
                            blocked = fireUser?.blockedUsers.orEmpty()
                        )
                    }
                    repo.getChatUpdates{ messages, deleted, map, ur ->
                        val filtered = messages.orEmpty().filter { m -> m.isPrivate }.apply {
                            if(log.value.selectedUser != null) {
                                filter {
                                        m -> m.senderUid == log.value.selectedUser!!.uid ||
                                        m.receiverUid == log.value.selectedUser!!.uid ||
                                        m.recipientsUid.contains(log.value.selectedUser!!.uid)
                                }
                            }
                        }
                        val fMap = map.orEmpty().filter { m -> m.key.isPrivate }
                        _log.value = log.value.copy(
                            messages = filtered,
                            deleted = deleted.orEmpty().filter { m -> m.isPrivate },
                            myMessages = filtered.filter { m -> m.sender == state.value.sender },
                            messagesToMe = filtered.filter { m -> m.sender != state.value.sender },
                            messageMap = fMap.orEmpty(),
                            unread = filtered.filter { m -> !m.readBy.contains(state.value.user?.name) }
                        )
                    }
                }
            }
            3 -> {                                          // ANNOUNCEMENTS
                viewModelScope.launch {
                    repo.getAllUsers { fireUsers, fireUser ->
                        _state.value = state.value.copy(
                            availUsers = fireUsers.orEmpty(),
                            user = fireUser,
                            auth = me,
                            sender = fireUser?.name?: fireUser?.email?: me?.displayName?: me?.email?: "Anonymous",
                            blocked = fireUser?.blockedUsers.orEmpty()
                        )
                        blocked = fireUser?.blockedUsers.orEmpty().toMutableList()
                    }
                    repo.getAnnouncements { messages, map ->
                        val sorted = messages.orEmpty().sortedByDescending { m -> m.sender == currentUser?.name }
                        val fMap = map.orEmpty().filter { m -> m.key.isAnnouncement }
                        _log.value = log.value.copy(
                            messages = sorted,
                            myMessages = sorted.filter { m -> m.sender == state.value.sender },
                            messagesToMe = sorted.filter { m -> m.sender != state.value.sender },
                            messageMap = fMap.orEmpty(),
                            unread = sorted.filter { m -> !m.readBy.contains(state.value.user?.name) }
                        )
                    }
                }
            }
            else -> {}
        }
        _state.value.markForUpdate = true
    }

    fun onEvent(event: ChatEvent) {
        when(event) {
            is ChatEvent.Ban -> {
                val name = event.user.name?: event.user.email?: "Unknown User"
                if (currentUser?.isAdmin == true && !event.user.isAdmin) {
                    viewModelScope.launch {
                        banUser(event.user)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Blocked user $name."))
                    }
                }
                refresh()
            }
            is ChatEvent.BlockUser -> {
                val name = event.user.name?: event.user.email?: "Unknown User"
                viewModelScope.launch {
                    blockUser(event.user)
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "User $name has been chat banned."))
                }
                refresh()
            }
            is ChatEvent.ChangeChatRoom -> {
//                refreshUpd()
                _log.value = log.value.copy(
                    roomNo = event.no
                )
                _roomNo.value = event.no
                refresh()
            }
            is ChatEvent.ChangeMsgText -> {
                _state.value = state.value.copy(
                    msg = event.str
                )
                refresh()
            }
            is ChatEvent.ChangePrivateMsgUser -> {
                if (event.ur == null || event.ur.name == "All (View only)") {
                    _log.value = log.value.copy(
                        selectedUser = null
                    )
                } else {
                    _log.value = log.value.copy(
                        selectedUser = event.ur
                    )
                }
                refresh()
            }
            is ChatEvent.DeleteAllMarked -> {
                viewModelScope.launch {
                    val marked = log.value.deleted
                    marked.forEach { msg ->
                        deleteMsgPerm(msg)
                        deletedMessages.add(msg)
                    }
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Deleted ALL messages marked for deletion"))
                }
            }
            is ChatEvent.DeleteMsg -> {
                viewModelScope.launch {
                    deleteMsg(event.msg, true)
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Message marked for deletion"))
                }
            }
            is ChatEvent.DeleteMsgPerm -> {
                viewModelScope.launch {
                    deleteMsgPerm(event.msg)
                    deletedMsg = event.msg
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Message permanently deleted"))
                }
            }
            is ChatEvent.DemoteMessage -> {
                viewModelScope.launch {
                    editMsgBool(
                        event.msg,
                        "isAnnouncement",
                        false
                    )
                    editMsgBool(
                        event.msg,
                        "expires",
                        false
                    )
                    editMsgLong(
                        event.msg,
                        "timer",
                        -1
                    )
                }
                refresh()
            }
            is ChatEvent.DemoteUser -> {
                val demTxt = "Demoted User ${event.ur.name?: event.ur.email?: "Unknown User"} "
                val name = event.ur.name?: event.ur.email?: "Unknown User"
                viewModelScope.launch {
                    if (currentUser?.isHeadmaster == false && event.ur.isHeadmaster || event.ur.isAdmin) {
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "You are not authorized to demote this user."))
                        return@launch
                    } else if (currentUser?.isHeadmaster == true && !event.ur.isHeadmaster && event.ur.isAdmin) {
                        editUserBool(event.ur, "isAdmin", false)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = demTxt + "to Moderator"))
                    } else if (event.ur.isModerator) {
                        editUserBool(event.ur, "isModerator", false)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = demTxt + "to Power User"))
                    } else if (event.ur.isPowerUser) {
                        editUserBool(event.ur, "isPowerUser", false)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = demTxt + "to Regular User"))
                    } else if (event.ur.isVerified) {
                        editUserBool(event.ur, "isVerified", false)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = demTxt + "to Unverified User"))
                    } else if (!event.ur.isWarned) {
                        editUserBool(event.ur, "isWarned", true)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Added Warn status to user $name"))
                    } else if (!event.ur.isRestricted) {
                        editUserBool(event.ur, "isRestricted", true)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Restricted user $name"))
                    } else if (!event.ur.isMuted) {
                        editUserBool(event.ur, "isMuted", true)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Muted user $name"))
                    } else if (!event.ur.isSilenced) {
                        editUserBool(event.ur, "isSilenced", true)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Silenced user $name"))
                    } else if (!event.ur.isDisabled) {
                        editUserBool(event.ur, "isDisabled", true)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Disabled user $name"))
                    } else {
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "This user account has already been disabled!"))
                    }
                }
                refresh()
            }
            is ChatEvent.DisableUser -> {
                viewModelScope.launch {
                    editUserBool(event.ur, "isDisabled", event.set)
                }
            }
            is ChatEvent.EditMsg -> {
                viewModelScope.launch {
                    editMsg(event.msg, event.newText)
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Saved Edited Message"))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.EditTxt -> {
                _state.value = state.value.copy(
                    editMsg = event.str
                )
            }
            is ChatEvent.FlagMsg -> {
                viewModelScope.launch {
                    editMsgBool(
                        event.msg,
                        "isFlagged",
                        true
                    )
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Flagged Message"))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.HideMsg -> {
                viewModelScope.launch {
                    editMsgBool(
                        event.msg,
                        "isHidden",
                        true
                    )
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Hiding Message"))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.HooMkAnnouncement -> {
                viewModelScope.launch {
                    chatRef.add(event.msg)
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Announcement Added"))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.PinMsg -> {
                viewModelScope.launch {
                    editMsgBool(
                        event.msg,
                        "isPinned",
                        true
                    )
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Pinned Message"))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.PromoteMessage -> {
                viewModelScope.launch {
                    editMsgBool(
                        event.msg,
                        "isAnnouncement",
                        true
                    )
                    event.timer?.let {
                        editMsgBool(
                            event.msg,
                            "expires",
                            true
                        )
                        editMsgLong(
                            event.msg,
                            "timer",
                            event.timer
                        )
                    }
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Message Promoted to Announcement"))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.PromoteUser -> {
                viewModelScope.launch {
                    val promTxt = "Promoted User ${event.ur.name?: event.ur.email?: "Unknown User"} "
                    val name = event.ur.name?: event.ur.email?: "Unknown User"
                    if (event.ur.isDisabled) {
                        editUserBool(event.ur, "isDisabled", false)
                        _eventFlow.emit(UIEvent.ToastMsg("Removed disabled status from $name"))
                    } else if (event.ur.isSilenced) {
                        editUserBool(event.ur, "isSilenced", false)
                        _eventFlow.emit(UIEvent.ToastMsg("Removed silenced status from $name"))
                    } else if (event.ur.isMuted) {
                        editUserBool(event.ur, "isMuted", false)
                        _eventFlow.emit(UIEvent.ToastMsg("Removed muted status from $name"))
                    } else if (event.ur.isWarned) {
                        editUserBool(event.ur, "isWarned", false)
                        _eventFlow.emit(UIEvent.ToastMsg("Removed warning status from $name"))
                    } else if (!event.ur.isVerified) {
                        editUserBool(event.ur, "isVerified", true)
                        editUserLong(event.ur, "reputation", min(event.ur.reputation+5, 5))
                        _eventFlow.emit(UIEvent.ToastMsg(msg = promTxt + "to Verified User"))
                    } else if (!event.ur.isPowerUser) {
                        editUserBool(event.ur, "isPowerUser", true)
                        editUserLong(event.ur, "reputation", max(event.ur.reputation+5, 10))
                        _eventFlow.emit(UIEvent.ToastMsg(msg = promTxt + "to Power User"))
                    } else if (!event.ur.isModerator) {
                        editUserBool(event.ur, "isModerator", true)
                        editUserLong(event.ur, "reputation", max(event.ur.reputation+10, 30))
                        _eventFlow.emit(UIEvent.ToastMsg(msg = promTxt + "to Moderator"))
                    } else if (currentUser?.isHeadmaster == true && !event.ur.isAdmin) {
                        editUserBool(event.ur, "isAdmin", true)
                        editUserLong(event.ur, "reputation", max(event.ur.reputation+20, 60))
                        _eventFlow.emit(UIEvent.ToastMsg(msg = promTxt + "to Administrator"))
                    } else {
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Cannot promote this user further!"))
                    }
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.Purge -> {
                try {
                    if (event.uid == null) {
                        throw NullPointerException("User cannot have a null value for uid")
                    }
                    viewModelScope.launch {
                        val uRef = userRef.document(event.uid).delete().await()
                        _eventFlow.emit(UIEvent.ToastMsg("Permanently deleted user ${event.uid}"))
                    }.invokeOnCompletion {
                        refresh()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            is ChatEvent.Reply -> {
                viewModelScope.launch {
                    chatRef.add(
                        event.msg
                    )
                    _state.value = state.value.copy(
                        iDGen = Random.nextLong(10000 * 10000),
                        msg = "",
                        replyToMsg = null,
                        replyTo = null,
                        replying = false,
                        isReply = false,
                        isPrivate = false
                    )
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Reply sent."))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.ReportMsg -> {

            }
            is ChatEvent.RestoreMsg -> {
                viewModelScope.launch {
                    deleteMsg(event.msg, false)
                }.invokeOnCompletion {
                    refresh()
                }
//                refresh()
//                _log.value = log.value.copy(
//                    messages = chatLog
//                )
            }
            is ChatEvent.RestrictUser -> {
                viewModelScope.launch {
                    if (event.bool && event.user.isWarned) {
                        editUserBool(
                            event.user,
                            "isRestricted",
                            true
                        )
                        editUserBool(
                            event.user,
                            "isVerified",
                            false
                        )
                    }
                    if (!event.bool) {
                        editUserBool(
                            event.user,
                            "isWarned",
                            false
                        )
                    }
                    val ur = event.user.name?: event.user.email?: "${event.user.uid} (Unknown Name)"
                    val txt = if (event.bool) "has been chat restricted."
                            else "chat restrictions and warnings have been lifted."
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "User $ur $txt"))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.SelfDemote -> {
                viewModelScope.launch {
                    if (event.ur.isHeadmaster) {
                        editUserBool(event.ur, "isHeadmaster", false)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Self-Demoted to Administrator!"))
                    } else if (event.ur.isAdmin) {
                        editUserBool(event.ur, "isAdmin", false)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Self-Demoted to Power User!"))
                    } else {
                        editUserBool(event.ur, "isPowerUser", false)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Self-Demoted to Regular User!"))
                    }
                }.invokeOnCompletion {
                    refresh()
                }
//                refresh()
//                _log.value = log.value.copy(
//                    messages = chatLog
//                )
            }
            is ChatEvent.SelfPromote -> {
                viewModelScope.launch {
                    if (!event.ur.isHeadmaster) {
                        editUserBool(event.ur, "isHeadmaster", true)
                        editUserLong(event.ur, "reputation", max(event.ur.reputation+50, 100))
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Self-Promoted to Headmaster!"))
                    } else if (!event.ur.isVerified){
                        editUserBool(event.ur, "isVerified", true)
                        editUserLong(event.ur, "reputation", min(event.ur.reputation+5, 5))
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Self-Promoted to Verified User"))
                    } else if (!event.ur.isPowerUser) {
                        editUserBool(event.ur, "isPowerUser", true)
                        editUserLong(event.ur, "reputation", max(event.ur.reputation+5, 10))
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Self-Promoted to Power User"))
                    } else if (!event.ur.isModerator) {
                        editUserBool(event.ur, "isModerator", true)
                        editUserLong(event.ur, "reputation", max(event.ur.reputation+10, 30))
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Self-Promoted to Moderator"))
                    } else if (!event.ur.isAdmin) {
                        editUserBool(event.ur, "isAdmin", true)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Self-Promoted to Administrator"))
                    } else {
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "Cannot Promote Further!"))
                    }
                }.invokeOnCompletion {
                    refresh()
                }
//                refresh()
            }
            is ChatEvent.SendMsg -> {
                chatRef.add(
                    ChatMessage(
                        id = state.value.iDGen,
                        msg = event.msg,
                        extraTxt = "",
                        sender = state.value.sender,
                        senderPic = state.value.user?.photo?: "",
                        senderUid = state.value.user?.uid,
                        signature = event.sig,
                        readBy = listOf(state.value.sender),
                        timestamp = formatDT(),
                        timestampAbs = System.currentTimeMillis(),
                        isPrivate = roomNo.value == 2,
                        sent = true
                    )
                )
                _state.value = state.value.copy(
                    iDGen = Random.nextLong(10000 * 10000),
                    msg = ""
                )
                viewModelScope.launch {
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Chat message sent."))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.SendMsgLater -> {
                chatRef.add(
                    ChatMessage(
                        id = state.value.iDGen,
                        msg = event.msg,
                        extraTxt = "",
                        sender = state.value.sender,
                        senderPic = state.value.user?.photo?: "",
                        senderUid = state.value.user?.uid,
                        signature = event.sig,
                        readBy = listOf(state.value.sender),
                        isPrivate = roomNo.value == 2,
                        timestamp = formatDT(event.time),
                        timer = getTymDiff(LocalDateTime.now(), event.time)
                    )
                )
                _state.value = state.value.copy(
                    iDGen = Random.nextLong(10000 * 10000),
                    msg = ""
                )
                viewModelScope.launch {
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Chat message scheduled to be sent at ${formatDT(event.time)}."))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.SendMsgSecret -> {
                chatRef.add(
                    ChatMessage(
                        id = state.value.iDGen,
                        msg = event.msg,
                        extraTxt = "",
                        sender = event.alias,
                        senderPic = "????",
                        senderUid = "????",
                        signature = "~ ????",
                        readBy = listOf(event.alias),
                        isPrivate = roomNo.value == 2,
                        timestamp = formatDT(),
                        timestampAbs = System.currentTimeMillis(),
                        isSecret = true,
                        hideName = true,
                        sent = true
                    )
                )
                _state.value = state.value.copy(
                    iDGen = Random.nextLong(10000 * 10000),
                    msg = ""
                )
                viewModelScope.launch {
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Secret message sent."))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.SendPM -> {
                val msg = event.msg
                chatRef.add(
                    ChatMessage(
                        id = msg.id,
                        title = msg.title,
                        msg = msg.body,
                        extraTxt = msg.extraTxt?: "(Private to ${msg.recipient?.name?: msg.recipient?.email?: "Unknown"})",
                        signature = if (msg.secret) "~ ????" else msg.signature,
                        sender = if (msg.secret) "????" else msg.sender.name?: msg.sender.email?: "Unknown",
                        senderPic = if (msg.secret) "????" else msg.sender.photo?: "",
                        senderUid = if (msg.secret) "????" else msg.sender.uid,
                        receiver = msg.recipient?.name?: msg.recipient?.email?: "Unknown",
                        receiverPic = msg.recipient?.photo?: "",
                        receiverUid = msg.recipient?.uid,
                        readBy = listOf(msg.sender.name?: msg.sender.email?: "Unknown"),
                        timestamp = formatDT(),
                        timestampAbs = System.currentTimeMillis(),
                        isPrivate = true,
                        isSecret = msg.secret,
                        hideName = msg.secret,
                        isUnread = true,
                        sent = true
                    )
                )
                _state.value = state.value.copy(
                    iDGen = Random.nextLong(10000 * 10000),
                    msg = ""
                )
                viewModelScope.launch {
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Private message sent."))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.SendScheduledMsg -> {
                chatRef.add(
                    ChatMessage(
                        id = state.value.iDGen,
                        title = event.msg.title,
                        msg = event.msg.msg,
                        extraTxt = "",
                        sender = state.value.sender,
                        senderPic = state.value.user?.photo?: "",
                        senderUid = state.value.user?.uid,
                        readBy = listOf(state.value.sender),
                        timestamp = formatDT(),
                        timestampAbs = System.currentTimeMillis(),
                        sent = true
                    )
                )
                _state.value = state.value.copy(
                    iDGen = Random.nextLong(10000 * 10000),
                    msg = ""
                )
                viewModelScope.launch {
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Scheduled message sent."))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.StartReply -> {
                _state.value = state.value.copy(
                    isPrivate = event.message.isPrivate,
                    replyToMsg = event.message,
                    replyTo = event.replyTo,
                    replying = true,
                    isReply = true
                )
            }
            is ChatEvent.Unban -> {
                val name = event.user.name?: event.user.email?: "Unknown User"
                if (currentUser?.isAdmin == true && !event.user.isAdmin) {
                    viewModelScope.launch {
                        banUser(event.user, false)
                        _eventFlow.emit(UIEvent.ToastMsg(msg = "User $name has been chat unbanned."))
                    }.invokeOnCompletion {
                        refresh()
                    }
                }
            }
            is ChatEvent.UnblockUser -> {
                val name = event.user.name?: event.user.email?: "Unknown User"
                viewModelScope.launch {
                    blockUser(event.user, false)
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Unblocked user $name."))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.UndoDelete -> {
                viewModelScope.launch {
                    deletedMsg?.let {
                        it.isDeleted = false
                        chatRef.add(it)
                    }
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Message restored."))
                }.invokeOnCompletion {
                    refresh()
                }
//                _log.value = log.value.copy(
//                    messages = chatLog
//                )
            }
            is ChatEvent.UndoDeleteAll -> {
                viewModelScope.launch {
                    deletedMessages.forEach {
                        it.isDeleted = false
                        chatRef.add(it)
                    }
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Deleted messages have been restored."))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            is ChatEvent.UnFlagMsg -> {
                viewModelScope.launch {
                    editMsgBool(
                        event.msg,
                        "isFlagged",
                        false
                    )
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Removed flag."))
                }.invokeOnCompletion {
                    refresh()
                }
//                }.invokeOnCompletion {
//                    refreshUpd()
//                }
//                refresh()
//                _log.value = log.value.copy(
//                    messages = chatLog
//                )
            }
            is ChatEvent.UnHideMsg -> {
                viewModelScope.launch {
                    editMsgBool(
                        event.msg,
                        "isHidden",
                        false
                    )
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Removed hidden status."))
                }.invokeOnCompletion {
                    refresh()
                }
//                refresh()
//                _log.value = log.value.copy(
//                    messages = chatLog
//                )
            }
            is ChatEvent.UnpinMsg -> {
                viewModelScope.launch {
                    editMsgBool(
                        event.msg,
                        "isPinned",
                        false
                    )
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "Removed pin."))
                }.invokeOnCompletion {
                    refresh()
                }
//                refresh()
//                _log.value = log.value.copy(
//                    messages = chatLog
//                )
            }
            is ChatEvent.Warn -> {
                viewModelScope.launch {
                    editUserBool(
                        event.warn,
                        "isWarned",
                        event.bool
                    )
                    val ur = event.warn.name?: event.warn.email?: "${event.warn.uid} (Unknown Name)"
                    val txt = if (event.bool) "has been warned." else "warning status has been removed."
                    refresh()
                    _eventFlow.emit(UIEvent.ToastMsg(msg = "User $ur $txt"))
                }.invokeOnCompletion {
                    refresh()
                }
            }
            else -> {}
        }
    }

    sealed class UIEvent {
        data class ToastMsg(val msg: String, val dur: Int = Toast.LENGTH_SHORT): UIEvent()
    }

    private suspend fun editUserBool(
        u: FireUser,
        field: String,
        value: Boolean
    ) {
        matchUser(u.uid) { id ->
            try {
                val uRef = userRef.document(id)
                uRef.update(field, value)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Firestore Repo", "An error occurred during block user action")
            }
        }
    }

    private suspend fun editUserStr(
        u: FireUser,
        field: String,
        value: String
    ) {
        matchUser(u.uid) { id ->
            try {
                val uRef = userRef.document(id)
                uRef.update(field, value)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Firestore Repo", "An error occurred during block user action")
            }
        }
    }

    private suspend fun editUserLong(
        u: FireUser,
        field: String,
        value: Long
    ) {
        matchUser(u.uid) { id ->
            try {
                val uRef = userRef.document(id)
                uRef.update(field, value)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Firestore Repo", "An error occurred during block user action")
            }
        }
    }

    private suspend fun editMsgBool(
        msg: ChatMessage,
        field: String,
        value: Boolean
    ){
        getMsgRef(msg) {
            it?.let {
                viewModelScope.launch {
                    try {
                        val ref = chatRef.document(it)
                        repo.db.runTransaction { tx ->
                            tx.update(
                                ref,
                                field, value,
                                "timestamp", formatDT(),
                                "timestampAbs", System.currentTimeMillis(),
                            )
                        }.await()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("Firestore Repo", "Error modifying chat message ${msg.msg}.")

                    }
                }
            }
        }
    }

    private suspend fun editMsg(
        msg: ChatMessage,
        newText: String,
    ) {
        getMsgRef(msg) {
            it?.let {
                    viewModelScope.launch {
                    try {
                        val ref = chatRef.document(it)
                        repo.db.runTransaction { tx ->
                            tx.update(
                                ref,
                                "msg", newText,
                                "timestamp", formatDT(),
                                "timestampAbs", System.currentTimeMillis(),
                            )
                        }.await()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("Firestore Repo", "Error editing chat message ${msg.msg}.")

                    }
                }
            }
        }
    }

    private suspend fun editMsgLong(
        msg: ChatMessage,
        field: String,
        newValue: Long,
    ) {
        getMsgRef(msg) {
            it?.let {
                viewModelScope.launch {
                    try {
                        val ref = chatRef.document(it)
                        repo.db.runTransaction { tx ->
                            tx.update(
                                ref,
                                field, newValue,
                                "timestamp", formatDT(),
                                "timestampAbs", System.currentTimeMillis(),
                            )
                        }.await()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("Firestore Repo", "Error editing chat message ${msg.msg}.")

                    }
                }
            }
        }
    }

    private suspend fun updateMsg(
        dc: String?,
        field: String,
        value: String,
        field2: String? = null,
        value2: Long? = null,
    ) {
        if (dc == null) return
        try {
            val ref = chatRef.document(dc)
            repo.db.runTransaction { tx ->
                tx.update(ref, field, value)
                value2?.let { v -> field2?.let { f -> tx.update(ref, f, v)  } }
            }.await()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Chat update action failure.")
            }
        }
    }

    private suspend fun getMsgRef(
        msg: ChatMessage?,
        onDone: (String?) -> Unit
    ) {
        if (msg == null) return
        try {
            val query = chatRef
                .whereEqualTo("id", msg.id)
                .limit(1)
                .get().await()
            if (!query.isEmpty) {
                query.documents.forEach { dc ->
                    val obj = dc.toObject<FireUser>()
                    onDone.invoke(dc.id)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Chat reference lookup failed.")
            }
        }
    }

    private suspend fun deleteMsg(
        msg: ChatMessage?,
        delete: Boolean = true
    ) {
        getMsgRef(msg) {
            it?.let {
                try {
                    chatRef.document(it).update("isDeleted", delete)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("Firestore Repo", "Error deleting chat message ${msg?.msg?:"None"}.")

                }
            }
        }
    }

    private suspend fun deleteMsgPerm(
        msg: ChatMessage?,
    ) {
        getMsgRef(msg) {
            it?.let {
                try {
                    chatRef.document(it).delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("Firestore Repo", "Error deleting chat message ${msg?.msg?:"None"}.")

                }
            }
        }
    }

    private suspend fun banUser(
        user: FireUser,
        set: Boolean = true,
    ) {
        matchUser(user.uid) { id ->
            try {
                val uRef = userRef.document(id)
                uRef.update("isBanned", set)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Firestore Repo", "An error occurred during ban user action")
            }
        }
    }

    private suspend fun blockUser(
        user: FireUser,
        set: Boolean = true
    ) {
        if (user.uid == null) return
        try {
            user.uid?.let {
                if (set)
                    blocked.add(it)
                else blocked.remove(it)
            }
            matchUser(currentUser?.uid) { id ->
                val uRef = userRef.document(id)
                uRef.update("blockedUsers", blocked)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("Firestore Repo", "An error occurred during block user action")
        }
    }

    private fun updateUser(uid: String) {

    }

    suspend fun matchUser(
        uid: String?,
        onDone: (String) -> Unit
    ): FireUser? {
        if (uid == null) return null
        var x = FireUser()
        try {
            val query = userRef
                .whereEqualTo("uid", uid)
                .limit(1)
                .get()
                .await()
            if(!query.isEmpty) {
                query.documents.forEach { dc ->
                    val obj = dc.toObject<FireUser>()
                    obj?.let {
                        x = obj
                    }
                    onDone.invoke(dc.id)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to match user during chat action.")
            }
        }
        return x
    }
}