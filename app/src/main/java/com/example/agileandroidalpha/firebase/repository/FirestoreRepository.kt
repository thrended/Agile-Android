package com.example.agileandroidalpha.firebase.repository

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.feature_board.domain.model.IssueType
import com.example.agileandroidalpha.feature_board.domain.model.SprintInfo
import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.User
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.AddEditTaskViewModel
import com.example.agileandroidalpha.feature_board.presentation.edit_subtask.checkDone
import com.example.agileandroidalpha.feature_board.presentation.tasks.TasksState
import com.example.agileandroidalpha.firebase.firestore.Statics
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.addedMessages
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.addedSprints
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.addedStories
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.addedSubtasks
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.addedUsers
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.chatLog
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.chatMap
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.currentUser
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.messageMap
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.modifiedMessages
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.modifiedSprints
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.modifiedStories
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.modifiedSubtasks
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.modifiedUsers
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.removedMessages
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.removedSprints
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.removedStories
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.removedSubtasks
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.removedUsers
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.unreadCount
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.unreadMessages
import com.example.agileandroidalpha.firebase.firestore.Statics.Deleted.deletedMessages
import com.example.agileandroidalpha.firebase.firestore.Statics.Deleted.deletedSprints
import com.example.agileandroidalpha.firebase.firestore.Statics.Deleted.deletedStories
import com.example.agileandroidalpha.firebase.firestore.Statics.Deleted.deletedSubtasks
import com.example.agileandroidalpha.firebase.firestore.Statics.Deleted.deletedUsers
import com.example.agileandroidalpha.firebase.firestore.Statics.Updated.fullTasks
import com.example.agileandroidalpha.firebase.firestore.Statics.Updated.gudMap
import com.example.agileandroidalpha.firebase.firestore.Statics.Updated.sprintUserMap
import com.example.agileandroidalpha.firebase.firestore.Statics.Updated.yukMap
import com.example.agileandroidalpha.firebase.firestore.Statics.Users.IDNameMap
import com.example.agileandroidalpha.firebase.firestore.Statics.Users.uidNameMap
import com.example.agileandroidalpha.firebase.firestore.model.ChatMessage
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

const val TAG = "Firestore Repository"

