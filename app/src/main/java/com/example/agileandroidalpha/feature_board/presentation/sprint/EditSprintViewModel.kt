@file:OptIn(ExperimentalTime::class)

package com.example.agileandroidalpha.feature_board.presentation.sprint

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.feature_board.domain.model.SprintInfo
import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import com.example.agileandroidalpha.feature_board.domain.use_case.SprintUseCases
import com.example.agileandroidalpha.feature_board.presentation.sprint.components.SprintTextFieldState
import com.example.agileandroidalpha.firebase.firestore.Statics
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.currentUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.util.Date
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@ExperimentalTime
@HiltViewModel
class EditSprintViewModel @Inject constructor(
    private val sprintUseCases: SprintUseCases,
    private val auth: FirebaseAuth,
    private val repo: FirestoreRepository,
    private val room: RoomRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(SprintState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(
            5000L,
            //500L,
        ),
        _state.value
    )

    private val _title = mutableStateOf(SprintTextFieldState(
        hint = "Enter a Sprint Title here..."
    ))
    val title: State<SprintTextFieldState> = _title

    private val _desc = mutableStateOf(SprintTextFieldState(
        hint = "Enter sprint description here..."
    ))
    val desc: State<SprintTextFieldState> = _title

    private val _start = mutableStateOf(SprintTextFieldState(
        date = Date()
    ))
    val start: State<SprintTextFieldState> = _start

    private val _end = mutableStateOf(SprintTextFieldState(

    ))
    val end: State<SprintTextFieldState> = _end

    private val _length = mutableStateOf(SprintTextFieldState(
        num2 = 21.0
    ))
    val length: State<SprintTextFieldState> = _length

    private val _owner = mutableStateOf(SprintTextFieldState(
        text = "Owner"
    ))
    val owner: State<SprintTextFieldState> = _owner

    private val _manager = mutableStateOf(SprintTextFieldState(
        text = "Manager"
    ))
    val manager: State<SprintTextFieldState> = _manager

    private val _startDate = mutableStateOf(Date())
    private val _endDate = mutableStateOf((Date()))
    val dates: Pair<State<Date>, State<Date>> = Pair(_startDate, _endDate)

    private var getSprintJob: Job? = null

    private var currentId: Long? = null

    init {
        savedStateHandle.get<Long>("id")?.let { id ->
            if (id != -1L) {
                viewModelScope.launch {
                    repo.getSprint(id) { res, dc ->
                        if (res == null) {
                            return@getSprint
                        }
                        currentId = res.id
                        _state.value = state.value.copy(
                            auth = auth.currentUser,
                            user = currentUser,
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
                            startDate = res.startDate?:0,
                            endDate = res.endDate?:0,
                            meetingTime = res.meetingTime?: "00:00",
                            reviewTime = res.reviewTime?: "00:00",
                            freq = res.freq?:1,
                            totalPoints = res.totalPoints,
                            remPoints = res.remPoints,
                            cloned = res.cloned,
                            status = res.status,
                            color = res.color,
                            active = res.active?: false,
                            started = res.started,
                            paused = res.paused,
                            progress = res.progress,
                            target = res.target,
                            completed = res.completed,
                            resolution = res.resolution,
                            isApproved = res.isApproved,
                            isArchived = res.isArchived,
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

                    repo.getAllUsers { users, user ->
                        _state.value = state.value.copy(
                            users = users.orEmpty(),
                            user = user
                        )
                    }
                    sprintUseCases.getSprint.async(id)?.also { s ->
//                        currentId = s.sprint.sprintId
                        _state.value = state.value.copy(
//                            id = s.sprint.sprintId,
                            tasks = s.tasks,
                            roomUsers = s.users,
                            boardId = s.sprint.boardId,
                            info = s.sprint.info,
                            room = s.sprint,


                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: SprintEvent) {
        when(event) {
            is SprintEvent.SaveComment -> {
                val y = run { state.value.comments?.let {
                    state.value.comments as MutableList
                }?: mutableListOf() }
                y.add(event.str)
                val z = run { state.value.signatures?.let {
                    state.value.signatures as MutableList
                }?: mutableListOf() }
                if (event.user == null) z.add("Anonymous User")
                else z.add(event.user.name?: "Unnamed User")
                _state.value = state.value.copy(
                    comments = y,
                    signatures = z
                )
                viewModelScope.launch {
                    updateSprint(
                        state.value.id ?: currentId ?: state.value.sprint?.id ?: -1)
                    { res ->
                        viewModelScope.launch {
                            room.updateSprint(repo.toSprint(res))
                        }
                    }
                }
                refresh()
            }
            is SprintEvent.ChangeColor -> {
                _state.value.color = event.color
            }
            is SprintEvent.ChangeDates -> {
                _state.value = state.value.copy(
                    startDate = event.start,
                    endDate = event.end,
                    duration = event.dur,
                    countdown = event.cd,
                    elapsed = max(0, event.dur - event.cd)
                )
                refresh()
            }
            is SprintEvent.ChangeDesc -> {
                _state.value = state.value.copy(
                    desc = event.desc
                )
                refresh()
            }
            is SprintEvent.ChangeManager -> {
                _state.value = state.value.copy(
                    managerId = event.id,
                    managerUid = event.uid,
                    manager = event.name,
                    managerUri = event.photo.orEmpty(),
                    managerUser = event.user,
                    users = if (event.user != null && state.value.users.find { u -> u.id == event.user.id } == null)
                        state.value.users.plus(event.user) else state.value.users
                )
                refresh()
            }
            is SprintEvent.ChangeOwner -> {
                _state.value = state.value.copy(
                    ownerId = event.id,
                    ownerUid = event.uid,
                    owner = event.name,
                    ownerUri = event.photo.orEmpty(),
                    ownerUser = event.user,
                    users = if (event.user != null && state.value.users.find { u -> u.id == event.user.id } == null)
                        state.value.users.plus(event.user) else state.value.users
                )
                refresh()
            }
            is SprintEvent.ChangeResolution -> {
                _state.value = state.value.copy(
                    resolution = event.res
                )
                refresh()
            }
            is SprintEvent.ChangeStatus -> {
                _state.value = state.value.copy(
                    status = event.sta
                )
                refresh()
            }
            is SprintEvent.ChangeTitle -> {
                _state.value = state.value.copy(
                    title = event.title
                )
                refresh()
            }
            is SprintEvent.ChangeTarget -> {
                _state.value = state.value.copy(
                    target = event.tgt
                )
                refresh()
            }
            is SprintEvent.EditComment -> {
                _state.value = state.value.copy(
                    newComment = event.str
                )
                refresh()
            }
            is SprintEvent.SaveSprint -> {
                viewModelScope.launch {
                    updateState()
                    updateSprint(
                        state.value.id ?: currentId ?: state.value.sprint?.id ?: -1
                    ) { res ->
                        viewModelScope.launch {
                            room.updateSprint(repo.toSprint(res))
                        }
                    }
                }
                refresh()
            }
            is SprintEvent.SetMeetingTime -> {
                var hr = event.hr.toString()
                var min = event.min.toString()
                if (hr.length < 2) hr = "0$hr"
                if (min.length < 2) min = "0$min"
                _state.value = state.value.copy(
                    meetingTime = "$hr:$min"
                )
            }
            is SprintEvent.SetReviewTime -> {
                var hr = event.hr.toString()
                var min = event.min.toString()
                if (hr.length < 2) hr = "0$hr"
                if (min.length < 2) min = "0$min"
                _state.value = state.value.copy(
                    reviewTime = "$hr:$min"
                )
            }

            else -> {}
        }
    }

    fun refresh(){
        _state.value = state.value.copy(
            updateFlag = true
        )
    }

    fun updateState() {
        val res = state.value
        _state.value = state.value.copy(
            sprint = Sprint(
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
                startDate = res.startDate,
                endDate = res.endDate,
                totalPoints = res.totalPoints,
                remPoints = res.remPoints,
                cloned = res.cloned,
                status = res.status,
                color = res.color,
                started = res.started,
                paused = res.paused,
                progress = res.progress,
                progressPct = res.progressPct,
                target = res.target,
                completed = res.completed,
                resolution = res.resolution,
                isHidden = res.isHidden,
                isReviewed = res.isReviewed,
                isApproved = res.isApproved,
                isArchived = res.isArchived,
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
                associatedUsers = res.uList,
                authorizedUsers = res.authorizedUsers,
                restrictions = res.restrictions,
                comments = res.comments,
                signatures = res.signatures,
                log = res.log,
                clones = res.clones,
            ),
            room = SprintRoom(
                sprintId = res.id,
                uid = res.uid,
                uri = res.uri,
                origId = res.origId,
                title = res.title.orEmpty(),
                desc = res.desc.orEmpty(),
                color = res.color,
                target = res.target,
                completed = res.completed,
                info = SprintInfo(
                    duration = Period.ofDays(res.duration),
                    countdown = res.countdown,
                    elapsed = res.elapsed,
                    startDate = res.startDate.let { LocalDate.ofEpochDay(msToDays(it)) },
                    endDate = res.endDate.let { LocalDate.ofEpochDay(msToDays(it)) },
                    totalPoints = res.totalPoints,
                    remPoints = res.remPoints,
                    cloned = res.cloned,
                    status = res.status,
                    started = res.started,
                    paused = res.paused,
                    progress = res.progress,
                    progressPct = res.progressPct,
                    resolution = res.resolution,
                    isReviewed = res.isReviewed,
                    isApproved = res.isApproved,
                    isArchived = res.isArchived,
                    manual = res.manual,
                    createdBy = res.createdBy,
                    creatorID = res.creatorID,
                    owner = res.owner,
                    manager = res.manager,
                    new = false,
                    backlogWt = res.backlogWt
                )
            )
        )
    }

    suspend fun updateSprint(
        id: Long,
        map: Map<String, Any> = updateSprintMap(),
        onDone: (Sprint) -> Unit
    ) {
        if (id < 0) {
            Log.d("Edit Sprint", "ERROR - Sprint Saving failed! Sprint ${state.value.id} is invalid")
            return
        }
        repo.matchID("sprints", id) { res ->
            if (res == null) {
                return@matchID
            }
            else { viewModelScope.launch {
                repo.bigUpdateSprintHelper(res, map) { sp, id ->
                    Statics.sprintsList[Statics.sprintsList.indexOf(Statics.sprintsList.find { s -> s.id == id })] = sp!!
                    onDone.invoke(sp)
                }
            } }
        }
    }

    fun updateSprintMap(
    ): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
//        val spr = state.value
        val spr = state.value.sprint!!

        if (!spr.title.isNullOrBlank()) {
            map["title"] = spr.title?: "Sprint ${spr.id} Title"
        }
        if (!spr.desc.isNullOrBlank()) {
            map["desc"] = spr.desc?: "Sprint ${spr.id} Description"
        }
        map["active"] = spr.active != false
        map["backlogWt"] = spr.backlogWt
        map["started"] = spr.started
        map["elapsed"] = spr.elapsed
        map["paused"] = spr.paused
        map["cloned"] = spr.cloned
        map["new"] = false
        map["completed"] = spr.completed
        map["color"] = spr.color
        map["totalPoints"] = spr.totalPoints
        map["remPoints"] = spr.remPoints
        map["startDate"] = spr.startDate?: state.value.startDate
        map["endDate"] = spr.endDate?: state.value.endDate
        map["meetingTime"] = spr.meetingTime?: state.value.meetingTime
        map["reviewTime"] = spr.reviewTime?: state.value.reviewTime
        map["totalPoints"] = spr.totalPoints
        map["remPoints"] = spr.remPoints
        map["duration"] = spr.duration
        map["countdown"] = spr.countdown
        map["elapsed"] = spr.elapsed
        map["isApproved"] = spr.isApproved
        map["isArchived"] = spr.isArchived
        map["isReviewed"] = spr.isReviewed
        map["isHidden"] = spr.isHidden
        map["manager"] = spr.manager.orEmpty()
        map["modDate"] = System.currentTimeMillis()
        map["freq"] = spr.freq?: state.value.freq
        map["new"] = false
        map["backlogWt"] = spr.backlogWt
        map["target"] = spr.target

        if (!spr.status.isNullOrBlank()) {
            map["status"] = spr.status!!
            map["done"] = checkDoneSpecial(spr.status?:"", spr.resolution?: "Unresolved")
            map["resolution"] = spr.resolution?: "Unresolved"
            map["approvalStatus"] = spr.backlogWt
        }
        spr.logo?.let { map["logo"] = it }
        spr.icon?.let { map["icon"] = it }
        spr.pic?.let { map["pic"] = it }
        spr.uidList?.let { map["uidList"] = it }
        //spr.uList
        spr.associatedUsers?.let { map["associatedUsers"] = it }
        spr.uris?.let { map["uris"] = it }
        spr.restrictions?.let { map["restrictions"] = it }
        spr.comments?.let { map["comments"] = it }
        spr.signatures?.let { map["signatures"] = it }
        spr.log?.let { map["log"] = it }
        spr.manual?.let { map["manual"] = it }
        spr.sid?.let { map["sid"] = it }
        spr.uid?.let { map["uid"] = it }
        spr.uri?.let { map["uri"] = it }
        spr.progress?.let { map["progress"] = it }
        spr.progressPct?.let { map["progressPct"] = it }
        spr.status?.let { map["status"] = it }
        spr.resolution?.let { map["resolution"] = it }
        spr.manual?.let {map["manual"] = it }
        spr.owner?.let { map["owner"] = it }
        spr.ownerId?.let { map["ownerId"] = it }
        spr.ownerUid?.let { map["ownerUid"] = it }
        spr.ownerUri?.let { map["ownerUri"] = it }
        spr.manager?.let { map["manager"] = it }
        spr.managerId?.let { map["managerId"] = it }
        spr.managerUid?.let { map["managerUid"] = it }
        spr.managerUri?.let { map["managerUri"] = it }
        spr.projectId?.let { map["projectId"] = it}

        spr.boardId?.let { map["boardId"] = it }
        spr.uid?.let { map["uid"] = it }
        spr.createdBy?.let { map["createdBy"] = it }
        spr.creatorID?.let { map["creatorId"] = it }
        spr.id?.let { map["componentId"] = it }
        spr.clones?.let { map["clones"] = it }
        spr.authorizedUsers?.let { map["authorizedUsers"] = it }
        map["modDate"] = System.currentTimeMillis()
        map["accDate"] = System.currentTimeMillis()

        when(spr.status?.let { checkStatus(it) }) {
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

fun checkDoneSpecial(stat: String?, res: String): Boolean {
    if (stat.isNullOrBlank()) return false
    return stat in listOf("Awaiting Review", "Reviewing", "Reviewed", "Awaiting Approval",
        "Pending Review", "Pending Approval", "Done", "Skipped", "Approved", "Closed", "Archived")
            || res in listOf("Resolved with Workaround", "Patched", "Requires Special Patch",
        "Fixed in Newer Version", "Cancelled", "Fully Completed", "Fully Archived", "Classified")
}

fun checkStatus(str: String): String {
    if (str.contains("Not")) return "NO"
    if (str in listOf("Awaiting", "Pending", "Reviewing"))
        return if (str.contains("Review")) "AR" else "AA"
    if (str.contains("Reviewed")) return "REV"
    if (str in listOf("Done", "Approved")) return "DONE"
    if (str in listOf("Closed", "Archived")) return "ARCH"
    if (str in listOf("Skipped", "Cancelled, Stalled")) return "ER"
    return "Normal"
}

@ExperimentalTime
fun msToDays(tym: Long): Long {
    return Duration.convert(tym + 0.0, DurationUnit.MILLISECONDS, DurationUnit.DAYS).roundToLong()
}