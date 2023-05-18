@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.example.agileandroidalpha.feature_board.presentation.edit_subtask

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.feature_board.domain.model.IssueType
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.AddEditTaskEvent
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.EditTaskState
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.TaskTextFieldState
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.components.TaskSubtaskData
import com.example.agileandroidalpha.firebase.repository.AuthRepo
import com.example.agileandroidalpha.firebase.repository.FirestoreRepository
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask
import com.example.agileandroidalpha.firebase.login.UserData.DATA.offlineMode
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

@HiltViewModel
class EditSubTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepo: AuthRepo,
    private val room: RoomRepo,
    private val repo: FirestoreRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(EditTaskState())
    val state = _state

    private val sprintRef = Firebase.firestore.collection("sprints")
    private val subtaskRef = Firebase.firestore.collection("stories")
    private val storyRef = Firebase.firestore.collection("stories")
    private val userRef = Firebase.firestore.collection("users")
    val user = Firebase.auth.currentUser
    private val tag = "AddEditTaskVM"

    private var _availUsers = mutableStateListOf<FireUser>()
    var availUsers: List<FireUser> = _availUsers

    private var _availSprints = mutableStateListOf<Sprint>()
    var availSprints: List<Sprint> = _availSprints

    private val _sprints = MutableStateFlow<SnapshotStateList<Sprint>>(mutableStateListOf())
    val sprints: StateFlow<SnapshotStateList<Sprint>> = _sprints

    private val _subTitle = mutableStateOf(
        TaskTextFieldState(
        hint = "Enter title..."
    )
    )
    val subTitle: State<TaskTextFieldState> = _subTitle

    private val _subBody = mutableStateOf(
        TaskTextFieldState(
        hint = "Enter some content..."
    )
    )
    val subBody: State<TaskTextFieldState> = _subBody

    private val _subDesc = mutableStateOf(
        TaskTextFieldState(
            hint = "Enter a description..."
        )
    )
    val subDesc: State<TaskTextFieldState> = _subDesc

    private val _subDoD = mutableStateOf(
        TaskTextFieldState(
        hint = "Enter some content..."
    )
    )
    val subDoD: State<TaskTextFieldState> = _subDoD

    private val _subAss = mutableStateOf(
        TaskTextFieldState(
        hint = "Assign user to this subtask..."
    )
    )
    val subAss: State<TaskTextFieldState>  = _subAss

    private val _subRep = mutableStateOf(
        TaskTextFieldState(
        hint = "Designate reporter for this subtask..."
    )
    )
    val subRep: State<TaskTextFieldState>  = _subRep

    private val _subPoints = mutableStateOf(generateRndPoints())
    val subPoints: State<Long> = _subPoints

    private val _subPri = mutableStateOf(
        TaskTextFieldState(
        text = Task.priorities.random().name,
    )
    )
    val subPri: State<TaskTextFieldState> = _subPri

    private val _subColor = mutableStateOf(Task.colors.random().toArgb())
    val subColor: State<Int> = _subColor

    private val _subStatus = mutableStateOf("TO DO")
    val subStatus: State<String> = _subStatus

    private val _subDone = mutableStateOf(false)
    val subDone: State<Boolean> = _subDone

    private val _subRes = mutableStateOf("")
    val subRes: State<String> = _subRes

    private val _subType = mutableStateOf("")
    val subType: State<String> = _subType

    private val _subLabels = mutableStateListOf<String>()
    val subLabels: List<String> = _subLabels

    private val _subs = mutableStateListOf<SubTask>()
    val subs: MutableList<SubTask> = _subs

    private var _subSID = mutableStateOf(1L)
    var subSID: State<Long> = _subSID

    private val _subStates: MutableState<TaskSubtaskData> = mutableStateOf(
        TaskSubtaskData(

        )
    )
    val subStates: MutableState<TaskSubtaskData> = _subStates

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentId: Long? = null

    private var taskId: Long? = null

    var me = MutableStateFlow(FireUser())
    private set

    var users = mutableStateListOf<FireUser>()

    private var sprintId: Long? = null

    init {
        viewModelScope.launch {
//            getUserList {
//                availUsers = it
//                UIEvent.ToastMsg("Number of users = ${availUsers.size}")
//            }
            repo.getAllUsers { fireUsers, user ->
                user?.let { me.value = user }
                fireUsers?.let {
                    users.addAll(fireUsers)
                    state.value.users = fireUsers
                }
                UIEvent.ShowSNB("Number of users = ${users.size}")
            }
            retrieveSprints()

            savedStateHandle.get<Long>("sid")?.let { sid ->
                if (sid != -1L) {
                    sprintId = max(1L, sid)
                    state.value.sid = sid
                }
            }
            savedStateHandle.get<Long>("tid")?.let { tid ->
                if (tid != -1L) {
                    taskId = max(1L, tid)
                    _state.value = state.value.copy(
                        taskId = taskId!!
                    )
                }

            }
            savedStateHandle.get<Long>("id")?.let { id ->
                if (id != -1L) {
                    viewModelScope.launch {
                        repo.getSubTask(id) { sub ->
                            _state.value = state.value.copy(
                                title = sub!!.title,
                                desc = sub.desc,
                                body = sub.body,
                                dod = sub.dod.orEmpty(),
                                assignee = sub.assignee?: "None",
                                reporter = sub.reporter?: "None",
                                assId = sub.assId?: -1,
                                assUid = sub.assUid,
                                assUri = sub.assUri.orEmpty(),
                                repId = sub.repId?: -1,
                                repUid = sub.repUid,
                                repUri = sub.repUri.orEmpty(),
                                points = sub.points,
                                priority = sub.priority,
                                color = sub.color,
                                logo = sub.logo,
                                status = sub.status,
                                done = sub.done,
                                type = sub.type,
                                sid = sub.sid ?: subSID.value,
                                taskId = sub.storyId?: 1,
                                createdBy = sub.uid.orEmpty(),
                                creatorID = sub.creatorID?: -1,
                                uri = sub.uri,
                                uid = sub.uid,
                                id = sub.id!!
                            )

                        }
//                        taskUseCases.getTask(id)?.also { task ->
//                            currentId = task.task.taskId
//                            sprintId = task.task.SID
//                            _taskTitle.value = taskTitle.value.copy(
//                                text = task.task.title,
//                                isHintVisible = false
//                            )
//                            _taskBody.value = taskBody.value.copy(
//                                text = task.task.content,
//                                isHintVisible = false
//                            )
//                            _taskColor.value = task.task.color
//                            _taskPoints.value = task.task.points
//                            _taskPri.value = taskPri.value.copy(
//                                text = task.task.priority.name,
//                                num = task.task.priority.ordinal,
//                                isHintVisible = false
//                            )
//                            _taskAss.value = taskAss.value.copy(
//                                text = task.task.assignee?: "None",
//                                uid = task.task.assId,
//                                id = task.task.UID,
//                                isHintVisible = false
//                            )
//                            _taskRep.value = taskRep.value.copy(
//                                text = task.task.reporter?: "None",
//                                uid = task.task.repId,
//                                id = task.task.UID2,
//                                isHintVisible = false
//                            )
//                            _taskStatus.value = taskStatus.value
//                            _taskDone.value = taskDone.value
//                            _taskSubs = task.subtasks.map{ s->s.subtask } as MutableList<Subtask>
//                            _taskSID.value = task.task.SID!!
//                            _subStates.value = subStates.value.copy(
//                                id = currentId,
//                                task = task.task,
//                                subtasks = task.subtasks.map{ s->s.subtask }
//                            )
//                        }
//                        _state.value = state.value.copy(
//                            id = currentId?: 1,
//                            taskId = taskId?: 1,
//                            sid = sprintId?: 1,
//                            title = subTitle.value.text,
//                            body = subBody.value.text,
//                            desc = subDesc.value.text,
//                            DoD = subDoD.value.text,
//                            timestamp = System.currentTimeMillis(),
//                            points = subPoints.value,
//                            priority = subPri.value.text,
//                            color = subColor.value,
//                            status = subStatus.value,
//                            done = subDone.value,
//                            type = subType.value,
//                            assignee = subAss.value.text,
//                            reporter = subRep.value.text,
//                            assId = subAss.value.id?: -1,
//                            assUid = subAss.value.uid ?: "",
//                            repId = subAss.value.id?: -1,
//                            repUid = subAss.value.uid ?: "",
//                            subtasks = subs.map { sub -> sub.id}
//                        )
                    }
                }
            }
        }


    }



    fun onEvent(event: AddEditTaskEvent) {
        when(event) {
            is AddEditTaskEvent.SaveSubTask -> {
                viewModelScope.launch {
                    updateState(event.id, event.tid, event.sid)
                    val sb = state.value.currentSub
                    val sT = state.value.subItem
                    if (sb == null || sT == null) throw NullPointerException()
                    room.updateSubtask(sb)
                    if ( !offlineMode ) {
                    updateSubTask(event.id)
                    }

                }
            }
            is AddEditTaskEvent.EnteredTitle -> {
//                _state.value.title = event.value
                _state.value = state.value.copy(
                    title = event.value
                )
            }
            is AddEditTaskEvent.ChangeTitleFocus -> {
            }
            is AddEditTaskEvent.EnteredDesc -> {
                //_state.value.desc = event.value
                _state.value = state.value.copy(
                    desc = event.value
                )
            }
            is AddEditTaskEvent.ChangeDescFocus -> {
            }
            is AddEditTaskEvent.EnteredBody -> {
//                _state.value.body = event.value
                _state.value = state.value.copy(
                    body = event.value
                )
            }
            is AddEditTaskEvent.ChangeBodyFocus -> {
            }
            is AddEditTaskEvent.EnteredDoD -> {
                _state.value.dod = event.value
                _state.value = state.value.copy(
                    dod = event.value
                )
            }
            is AddEditTaskEvent.ChangeDoDFocus -> {
            }
            is AddEditTaskEvent.ChangePoints -> {
                _state.value.points = event.value
            }
            is AddEditTaskEvent.ChangePri -> {
                _state.value.priority = event.pri
            }
            is AddEditTaskEvent.ChangeType -> {
                _state.value.type = event.type
            }
            is AddEditTaskEvent.ChangeColor -> {
                _state.value.color = event.color
            }
            is AddEditTaskEvent.ChangeAssignee -> {
                _state.value = state.value.copy(
                    assId = event.id,
                    assUid = event.uid,
                    assignee = event.name,
                    assUri = event.photo.orEmpty(),
                    assUser = event.user
                )
            }
            is AddEditTaskEvent.ChangeStatus -> {
                _state.value.status = event.sta
                _state.value.done = (checkDone(event.sta))
            }
            is AddEditTaskEvent.ChangeAssFocus -> {
            }
            is AddEditTaskEvent.ChangeReporter -> {
                _state.value = state.value.copy(
                    repId = event.id,
                    repUid = event.uid,
                    reporter = event.name,
                    repUri = event.photo.orEmpty(),
                    repUser = event.user
                )
            }
            is AddEditTaskEvent.ChangeResolution -> {
                _state.value = state.value.copy(
                    resolution = event.res
                )
            }
            is AddEditTaskEvent.ChangeSprint -> {
                state.value.sid = event.id
                sprintId = event.id
            }
            else -> {}
        }
    }

    fun updateState(id: Long, tid: Long, sid: Long) {
        _state.value = state.value.copy(
            currentSub = Subtask(
                title = state.value.title,
                desc = state.value.desc,
                content = state.value.body,
                dod = state.value.dod,
                assignee = state.value.assignee,
                reporter = state.value.reporter,
                assigneeId = state.value.assId,
                assUid = state.value.assUid,
                assUri = state.value.assUri,
                reporterId = state.value.repId,
                repUid = state.value.repUid,
                repUri = state.value.repUri,
                points = state.value.points,
                priority = if(state.value.priority.isNotBlank()) Priority.valueOf(state.value.priority)
                            else Priority.Lowest,
                color = state.value.color,
                status = state.value.status,
                done = state.value.done,
                type = if(state.value.type.isNotBlank()) IssueType.valueOf(state.value.type)
                        else IssueType.Subtask,
                sprintId = sid,
                parentId = tid,
                id = id,
                subId = id
            ),
            subItem = SubTask(
                title = state.value.title,
                desc = state.value.desc,
                body = state.value.body,
                dod = state.value.dod,
                assignee = state.value.assignee,
                reporter = state.value.reporter,
                assId = state.value.assId,
                assUid = state.value.assUid,
                assUri = state.value.assUri,
                repId = state.value.repId,
                repUid = state.value.repUid,
                repUri = state.value.repUri,
                points = state.value.points,
                priority = state.value.priority,
                color = state.value.color,
                status = state.value.status,
                done = state.value.done,
                type = state.value.type,
                sid = sid,
                storyId = tid,
                subId = id,
                id = id,
            ),
            sid = sid,
            taskId = tid,
            id = id
        )
    }

    suspend fun updateSubTask(
        id: Long,
        map: Map<String, Any> = getUpdatedSubTask(),
        onDone: (SubTask) -> Unit = {}
    ) {
        repo.matchID("subtasks", id) { res ->
            if (res == null) {
                Log.d("Edit Subtask", "No subtask with id = $id was found")
                return@matchID
            }
            else { viewModelScope.launch {
                repo.bigUpdateSubtaskHelper(res, map) { sub, id ->
                    subs[subs.indexOf(subs.find { s -> s.id == id })] = sub!!
                    _state.value = state.value.copy(
                        subItem = sub,
                        title = sub.title,
                        body = sub.body,
                        desc = sub.desc,
                        status = sub.status,
                        done = sub.done,
                        priority = sub.priority,
                        points = sub.points,
                        type = sub.type,
                        logo = sub.logo,
                        assignee = sub.assignee?: "",
                        reporter = sub.reporter?: "",
                        assId = sub.assId?: -1,
                        assUid = sub.repUid,
                        assUri = sub.assUri.orEmpty(),
                        repId = sub.repId?: -1,
                        repUid = sub.repUid,
                        repUri = sub.repUri.orEmpty(),
                        resolution = sub.resolution?: "",
                        labels = sub.labels?: emptyList(),
                        sid = sub.sid?: 1,
                        taskId = sub.storyId?: 1,

                        )
                    onDone.invoke(sub)
                }
            } }
        }
    }

    suspend fun updateSubTask(
        id: Long,
        title: String,
        body: String,
        pri: String,
        stat: String,
        //priInc: Int,
        ass: FireUser? = null,
        rep: FireUser? = null,
        onDone: (SubTask) -> Unit
    ) {
        repo.matchID("subtasks", id) { res ->
            if (res == null) {
                return@matchID
            }
            else { viewModelScope.launch {
                repo.updateSubtaskHelper(res, title, body, pri, stat, ass, rep) { sub, id ->
                    //subs[subs.indexOf(subs.find { s -> s.id == id })] = sub!!
                    _state.value = state.value.copy(
                        id = id!!,
                        subItem = sub!!,
                        title = sub.title,
                        body = sub.body,
                        desc = sub.desc,
                        status = sub.status,
                        done = sub.done,
                        priority = sub.priority,
                        points = sub.points,
                        type = sub.type,
                        logo = sub.logo,
                        assignee = sub.assignee?: "",
                        reporter = sub.reporter?: "",
                        assId = sub.assId?: -1,
                        assUid = sub.repUid,
                        assUri = sub.assUri.orEmpty(),
                        repId = sub.repId?: -1,
                        repUid = sub.repUid,
                        repUri = sub.repUri.orEmpty(),
                        color = sub.color,
                        sid = sub.sid ?: subSID.value,
                        taskId = sub.storyId?: 1,
                    )
                    onDone.invoke(sub)
                }
            } }
        }
    }

    /*fun onEvent(event: AddEditTaskEvent) {
        when(event) {
            is AddEditTaskEvent.EnteredTitle -> {
                _taskTitle.value = taskTitle.value.copy(
                    text = event.value
                )
            }
            is AddEditTaskEvent.ChangeTitleFocus -> {
                _taskTitle.value = taskTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            taskTitle.value.text.isBlank()
                )
            }
            is AddEditTaskEvent.EnteredBody -> {
                _taskBody.value = taskBody.value.copy(
                    text = event.value
                )
            }
            is AddEditTaskEvent.ChangeBodyFocus -> {
                _taskBody.value = taskBody.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            taskBody.value.text.isBlank()
                )
            }
            is AddEditTaskEvent.ChangePoints -> {
                _taskPoints.value = event.value
            }
            is AddEditTaskEvent.ChangePri -> {
                _taskPri.value = taskPri.value.copy(
                    text = event.pri
                )
            }
            is AddEditTaskEvent.ChangeColor -> {
                _taskColor.value = event.color
            }
            is AddEditTaskEvent.ChangeAssignee -> {
                _taskAss.value = taskAss.value.copy(
                    id = event.id,
                    uid = event.uid,
                    text = event.name
                )
                _state.value = state.value.copy(
                    assId = event.id,
                    assUid = event.uid,
                    assignee = event.name
                )
            }
            is AddEditTaskEvent.ChangeStatus -> {
                _taskStatus.value = event.sta
            }
            is AddEditTaskEvent.ChangeAssFocus -> {
                _taskAss.value = taskAss.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            taskAss.value.text.isBlank()
                )
            }
            is AddEditTaskEvent.ChangeReporter -> {
                _taskRep.value = taskRep.value.copy(
                    id = event.id,
                    uid = event.uid,
                    text = event.name
                )
                _state.value = state.value.copy(
                    repId = event.id,
                    repUid = event.uid,
                    reporter = event.name
                )
            }
            is AddEditTaskEvent.ChangeSprint -> {
                _taskSID.value = event.id
                sprintId = event.id
            }
            is AddEditTaskEvent.SaveTask -> {
                viewModelScope.launch {
                    try {
                        taskUseCases.addTask(
                            sprintId?.let { sprintExists(it) } ?:
                            SprintRoom(sprintId = 1L, info = SprintInfo()),
                            Task(
                                title = taskTitle.value.text,
                                content = taskBody.value.text,
                                desc = taskBody.value.text.substring(
                                    min(taskBody.value.text.length , max(75, taskBody.value.text.length / 5) )),
                                DoD = "Basic DoD = All Subtasks marked as done + All paperwork done",
                                timestamp = System.currentTimeMillis(),
                                points = taskPoints.value,
                                priority = Priority.valueOf(taskPri.value.text),
                                color = taskColor.value,
                                assignee = taskAss.value.text,
                                assId = taskAss.value.uid,
                                reporter = taskRep.value.text,
                                repId = taskRep.value.uid,
                                status = taskStatus.value,
                                done = taskDone.value || (taskStatus.value == "Done"),
                                SID = sprintId ?: taskSID.value,
                                UID = taskAss.value.id,
                                UID2 = taskRep.value.id,
                                taskId = currentId
                            ),
                            subStates.value.subtasks
                        )
                        saveStoryHelper()
                        _eventFlow.emit(UIEvent.SaveTask)
                    } catch(e: InvalidTaskException) {
                        _eventFlow.emit(
                            UIEvent.ToastMsg(e.message?: "Error saving task!")
                        )
                    }
                    taskUseCases.addSubtasks(taskSubs)
                }
            }
            is AddEditTaskEvent.ToggleDone -> {
                _taskDone.value = !taskDone.value
                _taskStatus.value = if (_taskDone.value) "Done"
                else "TO DO"
            }
            is AddEditTaskEvent.AddSubTask -> {
                viewModelScope.launch {
                    try {
                        currentId?.let {
//                            taskUseCases.addSubtask(
//                                currentId!!,
//                                event.sub
//                            )
                            _taskSubs.add(event.sub)
                            _subStates.value = subStates.value.copy(
                                task = event.task.task,
                                id = currentId!!,
                                subtasks = event.task.subtasks
                            )
                        }
                        _eventFlow.emit(UIEvent.AddSubtask)
                    } catch(e: InvalidTaskException) {
                        _eventFlow.emit(
                            UIEvent.ToastMsg(e.message?: "Error adding subtask!")
                        )
                    }
                }
            }
            is AddEditTaskEvent.EditSubTask -> {

            }
            else -> {

            }
        }
    }*/

    sealed class UIEvent {
        data class ShowSNB(val msg: String): UIEvent()
        object AddSubtask: UIEvent()
        object SaveSubTask: UIEvent()
    }

    private fun fib(i: Int): Long {
        if (i < 3 || i > 50)
            return if (i > 0) 1 else 0
        return fib(i - 1) + fib(i - 2)
    }

    private fun generateRndPoints(): Long {
        val ary = LongArray(10) { i -> fib(i+2) }
        return ary[Random.nextInt(10)]
    }