class FirestoreRepository(

) {
    // Access a Cloud Firestore instance from your Activity
    val db = Firebase.firestore
    val auth = Firebase.auth
    private val userRef = db.collection("users")
    private val sprintRef = db.collection("sprints")
    private val storyRef = db.collection("stories")
    private val subRef = db.collection("subtasks")
    private val chatRef = db.collection("chats")
    private var _state = MutableStateFlow(TasksState())
    val state = _state
    var cache = MutableStateFlow(Statics())
    var updateSub = MutableStateFlow(SubTask())
    var updateStory = MutableStateFlow(Story())
    var updateSprint = MutableStateFlow(Sprint())
    var updateTrue = mutableStateOf(false)

    var subtasks = mutableListOf<SubTask>()
    var subs = mutableListOf<Subtask>()
    var stories = mutableListOf<Story>()
    var tasks = mutableListOf<Task>()
    var sprints = mutableListOf<Sprint>()
    var rooms = mutableListOf<SprintRoom>()
    var users = mutableListOf<FireUser>()
    var usrList = mutableListOf<User>()
    var anyList = mutableStateListOf<Any>()
    var chats = mutableListOf<ChatMessage>()
    var messages = mutableListOf<ChatMessage>()

    fun resetStates() {
        _state = MutableStateFlow(TasksState())
        updateSub = MutableStateFlow(SubTask())
        updateStory = MutableStateFlow(Story())
        updateSprint = MutableStateFlow(Sprint())
        updateTrue = mutableStateOf(false)
    }

    suspend fun loadFireStoreData(
        onResult: (List<Sprint>?, List<FireUser>?, FireUser?, List<Story>?, List<SubTask>?,
                   Map<Story, List<SubTask>>, List<TaskAndSubtasks>) -> Unit
    ) {
        updateTimers()
        getAllSprints {
            _state.value = state.value.copy(
                sprints = it.orEmpty().sortedByDescending { s -> s.timestamp } .distinctBy { s -> s.id } as MutableList<Sprint>
            )
        }
        getAllUsers { usrs, usr ->
            _state.value = state.value.copy(
                users = usrs.orEmpty().distinctBy { s -> s.id } as MutableList<FireUser>,
                currentUser = usr
            )
        }
        getAllStories {
            _state.value = state.value.copy(
                stories = it.orEmpty().sortedByDescending { s -> s.modDate }.distinctBy { s -> s.id } as MutableList<Story>
            )
        }
        getAllSubTasks {
            _state.value = state.value.copy(
                subs = it.orEmpty().sortedByDescending { s -> s.modDate }.distinctBy { s -> s.id } as MutableList<SubTask>
            )
        }
        getAllChats { it, it2 ->
            _state.value = state.value.copy(
                messages = it.orEmpty()
            )
        }
        state.value.sprints.forEach { sp ->
            fetchSprintUsers(sp.sid!!) { s, ids, uris, uids ->
                sp.associatedUsers = ids.toList()
                sp.uris = uris
                sp.uidList = uids
                sprintUserMap[sp] = ids
            }
        }
        state.value.stories.forEach { st ->
            val sbs = state.value.subs.filter {s -> s.storyId == st.id }
            gudMap[st] = sbs
            val tk = toTask(st)
            yukMap.putIfAbsent(tk, toSubtaskList(sbs) as List<Subtask>).orEmpty()
            fullTasks.add(TaskAndSubtasks(tk, toSubtaskList(sbs) as List<Subtask>))
        }
        onResult.invoke( state.value.sprints, state.value.users, state.value.currentUser,
            state.value.stories, state.value.subs, gudMap, fullTasks )
    }

    suspend fun fetchFirestoreChanges(
        onResult: (List<Sprint>, List<FireUser>, FireUser?, List<Story>, List<SubTask>,
                   Map<Story, List<SubTask>>, List<TaskAndSubtasks>, Statics.Deleted) -> Unit
    ) {
        getSprintUpdates { added, modified, removed ->
            sprints = sprints.apply {
                addAll(modified.orEmpty())
                addAll(added.orEmpty())
            }.run {
                sortedByDescending { s -> s.timestamp } .distinctBy { s -> s.id } as MutableList<Sprint>
            }
            deletedSprints.addAll(removed.orEmpty())
        }
        getUserUpdates { added, modified, removed, usr ->
            users = users.apply {
                addAll(modified.orEmpty())
                addAll(added.orEmpty())
            }.run {
                sortedByDescending { u -> u.lastLogin } .distinctBy { s -> s.id } as MutableList<FireUser>
            }
            deletedUsers.addAll(removed.orEmpty())
            _state.value = state.value.copy(
                currentUser = usr,
                users = users
            )
            currentUser = usr
        }
        getStoryUpdates { added, modified, removed ->
            stories = stories.apply {
                addAll(modified.orEmpty())
                addAll(added.orEmpty())
            }.run {
                sortedByDescending { s -> s.modDate } .distinctBy { s -> s.id } as MutableList<Story>
            }
            deletedStories.addAll(removed.orEmpty())
        }
        getSubTaskUpdates { added, modified, removed ->
            subtasks = subtasks.apply {
                addAll(modified.orEmpty())
                addAll(added.orEmpty())
            }.run {
                sortedByDescending { s -> s.modDate }.distinctBy { s -> s.id } as MutableList<SubTask>
            }
            deletedSubtasks.addAll(removed.orEmpty())
        }
        getChatUpdates { messages, deleted, map, usr ->
            _state.value = state.value.copy(
                messages = messages.orEmpty(),
                msgMap = map.orEmpty(),
            )
            currentUser = usr
        }
        stories.forEach { st ->
            val sbs = subtasks.filter { sb -> sb.storyId == st.id }
            gudMap[st] = sbs
            val tk = toTask(st)
            yukMap[tk] = toSubtaskList(sbs)
        }
        state.value.sprints.forEach { sp ->
            fetchSprintUsers(sp.sid!!) { s, ids, uris, uids ->
                sp.associatedUsers = ids.toList()
                sp.uris = uris
                sp.uidList = uids
                sprintUserMap[sp] = ids
            }
        }
        yukMap.forEach { (t, u) ->
            if (stories.find {s -> s.id == t.id } == null) {
                yukMap.remove(t)
            }
            if (fullTasks.find { s -> s.task.taskId == t.id } == null) {
                fullTasks.add(TaskAndSubtasks(t, u))
            } else {
                fullTasks[fullTasks.indexOf(fullTasks.find { s -> s.task.taskId == t.id })] =
                    TaskAndSubtasks(t, u)
            }
        }
        fullTasks.removeIf { f -> stories.find {s -> s.id == f.task.taskId } == null }
        onResult.invoke( sprints, users, state.value.currentUser,
            stories, subtasks, gudMap, fullTasks, Statics.Deleted )
    }

    suspend fun fetchSprintUsers(
        sid: Long,
        onResult: (Sprint?, Set<Long>, List<String>, List<String>) -> Unit
    ) {
        var savGUD: Sprint? = null
        var osm = mutableListOf<String>()
        var orhh = mutableListOf<String>()
        var set = mutableSetOf<Long>()
        try {
            val query = storyRef
                .whereEqualTo("sid", sid)
                .get()
                .await()
            if (!query.isEmpty) {
                query.documents.forEach { dc ->
                    val obj = dc.toObject<Story>()
                    obj?.let {
                        set = set.apply {
                            obj.creatorID?.let { add(it) }
                            obj.assId?.let { add(it) }
                            obj.repId?.let { add(it) }
                        }
                        osm = osm.apply {
                            obj.assUri?.let { add(it) }
                            obj.repUri?.let { add(it) }
                            obj.uri?.let { add(it) }
                        }
                        orhh = orhh.apply {
                            add(obj.assUid)
                            add(obj.repUid)
                            obj.uid?.let { add(it) }
                        }
                    }
                }
            }
            val subQuery = subRef
                .whereEqualTo("sid", sid)
                .get()
                .await()
            if (!subQuery.isEmpty) {
                subQuery.documents.forEach { dc ->
                    val obj = dc.toObject<SubTask>()
                    obj?.let {
                        set = set.apply {
                            obj.creatorID?.let { add(it) }
                            obj.assId?.let { add(it) }
                            obj.repId?.let { add(it) }
                        }
                        osm = osm.apply {
                            obj.assUri?.let { add(it) }
                            obj.repUri?.let { add(it) }
                            obj.uri?.let { add(it) }
                        }
                        orhh = orhh.apply {
                            add(obj.assUid)
                            add(obj.repUid)
                            obj.uid?.let { add(it) }
                        }
                    }
                }
            }
            matchID("sprint", sid) { res ->
                db.runTransaction { transaction ->
                    val sRef = sprintRef.document(res!!)
                    val sp = transaction.get(sRef).toObject<Sprint>()
                    savGUD = sp
                    transaction.update(sRef, "users", set,
                        "uris", osm,
                        "uidList", orhh)
                }
                onResult.invoke(savGUD, set, osm, orhh)
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve Firestore Sprint List.")
            }
        }

//        val query = storyRef.where(
//            Filter.or(
//                Filter.equalTo("assId", id),
//                Filter.or(
//                    Filter.equalTo("repId", id),
//                    Filter.equalTo("createdBy", id)
//                )
//            )
//        ).get().await()

    }

    suspend fun getAllSprints(
        onResult: (List<Sprint>?) -> Unit
    ) {
        val ref = db.collection("sprints").id
        try {
            val query = db.collectionGroup(ref)
                .orderBy("id")
                .get()
                .await()
            if ( query.isEmpty ) {
                onResult.invoke(null)
            } else {
                sprints.clear()
                query.documents.forEach { doc ->
                    doc?.let { sprints.add(doc.toObject<Sprint>()!!) }
                }
                onResult.invoke(sprints)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve Firestore Sprint List.")
            }
        }
    }

    suspend fun getAllUsers(
        onResult: (List<FireUser>?, FireUser?) -> Unit
    ) {
        val ref = db.collection("users").id
        try {
            val query = db.collectionGroup(ref)
                .orderBy("id")
                .get()
                .await()
            if ( query.isEmpty ) {
                onResult.invoke(null, null)
            }
            else {
                users.clear()
                query.documents.forEach { doc ->
                    doc?.let { users.add(doc.toObject<FireUser>()!!) }
            }
            val result = auth.currentUser?.uid?.let {
                users.find { u -> u.uid == auth.currentUser!!.uid }
            }
            currentUser = result
            onResult.invoke(users, result)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve Firestore User List.")
            }
        }
    }

    suspend fun getAllChats(
        onResult: (List<ChatMessage>?, Map<ChatMessage, FireUser>?) -> Unit
    ) {
        val ref = db.collection("chats").id
        try {
            val query = db.collectionGroup(ref)
                .orderBy("timestampAbs")
                .orderBy("isAnnouncement", Query.Direction.DESCENDING)
                .orderBy("isPinned", Query.Direction.DESCENDING)
//                .orderBy("isFlagged", Query.Direction.DESCENDING)
                .get()
                .await()
            if ( query.isEmpty ) {
                Log.d("Firestore Repo", "Query found no messages in the chat log.")
                onResult.invoke(null, null)
            } else {
                chats.clear()
                query.documents.forEach { doc ->
                    doc?.let {
                        chats.add(doc.toObject<ChatMessage>()!!)
                        Log.d("Firestore Repo", "Found chat message: ${doc.toObject<ChatMessage>()?.msg}.")
                    }
                }
            }
            chatLog = chats
            chatLog.forEach {
                chatMap.putIfAbsent(it, matchUser(it.senderUid!!))
            }
            unreadMessages = chats.filter { c -> !c.readBy.contains(currentUser?.name) }.toMutableList()
            unreadCount = unreadMessages.size
            onResult.invoke(chats, chatMap)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve list of chat messages.")
            }
        }
    }

    suspend fun getAnnouncements(
        onResult: (List<ChatMessage>?, Map<ChatMessage, FireUser>?) -> Unit
    ) {
        val ref = db.collection("chats").id
        try {
            val query = db.collectionGroup(ref)
                .whereEqualTo("isAnnouncement", true)
                .whereEqualTo("isDeleted", false)
                .orderBy("timestampAbs", Query.Direction.DESCENDING)
                .get()
                .await()
            if ( query.isEmpty ) {
                Log.d("Firestore Repo", "Query found no messages in the chat log.")
                onResult.invoke(null, null)
            } else {
                chats.clear()
                query.documents.forEach { doc ->
                    doc?.let {
                        chats.add(doc.toObject<ChatMessage>()!!)
                        Log.d("Firestore Repo", "Found chat message: ${doc.toObject<ChatMessage>()?.msg}.")
                    }
                }
            }
            chatLog = chats
            chatLog.forEach {
                chatMap.putIfAbsent(it, matchUser(it.senderUid!!))
            }
            unreadMessages = chats.filter { c -> !c.readBy.contains(currentUser?.name) }.toMutableList()
            unreadCount = unreadMessages.size
            onResult.invoke(chats, chatMap)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve list of chat messages.")
            }
        }
    }

    suspend fun getAllStories (
        id: Long? = null,
        onResult: (List<Story>?) -> Unit
    ): List<String> {
        val ref = db.collection("stories").id
        try {
            val query = if (id == null) {
                db.collectionGroup(ref)
                    .orderBy("id")
                    .get()
                    .await()
            }
            else {
                db.collectionGroup(ref)
                    .whereEqualTo("id", id)
                    .orderBy("id")
                    .get()
                    .await()
            }
            if ( query.isEmpty ) {
                onResult.invoke(null)
            } else {
                Statics.ids.clear()
                stories.clear()
                query.documents.forEach { doc ->
                    doc?.let {
                        stories.add(doc.toObject<Story>()!!)
                        Statics.ids.add(doc.id)
                    }
                }
                onResult.invoke(stories)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve Firestore Story List.")
            }
        }
        return Statics.ids
    }

    suspend fun getAllSubTasks(
        sid: Long? = null,
        tid: Long? = null,
        onResult: (List<SubTask>?) -> Unit
    ): List<String> {
        val ref = db.collection("subtasks").id
        try {
            val query = if (sid == null && tid == null) {
                db.collectionGroup(ref)
                    .orderBy("id")
                    .get()
                    .await()
            }
            else if (tid == null) {
                db.collectionGroup(ref)
                    .whereEqualTo("sid", sid)
                    .orderBy("id")
                    .get()
                    .await()
            }
            else {
                db.collectionGroup(ref)
                    .whereEqualTo("tid", tid)
                    .orderBy("id")
                    .get()
                    .await()
            }
            if ( query.isEmpty ) {
                onResult.invoke(null)
            } else {
                Statics.ids.clear()
                subtasks.clear()
                query.documents.forEach { doc ->
                    doc?.let {
                        subtasks.add(doc.toObject<SubTask>()!!)
                        Statics.ids.add(doc.id)
                    }
                }
                onResult.invoke(subtasks)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve Firestore Sub-Task List.")
            }
        }
        return Statics.ids
    }

    suspend fun updateTimers(){
        val sps = mutableListOf<Sprint>()
        try {
            val query = sprintRef
                .whereEqualTo("active", true)
                .whereEqualTo("started", true)
                .whereEqualTo("paused", false)
                .whereEqualTo("completed", false)
                .whereEqualTo("archived", false)
                .get()
                .await()
            if (query.isEmpty) { return }
            db.runTransaction { transaction ->
                query.documents.forEach { doc ->
                    val sRef = sprintRef.document(doc.id)
                    val sp = transaction.get(sRef).toObject<Sprint>()
                    val res = sp?.run {
                        checkDuration(sp)
                    }
                    res?.let {
                        sps.add(sp)
                        transaction.update(sRef,
                            "countdown", it.first,
                            "elapsed", it.second,
                            "expired", it.third, "paused", it.third,
                            "active", !it.third, "started", !it.third
                        )
                    }
                }
            }.addOnSuccessListener { result ->
                Log.d(TAG, "Transaction success: $result")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Transaction failure.", e)
            }.await()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to update Sprint Timers.")
            }
        }


    }

    fun checkDuration(sp: Sprint): Triple<Int, Int, Boolean>? {
        val today = LocalDate.now()
        val start = sp.startDate
        val end = sp.endDate
        if (start == null || end == null || today == LocalDate.ofEpochDay(start)){
            return null
        }
        val total = Period.ofDays(sp.duration)
        val cd = Period.between( today, LocalDate.ofEpochDay(end),).days
        val elapsed = Period.between(LocalDate.ofEpochDay(start), today).days
        if (cd <= 0) {
            // Add a new condition for sprint overdue
        }
        return Triple(cd, elapsed, cd <= 0)
    }

    suspend fun getSprintUpdates(
        onResult: (List<Sprint>?, List<Sprint>?, List<Sprint>?) -> Unit
    ) {
        val ref = db.collection("sprints").id
        try {
            val query = db.collectionGroup(ref)
                .orderBy("id")
                .get()
                .await()
            if ( query.isEmpty ) {
                onResult.invoke(null, null, null)
            } else {
                addedSprints.clear()
                modifiedSprints.clear()
                removedSprints.clear()
                query.documentChanges.forEach { doc ->
                    when (doc.type) {
                        DocumentChange.Type.ADDED -> addedSprints.add(doc.document.toObject())
                        DocumentChange.Type.MODIFIED -> modifiedSprints.add(doc.document.toObject())
                        DocumentChange.Type.REMOVED -> removedSprints.add(doc.document.toObject())
                    }
                    if(doc.newIndex == -1){
                        cache.value.binSprints.add(doc.document.toObject())
                        Statics.oldSprints.add(doc.document.toObject())
                    }
                    if(doc.oldIndex == -1)
                         Statics.newSprints.add(doc.document.toObject())
                    Statics.sprintsList.add(doc.document.toObject())
                }
                onResult.invoke(addedSprints, modifiedSprints, removedSprints)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve Firestore Sprint List.")
            }
        }
    }

    suspend fun getChatUpdates(
        onResult: (
//            List<ChatMessage>?, List<ChatMessage>?, List<ChatMessage>?,
            List<ChatMessage>?, List<ChatMessage>?, Map<ChatMessage, FireUser>?, FireUser?) -> Unit
    ) {
        val ref = db.collection("chats").id
        try {
            val query = db.collectionGroup(ref)
                .orderBy("timestampAbs")
                .orderBy("isAnnouncement", Query.Direction.DESCENDING)
                .orderBy("isPinned", Query.Direction.DESCENDING)
//                .orderBy("isFlagged", Query.Direction.DESCENDING)
                .get()
                .await()
            if ( query.isEmpty ) {
                onResult.invoke(null, null, null, null)
            }
            else {
                addedMessages.clear()
                modifiedMessages.clear()
                deletedMessages.clear()
                query.documentChanges.forEach { doc ->
                    when (doc.type) {
                        DocumentChange.Type.ADDED -> addedMessages.add(doc.document.toObject())
                        DocumentChange.Type.MODIFIED -> modifiedMessages.add(doc.document.toObject())
                        DocumentChange.Type.REMOVED -> removedMessages.add(doc.document.toObject())
                    }
                    if(doc.newIndex == -1) {
                        cache.value.binMessages.add(doc.document.toObject())
                        Statics.oldMessages.add(doc.document.toObject())
                    }
                    if(doc.oldIndex == -1)
                        Statics.newMessages.add(doc.document.toObject())
                    Statics.messagesList.add(doc.document.toObject())
                }
                messages = messages.apply {
                    removeAll(removedMessages)
                    addAll(modifiedMessages)
                    addAll(addedMessages)
                }.run {
                    sortedByDescending { c -> c.timestampAbs } .distinctBy { s -> s.id }
                        .sortedBy { c -> c.timestampAbs } as MutableList<ChatMessage>
                }
                deletedMessages.addAll(removedMessages)
                removedMessages.forEach {
                    messageMap.remove(it)
                }
                messages.forEach {
                    messageMap.putIfAbsent(it, matchUser(it.senderUid!!))
                }
                _state.value = state.value.copy(
                    messages = messages,
                    msgMap = messageMap,
                )
                chats = messages
                val result = auth.currentUser?.uid?.let {
                    users.find { u -> u.uid == auth.currentUser!!.uid }
                }
                currentUser = result
                onResult.invoke(/*addedMessages, modifiedMessages, */ messages, removedMessages, messageMap, result)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve updated ChatMessage list.")
            }
        }
    }

    suspend fun getUserUpdates(
        onResult: (List<FireUser>?, List<FireUser>?, List<FireUser>?, FireUser?) -> Unit
    ) {
        val ref = db.collection("users").id
        try {
            val query = db.collectionGroup(ref)
                .orderBy("id")
                .get()
                .await()
            if ( query.isEmpty ) {
                onResult.invoke(null, null, null, null)
            }
            else {
                addedUsers.clear()
                modifiedUsers.clear()
                deletedUsers.clear()
                query.documentChanges.forEach { doc ->
                    when (doc.type) {
                        DocumentChange.Type.ADDED -> addedUsers.add(doc.document.toObject())
                        DocumentChange.Type.MODIFIED -> modifiedUsers.add(doc.document.toObject())
                        DocumentChange.Type.REMOVED -> removedUsers.add(doc.document.toObject())
                    }
                    if(doc.newIndex == -1) {
                        cache.value.binUsers.add(doc.document.toObject())
                        Statics.oldUsers.add(doc.document.toObject())
                    }
                    if(doc.oldIndex == -1)
                        Statics.newUsers.add(doc.document.toObject())
                    Statics.usersList.add(doc.document.toObject())
                }
                val result = auth.currentUser?.uid?.let {
                    users.find { u -> u.uid == auth.currentUser!!.uid }
                }
                currentUser = result
                onResult.invoke(addedUsers, modifiedUsers, removedUsers, result)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve FireUser list.")
            }
        }
    }

    suspend fun getStoryUpdates(
        onResult: (List<Story>?, List<Story>?, List<Story>?) -> Unit
    ) {
        val ref = db.collection("stories").id
        try {
            val query = db.collectionGroup(ref)
                .orderBy("id")
                .get()
                .await()
            if ( query.isEmpty ) {
                onResult.invoke(null, null, null)
            } else {
                addedStories.clear()
                modifiedStories.clear()
                deletedStories.clear()
                query.documentChanges.forEach { doc ->
                    when (doc.type) {
                        DocumentChange.Type.ADDED -> addedStories.add(doc.document.toObject())
                        DocumentChange.Type.MODIFIED -> modifiedStories.add(doc.document.toObject())
                        DocumentChange.Type.REMOVED -> removedStories.add(doc.document.toObject())
                    }
                    if(doc.newIndex == -1 ){
                        cache.value.binStories.add(doc.document.toObject())
                        Statics.oldStories.add(doc.document.toObject())
                    }
                    if(doc.oldIndex == -1)
                        Statics.newStories.add(doc.document.toObject())
                    Statics.storiesList.add(doc.document.toObject())
                }
                onResult.invoke(addedStories, modifiedStories, removedStories)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve Firestore Story List.")
            }
        }
    }

    suspend fun getSubTaskUpdates(
        onResult: (List<SubTask>?, List<SubTask>?, List<SubTask>?) -> Unit
    ) {
        val ref = db.collection("subtasks").id
        try {
            val query = db.collectionGroup(ref)
                .orderBy("id")
                .get()
                .await()
            if ( query.isEmpty ) {
                onResult.invoke(null, null, null)
            } else {
                addedSubtasks.clear()
                modifiedSubtasks.clear()
                deletedSubtasks.clear()
                query.documentChanges.forEach { doc ->
                    when (doc.type) {
                        DocumentChange.Type.ADDED -> addedSubtasks.add(doc.document.toObject())
                        DocumentChange.Type.MODIFIED -> modifiedSubtasks.add(doc.document.toObject())
                        DocumentChange.Type.REMOVED -> removedSubtasks.add(doc.document.toObject())
                    }
                    if(doc.newIndex == -1) {
                        cache.value.binSubtasks.add(doc.document.toObject())
                        Statics.oldSubtasks.add(doc.document.toObject())
                    }
                    if(doc.oldIndex == -1)
                        Statics.newSubtasks.add(doc.document.toObject())
                    Statics.subtasksList.add(doc.document.toObject())
                    //Statics.subtasks.distinctBy { s -> Pair(s.id, s.subId) }
                }
                onResult.invoke(addedSubtasks, modifiedSubtasks, removedSubtasks)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve Firestore Sub-Task List.")
            }
        }
    }

    suspend fun matchUID(uid: String = auth.uid.orEmpty()): Pair<Long, String>  {
        val query = userRef
            .whereEqualTo("uid", uid)
            .limit(1)
            .get()
            .await()
        var x = -1L
        var u = ""
        if(!query.isEmpty) {
            query.documents.forEach { dc ->
                val obj = dc.toObject<FireUser>()
                obj?.let {
                    x = obj.id?: x
                    u = obj.photo?: u
                }
            }
        }
        return Pair(x, u)
    }

    suspend fun matchUser(uid: String = auth.uid.orEmpty()): FireUser {
        val query = userRef
            .whereEqualTo("uid", uid)
            .limit(1)
            .get()
            .await()
        var x = FireUser()
        if(!query.isEmpty) {
            query.documents.forEach { dc ->
                val obj = dc.toObject<FireUser>()
                obj?.let {
                    x = obj
                }
            }
        }
        return x
    }

    suspend fun matchID(
        coll: String,
        id: Long,
        onRes: (String?) -> Unit
    ): String {
        if (id < 0) { throw IndexOutOfBoundsException() }
        try {
            val querySnapshot = db.collection(coll)
                .whereEqualTo("id", id)
                .limit(1)
                .get()
                .await()
            val sb = StringBuilder()
            querySnapshot.documents.forEachIndexed { i, doc ->
                Statics.result.value = doc.id
                onRes.invoke(doc.id)
                return@forEachIndexed
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                Log.d("Firestore Repo", "Failed to retrieve online ${coll.dropLast(1)} data.")
            }
        }
        return Statics.result.value.toString()
    }

    suspend fun getSubTask(
        id: Long,
        onRes: (SubTask?) -> Unit
    ) {
        matchID("subtasks", id) { res ->
            db.runTransaction { transaction ->
                val sRef = subRef.document(res!!)
                val sub = transaction.get(sRef).toObject<SubTask>()
                onRes.invoke(sub)
            }

        }
    }

    suspend fun bigUpdateSubtaskHelper(
        id: String,
        map: Map<String, Any>,
        onDone: (SubTask?, Long?) -> Unit
    ) {
        try {
            db.runTransaction { transaction ->
                val sRef = subRef.document(id)
                transaction.update(sRef, map)
//                transaction.set(sRef,
//                    map,
//                    SetOptions.merge()
//                )
            }.addOnSuccessListener { result ->
                Log.d(TAG, "Transaction success: $result")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Transaction failure.", e)
            }.await()
            val res = subRef.document(id).get().await().toObject<SubTask>()
            onDone.invoke(res, res?.id)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Firestore Repo", "Failed to update online subtask data.")
            }
        }
    }

    suspend fun bigUpdateSprintHelper(
        id: String,
        map: Map<String, Any>,
        onDone: (Sprint?, Long?) -> Unit
    ) {
        try {
            db.runTransaction { transaction ->
                val sRef = sprintRef.document(id)
                transaction.update(sRef, map)
//                transaction.set(sRef,
//                    map,
//                    SetOptions.merge()
//                )
            }.addOnSuccessListener { result ->
                Log.d(TAG, "Transaction success: $result")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Transaction failure.", e)
            }.await()
            val res = sprintRef.document(id).get().await().toObject<Sprint>()
            onDone.invoke(res, res?.id)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Firestore Repo", "Failed to update online sprint data.")
            }
        }
    }

    suspend fun getSprint(
        id: Long,
        onRes: (Sprint?, String?) -> Unit
    ) {
        matchID("sprints", id) { res ->
            if (res == null) {
                onRes.invoke(null, null)
                return@matchID
            }
            db.runTransaction { transaction ->
                val sRef = sprintRef.document(res)
                val sp = transaction.get(sRef).toObject<Sprint>()
                onRes.invoke(sp, sRef.id)
            }

        }
    }

    fun getUpdatedSprint(sp : SprintRoom): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        if (sp.title.isNotBlank()) {
            map["title"] = sp.title
        }
        if (sp.desc.isNotBlank()) {
            map["desc"] = sp.desc
        }
        map["active"] = sp.active
        map["backlogWt"] = sp.info.backlogWt
        map["started"] = sp.info.started
        map["elapsed"] = sp.info.elapsed
        map["paused"] = sp.info.paused
        map["cloned"] = sp.info.cloned
        map["new"] = false
        map["progress"] = sp.info.progress?: 0.0f
        map["progressPct"] = sp.info.progressPct?: 0.0f
        map["completed"] = sp.completed
        map["color"] = sp.color
        map["totalPoints"] = sp.info.totalPoints
        map["remPoints"] = sp.info.remPoints
        map["startDate"] = sp.info.startDate.toEpochDay()
        map["endDate"] = sp.info.endDate.toEpochDay()
        map["meetingTime"] = formatTym(sp.info.meetingTime)
        map["reviewTime"] = formatTym(sp.info.reviewTime)
        map["status"] = sp.info.status.orEmpty()
        map["resolution"] = sp.info.resolution?: ""
        map["totalPoints"] = sp.info.totalPoints
        map["remPoints"] = sp.info.remPoints
        map["duration"] = sp.info.duration
        map["countdown"] = sp.info.countdown
        map["isReviewed"] = sp.info.isArchived
        map["isApproved"] = sp.info.isApproved
        map["isArchived"] = sp.info.isArchived
        map["owner"] = sp.info.owner.orEmpty()
        map["manager"] = sp.info.manager.orEmpty()
        map["modDate"] = System.currentTimeMillis()

        if (!sp.info.status.isNullOrBlank()) {
            map["status"] = sp.info.status
            map["done"] = checkDone(sp.info.status)
        }
        sp.info.manual?.let {map["manual"] = it }

        if (sp.projectId != null) {
            map["projectId"] = sp.projectId
        }
        if (sp.boardId != null) {
            map["boardId"] = sp.boardId
        }
        sp.uid?.let { map["uid"] = it }
        sp.info.createdBy?.let {map["createdBy"] = it}
        sp.storyId?.let { map["componentId"] = it }
        sp.info.owner?.let { map["owner"] = it }
        sp.info.manager?.let { map["manager"] = it }
        map["modDate"] = System.currentTimeMillis()
        map["accDate"] = System.currentTimeMillis()
        return map
    }

    suspend fun updateSubtaskHelper(
        id: String,
        title: String,
        body: String,
        pri: String,
        stat: String,
        //priInc: Int,
        ass: FireUser? = null,
        rep: FireUser? = null,
        onRes: (SubTask?, Long?) -> Unit
        )
    {
        try {
            Firebase.firestore.runTransaction { transaction ->
                val sRef = subRef.document(id)
                val sub = transaction.get(sRef)
                //val new = Priority.iToS(Priority.valueOf(sub["priority"] as String).ordinal + priInc)
                transaction.update(sRef, "title", title,
                    "body", body, "priority", pri)
                transaction.update(sRef, "stat", stat)
                if (ass != null) {
                    transaction.update(sRef, "assignee", ass.name,
                        "assId", ass.id, "assUid", ass.uid, "assUri", ass.photo)
                }
                if (rep != null) {
                    transaction.update(sRef, "reporter", rep.name,
                        "repId", rep.id, "repUid", rep.uid)
                }
//                val res = transaction.get(sRef).toObject<SubTask>()
//                onRes.invoke(res, res?.id)
                null
            }.await()
            val res = subRef.document(id).get().await().toObject<SubTask>()
            onRes.invoke(res, res?.id)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Firestore Repo", "Failed to retrieve online story data.")
            }
        }
    }

    suspend fun cloudUpdateSubTask(
        id: Long,
        sb: Subtask,
        map: Map<String, Any> = getUpdatedSubTask(sb),
        onDone: (SubTask) -> Unit
    ) {
        matchID("subtasks", id) { res ->
            if (res == null) {
                 CoroutineScope(Dispatchers.IO).launch {
                    addSub(toSub(sb, id))
                }
            }
            else { CoroutineScope(Dispatchers.IO).launch {
                bigUpdateSubtaskHelper(res, map) { sub, id ->
                    subtasks[subtasks.indexOf(subtasks.find { s -> s.id == id })] = sub!!
                    updateSub.value = sub
                    onDone.invoke(sub)
                }
            } }
        }
    }

    private fun getUpdatedSubTask(sub: Subtask): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        if(sub.title.isNotBlank()) {
            map["title"] = sub.title
        }
        if(sub.content.isNotBlank()) {
            map["body"] = sub.content
        }
        if(sub.desc.isNotBlank()) {
            map["desc"] = sub.desc
        }
        map["assUid"] = sub.assUid.orEmpty()
        map["assUri"] = sub.assUri.orEmpty()
        map["assignee"] = sub.assignee.orEmpty()
        if(sub.assigneeId != null && sub.assigneeId!! > -1) {
            map["assId"] = sub.assigneeId!!
        }
        map["repUid"] = sub.repUid.orEmpty()
        map["repUri"] = sub.repUri.orEmpty()
        map["reporter"] = sub.reporter.orEmpty()
        if(sub.reporterId != null && sub.reporterId!! > -1) {
            map["repId"] = sub.reporterId!!
        }
        map["resolution"] = sub.resolution.orEmpty()
        map["points"] = sub.points
        map["priority"] = sub.priority.name
        if(sub.color > 0) {
            map["color"] = sub.color
        }
        map["status"] = sub.status
        map["done"] = sub.done
        if(sub.sprintId!! > -1) {
            map["sid"] = sub.sprintId!!
        }
        if(sub.parentId!! > -1) {
            map["storyId"] = sub.parentId!!
        }
        if(sub.subId!! > -1) {
            map["id"] = sub.subId!!
        }
        map["current"] = sub.current
        map["cloned"] = sub.cloned
        map["type"] = sub.type.name
        map["dod"] = sub.dod.orEmpty()
        map["modDate"] = System.currentTimeMillis()
        map["accDate"] = System.currentTimeMillis()
        return map
    }

    private fun saveUpdatedSubTask(id: Long, subtask: Subtask) = CoroutineScope(Dispatchers.IO).launch {
        cloudUpdateSubTask(/*retrieveStory()!!,*/ id, subtask, getUpdatedSubTask(subtask)) {

        }

    }

    suspend fun retrieveStory(
        id: Long?,
        onResult: (Story?) -> Unit
    ): Story? {
        var story: Story? = null
        try {
            val querySnapshot = storyRef
                .whereEqualTo("id", id)
                .limit(1)
                .get()
                .await()
            val sb = StringBuilder()
            querySnapshot.documents.forEachIndexed { i, doc ->
                story = doc.toObject<Story>()
                sb.append("$story\n")
                onResult.invoke(story)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Firestore Repo", "Failed to retrieve online story data.")
            }
        }
        return story
    }

    suspend fun retrieveSub(
        id: Long?,
        onResult: (SubTask?) -> Unit
    ): SubTask? {
        var sub: SubTask? = null
        try {
            val querySnapshot = subRef
                .whereEqualTo("id", id)
                .limit(1)
                .get()
                .await()
            val sb = StringBuilder()
            querySnapshot.documents.forEachIndexed { i, doc ->
                sub = doc.toObject<SubTask>()
                sb.append("$sub\n")
                onResult.invoke(sub)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Firestore Repo", "Failed to retrieve online story data.")
            }
        }
        return sub
    }

    suspend fun retrieveSubsByStory(
        id: Long?,
        onResult: (List<SubTask>) -> Unit
    ) {
        var result: MutableList<SubTask> = mutableStateListOf()
        val snap = subRef
            .whereEqualTo("storyId", id)
            .get()
            .await()
        val sb = StringBuilder()
        snap.documents.forEachIndexed { i, doc ->
            val subtask = doc.toObject<SubTask>()
            sb.append("$subtask\n")
            result.add(subtask!!)
            onResult.invoke(result)
        }
    }

    fun toSub(sub: SubTask) = Subtask (
        id = sub.subId,
        subId = sub.id,
        origId = if(sub.cloned) sub.origId else sub.id,
        parentId = sub.storyId,
        sprintId = sub.sid,
        title = sub.title,
        content = sub.body,
        desc = sub.desc,
        timestamp = System.currentTimeMillis(),
        points = sub.points,
        priority = Priority.valueOf(sub.priority),
        color = sub.color,
        assigneeId = sub.assId,
        assUid = sub.assUid,
        assUri = sub.assUri,
        assignee = sub.assignee?: "None",
        type = IssueType.valueOf(sub.type),
        reporterId = sub.repId,
        repUid = sub.repUid,
        repUri = sub.repUri,
        reporter = sub.reporter?: "None",
        resolution = sub.resolution,
        current = sub.current,
        creator = sub.creator?: uidNameMap[sub.createdBy]?: IDNameMap[sub.creatorID],
        createdBy = sub.createdBy?: auth.uid,
        status = sub.status,
        done = sub.done,
        cloned = sub.cloned,
        userId = sub.creatorID,
        uid = auth.uid,
        uri = sub.uri
    )

    fun toSub(sub:Subtask, c: Long? = null) = SubTask (
        id = c ?: sub.subId,
        subId = c ?: sub.id,
        origId = if(sub.cloned) sub.origId else sub.subId,
        storyId = sub.parentId,
        sid = sub.sprintId,
        title = sub.title,
        body = sub.content,
        desc = sub.desc,
        modDate = System.currentTimeMillis(),
        accDate = System.currentTimeMillis(),
        points = sub.points,
        priority = sub.priority.string,
        color = sub.color,
        assId = sub.assigneeId,
        assUid = sub.assUid?: "",
        assUri = sub.assUri,
        assignee = sub.assignee?: "None",
        type = sub.type.name,
        repId = sub.reporterId,
        repUid = sub.repUid?: "",
        repUri = sub.repUri,
        reporter = sub.reporter?: "None",
        resolution = sub.resolution,
        current = sub.current,
        creator = sub.creator?: uidNameMap[sub.createdBy]?: IDNameMap[sub.userId],
        createdBy = sub.createdBy?: auth.uid,
        creatorID = sub.userId,
        status = sub.status,
        done = sub.done,
        cloned = sub.cloned,
        uid = auth.uid,
        uri = sub.uri

    )

    fun toTask(story: Story) = Task (
        id = story.storyId,
        taskId = story.id,
        sprintId = story.sid,
        origId = if(story.cloned) story.origId else story.id,
        title = story.title,
        content = story.body,
        desc = story.desc,
        timestamp = story.modDate,
        points = story.points,
        priority = Priority.valueOf(story.priority),
        color = story.color,
        assigneeId = story.assId,
        assUid = story.assUid,
        assUri = story.assUri,
        assignee = story.assignee,
        type = IssueType.valueOf(story.type),
        reporterId = story.repId,
        repUid = story.repUid,
        repUri = story.repUri,
        reporter = story.reporter?: "None",
        resolution = story.resolution,
        current = story.current,
        creator = story.creator?: uidNameMap[story.createdBy]?: IDNameMap[story.creatorID],
        createdBy = story.createdBy?: auth.uid,
        status = story.status,
        done = story.done,
        cloned = story.cloned,
        userId = story.creatorID,
        uid = auth.uid,
        uri = story.uri
    )

    fun toStory(task: Task, c: Long? = null) = Story (
        id = c ?: task.taskId,
        storyId = c ?: task.id,
        origId = if(task.cloned) task.origId else task.taskId,
        sid = task.sprintId,
        title = task.title,
        body = task.content,
        desc = task.desc,
        modDate = task.timestamp,
        accDate = System.currentTimeMillis(),
        points = task.points,
        priority = task.priority.string,
        color = task.color,
        assId = task.assigneeId,
        assUid = task.assUid?: "",
        assUri = task.assUri,
        assignee = task.assignee?: "None",
        type = task.type.name,
        repId = task.reporterId,
        repUid = task.repUid?: "",
        repUri = task.repUri,
        reporter = task.reporter?: "None",
        resolution = task.resolution,
        current = task.current,
        creator = task.creator?: uidNameMap[task.createdBy]?: IDNameMap[task.userId],
        createdBy = task.createdBy?: auth.uid,
        creatorID = task.userId,
        status = task.status,
        done = task.done,
        cloned = task.cloned,
        uid = auth.uid,
        uri = task.uri
    )

    @OptIn(ExperimentalTime::class)
    fun toSprint(sp: Sprint) = SprintRoom (
        sprintId = sp.id,
        title = sp.title?: "Sprint ${sp.id}",
        desc = sp.desc?: "Description",
        origId = if(sp.cloned) sp.origId else sp.id,
        active = sp.active!!,
        target = sp.target,
        completed = sp.completed,
        projectId = sp.projectId,
        boardId = sp.boardId,
        storyId = sp.componentId,
        epicId = sp.epicId,
        uid = auth.uid,
        uri = sp.uri,
        info = SprintInfo(
            cloned = sp.cloned,
            totalPoints = sp.totalPoints,
            remPoints = sp.remPoints,
            meetingTime = sp.meetingTime?.let { LocalTime.parse(sp.meetingTime) },
            reviewTime = sp.reviewTime?.let { LocalTime.parse(sp.reviewTime) },
            startDate = sp.startDate?.let { LocalDate.ofEpochDay(msToDays(it)) } ?: LocalDate.now(),
            endDate = sp.endDate?.let { LocalDate.ofEpochDay(msToDays(it)) } ?: LocalDate.MAX,
            duration = Period.ofDays(sp.duration),
            countdown = sp.countdown,
            elapsed = sp.elapsed,
            started = sp.started,
            paused = sp.paused,
            expired = sp.expired,
            status = sp.status,
            progress = sp.progress,
            progressPct = sp.progressPct,
            resolution = sp.resolution,
            isApproved = sp.isApproved,
            isReviewed = sp.isReviewed,
            isArchived = sp.isArchived,
            manual = sp.manual,
            owner = sp.owner,
            manager = sp.manager,
            creator = sp.creator?: uidNameMap[sp.createdBy]?: IDNameMap[sp.creatorID],
            createdBy = sp.createdBy?: auth.uid,
            creatorID = sp.creatorID,
            backlogWt = sp.backlogWt,
            modDate = sp.modDate,
            new = sp.new,
        ),
    )

    fun toSprint(sp: SprintRoom, c: Long? = null) = Sprint (
        id = c ?: sp.sprintId,
        sid = c ?: sp.sprintId,
        title = sp.title,
        desc = sp.desc,
        origId = if(sp.info.cloned) sp.origId else sp.sprintId,
        active = sp.active,
        projectId = sp.projectId,
        boardId = sp.boardId,
        componentId = sp.storyId,
        epicId = sp.epicId,
        cloned = sp.info.cloned,
        meetingTime = formatTym(sp.info.meetingTime),
        reviewTime = formatTym(sp.info.reviewTime),
        totalPoints = sp.info.totalPoints,
        remPoints = sp.info.remPoints,
        backlogWt = sp.info.backlogWt,
        startDate = sp.info.startDate.toEpochDay(),
        endDate = sp.info.endDate.toEpochDay(),
        duration = sp.info.duration.days,
        countdown = sp.info.countdown,
        elapsed = sp.info.elapsed,
        started = sp.info.started,
        paused = sp.info.paused,
        expired = sp.info.expired,
        status = sp.info.status,
        progress = sp.info.progress,
        progressPct = sp.info.progressPct,
        target = sp.target,
        completed = sp.completed,
        resolution = sp.info.resolution,
        isApproved = sp.info.isApproved,
        isReviewed = sp.info.isReviewed,
        isArchived = sp.info.isArchived,
        manual = sp.info.manual,
        owner = sp.info.owner,
        manager = sp.info.manager,
        creator = sp.info.creator?: uidNameMap[sp.info.createdBy]?: IDNameMap[sp.info.creatorID],
        createdBy = sp.info.createdBy?: auth.uid,
        creatorID = sp.info.creatorID,
        modDate = sp.info.modDate,
        new = sp.info.new,
        uri = sp.uri,
        uid = auth.uid,
    )

    fun toSubTaskList(list: List<Subtask>?): List<SubTask> {
        if (list.isNullOrEmpty()) return emptyList()
        subtasks.clear()
        subtasks.addAll(list.map { s -> toSub(s) })
        return subtasks
    }

    fun toSubtaskList(list: List<SubTask>?): List<Subtask> {
        if (list.isNullOrEmpty()) return emptyList()
        subs.clear()
        subs.addAll(list.map { s -> toSub(s) })
        return subs
    }

    fun toStoryList(list: List<Task>?): List<Story> {
        if (list.isNullOrEmpty()) return emptyList()
        stories.clear()
        stories.addAll(list.map { t -> toStory(t) } )
        return stories
    }

    fun toTaskList(list: List<Story>?): List<Task> {
        if (list.isNullOrEmpty()) return emptyList()
        tasks.clear()
        tasks.addAll(list.map { st -> toTask(st) } )
        return tasks
    }

    fun toSprintList(list: List<SprintRoom>?): List<Sprint> {
        if (list.isNullOrEmpty()) return emptyList()
        sprints.clear()
        sprints.addAll(list.map { sp -> toSprint(sp) } )
        return sprints
    }

    fun toSprintRoomList(list: List<Sprint>?): List<SprintRoom> {
        if (list.isNullOrEmpty()) return emptyList()
        rooms.clear()
        rooms.addAll(list.map { sp -> toSprint(sp) } )
        return rooms
    }

    suspend fun deleteSprint(sp: Sprint, sid: Long) {
        val id = matchID("sprints", sid,) {
            if (it.isNullOrBlank()) throw IllegalArgumentException()
            Statics.Recycler.lastDelSprints.clear()
            Statics.Recycler.lastDelStories.clear()
            Statics.Recycler.lastDelSubtasks.clear()
            Statics.ids.add(it)
            Statics.Recycler.lastDelSprints.add(sp)
            Statics.recentSprints.add(sp)
            //Statics.oldIds.add(it)
        }
        val stories = getAllStories(sid) {
            Statics.oldStories.addAll(it.orEmpty())
            Statics.recentStories.addAll(it.orEmpty())
            Statics.Recycler.lastDelStories.addAll(it.orEmpty())
        }
        val subtasks = getAllSubTasks(sid) {
            Statics.oldSubtasks.addAll(it.orEmpty())
            Statics.recentSubTasks.addAll(it.orEmpty())
            Statics.Recycler.lastDelSubtasks.addAll(it.orEmpty())
        }
        try {
            db.runBatch { batch ->
                Statics.oldIds.add(id)
                batch.delete(sprintRef.document(id))
                if (stories.isNotEmpty()) {
                    stories.forEach { tid ->
                        Statics.oldIds.add(tid)
                        batch.delete(storyRef.document(tid))
                    }
                }
                if (subtasks.isNotEmpty()) {
                    subtasks.forEach { sid ->
                        Statics.oldIds.add(sid)
                        batch.delete(subRef.document(sid))
                    }
                }
                batch.commit()
            }
        } catch(e: Exception) {
            Log.d("fireRepo", "Sprint deletion cancelled - An error occurred")
            e.printStackTrace()
        }
    }

    fun addStory(story: Story) = CoroutineScope(Dispatchers.IO).launch {
        try {
            storyRef.add(story).await()
        } catch(e: Exception) {
            e.printStackTrace()

        }
    }

    fun addSub(sb: SubTask) = CoroutineScope(Dispatchers.IO).launch {
        try {
            storyRef.add(sb).await()
        } catch(e: Exception) {
            e.printStackTrace()

        }
    }

    fun restoreSprint(

    ) {

    }

    fun restore(
        sprint: Sprint? = if (Statics.recentSprints.isNotEmpty()) Statics.recentSprints[0] else null,
        story: Story = Statics.recentStory.value,
        list: List<SubTask>? = Statics.recentSubTasks) = CoroutineScope(Dispatchers.IO).launch {
        try {
            sprint?.let {
                sprintRef.add(sprint).await()
                Statics.recentSprints.remove(sprint)
                Statics.recentStories.forEach { s ->
                    if (s.sid == sprint.id) {
                        storyRef.add(s).await()
                        Statics.recentStories.remove(s)
                    }
                }
                Statics.recentSubTasks.forEach { s ->
                    if (s.sid == sprint.id) {
                        subRef.add(s).await()
                        Statics.recentSubTasks.remove(s)
                    }
                }
                return@launch
            }
            storyRef.add(story).await()
            Statics.recentStories.remove(story)
            list?.forEach {
                if (it.storyId == story.id) {
                    subRef.add(it).await()
                    Statics.recentSubTasks.remove(it)
                }
            }
            Statics.recentStory.value = Story()
            return@launch
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }



    fun count(coll: String): Long {
        var x = 0L
        try {
            val countQ = db.collection(coll).count()
            countQ.get(AggregateSource.SERVER).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    x = task.result.count
                    Log.d(coll.dropLast(1), " Count: $x")
                    return@addOnCompleteListener
                } else {
                    Log.d(coll.dropLast(1), "Count failed: ", task.exception)
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return x
    }

    suspend fun saveStoryHelper(task: Task, id: Long) {
        retrieveStory(id) { res ->
            if(res != null) {
                saveUpdatedStory(task, id)
            }
            else {
                if (id < 0) throw (IndexOutOfBoundsException())
                CoroutineScope(Dispatchers.IO).launch {
                    saveStory(
                        toStory(task, id)
                    )
                }
            }
        }
    }

    private fun saveUpdatedStory(task: Task, id: Long) = CoroutineScope(Dispatchers.IO).launch {
        updateStory(/*retrieveStory()!!,*/ id, getUpdatedStory(task))
    }

    private fun getUpdatedStory(task: Task): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        if(task.title.isNotBlank()) {
            map["title"] = task.title
        }
        if(task.desc.isNotBlank()) {
            map["desc"] = task.desc
        }
        map["assUid"] = task.assUid.orEmpty()
        map["assUri"] = task.assUri.orEmpty()
        map["assignee"] = task.assignee.orEmpty()
        if(task.assigneeId != null && task.assigneeId!! > -1) {
            map["assId"] = task.assigneeId!!
        }
        map["repUid"] = task.repUid.orEmpty()
        map["repUri"] = task.repUri.orEmpty()
        map["reporter"] = task.reporter.orEmpty()
        if(task.reporterId != null && task.reporterId!! > -1) {
            map["repId"] = task.reporterId!!
        }
        map["resolution"] = task.resolution.orEmpty()
        map["points"] = task.points
        map["priority"] = task.priority.name
        if(task.color > 0) {
            map["color"] = task.color
        }
        map["uid"] = task.uid!!
        map["status"] = task.status
        map["done"] = task.done
        if(task.sprintId!! > -1) {
            map["sid"] = task.sprintId!!
        }
        if(task.taskId!! > -1) {
            map["storyId"] = task.taskId!!
        }
        map["current"] = task.current
        map["cloned"] = task.cloned
        if(task.cloned && task.origId != null) {
            map["origId"] = task.origId!!
        }
        map["type"] = task.type.name
        map["body"] = task.content.orEmpty()
        map["dod"] = task.dod.orEmpty()
        map["modDate"] = System.currentTimeMillis()
        map["accDate"] = System.currentTimeMillis()
        if(!task.uri.isNullOrBlank()) {
            map["uri"] = task.uri!!
        }
        if(!task.uid.isNullOrBlank()) {
            map["uid"] = task.uid!!
        }
        return map
    }

    private suspend fun updateStory(id: Long, updatedMap: Map<String, Any>) {
        val query = storyRef
            .whereEqualTo("id", id)
//            .whereEqualTo("sid", story.sid)
//            .whereEqualTo("title", story.title)
//            .whereEqualTo("body", story.body)
//            .whereEqualTo("desc", story.desc)
//            .whereEqualTo("assignee", story.assignee)
//            .whereEqualTo("reporter", story.reporter)
//            .whereEqualTo("resolution", story.resolution)
//            .whereEqualTo("points", story.points)
//            .whereEqualTo("priority", story.priority)
//            .whereEqualTo("color", story.color)
//            .whereEqualTo("status", story.status)
//            .whereEqualTo("done", story.done)
//            .whereEqualTo("labels", story.labels)
//            .whereEqualTo("project", story.project)
//            .whereEqualTo("numSubtasks", story.numSubtasks)
//            .whereEqualTo("sid", story.sid)
//            .whereEqualTo("taskId", story.taskId)
//            .whereEqualTo("type", story.type)
//            .whereEqualTo("project", story.project)
//            .whereEqualTo("labels", story.labels)
//            .whereEqualTo("active", story.active)
//            .whereEqualTo("current", story.current)
//            .whereEqualTo("component", story.component)
//            .whereEqualTo("comments", story.comments)
//            .whereEqualTo("log", story.log)
//            .whereEqualTo("DoD", story.DoD)
            .limit(1)
            .get()
            .await()
        if(query.documents.isNotEmpty()) {
            for(doc in query) {
                try {
                    storyRef.document(doc.id).set(
                        updatedMap,
                        SetOptions.merge()
                    ).await()
                } catch(e: Exception) {
                    withContext(Dispatchers.Main) {
                        AddEditTaskViewModel.UIEvent.ShowSNB(e.localizedMessage?: "Error during document iteration")
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                AddEditTaskViewModel.UIEvent.ShowSNB("No user found to perform update")
            }
        }
    }

    private fun saveStory(story: Story) = CoroutineScope(Dispatchers.IO).launch {
        try {
            storyRef.add(story).await()
            withContext(Dispatchers.Main) {
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
            }
        }
    }

    private fun saveSubs(list: List<SubTask>?) = CoroutineScope(Dispatchers.IO).launch {
        try {
            list?.forEach { sub ->
                subRef.add(sub).await()
            }
            withContext(Dispatchers.Main) {

            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
            }
        }

    }

    fun formatDT(dt: LocalDateTime = LocalDateTime.now()): String = run {
        DateTimeFormatter
            .ofPattern("EEEE, MMM dd yyyy hh:mm aa")
            .format(dt)
    }?: "00:00"

    fun formatTym(tym: LocalTime?): String = run {
        DateTimeFormatter
            .ofPattern("hh:mm")
            .format(tym)
    }?: "00:00"

    fun formatDate(dt: LocalDate?): String? = run {
        DateTimeFormatter
            .ofPattern("yyyy mm dd")
            .format(dt)
    }

    fun formatDateDisplay(dt: LocalDate?): String? = run {
        DateTimeFormatter
            .ofPattern("MMM dd yyyy")
            .format(dt)
    }
    @OptIn(ExperimentalTime::class)
    fun formatDateDisplay(dt: Long?): String? = run {
        DateTimeFormatter
            .ofPattern("MMM dd yyyy")
            .format(LocalDate.ofEpochDay(msToDays(dt?:0)))
    }
    @ExperimentalTime
    fun msToDays(tym: Long): Long {
        return Duration.convert(tym + 0.0, DurationUnit.MILLISECONDS, DurationUnit.DAYS).roundToLong()
    }

}