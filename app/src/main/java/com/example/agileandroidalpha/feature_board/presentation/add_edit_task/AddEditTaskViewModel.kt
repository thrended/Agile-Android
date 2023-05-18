package com.example.agileandroidalpha.feature_board.presentation.add_edit_task

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.feature_board.domain.model.InvalidTaskException
import com.example.agileandroidalpha.feature_board.domain.model.IssueType
import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import com.example.agileandroidalpha.feature_board.domain.use_case.SprintUseCases
import com.example.agileandroidalpha.feature_board.domain.use_case.TaskUseCases
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.components.TaskSubtaskData
import com.example.agileandroidalpha.feature_board.presentation.edit_subtask.EditSubTaskViewModel
import com.example.agileandroidalpha.firebase.repository.AuthRepo
import com.example.agileandroidalpha.firebase.repository.FirestoreRepository
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask
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
import java.time.LocalDateTime
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.math.max
import kotlin.random.Random

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val sprintUseCases: SprintUseCases,
    savedStateHandle: SavedStateHandle,
    private val authRepo: AuthRepo,
    private val repo: FirestoreRepository,
    private val room: RoomRepo
) : ViewModel() {

    private val _state = MutableStateFlow(EditTaskState())
    val state = _state

    private val subtaskRef = Firebase.firestore.collection("subtasks")
    private val sprintRef = Firebase.firestore.collection("sprints")
    private val storyRef = Firebase.firestore.collection("stories")
    private val userRef = Firebase.firestore.collection("users")
    val user = Firebase.auth.currentUser
    private val tag = "AddEditTaskVM"

    private var _availUsers = mutableStateListOf<FireUser>()
    var availUsers: List<FireUser> = _availUsers

    private var _availSprints = mutableStateListOf<SprintRoom>()
    var availSprints: List<SprintRoom> = _availSprints

    private val _sprints = MutableStateFlow<SnapshotStateList<SprintRoom>>(mutableStateListOf())
    val sprints: StateFlow<SnapshotStateList<SprintRoom>> = _sprints

    private val _taskTitle = mutableStateOf(TaskTextFieldState(
        hint = "Enter title..."
    ))
    val taskTitle: State<TaskTextFieldState> = _taskTitle

    private val _taskBody = mutableStateOf(TaskTextFieldState(
        hint = "Enter more detailed information..."
    ))
    val taskBody: State<TaskTextFieldState> = _taskBody

    private val _taskDesc = mutableStateOf(TaskTextFieldState(
        hint = "Enter a basic description..."
    ))
    val taskDesc: State<TaskTextFieldState> = _taskDesc

    private val _taskDoD = mutableStateOf(TaskTextFieldState(
        hint = "Enter a definition of done...",
        text = ""
    ))
    val taskDoD: State<TaskTextFieldState> = _taskDoD

    private val _taskAss = mutableStateOf(TaskTextFieldState(
        hint = "Assign user to this task..."
    ))
    val taskAss: State<TaskTextFieldState>  = _taskAss

    private val _taskRep = mutableStateOf(TaskTextFieldState(
        hint = "Designate reporter for this task..."
    ))
    val taskRep: State<TaskTextFieldState>  = _taskRep

    private val _taskPoints = mutableStateOf(generateRndPoints())
    val taskPoints: State<Long> = _taskPoints

    private val _taskPri = mutableStateOf(TaskTextFieldState(
        text = Task.priorities.random().name,
    ))
    val taskPri: State<TaskTextFieldState> = _taskPri

    private val _taskColor = mutableStateOf(Task.colors.random().toArgb())
    val taskColor: State<Int> = _taskColor

    private val _taskStatus = mutableStateOf("TO DO")
    val taskStatus: State<String> = _taskStatus

    private val _taskDone = mutableStateOf(false)
    val taskDone: State<Boolean> = _taskDone

    private val _taskRes = mutableStateOf("")
    val taskRes: State<String> = _taskRes

    private val _taskType = mutableStateOf("Story")
    val taskType: State<String> = _taskType

    private val _taskLogo = mutableStateOf(0)
    val taskLogo: State<Int> = _taskLogo

    private val _taskLabels = mutableStateListOf<String>()
    val taskLabels: List<String> = _taskLabels

    private var _taskSubs = mutableListOf<Subtask>()
    var taskSubs: MutableList<Subtask> = _taskSubs

    private var _taskSID = mutableStateOf(1L)
    var taskSID: State<Long> = _taskSID

    private val _subStates: MutableState<TaskSubtaskData> = mutableStateOf(
        TaskSubtaskData(

        )
    )
    val subStates: MutableState<TaskSubtaskData> = _subStates

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentId: Long? = null

    var me = MutableStateFlow(FireUser())
        private set

    var users = mutableStateListOf<FireUser>()
        private set

    private var sprintId: Long? = null

    private var isNew: Boolean = true

    init {
        viewModelScope.launch {
//            getUserList {
//                availUsers = it
//                UIEvent.ToastMsg("Number of users = ${availUsers.size}")
//            }
            repo.getAllUsers { fireUsers, user ->
                user?.let {
                    me.value = user
                    state.value.me = user
                }
                fireUsers?.let {
                    users.addAll(fireUsers)
                    state.value.users = fireUsers
                }
                EditSubTaskViewModel.UIEvent.ShowSNB("Number of users = ${users.size}")
            }
            retrieveSprints()

        savedStateHandle.get<Long>("sid")?.let { sid ->
            if (sid != -1L ) {
                sprintId = max(1L, sid)
                viewModelScope.launch {
                    sprintUseCases.loadSprints.list()?.also { sps ->
                        availSprints = sps
                    }
                }
            }
            else sprintId = 1L
        }
        savedStateHandle.get<Long>("id")?.let { id ->
            if (id != -1L) {
                viewModelScope.launch {
                    taskUseCases.getTask(id)?.also { task ->
                        currentId = task.task.taskId
                        sprintId = task.task.sprintId
                        _taskTitle.value = taskTitle.value.copy(
                            text = task.task.title,
                            isHintVisible = false
                        )
                        _taskDesc.value = taskDesc.value.copy(
                            text = task.task.desc,
                            isHintVisible = false
                        )
                        _taskBody.value = taskBody.value.copy(
                            text = task.task.content,
                            isHintVisible = false
                        )
                        _taskDoD.value = taskDoD.value.copy(
                            text = task.task.dod?: "Basic DoD",
                            isHintVisible = false
                        )
                        _taskRes.value = task.task.resolution?: "Unresolved"
                        _taskColor.value = task.task.color
                        _taskPoints.value = task.task.points
                        _taskPri.value = taskPri.value.copy(
                            text = task.task.priority.name,
                            num = task.task.priority.ordinal,
                            isHintVisible = false
                        )
                        _taskAss.value = taskAss.value.copy(
                            text = task.task.assignee?: "None",
                            uid = task.task.assUid,
                            id = task.task.assigneeId,
                            misc = task.task.assUri.orEmpty(),
                            isHintVisible = false
                        )
                        _taskRep.value = taskRep.value.copy(
                            text = task.task.reporter?: "None",
                            uid = task.task.repUid,
                            id = task.task.reporterId,
                            misc = task.task.repUri.orEmpty(),
                            isHintVisible = false
                        )
                        _taskStatus.value = task.task.status
                        _taskType.value = task.task.type.name
                        _taskLogo.value = toLogo(task.task.type.name)
                        _taskDone.value = task.task.done
                        _taskSubs = task.subtasks as MutableList<Subtask>
                        _taskSID.value = task.task.sprintId!!
                        _subStates.value = subStates.value.copy(
                            id = currentId,
                            task = task.task,
                            subtasks = task.subtasks//.map{ s->s.subtask }
                        )
                    }
                    _state.value = state.value.copy(
                        id = currentId?: -1,
                        sid = sprintId?: -1,
                        title = taskTitle.value.text,
                        body = taskBody.value.text,
                        desc = taskDesc.value.text,
                        dod = taskDoD.value.text,
                        timestamp = System.currentTimeMillis(),
                        points = taskPoints.value,
                        priority = taskPri.value.text,
                        color = taskColor.value,
                        status = taskStatus.value,
                        resolution = taskRes.value,
                        done = taskDone.value,
                        type = taskType.value,
                        logo = taskLogo.value,
                        assignee = taskAss.value.text,
                        reporter = taskRep.value.text,
                        assId = taskAss.value.id?: -1,
                        assUid = taskAss.value.uid ?: "",
                        assUri = taskAss.value.misc,
                        repId = taskRep.value.id?: -1,
                        repUid = taskRep.value.uid ?: "",
                        repUri = taskRep.value.misc,
                        subtasks = taskSubs.map { sub -> sub.subId}
                    )
                }
            }
            else isNew = true
        }
        }


    }

    fun onEvent(event: AddEditTaskEvent) {
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
            is AddEditTaskEvent.EnteredDesc -> {
                _taskDesc.value = taskDesc.value.copy(
                    text = event.value
                )
            }
            is AddEditTaskEvent.ChangeDescFocus -> {
                _taskDesc.value = taskDesc.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            taskDesc.value.text.isBlank()
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
            is AddEditTaskEvent.EnteredDoD -> {
                _taskDoD.value = taskDoD.value.copy(
                    text = event.value
                )
            }
            is AddEditTaskEvent.ChangeDoDFocus -> {
                _taskDoD.value = taskDoD.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            taskDoD.value.text.isBlank()
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
            is AddEditTaskEvent.ChangeType -> {
                _taskType.value = event.type
                _taskLogo.value = toLogo(event.type)
            }
            is AddEditTaskEvent.ChangeColor -> {
                _taskColor.value = event.color
            }
            is AddEditTaskEvent.ChangeAssignee -> {
                _taskAss.value = taskAss.value.copy(
                    id = event.id,
                    uid = event.uid,
                    text = event.name,
                    misc = event.photo.orEmpty()
                )
                _state.value = state.value.copy(
                    assId = event.id,
                    assUid = event.uid,
                    assignee = event.name,
                    assUri = event.photo.orEmpty(),
                    assUser = event.user
                )
            }
            is AddEditTaskEvent.ChangeStatus -> {
                _taskStatus.value = event.sta
                _taskDone.value = (checkDone(event.sta))
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
                    text = event.name,
                    misc = event.photo.orEmpty()
                )
                _state.value = state.value.copy(
                    repId = event.id,
                    repUid = event.uid,
                    reporter = event.name,
                    repUri = event.photo.orEmpty(),
                    repUser = event.user
                )
            }
            is AddEditTaskEvent.ChangeResolution -> {
                _taskRes.value = event.res
                _state.value = state.value.copy(
                    resolution = event.res
                )
            }
            is AddEditTaskEvent.ChangeSprint -> {
                _taskSID.value = event.id
                sprintId = event.id
            }
            is AddEditTaskEvent.SaveTask -> {
                viewModelScope.launch {
                    val uid = authRepo.getUID()
                    val id = repo.matchUID()
                    val ur = repo.matchUser()
                    try {
                        val t = Task(
                            title = taskTitle.value.text,
                            content = taskBody.value.text,
                            desc = taskDesc.value.text,
                            dod = taskDoD.value.text,
                            timestamp = System.currentTimeMillis(),
                            creDate = LocalDateTime.now(),
                            points = taskPoints.value,
                            priority = Priority.valueOf(taskPri.value.text),
                            color = taskColor.value,
                            assignee = taskAss.value.text,
                            assUid = taskAss.value.uid,
                            assUri = taskAss.value.misc,
                            reporter = taskRep.value.text,
                            repUid = taskRep.value.uid,
                            repUri = taskRep.value.misc,
                            status = taskStatus.value,
                            type = IssueType.valueOf(taskType.value),
                            done = taskDone.value,
                            createdBy = uid,
                            creator = ur.name?: ur.email?: "Anonymous User",
                            userId = id.first,
                            uri = id.second,
                            sprintId = event.sprintId,
                            assigneeId = taskAss.value.id,
                            reporterId = taskRep.value.id,
                            taskId = if (event.id > 0) event.id else null,
                            uid = authRepo.getUID()
                        )
                        updateStates()
                        val x =  taskUseCases.addTask(
                            t,
                            subStates.value.subtasks
                        ) { subs ->
                            subs?.let {
                                if (event.id <= 0) saveSubs(repo.toSubTaskList(subs))
                            }
                        }
                        isNew = x < 0
                        saveStoryHelper(
                            t,
                           if (event.id > 0) event.id else x
                        )
                        _eventFlow.emit(UIEvent.SaveTask)
                    } catch(e: InvalidTaskException) {
                        _eventFlow.emit(
                            UIEvent.ShowSNB(e.message?: "Error saving task!")
                        )
                    }
                    taskUseCases.addSubtasks(taskSubs)
                }
            }
            is AddEditTaskEvent.ToggleDone -> {
                _taskDone.value = !taskDone.value
                _taskStatus.value = if (_taskDone.value) "Done"
                                    else "In Progress"
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
                            UIEvent.ShowSNB(e.message?: "Error adding subtask!")
                        )
                    }
                }
            }
            is AddEditTaskEvent.EditSubTask -> {

            }
            else -> {

            }
        }
    }

    sealed class UIEvent {
        data class ShowSNB(val msg: String): UIEvent()
        object AddSubtask: UIEvent()
        object SaveTask: UIEvent()
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

    private fun saveSubs(list: List<SubTask>?) = CoroutineScope(Dispatchers.IO).launch {
        try {
            list?.forEach { sub ->
                subtaskRef.add(sub).await()
            }
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB("Story/task online data successfully saved."))
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB(e.message?: "Error saving online story data"))
            }
        }

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

    private suspend fun sprintExists(id: Long): SprintRoom? {
        return sprintUseCases.getSprint.asyncBasic(id)
    }

    private suspend fun retrieveSprints() {
        try {
            val querySnapshot = sprintRef.get().await()
            val sb = StringBuilder()
            if (querySnapshot.isEmpty) {
                withContext(Dispatchers.Main) {
                    _eventFlow.emit(
                        UIEvent.ShowSNB(
                            "No sprints were found."
                        ))
                }
                return
            }
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

    private suspend fun saveStoryHelper(task: Task, id: Long) {
        retrieveStory(id) { res ->
            if(res != null) {
                saveUpdatedStory(id)
            }
            else {
                if (id < 0 && !isNew) throw (IndexOutOfBoundsException())
                saveStory(
                    repo.toStory(task, id)
//                    Story(
//                        title = taskTitle.value.text,
//                        body = taskBody.value.text,
//                        desc = taskBody.value.text.substring(
//                            min(taskBody.value.text.length , max(75, taskBody.value.text.length / 5) )),
//                        DoD = "Definition of Done = All Subtasks done + All paperwork done",
//                        logo = R.drawable.redbox,
//                        points = taskPoints.value,
//                        priority = taskPri.value.text,
//                        color = taskColor.value,
//                        assignee = taskAss.value.text,
//                        reporter = taskRep.value.text,
//                        status = taskStatus.value,
//                        done = taskDone.value,
//                        sid = sprintId ?: taskSID.value,
//                        assId = taskAss.value.id,
//                        repId = taskRep.value.id,
//                        taskId = currentId,
//                        createdBy = user?.uid?: "Anonymous"
//                    )
                )
            }
        }
        //addStoryToSprint()
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
                    .whereEqualTo("id", id)
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

    suspend fun retrieveStory(
        id: Long? = currentId,
        onResult: (Story?) -> Unit
    ): Story? {
        if (id == null || id < 0) onResult.invoke(null)
        var story: Story? = null
        try {
            val querySnapshot = storyRef
                .whereEqualTo("id", id)
                .limit(1)
                .get()
                .await()
            val sb = StringBuilder()
            if(!querySnapshot.isEmpty) {
                querySnapshot.documents.forEachIndexed { i, doc ->
                    story = doc.toObject<Story>()
                    sb.append("$story\n")
                }
            }
            onResult.invoke(story)
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
        return story
    }

    private fun updateStates() {
        _state.value = state.value.copy(
            title = taskTitle.value.text,
            body = taskBody.value.text,
            desc = taskDesc.value.text,
            dod = taskDoD.value.text,
            timestamp = System.currentTimeMillis(),
            assignee = taskAss.value.text,
            reporter = taskAss.value.text,
            points = taskPoints.value,
            priority = taskPri.value.text,
            color = taskColor.value,
            status = taskStatus.value,
            type = taskType.value,
            done = taskDone.value,
            subtasks = taskSubs.map { s -> s.subId },
            numSubtasks = taskSubs.size,
            sid = taskSID.value,
            taskId = currentId?: -1,
            assId = taskAss.value.id?: -1,
            repId = taskRep.value.id?: -1,
            assUri = taskAss.value.misc,
            repUri = taskRep.value.misc,
            assUid = taskAss.value.uid.orEmpty(),
            repUid = taskRep.value.uid.orEmpty()
        )
    }

    private fun getUpdatedStory(): Map<String, Any> {
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
        if(state.value.dod.isNotBlank()) {
            map["dod"] = state.value.dod
        }
        if(state.value.assUid.isNotBlank()) {
            map["assUid"] = state.value.assUid
        }
        if(state.value.assignee.isNotBlank()) {
            map["assignee"] = state.value.assignee
        }
        if(state.value.assId > -1) {
            map["assId"] = state.value.assId
        }
        if(state.value.repUid.isNotBlank()) {
            map["repUid"] = state.value.repUid
        }
        if(state.value.reporter.isNotBlank()) {
            map["reporter"] = state.value.reporter
        }
        if(state.value.repId > -1) {
            map["repId"] = state.value.repId
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
        if(state.value.color > 0) {
            map["color"] = state.value.color
        }
        if(state.value.status.isNotBlank()) {
            map["status"] = state.value.status
        }
        if(state.value.labels.isNotEmpty()) {
            map["labels"] = state.value.labels
        }
        if(state.value.project.isNotBlank()) {
            map["project"] = state.value.project
        }
        if(!state.value.subtasks.isNullOrEmpty()) {
            map["subtasks"] = state.value.subtasks!!
        }
        if(state.value.sid > -1) {
            map["sid"] = state.value.sid
        }
        if(state.value.taskId > -1) {
            map["storyId"] = state.value.taskId
        }
        if(state.value.assUri.isNotBlank()) {
            map["assUri"] = state.value.assUri
        }
        if(state.value.repUri.isNotBlank()) {
            map["repUri"] = state.value.repUri
        }
//        if(state.value.id > -1) {
//            map["id"] = state.value.taskId
//        }
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
        if(state.value.component.isNotBlank()) {
            map["component"] = state.value.component
        }
        if(state.value.labels.isNotEmpty()) {
            map["labels"] = state.value.labels
        }
        if(state.value.comments.isNotEmpty()) {
            map["comments"] = state.value.comments
        }
        if(state.value.log.isNotEmpty()) {
            map["log"] = state.value.log
        }
        map["done"] = state.value.done
        map["modDate"] = System.currentTimeMillis()
        map["accDate"] = System.currentTimeMillis()
        return map
    }

    private fun saveUpdatedStory(id: Long) {
        viewModelScope.launch {
            updateStory(/*retrieveStory()!!,*/ id, getUpdatedStory())
        }
    }

    private fun updateMinor(story: Story, field: String, value: Any)  = CoroutineScope(Dispatchers.IO).launch {
        val query = storyRef
            .whereEqualTo("id", story.id)
//            .whereEqualTo("sid", story.sid)
//            .whereEqualTo("title", story.title)
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
        val query = sprintRef
            .whereEqualTo("sid", id)
            .limit(1)
            .get()
            .await()
        if(query.documents.isNotEmpty()) {
            for(doc in query) {
                try {
                    sprintRef.document(doc.id).update( "stories", currentId,
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

    fun checkDone(stat: String): Boolean {
        return stat in listOf("Skipped", "Workaround Available",
            "Done", "Approved", "Closed", "Archived")
    }

    fun toLogo(typ: String): Int {
        return when (typ) {
            "Task" -> {
                R.drawable.id_viridia
            }
            "Story" -> {
                R.drawable.id_blue
            }
            "Subtask" -> {
                R.drawable.box_green
            }
            "Impediment" -> {
                R.drawable.ic_wall
            }
            "Bug" -> {
                R.drawable.box_red
            }
            "Feature" -> {
                R.drawable.ic_grab
            }
            "Initiative" -> {
                R.drawable.ic_pipe
            }
            "Issue" -> {
                R.drawable.ic_unknown
            }
            "Test" -> {
                R.drawable.ic_ry
            }
            "Spike" -> {
                R.drawable.ic_trap
            }
            "ChangeRequest" -> {
                R.drawable.ic_swap
            }
            "Epic" -> {
                R.drawable.ic_twins
            }
            else -> {
                -1
            }
        }
    }

}

//    Task("Task", R.drawable.box_green),
//    Story("Story", R.drawable.box_green),
//    Subtask("Sub-task", R.drawable.box_green),
//    Impediment("Impediment", R.drawable.box_red),
//    Bug("Bug", R.drawable.box_red),
//    Feature("Feature"),
//    Initiative("Initiative"),
//    Issue("Other Issue"),
//    Test("Test"),
//    Spike("Spike"),
//    ChangeRequest("Change Request"),
//    Epic("Epic")