//
//    private suspend fun sprintExists(id: Long): SprintRoom? {
//        return sprintUseCases.getSprint.asyncBasic(id)
//    }

    private suspend fun retrieveSprints() {
        try {
            val querySnapshot = sprintRef.get().await()
            val sb = StringBuilder()
            querySnapshot.documents.forEachIndexed { i, doc ->
                val sprint = doc.toObject<Sprint>()
                sb.append("$sprint\n")
            }
            withContext(Dispatchers.Main) {
                _eventFlow.emit(
                    UIEvent.ShowSNB(
                        "Successfully retrieved online sprint data."
                    ))
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                _eventFlow.emit(
                    UIEvent.ShowSNB(
                        "Failed to retrieve online sprint data."
                    ))
            }
        }
    }

    private suspend fun saveStoryHelper() {
        retrieveSubTask { res ->
            if(res != null) {
                saveUpdatedSubTask(res)
            }
            else {
                saveStory(
                    Story(
                        title = subTitle.value.text,
                        body = subBody.value.text,
                        desc = subBody.value.text.substring(
                            min(subBody.value.text.length , max(75, subBody.value.text.length / 5) )),
                        dod = "Definition of Done = All Subtasks done + All paperwork done",
                        logo = R.drawable.redbox,
                        points = subPoints.value,
                        priority = subPri.value.text,
                        color = subColor.value,
                        assignee = subAss.value.text,
                        reporter = subRep.value.text,
                        status = subStatus.value,
                        done = subDone.value,
                        sid = sprintId ?: subSID.value,
                        assId = subAss.value.id,
                        repId = subRep.value.id,
                        storyId = currentId,
                        createdBy = user?.uid?: "Anonymous"
                    )
                )
            }
        }
        addStoryToSprint()
    }

    private fun saveStory(story: Story) = CoroutineScope(Dispatchers.IO).launch {
        try {
            storyRef.add(story).await()
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB("Story/task online data successfully saved."))
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB(e.message?: "Error saving online story data"))
            }
        }

    }

    private fun getUserCount(
        onSuccess: (Long) -> Unit
    ): Any? {

        viewModelScope.launch {
            val countQ = userRef
                .whereNotEqualTo("isEnabled", false)
                .count()
            countQ.get(AggregateSource.SERVER).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    Log.d(tag,"Aggregate count returned ${snapshot.count} users")
                    onSuccess.invoke(task.result.count)
                } else {
                    Log.d(tag, "Count failed: ", task.exception)
                }

            }
        }
        return null
    }

    private suspend fun getUserList(
        onFinish: (List<FireUser>) -> Unit
    ): List<FireUser> {
        var fireUsers = mutableStateListOf<FireUser>()
        getUserCount { n ->
            if (n < 1) {
                return@getUserCount
            }
            Log.d(tag, "Counted $n users")
        }
        try {
            val snapshot = userRef
                .whereNotEqualTo("isEnabled", false)
                .get()
                .await()
            val sb = StringBuilder()
            snapshot.documents.forEach { doc ->
                fireUsers.add(doc.toObject<FireUser>()!!)
                sb.append("$fireUsers\n")
            }
            onFinish(fireUsers)
            withContext(Dispatchers.Main) {
                _eventFlow.emit(
                    UIEvent.ShowSNB(
                        "Successfully retrieved firestore user ${fireUsers.last().uid}"
                    )
                )
            }
            Log.d(
                "user",
                "Successfully retrieved firestore user ${fireUsers.last().uid}"
            )
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _eventFlow.emit(
                    UIEvent.ShowSNB(
                        e.message ?: "Error loading firestore user data"
                    )
                )
            }
            e.printStackTrace()
            if (e is CancellationException) throw e
            Log.e("user", e.localizedMessage ?: "Error loading firestore user data")
        } finally {
            Log.d("user", "Number of users: ${fireUsers.size}")
        }

        return fireUsers
    }

    private fun getUser(fireId: String): FireUser? {
        return null
    }

    private suspend fun matchUser(
        name: String,
        id: Long
    ): String? {
        var result: FireUser? = null
        user?.displayName.let {
            try {
                val querySnapshot = userRef
                    .whereEqualTo("name", name)
                    .whereEqualTo("userId", id)
                    .limit(1)
                    .get()
                    .await()
                val sb = StringBuilder()
                querySnapshot.documents.forEachIndexed { i, doc ->
                    result = doc.toObject<FireUser>()
                    sb.append("$result\n")
                    if(result != null) {
                        return@forEachIndexed
                    }
                }
                withContext(Dispatchers.Main) {
                    _eventFlow.emit(
                        UIEvent.ShowSNB(
                            "Matched user $name with id $id."
                        )
                    )
                }
                if(result != null) {
                    return@let
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _eventFlow.emit(
                        UIEvent.ShowSNB(
                            "Failed to retrieve online story data."
                        )
                    )
                }
            }
        }
        return result?.uid
    }

    suspend fun retrieveSubTask(
        id: Long? = currentId,
        onResult: (SubTask?) -> Unit
    ): SubTask? {
        var sub: SubTask? = null
        try {
            val querySnapshot = storyRef
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
            withContext(Dispatchers.Main) {
                _eventFlow.emit(
                    UIEvent.ShowSNB(
                        "Successfully retrieved online story data."
                    )
                )
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _eventFlow.emit(
                    UIEvent.ShowSNB(
                        "Failed to retrieve online story data."
                    )
                )
            }
        }
        return sub
    }

    private fun getUpdatedSubTask(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        if(state.value.title.isNotBlank()) {
            map["title"] = state.value.title
        }
        if(state.value.body.isNotBlank()) {
            map["body"] = state.value.body
        }
        if(state.value.desc.isNotBlank()) {
            map["desc"] = state.value.desc
        }
        if(state.value.assignee.isNotBlank() && state.value.assignee != "None") {
            map["assignee"] = state.value.assignee
            map["assId"] = state.value.assId
            map["assUid"] = state.value.assUid
        }
        if(state.value.reporter.isNotBlank() && state.value.assignee != "None") {
            map["reporter"] = state.value.reporter
            map["repId"] = state.value.repId
            map["repUid"] = state.value.repUid
        }
        if(state.value.resolution.isNotBlank()) {
            map["resolution"] = state.value.resolution
        }
        if(state.value.points > 0) {
            map["points"] = state.value.points
        }
        if(state.value.priority.isNotBlank()) {
            map["priority"] = state.value.priority
        }
        if(state.value.color != Color(0xFF81D8D0).toArgb()) {
            map["color"] = state.value.color
        }
        if(state.value.status.isNotBlank()) {
            map["status"] = state.value.status
            map["done"] = checkDone(state.value.status)
        }
        if(state.value.labels.isNotEmpty()) {
            map["labels"] = state.value.labels
        }
        if(state.value.project.isNotBlank()) {
            map["project"] = state.value.project
        }
        if(!state.value.images.isNullOrEmpty()) {
            map["attachments"] = state.value.images!!
        }
        if(state.value.sid > 0) {
            map["sid"] = state.value.sid
        }
        if(state.value.taskId > 0) {
            map["taskId"] = state.value.taskId
        }
        if(state.value.active != null) {
            map["active"] = state.value.active!!
        }
        if(state.value.current != null) {
            map["current"] = state.value.current!!
        }
        if(state.value.cloned != null) {
            map["cloned"] = state.value.cloned!!
        }
        if(state.value.type.isNotBlank()) {
            map["type"] = state.value.type
            map["logo"] = state.value.logo?: -1
        }
        if(state.value.dod.isNotBlank()) {
            map["DoD"] = state.value.dod
        }
        if(state.value.component.isNotBlank()) {
            map["component"] = state.value.component
        }
        if(state.value.comments.isNotEmpty()) {
            map["comments"] = state.value.comments
        }
        if(state.value.log.isNotEmpty()) {
            map["log"] = state.value.log
        }
        map["modDate"] = System.currentTimeMillis()
        map["accDate"] = System.currentTimeMillis()
        return map
    }

    private fun saveUpdatedSubTask(subtask: SubTask) {
        viewModelScope.launch {
            updateSubTask(/*retrieveStory()!!,*/ subtask, getUpdatedSubTask())
        }
    }

    fun cacheUpdate(sub: SubTask) {
        repo.updateSub.value = sub
        repo.updateTrue.value = true
    }

    private fun updateMinor(story: Story, field: String, value: Any)  = CoroutineScope(Dispatchers.IO).launch {
        val query = storyRef
            .whereEqualTo("sid", story.sid)
            .whereEqualTo("id", story.id)
            .whereEqualTo("title", story.title)
            .get()
            .await()
        if(query.documents.isNotEmpty()) {
            for(doc in query) {
                try {
                    storyRef.document(doc.id)
                        .update(field, value)
                } catch(e: Exception) {
                    withContext(Dispatchers.Main) {
                        UIEvent.ShowSNB(e.localizedMessage?: "Error during document iteration")
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                UIEvent.ShowSNB("No user found to perform update")
            }
        }
    }

    private suspend fun addStoryToSprint(id: Long = sprintId!!) {
        val query = storyRef
            .whereEqualTo("sid", id)
            .limit(1)
            .get()
            .await()
        if(query.documents.isNotEmpty()) {
            for(doc in query) {
                try {
                    storyRef.document(doc.id).update( "stories", currentId,
                        "subtasks", state.value.subtasks)
                        //"users", state.value.users, "uidList", state.value.uidList)
                        .await()
                    withContext(Dispatchers.Main) {
                        UIEvent.ShowSNB("Successfully added the story to sprint.")
                    }
                } catch(e: Exception) {
                    withContext(Dispatchers.Main) {
                        UIEvent.ShowSNB(e.localizedMessage?: "Error: Sprint does not exist!")
                    }
                }
            }
        }
    }


    private suspend fun updateSubTask(subtask: SubTask, updatedMap: Map<String, Any>) {
        val query = subtaskRef
            .whereEqualTo("id", subtask.id)
            .whereEqualTo("storyId", subtask.storyId)
            .whereEqualTo("title", subtask.title)
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
                        UIEvent.ShowSNB(e.localizedMessage?: "Error during document iteration")
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                UIEvent.ShowSNB("No user found to perform update")
            }
        }
    }

}

