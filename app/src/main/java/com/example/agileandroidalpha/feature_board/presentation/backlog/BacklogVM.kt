package com.example.agileandroidalpha.feature_board.presentation.backlog

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithTasksAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import com.example.agileandroidalpha.feature_board.domain.use_case.SprintUseCases
import com.example.agileandroidalpha.feature_board.domain.use_case.TaskUseCases
import com.example.agileandroidalpha.feature_board.domain.util.OrderType
import com.example.agileandroidalpha.feature_board.domain.util.SprintOrder
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.AddEditTaskViewModel
import com.example.agileandroidalpha.feature_board.presentation.sprint.SprintState
import com.example.agileandroidalpha.feature_board.presentation.sprint.checkDoneSpecial
import com.example.agileandroidalpha.feature_board.presentation.sprint.checkStatus
import com.example.agileandroidalpha.feature_board.presentation.tasks.TasksState
import com.example.agileandroidalpha.firebase.firestore.Statics
import com.example.agileandroidalpha.firebase.firestore.Statics.Deleted.deletedRooms
import com.example.agileandroidalpha.firebase.firestore.Statics.Deleted.deletedSubs
import com.example.agileandroidalpha.firebase.firestore.Statics.Deleted.deletedTasks
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.repository.AuthRepo
import com.example.agileandroidalpha.firebase.repository.FirestoreRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class BacklogVM @Inject constructor(
    private val sprintUseCases: SprintUseCases,
    private val taskUseCases: TaskUseCases,
    private val repo: FirestoreRepository,
    private val auth: AuthRepo,
    private val room: RoomRepo
) : ViewModel() {
    private val sprintRef = Firebase.firestore.collection("sprints")
    private val subtaskRef = Firebase.firestore.collection("stories")
    private val storyRef = Firebase.firestore.collection("stories")
    private val userRef = Firebase.firestore.collection("users")
    private val user = Firebase.auth.currentUser

    private val _state = MutableStateFlow(BacklogState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(
            5000L,
            //500L,
        ),
        _state.value
    )

    private val _tasksState = MutableStateFlow(TasksState())
    val tasksState = _tasksState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(
            5000L,
            //500L,
        ),
        _tasksState.value
    )

    private val _sprintState = MutableStateFlow(SprintState())
    val sprintState = _sprintState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(
            5000L,
            //500L,
        ),
        _sprintState.value
    )

    private var _excluded = mutableStateOf(0L)
    val excluded: State<Long> = _excluded

    private var focusId: Long? = null

    private var currentId: Long? = null

    private var getBacklogJob: Job? = null

    init {
        getBacklog(SprintOrder.Default(OrderType.Ascending))
        viewModelScope.launch {
//            repo.loadFireStoreData { sps, fireUsers, loggedInUser, stores, subtasks, map, allT ->
//                val sprints = sps?.apply { sps.filter { s -> !s.started && !s.completed }.sortedBy { sp -> sp.backlogWt } }
//                _state.value = state.value.copy(
//                    sprints = sprints.orEmpty(),
//                    weights = sprints.orEmpty().map { s -> s.backlogWt }.sorted(),
//                    users = fireUsers.orEmpty(),
//                    stories = stores.orEmpty(),
//                    currentUser = loggedInUser,
//                    subtasks = subtasks.orEmpty(),
//                    selectedSprint = if(!sprints.isNullOrEmpty()) sprints[0] else null,
//                    selectedStory = if(!stores.isNullOrEmpty()) stores[0] else null,
//                    storyMap = map,
//                    sprintMap = sprints?.associateWith { s -> state.value.storyMap }.orEmpty(),
//                    tasks = allT
//                )
//            }
        reload()
        }

    }

    fun onEvent(event: BacklogEvent) {
        when(event) {
            is BacklogEvent.Order -> {
                if(state.value.sprintOrder::class == event.sprintOrder::class
                    && state.value.sprintOrder.type == event.sprintOrder.type
                ) { return }
                getBacklog(event.sprintOrder)
                viewModelScope.launch {
                    repo.fetchFirestoreChanges { sps, fireUsers, loggedInUser, stores, subtasks, map, allT, bin ->
                        val sprints =
                            sps.apply { sps.filter { s -> !s.started && !s.completed }.sortedBy { sp -> sp.backlogWt } }
                        _state.value = state.value.copy(
                            sprints = sprints,
                            users = fireUsers,
                            weights = sprints.map { s -> s.backlogWt }.sorted(),
                            stories = stores,
                            currentUser = loggedInUser,
                            subtasks = subtasks,
                            selectedSprint = if (sprints.isNotEmpty()) sprints[0] else null,
                            storyMap = map,
                            sprintMap = sprints.associateWith { s -> state.value.storyMap },
                            tasks = allT,
                            reload = true
                        )
                        _sprintState.value = sprintState.value.copy(

                        )
                    }
                }
            }
            is BacklogEvent.Archive -> {
                _sprintState.value = sprintState.value.copy(
                    isArchived = !event.spr.isArchived,
                    archiveDate = if (!event.spr.isArchived) System.currentTimeMillis()
                                else sprintState.value.archiveDate
                )
                event.spr.isArchived = !event.spr.isArchived
                val on = event.spr.isArchived
                viewModelScope.launch {
                    updateSprint(
                        id = event.spr.id!!,
                        stat = if (on) "Archived" else "Unarchived",
                        app = on,
                        done = on,
                        resolution = if (on) "Archived" else "Resolved"
                    ) {
                        viewModelScope.launch {
                            room.updateSprint(repo.toSprint(it))
                        }
                    }
                    //fullUpdateSprintData(event.spr.id!!)
                }
                reload()

            }
            is BacklogEvent.Delete -> {
                viewModelScope.launch {
                    deleteSprintAction(event.spr, event.spr.id!!) {
                        viewModelScope.launch {
                            val sp = repo.toSprint(it)
                            val tks = repo.toTaskList(event.sts)
                            val sbs = repo.toSubtaskList(event.sbs)
                            sprintUseCases.deleteSprint.full(sp, tks, sbs)
                            deletedRooms.clear()
                            deletedTasks.clear()
                            deletedSubs.clear()
                            deletedRooms.add(sp)
                            deletedTasks.addAll(tks)
                            deletedSubs.addAll(sbs)
                        }
                    }
                }
                reload()
            }
            is BacklogEvent.EditSprintStories -> {

            }
            is BacklogEvent.Restore -> {
                viewModelScope.launch {
                    (if (deletedRooms.isNotEmpty()) deletedRooms[0] else null)?.let {
                        sprintUseCases.cloneSprint.restore(
                            it,
                            deletedTasks,
                            deletedSubs
                        )
                    }
                    // Restore Firestore Data
                    repo.restore()
                }
            }
            is BacklogEvent.ToggleDone -> {
                viewModelScope.launch {
                    event.spr.completed = !event.spr.completed
                    updateSprint(
                        id = event.spr.id!!,
                        stat = if (event.spr.isApproved) "Done" else "In Progress",
                        done = event.spr.completed,
                        ) {
                        viewModelScope.launch {
                            room.updateSprint(repo.toSprint(it))
                        }
                    }
                }
                reload()
            }
            is BacklogEvent.MarkApproved -> {
                viewModelScope.launch {
                    event.spr.isApproved = !event.spr.isApproved
                    updateSprint(
                        id = event.spr.id!!,
                        stat = if (event.spr.isApproved) "Approved" else "Re-opened",
                        done = event.spr.isApproved,
                        rev = event.spr.isApproved,
                        app = event.spr.isApproved,
                        resolution = if (event.spr.isApproved) "Resolved" else "Unresolved"
                    ) {
                        viewModelScope.launch {
                            room.updateSprint(repo.toSprint(it))
                        }
                    }
                }
                reload()
            }
            is BacklogEvent.MarkReviewed -> {
                viewModelScope.launch {
                    event.spr.isReviewed = !event.spr.isReviewed
                    updateSprint(
                        id = event.spr.id!!,
                        stat = if (event.spr.isReviewed) "Reviewed" else "Fixing",
                        done = event.spr.isReviewed,
                        rev = event.spr.isReviewed,
                        resolution = if (event.spr.isReviewed) "Fixed in Release" else "Unresolved"
                    ) {
                        viewModelScope.launch {
                            room.updateSprint(repo.toSprint(it))
                        }
                    }
                }
                reload()
            }
            is BacklogEvent.Refresh -> {
                reload()
            }
            is BacklogEvent.SprintDialogEvent -> {
                if (event.spr?.id == null) return
                viewModelScope.launch {
                    updateState(event.spr.id)
                    if (event.wt != 0) {
                        val x = if (event.set) event.wt else event.spr.backlogWt + event.wt
//                        if (event.set) event.spr.backlogWt = event.wt
//                        else event.spr.backlogWt += event.wt
                        _sprintState.value = sprintState.value.copy(
                            backlogWt = x
                        )
                        viewModelScope.launch {
//                    updateSprint(
//                        id = event.spr.id!!,
//                        stat = if (on) "Archived" else "Unarchived",
//                        done = true,
//                        resolution = if (on) "Archived" else "Resolved"
//                    ) {
//                        viewModelScope.launch {
//                            room.updateSprint(repo.toSprint(it))
//                        }
//                    }
                            updateBacklogWt(event.spr.id, x)
                        }
                        reload()
//                        updateBatchSprint(event.spr.id!!, "backlogWt", x) {
//                            viewModelScope.launch {
//                                room.updateSprint(repo.toSprint(it))
//                            }
//                        }
                    } else {
                        event.hide?.let {
                            _sprintState.value = sprintState.value.copy(
                                isHidden = event.hide
                            )
                            viewModelScope.launch {
                                fullUpdateSprintData(event.spr.id)
                            }
//                            updateBatchSprint(event.spr.id!!, "isHidden", event.hide) {
//                                viewModelScope.launch {
//                                    room.updateSprint(repo.toSprint(it))
//                                }
//                            }
                        }
                        event.own?.let {
                            val u = state.value.currentUser
                            u?.let {
                                _sprintState.value = sprintState.value.copy(
                                    owner = u.name?: "No Name",
                                    ownerId = u.id,
                                    ownerUid = u.uid,
                                    ownerUri = u.photo,
                                    ownerUser = u
                                )
                                viewModelScope.launch {
                                    fullUpdateSprintData(event.spr.id)
                                }

//                                updateBatchSprint(event.spr.id!!, "owner", u.name?: "No Name",
//                                    "ownerId", u.id, "ownerUid", u.uid, "ownerUri", u.photo
//                                    ) {
//                                        viewModelScope.launch {
//                                            room.updateSprint(repo.toSprint(it))
//                                        }
//                                    }
                            }
                        }
                        event.manage?.let {
                            val u = state.value.currentUser
                            u?.let {
                                _sprintState.value = sprintState.value.copy(
                                    manager = u.name?: "No Name",
                                    managerId = u.id,
                                    managerUid = u.uid,
                                    managerUri = u.photo,
                                    managerUser = u
                                )
                                viewModelScope.launch {
                                    fullUpdateSprintData(event.spr.id)
                                }
                                updateBatchSprint(
                                    event.spr.id, "manager", u.name?: "No Name",
                                    "managerId", u.id, "managerUid", u.uid, "managerUri", u.photo
                                ) {
                                    viewModelScope.launch {
                                        room.updateSprint(repo.toSprint(it))
                                    }
                                }
                            }
                        }
                    }

                }
                reload()
            }
            is BacklogEvent.StoryDialogEvent -> {
                val sorted = state.value.sprints.sortedBy { sp -> sp.backlogWt }
                if (event.story == null || sorted.size < 2) return
                viewModelScope.launch {
                    if (event.wt != 0) {
                        val old = event.story.sid
                        val index = max(sorted.indexOf(sorted.find { s -> s.id == event.story.sid }), 0)
                        if (event.set) {
                            event.story.sid = if (event.wt < 0) sorted.first().sid
                                            else sorted.last().sid
                        } else {
                            val newIndex = max(index + event.wt, 0)
                            event.story.sid = sorted[newIndex].sid
                        }
                        val new = event.story.sid
                        if (old != new) {
                            _sprintState.value = sprintState.value.copy(
                                totalPoints = state.value.stories.filter { s -> s.sid == old!! }.sumOf { t -> t.points },
                                remPoints = state.value.stories.filter { s -> s.sid == old!! }
                                    . filter {t -> !t.done} .sumOf { t -> t.points }
                            )
                            fullUpdateSprintData(old!!)
                            _sprintState.value = sprintState.value.copy(
                                totalPoints = state.value.stories.filter { s -> s.sid == new!! }.sumOf { t -> t.points },
                                remPoints = state.value.stories.filter { s -> s.sid == new!! }
                                    . filter {t -> !t.done} .sumOf { t -> t.points }
                            )
                            fullUpdateSprintData(new!!)
                        }
                        updateStoryMode(event.story.id!!, "sid", event.story.sid!!) {
                            viewModelScope.launch {
                                room.updateTask(repo.toTask(it))
                            }
                        }

                    } else {
                        event.spr?.let {
                            event.story.sid = event.spr.sid
                            updateStoryMode(event.story.id!!, "sid", event.story.sid!!) {
                                viewModelScope.launch {
                                    room.updateTask(repo.toTask(it))
                                }
                            }
                        }
                    }
                }
                reload()
            }
            is BacklogEvent.RevokeApproval -> {
                viewModelScope.launch {

                    updateSprint(
                        id = event.spr.id!!,
                        stat = "Done",
                        done = true,
                        app = true,
                        resolution = "Resolved"
                    ) {
                        viewModelScope.launch {
                            room.updateSprint(repo.toSprint(it))
                        }
                    }
                }
                reload()
            }
            is BacklogEvent.RecalculateProgress -> {
                viewModelScope.launch {
                    viewModelScope.launch {
                        val x = event.progress.first >= event.progress.third &&
                                event.progress.second >= event.progress.third
                        updateSprint(
                            id = event.spr.id!!,
                            stat = "In Progress",
                            done = x,
                            progress = event.progress.first,
                            pct = event.progress.second,
                            rem = event.rem,
                            tot = event.tot
                        ) {
                            viewModelScope.launch {
                                room.updateSprint(repo.toSprint(it))
                            }
                        }
                    }
                }
                reload()
            }
            else -> {}
        }
    }

    private fun getBacklog(order: SprintOrder) {
        getBacklogJob?.cancel()
        getBacklogJob = sprintUseCases.loadSprints(order)
            .onEach { sp ->
                val sprints = sp.apply { sp.filter { s -> !s.sprint.info.started && !s.sprint.completed } }
                _state.value = state.value.copy(
                    sprintList = sprints,
                    sprintSnapshot = sprints.map { s -> SprintWithTasksAndSubtasks(s.sprint, s.tasks) },
                    sprintOrder = order,
                    roomUsers = sprints.associate { s ->
                        Pair(s.sprint, s.users)
                    },
                    taskMap = sprints.associate { s ->
                        Pair(s.sprint, s.tasks)
                    },
                    sprintDetail = sprints.associate { s ->
                        Pair(s.sprint, Pair(s.users, s.tasks))
                    }
                )
            }
            .launchIn(viewModelScope)
        reload()
    }

    suspend fun updateStoryMode(
        id: Long,
        field: String? = null,
        value: Long? = null,
        field2: String? = null,
        value2: String? = null,
        onResult: (Story) -> Unit = {}
    ) {
        val query = storyRef
            .whereEqualTo("id", id)
//            .whereEqualTo("DoD", story.DoD)
            .limit(1)
            .get()
            .await()
        if(query.documents.isNotEmpty()) {
            for(doc in query) {
                try {
                    field?.let { storyRef.document(doc.id).update(field, value).await() }
                    field2?.let {storyRef.document(doc.id).update(field2, value2).await() }
                    val result = storyRef.document(doc.id).get().await().toObject<Story>()
                    onResult.invoke(result!!)
                } catch(e: Exception) {
                    withContext(Dispatchers.Main) {
                        AddEditTaskViewModel.UIEvent.ShowSNB(
                            e.localizedMessage?: "Error locating story to update"
                        )
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                AddEditTaskViewModel.UIEvent.ShowSNB("No story found . . . canceling update")
            }
        }
    }

    suspend fun updateStoryFields(
        id: Long,
        field: String,
        value: Any,
        field2: String? = null,
        value2: Any? = null,
        field3: String? = null,
        value3: Any? = null,
        field4: String? = null,
        value4: Any? = null,
        onDone: (Story) -> Unit
    ) {
        if (id < 0) {
            Log.d("Edit Story", "ERROR - Story Saving failed! Story $id is invalid")
            return
        }
        repo.matchID("stories", id) { res ->
            if (res == null) {
                return@matchID
            }
            else { viewModelScope.launch {
                try {
                    Firebase.firestore.runBatch { batch ->
                        val docRef = sprintRef.document(res)
                        batch.update(docRef,
                            field, value,
                            field2?.let { field2 }, value2?.let { value2 },
                            field3?.let { field3 }, value3?.let { value3 },
                            field4?.let { field4 }, value4?.let { value4 },
                        )
                        batch.commit()
                    }.await()
                    val result = storyRef.document(res).get().await().toObject<Story>()
                    onDone.invoke(result!!)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.d("Firestore Repo", "Failed to update story data.")
                    }
                }
            } }
        }
    }

    suspend fun updateBacklogWt(
        id: Long,
        wt: Int,
        field: String = "backlogWt",
        onRes: (Sprint?, Long?) -> Unit = { _, _ -> }
    ) {
        if (id < 0) {
            Log.d("Edit Sprint", "ERROR - Sprint Saving failed! Sprint $id is invalid")
            return
        }
        repo.matchID("sprints", id) { dc ->
            if (dc == null) {
                return@matchID
            } else {
                viewModelScope.launch {
                    try {
                        Firebase.firestore.runTransaction { transaction ->
                            val sRef = sprintRef.document(dc)
                            transaction.update(
                                sRef, field, wt,
                            )
                            null
                        }.await()
                        val res = sprintRef.document(dc).get().await().toObject<Sprint>()
                        onRes.invoke(res, res?.id)
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.d("Firestore Repo", "Failed to save sprint data.")
                        }
                    }
                }
            }
        }
    }

    suspend fun updateBatchSprint(
        id: Long,
        field: String,
        value: Any,
        field2: String? = null,
        value2: Any? = null,
        field3: String? = null,
        value3: Any? = null,
        field4: String? = null,
        value4: Any? = null,
        onDone: (Sprint) -> Unit
    ) {
        if (id < 0) {
            Log.d("Edit Sprint", "ERROR - Sprint Saving failed! Sprint $id is invalid")
            return
        }
        repo.matchID("sprints", id) { res ->
            if (res == null) {
                return@matchID
            }
            else { viewModelScope.launch {
                try {
                    Firebase.firestore.runBatch { batch ->
                        val docRef = sprintRef.document(res)
                        batch.update(docRef,
                            field, value,
                            field2?.let { field2 }, value2?.let { value2 },
                            field3?.let { field3 }, value3?.let { value3 },
                            field4?.let { field4 }, value4?.let { value4 },
                        )
                        batch.commit()
                    }.await()
                    val result = sprintRef.document(res).get().await().toObject<Sprint>()
                    onDone.invoke(result!!)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.d("Firestore Repo", "Failed to update sprint data.")
                    }
                }
            } }
        }
    }

    suspend fun updateSprint(
        id: Long,
        stat: String,
        done: Boolean,
        resolution: String? = null,
        rev: Boolean? = null,
        app: Boolean? = null,
        arch: Boolean? = null,
        progress: Float? = null,
        pct: Float? = null,
        rem: Long? = null,
        tot: Long? = null,
        onDone: (Sprint) -> Unit
    ) {
        if (id < 0) {
            Log.d("Edit Sprint", "ERROR - Sprint Saving failed! Sprint $id is invalid")
            return
        }
        repo.matchID("sprints", id) { res ->
            if (res == null) {
                return@matchID
            }
            else { viewModelScope.launch {
                updateSprintHelper(
                    res, stat, done, resolution, rev, app, arch, progress, pct, rem, tot
                ) { sp, id ->
                    Statics.sprintsList[Statics.sprintsList.indexOf(Statics.sprintsList.find { s -> s.id == id })] = sp!!
                    onDone.invoke(sp)
                }
            } }
        }
    }

    suspend fun updateSprintHelper(
        id: String,
        stat: String,
        done: Boolean,
        resolution: String? = null,
        rev: Boolean? = null,
        app: Boolean? = null,
        arch: Boolean? = null,
        progress: Float? = null,
        pct: Float? = null,
        rem: Long? = null,
        tot: Long? = null,
        //priInc: Int,
        reviewerId: String? = null,
        approverId: String? = null,
        archivist: String? = null,
        onRes: (Sprint?, Long?) -> Unit
    )
    {
        try {
            Firebase.firestore.runTransaction { transaction ->
                val sRef = sprintRef.document(id)
                //val new = Priority.iToS(Priority.valueOf(sub["priority"] as String).ordinal + priInc)
                transaction.update(sRef, "completed", done,
                    "status", stat
                )
                if(done) {
                    transaction.update(sRef, "started", false, "paused", true,)
                }
                resolution?.let{
                    transaction.update(sRef,"resolution", resolution)
                }
                rev?.let {
                    val txt = if (rev) "Reviewed" else "Requires Re-Review"
                    transaction.update(sRef, "isReviewed", rev, "reviewStatus", txt,
                        "completed", rev)
                }
                app?.let {
                    val txt = if (app) "Approved" else "Requires Re-Approval"
                    transaction.update(sRef, "isApproved", app,
                        "approvalStatus", txt)
                    if (app)
                        transaction.update(sRef,  "expired", false,
                            )
                    else
                        transaction.update(sRef, "paused", false,
                            "isReviewed", false, "started", true,
                            "completed", false)
                }
                arch?.let {
                    transaction.update(sRef, "active", !arch, "isHidden", arch,
                        "isApproved", arch)
                }
                progress?.let {
                    transaction.update(sRef, "progress", progress)
                }
                pct?.let {
                    transaction.update(sRef, "progressPct", pct)
                }
//                if (reviewerId != null) {
//                    transaction.update(sRef, "assignee", ass.name,
//                        "assId", ass.id, "assUid", ass.uid, "assUri", ass.photo)
//                }
//                if (approverId != null) {
//                    transaction.update(sRef, "reporter", rep.name,
//                        "repId", rep.id, "repUid", rep.uid)
//                }
//                if (archivist != null) {
//                    transaction.update(sRef, "reporter", rep.name,
//                        "repId", rep.id, "repUid", rep.uid)
//                }
//                val res = transaction.get(sRef).toObject<SubTask>()
//                onRes.invoke(res, res?.id)
                null
            }.await()
            val res = sprintRef.document(id).get().await().toObject<Sprint>()
            onRes.invoke(res, res?.id)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Firestore Repo", "Failed to save sprint data.")
            }
        }
    }

    suspend fun deleteSprintAction(
        sp: Sprint,
        id: Long,
        onDone: (Sprint) -> Unit = {}
    ) {
        repo.deleteSprint(sp, id)
        onDone.invoke(sp)
    }

    fun reload() = run {
        viewModelScope.launch {
            repo.fetchFirestoreChanges { sps, fireUsers, loggedInUser, stores, subtasks, map, allT, bin ->
                val sprints =
                    sps.apply { sps.filter { s -> !s.started && !s.completed }.sortedBy { sp -> sp.backlogWt } }
                _state.value = state.value.copy(
                    sprints = sprints,
                    users = fireUsers,
                    stories = stores,
                    weights = sprints.map { s -> s.backlogWt }.sorted(),
                    currentUser = loggedInUser,
                    subtasks = subtasks,
                    selectedSprint = if (sprints.isNotEmpty()) sprints[0] else null,
                    storyMap = map,
                    sprintMap = sprints.associateWith { s -> state.value.storyMap },
                    tasks = allT,
                    reload = true
                )
            }
        }
    }


    suspend fun fullUpdateSprintData(
        id: Long,
        onDone: (Sprint?) -> Unit = {}
        ) {
        repo.getSprint(id) { res, dc ->
            if (res == null) {
                return@getSprint
            }
            viewModelScope.launch {
                currentId = res.id
                repo.bigUpdateSprintHelper(
                    dc!!,
                    updateSprintMap()
                ) { result, resultID ->
                    viewModelScope.launch {
                        result?.let { repo.toSprint(it) }?.let { room.updateSprint(it) }
                    }
                }
            }
        }
    }

    suspend fun updateState(
        id: Long,
    ) {
        repo.getSprint(id) { res, dc ->
            if (res == null) {
                return@getSprint
            }
            currentId = res.id
            _sprintState.value = sprintState.value.copy(
                auth = auth.user,
                user = Statics.currentUser,
                sprint = res,
                id = res.id,
                sid = res.id,
                uid = res.uid,
                uri = res.uri,
                origId = res.origId,
                title = res.title,
                desc = res.desc,
                duration = res.duration,
                countdown = res.countdown,
                elapsed = res.elapsed,
                startDate = res.startDate ?: 0,
                endDate = res.endDate ?: 0,
                meetingTime = res.meetingTime ?: "00:00",
                reviewTime = res.reviewTime ?: "00:00",
                freq = res.freq ?: 1,
                totalPoints = res.totalPoints,
                remPoints = res.remPoints,
                cloned = res.cloned,
                status = res.status,
                color = res.color,
                active = res.active ?: false,
                started = res.started,
                paused = res.paused,
                progress = res.progress,
                target = res.target,
                completed = res.completed,
                resolution = res.resolution,
                isApproved = res.isApproved,
                isArchived = res.isArchived,
                archiveDate = res.archiveDate,
                manual = res.manual,
                backlogWt = res.backlogWt,
                createdBy = res.createdBy,
                creatorID = res.creatorID,
                owner = res.owner,
                manager = res.manager,
                projectId = res.projectId,
                boardId = res.boardId,
                componentId = res.componentId,
                epicId = res.epicId,
                logo = res.logo,
                pic = res.pic,
                icon = res.icon,
                new = false,
                uList = res.associatedUsers,
                authorizedUsers = res.authorizedUsers,
                restrictions = res.restrictions,
                comments = res.comments,
                signatures = res.signatures,
                log = res.log,
                clones = res.clones,
                room = repo.toSprint(res),
                info = repo.toSprint(res).info,
            )
        }
    }


    fun updateSprintMap(
    ): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        if (sprintState.value.title.isNullOrBlank()) {
            map["title"] = sprintState.value.title ?: "Sprint ${sprintState.value.id} Title"
        }
        if (!sprintState.value.desc.isNullOrBlank()) {
            map["desc"] = sprintState.value.desc ?: "Sprint ${sprintState.value.id} Description"
        }
        map["active"] = sprintState.value.active
        map["backlogWt"] = sprintState.value.backlogWt
        map["started"] = sprintState.value.started
        map["elapsed"] = sprintState.value.elapsed
        map["paused"] = sprintState.value.paused
        map["cloned"] = sprintState.value.cloned
        map["new"] = false
        map["completed"] = sprintState.value.completed
        map["color"] = sprintState.value.color
        map["totalPoints"] = sprintState.value.totalPoints
        map["remPoints"] = sprintState.value.remPoints
        map["startDate"] = sprintState.value.startDate
        map["endDate"] = sprintState.value.endDate
        map["meetingTime"] = sprintState.value.meetingTime
        map["reviewTime"] = sprintState.value.reviewTime
        map["totalPoints"] = sprintState.value.totalPoints
        map["remPoints"] = sprintState.value.remPoints
        map["duration"] = sprintState.value.duration
        map["countdown"] = sprintState.value.countdown
        map["elapsed"] = sprintState.value.elapsed
        map["isApproved"] = sprintState.value.isApproved
        map["isArchived"] = sprintState.value.isArchived
        map["archiveDate"] = sprintState.value.archiveDate
        map["isReviewed"] = sprintState.value.isReviewed
        map["isHidden"] = sprintState.value.isHidden
        map["manager"] = sprintState.value.manager.orEmpty()
        map["modDate"] = System.currentTimeMillis()
        map["freq"] = sprintState.value.freq
        map["new"] = false
        map["backlogWt"] = sprintState.value.backlogWt
        map["target"] = sprintState.value.target

        if (!sprintState.value.status.isNullOrBlank()) {
            map["status"] = sprintState.value.status!!
            map["done"] =
                checkDoneSpecial(sprintState.value.status ?: "", sprintState.value.resolution ?: "Unresolved")
            map["resolution"] = sprintState.value.resolution ?: "Unresolved"
            map["approvalStatus"] = sprintState.value.backlogWt
        }
        sprintState.value.logo?.let { map["logo"] = it }
        sprintState.value.icon?.let { map["icon"] = it }
        sprintState.value.pic?.let { map["pic"] = it }
        sprintState.value.uidList?.let { map["uidList"] = it }
        sprintState.value.uList?.let { map["associatedUsers"] = it }
        sprintState.value.uris?.let { map["uris"] = it }
        sprintState.value.restrictions?.let { map["restrictions"] = it }
        sprintState.value.comments?.let { map["comments"] = it }
        sprintState.value.signatures?.let { map["signatures"] = it }
        sprintState.value.log?.let { map["log"] = it }
        sprintState.value.manual?.let { map["manual"] = it }
        sprintState.value.sid?.let { map["sid"] = it }
        sprintState.value.uid?.let { map["uid"] = it }
        sprintState.value.uri?.let { map["uri"] = it }
        sprintState.value.progress?.let { map["progress"] = it }
        sprintState.value.progressPct?.let { map["progressPct"] = it }
        sprintState.value.status?.let { map["status"] = it }
        sprintState.value.resolution?.let { map["resolution"] = it }
        sprintState.value.manual?.let { map["manual"] = it }
        sprintState.value.owner?.let { map["owner"] = it }
        sprintState.value.ownerId?.let { map["ownerId"] = it }
        sprintState.value.ownerUid?.let { map["ownerUid"] = it }
        sprintState.value.ownerUri?.let { map["ownerUri"] = it }
        sprintState.value.manager?.let { map["manager"] = it }
        sprintState.value.managerId?.let { map["managerId"] = it }
        sprintState.value.managerUid?.let { map["managerUid"] = it }
        sprintState.value.managerUri?.let { map["managerUri"] = it }
        sprintState.value.projectId?.let { map["projectId"] = it }

        sprintState.value.boardId?.let { map["boardId"] = it }
        sprintState.value.uid?.let { map["uid"] = it }
        sprintState.value.createdBy?.let { map["createdBy"] = it }
        sprintState.value.creatorID?.let { map["creatorId"] = it }
        sprintState.value.id?.let { map["componentId"] = it }
        sprintState.value.clones?.let { map["clones"] = it }
        sprintState.value.authorizedUsers?.let { map["authorizedUsers"] = it }
        map["modDate"] = System.currentTimeMillis()
        map["accDate"] = System.currentTimeMillis()

        when (sprintState.value.status?.let { checkStatus(it) }) {
            "AR" -> {
                map["reviewStatus"] = "Pending"
            }

            "REV" -> {
                map["reviewStatus"] = "Passed"
                map["isReviewed"] = true
            }

            "AA" -> {
                map["reviewStatus"] = "Passed"
                map["approvalStatus"] = "Pending"
                map["isReviewed"] = true
            }

            "DONE" -> {
                map["reviewStatus"] = "Completed"
                map["approvalStatus"] = "Passed"
                map["projectStatus"] = "Passed"
                map["isReviewed"] = true
                map["isApproved"] = true
            }

            "ARCH" -> {
                map["reviewStatus"] = "Completed"
                map["approvalStatus"] = "Completed"
                map["projectStatus"] = "Completed"
                map["isReviewed"] = true
                map["isApproved"] = true
                map["isArchived"] = true
            }

            "ER" -> {
                map["reviewStatus"] = "Can't"
                map["approvalStatus"] = "Can't"
                map["projectStatus"] = "Can't"
                map["isReviewed"] = false
                map["isApproved"] = false
                map["isArchived"] = false
            }

            "Normal" -> {
                map["reviewStatus"] = "Not Ready"
                map["approvalStatus"] = "Not Ready"
                map["projectStatus"] = "Normal"
            }
        }
        return map
    }

}