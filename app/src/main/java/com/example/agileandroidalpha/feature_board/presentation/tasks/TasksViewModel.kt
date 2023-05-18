package com.example.agileandroidalpha.feature_board.presentation.tasks

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.feature_board.domain.model.InvalidSprintException
import com.example.agileandroidalpha.feature_board.domain.model.SprintInfo
import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithUsersAndTasks
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import com.example.agileandroidalpha.feature_board.domain.use_case.SprintUseCases
import com.example.agileandroidalpha.feature_board.domain.use_case.TaskUseCases
import com.example.agileandroidalpha.feature_board.domain.util.OrderType
import com.example.agileandroidalpha.feature_board.domain.util.TaskOrder
import com.example.agileandroidalpha.feature_board.presentation.sprint.SprintEvent
import com.example.agileandroidalpha.firebase.firestore.Statics
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.sprintsList
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask
import com.example.agileandroidalpha.firebase.login.UserData.DATA.offlineMode
import com.example.agileandroidalpha.firebase.repository.AuthRepo
import com.example.agileandroidalpha.firebase.repository.FirestoreRepository
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val sprintUseCases: SprintUseCases,
    private val room: RoomRepo,
    private val authRepo: AuthRepo,
    private val repo: FirestoreRepository,
    ssh: SavedStateHandle
) : ViewModel() {

    private val userRef = Firebase.firestore.collection("users")
    private val sprintRef = Firebase.firestore.collection("sprints")
    private val storyRef = Firebase.firestore.collection("stories")
    private val subtaskRef = Firebase.firestore.collection("subtasks")

    var uid: String = authRepo.getUID()
    private set

    private val _uiState = MutableStateFlow(TasksState())
    val uiState = _uiState.asStateFlow()

    var index = MutableStateFlow(0)
        private set

    private val _sprintLib = MutableStateFlow<SnapshotStateList<Sprint>>(mutableStateListOf())
    val sprintLib: StateFlow<SnapshotStateList<Sprint>> = _sprintLib

    private val _recycle = MutableStateFlow(Statics.Deleted)
    val recycle = _recycle.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(
            5000L,
//            500L,
        ),
        _recycle.value
    )

    private val _state = MutableStateFlow(TasksState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(
            5000L,
//            500L,
        ),
        _state.value
    )

    val test = flow {
        while (true) {
            emit(_state)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        0
    )

    private val _list = mutableStateListOf<Sprint>()
    val list: MutableList<Sprint> = _list

    var _users = mutableStateListOf<FireUser>()
        private set // val users: MutableList<FireUser> = _users

    private val _stories = mutableStateListOf(Story())
    val stories: MutableList<Story> = _stories

    private val _subTasks = mutableStateListOf<SubTask>()
    val subTasks: MutableList<SubTask> = _subTasks

    private val _taskStatus = mutableStateOf("TO DO")
    val taskStatus: State<String> = _taskStatus

    private val _taskDone = mutableStateOf(false)
    val taskDone: State<Boolean> = _taskDone

    private val _subtasks = mutableStateListOf<Subtask>()
    val subtasks: MutableList<Subtask> = _subtasks

    private var recycleSprint: Sprint? = null
    private var recycleStories: MutableList<Story> = mutableStateListOf()
    private var recycleStory: Story? = null
    private var recycleSubList: MutableList<SubTask> = mutableStateListOf()
    private var recycleSub: SubTask? = null

    private var recycleBinSprint: SprintWithUsersAndTasks? = null
    private var recycleBin: Task?  = null
    private var recycleBinSub: Subtask? = null
    private var recycleBinSubtasks: MutableList<Subtask>? = null

    var selectedSprint = mutableStateOf(Sprint())
        private set

    var selectedSprintId = mutableStateOf(-1L)
        private set

    var selectedSprintTitle = mutableStateOf("No Sprint")
        private set

    private val _started = mutableStateOf(false)
    val started: State<Boolean> = _started

    private val _cd = mutableStateOf(0)
    val cd: State<Int> = _cd

    private val _elapsed = mutableStateOf(0)
    val elapsed: State<Int> = _elapsed

    private val _points = mutableStateOf(0L)
    val points: State<Long> = _points

    private val _rem = mutableStateOf(0L)
    val rem: State<Long> = _rem

    var numSprints = mutableStateOf(0L)
        private set

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var getTasksJob: Job? = null

    private val tag: String = "Sprint Screen"

    init {
        viewModelScope.launch {
            if(repo.updateTrue.value) {
                if (repo.updateSub.value != SubTask()) {
                    taskUseCases.addSubtask(repo.toSub(repo.updateSub.value))
                }
                if (repo.updateStory.value != Story()) {
                    taskUseCases.addTask(repo.toTask(repo.updateStory.value))
                    { subs -> }
                }
                if (repo.updateSprint.value != Sprint()) {
                    sprintUseCases.quickAddSprint
                }
                repo.resetStates()
            }
            getActiveSprintCount { c ->
                if (c < 1) {
                    numSprints.value = 0
                    selectedSprintId.value = -1
                    selectedSprintTitle.value = "No Sprint"
//                    viewModelScope.launch {
//                        addSprint(addSprintHelper()) {
//                            _state.value = state.value.copy(
//                                sprints = list
//                            )
//                        }
//                    }
                } else {
                    numSprints.value = c
                }
            }
            repo.fetchFirestoreChanges { sps, fireUsers, loggedInUser, stores, subtasks, map, allT, bin ->
                viewModelScope.launch {
                val sprints = sps.apply { sps.filter { s -> (s.active != false || s.expired) && !s.completed } }
                _state.value = state.value.copy(
                    sprints = sprints,
                    users = fireUsers,
                    stories = stores,
                    subs = subtasks,
                    fullStories = map,
                    flagForUpdate = true,
                    sprintCount = sprints.size,
                    index = index.value,
                    sprintId = if (sprints.isNotEmpty()) 1L else -1L,
                    sprintTitle = if (sprints.isNotEmpty()) sprints[0].title?: "Sprint 1" else "No Sprint",
                    selectedSprint = if (sprints.isNotEmpty()) sprints[0] else null,
                    started = if (sprints.isNotEmpty()) sprints[0].started else false,
                    active = if (sprints.isNotEmpty()) sprints[0].active else false,
                    paused = if (sprints.isNotEmpty()) sprints[0].paused else false,
                    timeRem = if (sprints.isNotEmpty()) sprints[0].countdown else 21,
                    totalPoints = if (sprints.isNotEmpty()) sprints[0].totalPoints else 0,
                    remPoints = if (sprints.isNotEmpty()) sprints[0].remPoints else 0,
                    currentUser = loggedInUser,
                    currentSprint = if (sprints.isNotEmpty()) repo.toSprint(sprints[0]) else null,
                    storyMap = if(stores.isNotEmpty()) stores.associateWith { story -> repo.toTask(story) } else emptyMap(),
                    subtaskMap = if(subtasks.isNotEmpty()) subtasks.associateWith { sub -> repo.toSub(sub) } else emptyMap(),
                    sprintMap = if(sprints.isNotEmpty()) sprints.associateWith { sp -> repo.toSprint(sp) } else emptyMap(),
                    allTasks = allT
                )
                list.clear()
                list.addAll(sprints.orEmpty())
                _users.clear()
                _users.addAll(fireUsers.orEmpty())
                stories.clear()
                stories.addAll(stores.orEmpty())
                subTasks.clear()
                subTasks.addAll(subtasks.orEmpty())
                selectedSprint.value = if (sprints.isNotEmpty()) sprints[0] else Sprint()
                selectedSprintId.value = if (sprints.isNotEmpty()) 1L else -1L
                selectedSprintTitle.value = if (sprints.isNotEmpty()) sprints[0].title?: "Untitled Sprint #1" else "No Sprint"
                numSprints.value = sprints.size.toLong() ?: 0L

                }
            }
//            getUsersBySprint(state.value.sprintId) {
//                _state.value = state.value.copy(
//                    users = it
//                )
//            }
//            retrieveSprints {
//                _state.value = state.value.copy(
//                    sprints = it
//                )
//            }
//            retrieveStories { list ->
//                _state.value = state.value.copy(
//                    stories = list
//                )
//            }
//            retrieveSubTasks {
//                subTasks.clear()
//                subTasks.addAll(it)
//                _state.value = state.value.copy(
//                    subs = it
//                )
//            }
            room.getAllSprintBasicInfo().collect { sprints ->
                _state.value = state.value.copy(sprintList = sprints)
            }
//            if (sprintUseCases.loadSprints.count() < 1)
//            {
//                try {
////                    sprintUseCases.quickAddSprint(
////                        SprintRoom(
////                            title = "Dummy Sprint 0",
////                            desc = "Sprint 0 Initial Descriptor",
////                            completed = true,
////                            info = SprintInfo(
////                                status = "Done"
////                            ),
////                            active = false,
////                        )
////                    )
//                    val sp = SprintRoom(
//                        sprintId = 1,
//                        title = "Sprint 1",
//                        desc = "Sprint 1 Initial Descriptor",
//                        info = SprintInfo(),
//                        active = true
//                    )
//                    _state.value = state.value.copy(
//                        sprintId = 1,
//                        sprintTitle = sp.title,
//                        currentSprint = sp,
//                        sprintCount = 1,
//                        sprintActive = sp.active,
//                        sprintPaused = sp.info.paused
//                    )
//                    _numSprints.value = 1
//                    _selectedSprintId.value = 1
//                    _selectedSprintTitle.value = sp.title
//                    sprintUseCases.quickAddSprint(sp)
//                    _numSprints.value = numSprints.value + 1
//                    recycleBinSprint = SprintWithUsersAndTasks(sp, emptyList(), emptyList())
//                    _eventFlow.emit(UIEvent.Refresh(1))
//                }
//                catch (e: InvalidSprintException) {
//                    _eventFlow.emit(UIEvent.ToastMsg(
//                        msg = e.message?: "Error during Initialization!!")
//                    )
//                }
//            }
            if (!offlineMode) {
                sync()
            }
        }
        updateSprintList()
        getSprint(selectedSprintId.value)
        getTasks(selectedSprintId.value, TaskOrder.Date(OrderType.Descending))
        //getTasks(1L, TaskOrder.Date(OrderType.Descending))
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
                    subTasks[subTasks.indexOf(subTasks.find { s -> s.id == id })] = sub!!
                    _state.value = state.value.copy(
                        subs = subTasks
                    )
                    onDone.invoke(sub)
                }
            } }
        }
    }

    fun onEvent(event: TasksEvent) {
        when(event) {
            is TasksEvent.AddSprint -> {
                viewModelScope.launch {
                    _eventFlow.emit(UIEvent.AddSprint)
                    updateSprintList()
                }
            }
            is TasksEvent.ChangeSprint -> {
                if(state.value.sprintId == event.sprint.id) { return }
                viewModelScope.launch {
                    sync(state.value.sprints.indexOf(event.sprint))
                    _state.value = state.value.copy(
                        index = state.value.sprints.indexOf(event.sprint),
                        sprintId = event.sprint.id!!,
                        sprintTitle = event.sprint.title.orEmpty(),
                        sprintActive = event.sprint.active == true,
                        sprintPaused = event.sprint.paused,
                        totalPoints = event.sprint.totalPoints,
                        elapsed = event.sprint.elapsed,
                        timeRem = event.sprint.countdown,
                        selectedSprint = event.sprint,
                        creator = event.sprint.createdBy,
                        creatorID = event.sprint.creatorID,
                        allComplete = event.sprint.completed
                    )
                    index.value = state.value.index
                    selectedSprintId.value = event.sprint.id
                    selectedSprintTitle.value = event.sprint.title.orEmpty()
                    _started.value = event.sprint.active == true
                    _cd.value = event.sprint.countdown
                    _elapsed.value = event.sprint.elapsed
                    refresh(event.sprint.id)
                    _eventFlow.emit(UIEvent.ChangeSprint(event.sprint.id))
//                if(state.value.sprintId == event.sprint.sprintId) { return }
//                viewModelScope.launch {
//                    _state.value = state.value.copy(
//                        index = state.value.sprintList.indexOf(event.sprint),
//                        sprintId = event.sprint.sprintId!!,
//                        sprintTitle = event.sprint.title,
//                        sprintActive = event.sprint.active,
//                        sprintPaused = event.sprint.info.paused,
//                        totalPoints = event.sprint.info.totalPoints,
//                        elapsed = event.sprint.info.elapsed,
//                        timeRem = event.sprint.info.countdown,
//                        currentSprint = event.sprint,
//                        creator = event.sprint.info.createdBy,
//                        allComplete = event.sprint.completed
//                    )
//                    index.value = state.value.index
//                    selectedSprintId.value = event.sprint.sprintId?: 0
//                    selectedSprintTitle.value = event.sprint.title
//                    _started.value = event.sprint.active
//                    _cd.value = event.sprint.info.countdown
//                    _elapsed.value = event.sprint.info.elapsed
//                    refresh(event.sprint.sprintId?: 0)
//                    sync(state.value.index)
//                    _eventFlow.emit(UIEvent.ChangeSprint(event.sprint.sprintId?: 0))
                }
            }
            is TasksEvent.RenameSprint -> {
                viewModelScope.launch {
                    update("sprints", state.value.sprintId, "title", event.name) {
                        val sprint: Sprint = it as Sprint
                        _state.value = state.value.copy(
                            sprintTitle = it.title!!
                        )
                    }
                    selectedSprintTitle.value = event.name
                    _eventFlow.emit(UIEvent.Refresh(state.value.sprintId))
                    updateSprintList()
                }
            }
            is TasksEvent.Order -> {
                if(state.value.taskOrder::class == event.taskOrder::class
                    && state.value.taskOrder.type == event.taskOrder.type
                ) { return }
                _state.value = state.value.copy(
                    taskOrder = event.taskOrder,
                    allTasks = reSort(state.value.allTasks, event.taskOrder),
                    tasks = state.value.allTasks.map {t -> t.task},
                    taskToSubtasks = state.value.allTasks.associate { t ->
                        Pair(t.task, t.subtasks)
                    },
                    totalPoints = state.value.allTasks.sumOf { t -> t.task.points },
                    remPoints = state.value.allTasks.map{t -> t.task }.filter {t -> !t.done} .sumOf { t -> t.points },
                    flagForUpdate = true
                )
                viewModelScope.launch {
//                    _eventFlow.emit(UIEvent.ToastMsg("Task Order changed to ${event.taskOrder.type}"))
                    _eventFlow.emit(UIEvent.Reload)
                }
            }
            is TasksEvent.AddTask -> {
                viewModelScope.launch {
                    taskUseCases.addTask(event.task, event.subtasks) {
                        subs ->
                    }
                }
                updateSprintList()
            }
            is TasksEvent.DeleteTask -> {
                viewModelScope.launch {
                    taskUseCases.deleteTask(event.task, event.subtasks)
                    recycleBin = event.task
                    event.subtasks?.let {recycleBinSubtasks = event.subtasks as MutableList<Subtask>}
                    deleteStory(event.task.taskId!!)
                    deleteAllSubtasks(event.task.taskId?: 0)
                }
            }
            is TasksEvent.EditTask -> {
                // can be the same as Add Task for now
            }
            is TasksEvent.AddSubTask -> {
                viewModelScope.launch {
                    val userId = repo.matchUID()
                    try {
                        event.subtask.userId = userId.first
                        event.subtask.uri = userId.second
                        event.task.assignee?.let {
                            event.subtask.assignee = event.task.assignee
                            event.subtask.assigneeId = event.task.assigneeId
                            event.subtask.assUid = event.task.assUid
                            event.subtask.assUri = event.task.assUri
                        }
                        subtasks.add(event.subtask)
                        event.task.taskId?.let {
                            addSubtask( event.subtask, taskUseCases.addSubtask(event.subtask) )
                        }
                        _eventFlow.emit(UIEvent.AddSubtask)
                    }
                    catch (e: InvalidSprintException) {
                        _eventFlow.emit(UIEvent.ShowSNB(
                            msg = e.message?: "Error: Could not add subtask!")
                        )
                    }
                    sync()
                    reSort(state.value.allTasks, state.value.taskOrder)
                }
            }
            is TasksEvent.DeleteSubtask -> {
                viewModelScope.launch {
                    subtasks.remove(event.subtask)
                    taskUseCases.deleteSubtask(event.subtask)
                    recycleBinSub = event.subtask
                    deleteSubtask(event.subtask.subId!!)
                }
            }
            is TasksEvent.DragSubtask -> {
                viewModelScope.launch {
                    taskUseCases.dragSubtask(event.task, event.sub, event.stat, event.bool)
                    updateDragValues(event.task.task, event.sub, event.stat)
                    _eventFlow.emit(UIEvent.Refresh(state.value.sprintId))
                    updateSprintList()
                }
            }
            is TasksEvent.EditSubTask -> {
                viewModelScope.launch {

                }
            }
            is TasksEvent.RestoreSubtask -> {
                viewModelScope.launch {
                    taskUseCases.addSubtask(recycleBinSub ?: return@launch)
                    recycleBinSub = null
                    _eventFlow.emit(UIEvent.Refresh(state.value.sprintId))
                }
            }
            is TasksEvent.MarkComplete -> {
                viewModelScope.launch {
                    _taskDone.value = !taskDone.value
                    _subtasks.forEach { s ->
                        s.done = !s.done
                    }
                    taskUseCases.doneTask(event.task)
                    _eventFlow.emit(UIEvent.Refresh(state.value.sprintId))
                    updateSprintList()
                }
            }
            is TasksEvent.MarkSubtask -> {
                viewModelScope.launch {
                    taskUseCases.markSubtask(event.task, event.subtask, event.bool)
                    _eventFlow.emit(UIEvent.Refresh(state.value.sprintId))
                }
            }
            is TasksEvent.RestoreTask -> {
                viewModelScope.launch {
                    taskUseCases.addTask.restore(recycleBin ?: return@launch, recycleBinSubtasks ?: return@launch)
                    recycleBin = null
                    recycleBinSubtasks = null
                    // Restore Firestore Data
                    repo.restore()
                }
            }
            is TasksEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
                updateSprintList()
            }
            is TasksEvent.ToggleAllCompleted -> {
                _state.value = state.value.copy(
                    allComplete = !state.value.allComplete
                )
                updateSprintList()
            }
            else -> {

            }
        }
    }
    fun onEvent(event: SprintEvent) {
        when(event) {
            is SprintEvent.CloneSprint -> {
                viewModelScope.launch {
                    val sp = repo.toSprint(event.sprint)
                    val uid = repo.matchUID()
                    var newId = 0L
                    sp.markClone(uid)
                    addSprint(
                        repo.toSprint(sp, sprintUseCases.cloneSprint(sp))
                    ) { it, id ->
                        viewModelScope.launch {
                            _state.value = state.value.copy(
                                sprints = it,
                                selectedSprint = it.last(),
                                currentSprint = repo.toSprint(it.last()),
                                sprintTitle = it.last().title ?: "Untitled Sprint",
                                sprintId = id,
                                sprintCount = it.lastIndex + 1,
                                index = it.lastIndex,
                            )
                        }
                        it.last().id?.let { selectedSprintId.value = it }
                        selectedSprintTitle.value = it.last().title ?: "Untitled Sprint"
                        numSprints.value = numSprints.value + 1
                        Statics.recentSprints.add(it.last())
                        newId = id
                    }
                    event.tasks?.forEach {
                        it.task.markClone(newId, uid)
                        it.subtasks.forEach { s -> s.markClone(newId, uid) }
                        val ids = sprintUseCases.cloneSprint.clone(it)
                        clone(Statics.Cloner.clonedTasks[0], Statics.Cloner.clonedSubtasks, ids,
                           ) { st, sbS ->
                            Statics.recentStories.add(st)
                            Statics.recentSubTasks.addAll(sbS)
                        }
                    }
                    Statics.recentSprints = Statics.recentSprints.distinctBy { s -> s.id }.toMutableList()
                    Statics.recentStories = Statics.recentStories.distinctBy { s -> s.id }.toMutableList()
                    Statics.recentSubTasks = Statics.recentSubTasks.distinctBy { s -> s.id }.toMutableList()
                    sync()
                }
            }
            is SprintEvent.QuickAdd -> {
                updateSprintList()
                viewModelScope.launch {
                    try {
                        val x = sprintUseCases.loadSprints.count()
                        val y = SprintRoom(
                            title = "Sprint ${x + 1}",
                            desc = "Sprint ${x + 1} Description",
                            info = SprintInfo(
                                createdBy = authRepo.getUID(),
                                creatorID = repo.matchUID().first
                            ),
                            uri = repo.matchUID().second,
                            active = true,
                            completed = false,
                        )
//                        _state.value = state.value.copy(
//                            sprintId = x + 1L,
//                            sprintTitle = y.title,
//                            currentSprint = y,
//                            sprintCount = x + 1
//                        )
                        addSprint(
                            repo.toSprint(y, sprintUseCases.quickAddSprint(y))
                        ) { it, id ->
                            viewModelScope.launch {
                                _state.value = state.value.copy(
                                    sprints = it,
                                    selectedSprint = it.last(),
                                    currentSprint = repo.toSprint(it.last()),
                                    sprintTitle = it.last().title ?: "Untitled Sprint",
                                    sprintId = id,
                                    sprintCount = it.size,
                                    index = it.lastIndex,
                                )
                                it.last().id?.let { id -> selectedSprintId.value = id }
                                selectedSprintTitle.value = it.last().title ?: "Untitled Sprint"
                                numSprints.value = numSprints.value + 1
                            }
                        }
                        recycleBinSprint = SprintWithUsersAndTasks(y, emptyList(), emptyList())
                        _eventFlow.emit(UIEvent.AddSprint)
                        sync()
                    }
                    catch (e: InvalidSprintException) {
                        _eventFlow.emit(UIEvent.ShowSNB(
                            msg = e.message?: "Error while quick adding sprint!!")
                        )
                    }
                }
                updateSprintList()
            }
            is SprintEvent.RenameSprint -> {
                viewModelScope.launch {
                    _state.value = state.value.copy(
                        sprintTitle = event.name,
                        currentSprint = event.sprint,
                    )
                    selectedSprintTitle.value = event.name
                    sprintUseCases.quickAddSprint(event.sprint)
                    _eventFlow.emit(UIEvent.Reload)
                    updateSprintList()
                }
            }
            is SprintEvent.Refresh -> {
                refresh(event.id)
            }
            is SprintEvent.Reload -> {
                refresh(selectedSprintId.value)
            }
            is SprintEvent.StartPause -> {
                viewModelScope.launch {
                    try {
                        _state.value = state.value.copy(
                            currentSprint = event.sprint,
                            sprintActive = event.setStarted || event.sprint.active,
                            sprintStarted = event.setStarted,
                            sprintPaused = !event.setStarted,
                            timeRem = event.sprint.info.countdown
                        )
                        _started.value = !started.value
                        _state.value.currentSprint?.active = event.setStarted
                        if (event.setStarted) {
                            _state.value.currentSprint?.start(event.days)
                            event.sprint.start(event.days)
                        } else {
                            _state.value.currentSprint?.pause()
                            event.sprint.pause()
                        }
                        updateSprint( sprintUseCases.quickAddSprint(event.sprint), event.setStarted )
                        _eventFlow.emit(UIEvent.StartPause(event.setStarted, event.days))
                    }
                    catch (e: InvalidSprintException) {
                        _eventFlow.emit(UIEvent.ShowSNB(msg = e.message?: "Invalid sprint index!"))
                    }

                }
            }
            is SprintEvent.StartPauseTEST -> {
                viewModelScope.launch {
                    try {
                        _state.value = state.value.copy(
                            current = event.sprint,
                            selectedSprint = event.sprint,
                            sprintId = event.sprint.id!!,
                            sprintTitle = event.sprint.title.orEmpty(),
                            sprintActive = event.setStarted || event.sprint.active == true,
                            sprintStarted = event.setStarted,
                            sprintPaused = !event.setStarted,
                            timeRem = event.sprint.countdown
                        )
                        _started.value = !started.value
                        _state.value.currentSprint?.active = event.setStarted
                        if (event.setStarted) {
                            _state.value.currentSprint?.start(event.days)
                            event.sprint.start(event.days)
                        } else {
                            _state.value.currentSprint?.pause()
                            event.sprint.pause()
                        }
                        updateSprint( sprintUseCases.quickAddSprint(repo.toSprint(event.sprint)), event.setStarted )
                        _eventFlow.emit(UIEvent.StartPause(event.setStarted, event.days))
                    }
                    catch (e: InvalidSprintException) {
                        _eventFlow.emit(UIEvent.ShowSNB(msg = e.message?: "Invalid sprint index!"))
                    }

                }
            }
            else -> {

            }
        }
    }

    sealed class UIEvent {
        data class ChangeSprint(val id: Long): UIEvent()
        data class StartPause(val bool : Boolean, val days: Int): UIEvent()
        data class ShowSNB(val msg: String): UIEvent()
        data class Refresh(val id: Long): UIEvent()
        object AddSprint: UIEvent()
        object AddSubtask: UIEvent()
        object Reload: UIEvent()
    }

//    private fun getActiveSprint(taskOrder: TaskOrder) {
//        getTasksJob?.cancel()
//        getTasksJob = boardUseCases.loadActiveSprint(taskOrder)
//            .onEach { sprint ->
//                _state.value = state.value.copy(
//                    allTasks = sprint.tasks,
//                    tasks = sprint.tasks.map {t -> t.task},
//                    taskToSubtasks = sprint.tasks.associate { t ->
//                        Pair(t.task, t.subtasks)
//                    },
//                    taskOrder = taskOrder
//                )
//            }
//            .launchIn(viewModelScope)
//    }

    private suspend fun sync(idx: Int = state.value.sprints.lastIndex) {

        repo.fetchFirestoreChanges { sps, fireUsers, loggedInUser, stores, subtasks, map, allT, bin ->
            viewModelScope.launch {
                val sprints =
                    sps.apply { sps.filter { s -> (s.active != false || s.expired) && !s.completed } }
                if (index.value > idx) index.value = max(min(idx, sprints.size - 1), 0)
                _recycle.value = bin
                _state.value = state.value.copy(
                    sprints = sprints,
                    users = fireUsers,
                    stories = stores,
                    subs = subtasks,
                    fullStories = map,
                    sprintCount = sprints.size,
                    index = index.value,
                    sprintId = if (sprints.isNotEmpty()) sprints[index.value].id
                        ?: (index.value + 1L) else -1L,
                    sprintTitle = if (sprints.isNotEmpty()) sprints[index.value].title
                        ?: "Untitled Sprint #${state.value.sprintId}" else "No Sprint",
                    selectedSprint = if (sprints.isNotEmpty()) sprints[index.value] else null,
                    started = if (sprints.isNotEmpty()) sprints[index.value].started else false,
                    active = if (sprints.isNotEmpty()) sprints[index.value].active else false,
                    paused = if (sprints.isNotEmpty()) sprints[index.value].paused else false,
                    timeRem = if (sprints.isNotEmpty()) sprints[index.value].countdown else 21,
                    totalPoints = if (sprints.isNotEmpty()) sprints[index.value].totalPoints else 0,
                    remPoints = if (sprints.isNotEmpty()) sprints[index.value].remPoints else 0,
                    currentUser = loggedInUser,
                    currentSprint = if (sprints.isNotEmpty()) repo.toSprint(sprints[index.value]) else null,
                    storyMap = if (stores.isNotEmpty()) stores.associateWith { story ->
                        repo.toTask(
                            story
                        )
                    } else emptyMap(),
                    subtaskMap = if (subtasks.isNotEmpty()) subtasks.associateWith { sub ->
                        repo.toSub(
                            sub
                        )
                    } else emptyMap(),
                    sprintMap = if (sprints.isNotEmpty()) sprints.associateWith { sp ->
                        repo.toSprint(
                            sp
                        )
                    } else emptyMap(),
                    subtasks = repo.toSubtaskList(subtasks),
                    tasks = repo.toTaskList(stories),
                    rooms = repo.toSprintRoomList(sprints),
                    allTasks = allT,
                    flagForUpdate = true
                )

                list.clear()
                list.addAll(sprints)
                _users.clear()
                _users.addAll(fireUsers)
                stories.clear()
                stories.addAll(stores)
                subTasks.clear()
                subTasks.addAll(subtasks)
                selectedSprint.value = if (sprints.isNotEmpty()) sprints[index.value] else Sprint()
                selectedSprintId.value = if (sprints.isNotEmpty()) state.value.sprintId else -1L
                selectedSprintTitle.value =
                    if (sprints.isNotEmpty()) state.value.sprintTitle else "No Sprint"
            }
        }
        subtasks.clear()
        subtasks.addAll(state.value.subtasks)
//        repo.loadFireStoreData { sprints, fireUsers, loggedInUser, stores, subtasks->
//            _state.value = state.value.copy(
//                sprints = sprints.orEmpty(),
//                users = fireUsers.orEmpty(),
//                stories = stores.orEmpty(),
//                subs = subtasks.orEmpty(),
//                sprintCount = sprints?.size?: 0,
//                index = index.value,
//                sprintId = if (!sprints.isNullOrEmpty()) sprints[index.value].id ?: (index.value + 1L) else -1L,
//                sprintTitle = if (!sprints.isNullOrEmpty()) sprints[index.value].title ?: "Untitled Sprint #${state.value.sprintId}" else "No Sprint",
//                selectedSprint = if (!sprints.isNullOrEmpty()) sprints[index.value] else null,
//                started = if (!sprints.isNullOrEmpty()) sprints[index.value].started else false,
//                active = if (!sprints.isNullOrEmpty()) sprints[index.value].active else false,
//                paused = if (!sprints.isNullOrEmpty()) sprints[index.value].paused else false,
//                timeRem = if (!sprints.isNullOrEmpty()) sprints[index.value].countdown else 21,
//                totalPoints = if (!sprints.isNullOrEmpty()) sprints[index.value].totalPoints else 0,
//                remPoints = if (!sprints.isNullOrEmpty()) sprints[index.value].remPoints else 0,
//                currentUser = loggedInUser,
//                currentSprint = if (!sprints.isNullOrEmpty()) repo.toSprint(sprints[index.value]) else null,
//                storyMap = if(!stores.isNullOrEmpty()) stores.associateWith { story -> repo.toTask(story) } else emptyMap(),
//                subtaskMap = if(!subtasks.isNullOrEmpty()) subtasks.associateWith { sub -> repo.toSub(sub) } else emptyMap(),
//                sprintMap = if(!sprints.isNullOrEmpty()) sprints.associateWith { sp -> repo.toSprint(sp) } else emptyMap(),
//                subtasks = repo.toSubtaskList(subtasks).orEmpty(),
//                tasks = repo.toTaskList(stories).orEmpty(),
//                rooms = repo.toSprintRoomList(sprints).orEmpty(),
//                flagForUpdate = true
//            )
//
//            list.clear()
//            list.addAll(sprints.orEmpty())
//            _users.clear()
//            _users.addAll(fireUsers.orEmpty())
//            stories.clear()
//            stories.addAll(stores.orEmpty())
//            subTasks.clear()
//            subTasks.addAll(subtasks.orEmpty())
//            selectedSprintId.value = if (!sprints.isNullOrEmpty()) state.value.sprintId else -1L
//            selectedSprintTitle.value = if (!sprints.isNullOrEmpty()) state.value.sprintTitle else "No Sprint"
//
//        }
//        subtasks.clear()
//        subtasks.addAll(state.value.subtasks)
    }

    suspend fun syncFromCloud(
        onDone: (Boolean) -> Unit = { state.value.flagForUpdate = true }
    ) {

        try {
            sync()
            for (sprint in state.value.rooms/*list.map { sp -> repo.toSprint(sp) }*/ ) {
                room.upsertSprint(sprint)
            }
            for (task in state.value.tasks) {
                room.upsertTask(task)
            }
            room.upsertSubtasks(state.value.subtasks)
            refresh(state.value.sprintId)
            onDone.invoke(true)
        } catch (e: Exception) {
            onDone.invoke(false)
            e.printStackTrace()
        }
    }

    suspend fun syncToCloud(
        onDone: (Boolean) -> Unit = { state.value.flagForUpdate = true }
    ) {
        try {
            sprintUseCases.loadSprints().onEach { job ->
                job.forEach { sp ->
                    updateSprint(sp.sprint.sprintId!!, sp.sprint) {

                    }
                    taskUseCases.getTasks(sp.sprint.sprintId?: 0).onEach { jub ->
                        jub.forEach { task ->
                            repo.saveStoryHelper(task.task, task.task.id!!)
                            task.subtasks.forEach { sb ->
                                repo.cloudUpdateSubTask(sb.subId!!, sb) {

                                }

                            }
                        }
                    }
                }

            }

            sync()
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB("Successfully uploaded local data to Firestore."))
            }
        }
        catch(e: Exception) {
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB(e.message?: "An error occurred while uploading local data to Firestore."))
            }
        }
    }

    suspend fun updateSprint(
        id: Long,
        input: SprintRoom,
        map: Map<String, Any> = repo.getUpdatedSprint(input),
        onDone: (Sprint) -> Unit
    ) {
        repo.matchID("subtasks", id) { res ->
            if (res == null) {
                viewModelScope.launch {
                    addSprint(repo.toSprint(input, input.sprintId)) { it, id ->
                        sprintsList.add(it.last())
                    }
                }
                return@matchID
            }
            else { viewModelScope.launch {
                repo.bigUpdateSprintHelper(res, map) { sp, id ->
                    sprintsList[sprintsList.indexOf(sprintsList.find { s -> s.id == id })] = sp!!
                    _state.value = state.value.copy(
                        sprints = sprintsList,
                        sprintCount = sprintsList.size
                        )
                    onDone.invoke(sp)
                }
            } }
        }
    }

    private fun getTasks(id: Long, taskOrder: TaskOrder) {
        getTasksJob?.cancel()
        getTasksJob = taskUseCases.getTasks(id, taskOrder).filterNotNull()
            .onEach { tasks ->
                _state.value = state.value.copy(
                    allTasks = tasks,
                    tasks = tasks.map {t -> t.task},
                    taskToSubtasks = tasks.associate { t ->
                        Pair(t.task, t.subtasks)
                    },
                    totalPoints = tasks.sumOf { t -> t.task.points },
                    remPoints = tasks.map{t -> t.task }.filter {t -> !t.done} .sumOf { t -> t.points },
                    taskOrder = taskOrder,
                )
                selectedSprintId.value = id
                _points.value = state.value.totalPoints
                _rem.value = state.value.remPoints
            }
            .launchIn(viewModelScope)
    }

    private fun getSprint(id: Long) {
        getTasksJob?.cancel()
        getTasksJob = sprintUseCases.getSprint.basic(id).filterNotNull()
            .onEach { sp ->
                val upd = sp.update()
                _state.value = state.value.copy(
                    sprintId = id,
                    sprintTitle = sp.title,
                    flagForUpdate = upd,
                    currentSprint = sp,
                    sprintActive = sp.active,
                    started = !sp.info.paused,
                    timeRem = sp.info.countdown,
                    elapsed = sp.info.elapsed
                )
                sp.info.remPoints = state.value.remPoints
                sp.info.totalPoints = state.value.totalPoints
                sp.sprintId?.let { selectedSprintId.value = it }
                selectedSprintTitle.value = sp.title
                selectedSprintId.value = sp.sprintId!!
                _started.value = sp.active
                if (sp.active) {
                    _cd.value = sp.info.countdown
                    _state.value.currentSprint?.let {
                        it.info.countdown = sp.info.countdown
                        it.info.elapsed = sp.info.elapsed
                        _points.value = sp.info.totalPoints
                        _rem.value = sp.info.remPoints
                    }
                }
                if (upd) {
                    _elapsed.value = sp.info.elapsed
                    sprintUseCases.quickAddSprint.update(sp)
                }
            }
            .launchIn(viewModelScope)
        updateSprintList()
    }

    private fun updateSprintList() {
        getTasksJob?.cancel()
        getTasksJob = sprintUseCases.loadSprints.update().filterNotNull()
            .onEach { sp -> sp.apply { sp.filter { s -> s.active && !s.completed } }
                _state.value = state.value.copy(
                    sprintCount = sp.size,
                    sprintList = sp,
                    availSprints = sp.associate { s -> Pair (s.sprintId!!, s.title) }
                )
                numSprints.value = sp.size + 0L
            }
            .launchIn(viewModelScope)
    }

    private fun refresh(id: Long, order: TaskOrder = state.value.taskOrder) {
        updateSprintList()
        getSprint(id)
        getTasks(id, order)
    }

    fun reload(){
        state.value.flagForUpdate = true
        state.value.flagForUpdate = false
    }

    fun reSort(tasks: List<TaskAndSubtasks>, taskOrder: TaskOrder): List<TaskAndSubtasks> {
        return when (taskOrder.type) {
            is OrderType.Ascending -> {
                when(taskOrder) {
                    is TaskOrder.Priority -> tasks.sortedBy { it.task.priority.ordinal }
                    is TaskOrder.Title -> tasks.sortedBy { it.task.title.lowercase() }
                    is TaskOrder.Points -> tasks.sortedBy { it.task.points }
                    is TaskOrder.Date -> tasks.sortedBy { it.task.timestamp }
                    is TaskOrder.Assignee -> tasks.sortedBy { it.task.assignee }
                    is TaskOrder.Reporter -> tasks.sortedBy { it.task.reporter }
                    is TaskOrder.Creator -> tasks.sortedBy { it.task.createdBy }
                    is TaskOrder.Created -> tasks.sortedBy { it.task.creDate }
                    is TaskOrder.Accessed -> tasks.sortedBy { it.task.accDate }
                    is TaskOrder.Content -> tasks.sortedBy { it.task.content.lowercase() }
                    is TaskOrder.Color -> tasks.sortedBy { it.task.color }
                    is TaskOrder.ID -> tasks.sortedBy { it.task.taskId }
                    is TaskOrder.Size -> tasks.sortedBy { it.subtasks.size }
                    else -> tasks.sortedBy { it.task.priority.ordinal }
                }
            }
            is OrderType.Descending -> {
                when(taskOrder) {
                    is TaskOrder.Priority -> tasks.sortedByDescending { it.task.priority.ordinal }
                    is TaskOrder.Title -> tasks.sortedByDescending { it.task.title.lowercase() }
                    is TaskOrder.Points -> tasks.sortedByDescending { it.task.points }
                    is TaskOrder.Date -> tasks.sortedByDescending { it.task.timestamp }
                    is TaskOrder.Assignee -> tasks.sortedByDescending { it.task.assignee }
                    is TaskOrder.Reporter -> tasks.sortedByDescending { it.task.reporter }
                    is TaskOrder.Creator -> tasks.sortedByDescending { it.task.createdBy }
                    is TaskOrder.Created -> tasks.sortedByDescending { it.task.creDate }
                    is TaskOrder.Accessed -> tasks.sortedByDescending { it.task.accDate }
                    is TaskOrder.Content -> tasks.sortedByDescending { it.task.content.lowercase() }
                    is TaskOrder.Color -> tasks.sortedByDescending { it.task.color }
                    is TaskOrder.ID -> tasks.sortedByDescending { it.task.taskId }
                    is TaskOrder.Size -> tasks.sortedByDescending { it.subtasks.size }
                    else -> tasks.sortedByDescending { it.task.priority.ordinal }
                }
            }
        }
    }

    private suspend fun getUsersBySprint(
        id: Long,
        onDone: (List<FireUser>) -> Unit
    ) {
        _users.clear()
        val query = userRef
            .whereArrayContains("sprints", id)
            .orderBy("name")
            .get()
            .await()
        if (query.documents.isNotEmpty()) {
            try {
                for (doc in query) {
                    val usr = doc.toObject<FireUser>()
                    _users.add(usr)
                }
                onDone.invoke(_users)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _eventFlow.emit(UIEvent.ShowSNB(e.message?: "Error querying users for sprint."))
                }
            }
        }
    }

    private suspend fun updateDragValues(task: Task, sub: Subtask, stat: String) {
        val query = storyRef
            .whereEqualTo("id", task.taskId)
            .get()
            .await()
        if (query.documents.isNotEmpty()) {
            for (doc in query) {
                try {
                    storyRef.document(doc.id)
                        .update("status", task.status, "done", task.done)
                        .await()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _eventFlow.emit(UIEvent.ShowSNB(e.message?: "Error saving task drag changes to firestore."))
                    }
                }
            }
        }
        val snap = subtaskRef
            .whereEqualTo("id", sub.subId)
            .whereNotEqualTo("status", stat)
            .get()
            .await()
        if (snap.documents.isNotEmpty()) {
            for (doc in snap) {
                try {
                    subtaskRef.document(doc.id)
                        .update("status", sub.status, "done", sub.done)
                        .await()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _eventFlow.emit(UIEvent.ShowSNB(e.message?: "Error saving subtask drag changes to firestore."))
                    }
                }
            }
        }

    }

    private suspend fun update(
        collection: String,
        id: Long,
        field: String,
        value: Any,
        field2: String? = null,
        value2: Any? = null,
        field3: String? = null,
        value3: Any? = null,
        field4: String? = null,
        value4: Any? = null,
        onRes: (Any) -> Unit
    ) {
        var idx = 0
        var result: Any
//            when(collection) {
//                "sprints" -> {
//                    Sprint()
//                }
//                "users" -> {
//                    FireUser()
//                }
//                "stories" -> {
//                    Story()
//                }
//                "subtasks" -> {
//                    SubTask()
//                }
//                else -> {
//                    throw Exception("Unsupported collection name")
//                }
//            }
        val ref: CollectionReference =
            when(collection) {
                "sprints" -> {
                    sprintRef
                }
                "users" -> {
                    userRef
                }
                "stories" -> {
                    storyRef
                }
                "subtasks" -> {
                    subtaskRef
                }
                else -> {
                    throw Exception("Unsupported collection reference")
                }
            }
        //val ref = Firebase.firestore.collection(collection)
        val snap = ref //Firebase.firestore.collection(collection)
            .whereEqualTo("id", id)
            .get()
            .await()
        if (snap.documents.isNotEmpty()) {
            for (doc in snap) {
                try {
                    when(collection) {
                        "sprints" -> {
                            result = doc.toObject<Sprint>()
                            idx = list.indexOf(result)
                        }
                        "users" -> {
                            result = doc.toObject<FireUser>()
                            idx = _users.indexOf(result)
                        }
                        "stories" -> {
                            result = doc.toObject<Story>()
                            idx = stories.indexOf(result)
                        }
                        "subtasks" -> {
                            result = doc.toObject<SubTask>()
                            idx = subTasks.indexOf(result)
                        }
                        else -> {
                            throw Exception("Unsupported collection reference")
                        }
                    }
                    Firebase.firestore.runBatch { batch ->
                        val docRef = ref.document(doc.id)
                        batch.update(docRef,
                            field, value,
                            field2?.let { field2 }, value2?.let { value2 },
                            field3?.let { field3 }, value3?.let { value3 },
                            field4?.let { field4 }, value4?.let { value4 },
                        )
                    }.await()
                    when(collection) {
                        "sprints" -> {
                            result = doc.toObject<Sprint>()
                            list[idx] = result
                        }
                        "users" -> {
                            result = doc.toObject<FireUser>()
                            _users[idx] = result
                        }
                        "stories" -> {
                            result = doc.toObject<Story>()
                            stories[idx] = result
                        }
                        "subtasks" -> {
                            result = doc.toObject<SubTask>()
                            subTasks[idx] = result
                        }
                        else -> {
                            throw Exception("Unsupported collection reference")
                        }
                    }
                    onRes.invoke(result)
//                    field2?.let {
//                        ref.document(doc.id)
//                            .update(field2, value2!!)
//                        field3?.let {
//                            ref.document(doc.id)
//                                .update(field3, value3!!)
//                            field4?.let {
//                                ref.document(doc.id)
//                                    .update(field4, value4!!)
//                            }
//                        }
//                    }?.await()

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _eventFlow.emit(UIEvent.ShowSNB(
                            e.message?: "Error updating $collection value. Invalid entry"))
                    }
                }
            }
        }
    }

    private fun getSubtasks(id: Long) {
        getTasksJob?.cancel()
        getTasksJob = taskUseCases.getSubtasks(id)
            .onEach { subtasks ->
                _state.value = state.value.copy(
                    subtasks = subtasks
                )
            }
            .launchIn(viewModelScope)
    }

    private suspend fun addSubtask(sub: Subtask, id: Long? = null) {
        try {
            val subtask = repo.toSub(sub, id)
            subtaskRef.add(subtask).await()
            subTasks.add(subtask)
            Statics.newSubtasks.add(subtask)
            _state.value = state.value.copy(
                subs = subTasks
            )
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB("Story/task online data successfully saved."))
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB(e.message?: "Error saving online story data"))
            }
        }
    }

    private suspend fun addSubtask(sb: SubTask, id: Long? = null) {
        try {
            subtaskRef.add(sb).await()
            subTasks.add(sb)
            Statics.newSubtasks.add(sb)
            _state.value = state.value.copy(
                subs = subTasks
            )
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB("Story/task online data successfully saved."))
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB(e.message?: "Error saving online story data"))
            }
        }
    }

    private suspend fun deleteSubtask(id: Long) {

        val query = subtaskRef
            .whereEqualTo("id", id)
            .get().await()
        if (query.documents.isNotEmpty()) {
            for (doc in query) {
                try {
                    val subtask = doc.toObject<SubTask>()
                    subtaskRef.document(doc.id)
                        .delete().await()
                    subTasks.remove(subtask)
                    recycleSub = subtask
                    Statics.oldSubtasks.add(subtask)
                    Statics.recentSubTask.value = subtask
                    withContext(Dispatchers.Main) {
                        _eventFlow.emit(UIEvent.ShowSNB(
                            "Successfully deleted firestore subtask data."
                        ))
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _eventFlow.emit(UIEvent.ShowSNB(
                            "Failed to delete firestore subtask data."
                        ))
                    }
                }
            }
        }

    }

    private suspend fun deleteStory(id: Long) {

        val query = storyRef
            .whereEqualTo("id", id)
            .get().await()
        if (query.documents.isNotEmpty()) {
            for (doc in query) {
                try {
                    val story = doc.toObject<Story>()
                    storyRef.document(doc.id)
                        .delete().await()
                    stories.remove(story)
                    recycleStory = story
                    Statics.oldStories.add(story)
                    Statics.recentStory.value = story
                    withContext(Dispatchers.Main) {
                        _eventFlow.emit(UIEvent.ShowSNB(
                            "Successfully deleted firestore subtask data."
                        ))
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _eventFlow.emit(UIEvent.ShowSNB(
                            "Failed to delete firestore subtask data."
                        ))
                    }
                }
            }
        }

    }

    private suspend fun deleteAllStories(sid: Long) {
        val query = storyRef
            .whereEqualTo("sid", sid)
            .get().await()
        if (query.documents.isNotEmpty()) {
            for (doc in query) {
                try {

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _eventFlow.emit(UIEvent.ShowSNB(
                            "Failed to delete firestore task data during sprint deletion."
                        ))
                    }
                }
            }
        }
    }

    private suspend fun deleteAllSubtasks(tid: Long) {
        val query = subtaskRef
            .whereEqualTo("storyId", tid)
            .get().await()
        if (query.documents.isNotEmpty()) {
            for (doc in query) {
                try {
                    val subtask = doc.toObject<SubTask>()
                    subtaskRef.document(doc.id)
                        .delete().await()
                    subTasks.remove(subtask)
                    recycleSubList.add(subtask)
                    Statics.oldSubtasks.add(subtask)
                    Statics.recentSubTasks.add(subtask)
                    withContext(Dispatchers.Main) {
                        _eventFlow.emit(UIEvent.ShowSNB(
                            "Successfully deleted firestore multiple subtask data."
                        ))
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _eventFlow.emit(UIEvent.ShowSNB(
                            "Failed to delete firestore subtask data during story deletion."
                        ))
                    }
                }
            }
        }
    }

    private fun retrieveSprints(
        onSuccess: (List<Sprint>) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            list.clear()
            val querySnapshot = sprintRef.get().await()
            val sb = StringBuilder()
            querySnapshot.documents.forEachIndexed { i, doc ->
                val sprint = doc.toObject<Sprint>()
                sb.append("$sprint\n")
                list.add(sprint!!)
            }
            onSuccess.invoke(list)
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB(
                    "Successfully retrieved online sprint data."
                ))
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB(
                    "Failed to retrieve online sprint data."
                ))
            }
        }
    }

    private fun retrieveStories(
        onSuccess: (List<Story>) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            stories.clear()
            val querySnapshot = storyRef.get().await()
            val sb = StringBuilder()
            querySnapshot.documents.forEachIndexed { i, doc ->
                val story = doc.toObject<Story>()
                sb.append("$story\n")
                stories.add(story!!)
            }
            onSuccess.invoke(stories)
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB(
                    "Successfully retrieved online story data."
                ))
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB(
                    "Failed to retrieve online story data."
                ))
            }
        }
    }



    private fun retrieveSubTasks(
        onResult: (List<SubTask>) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val res = mutableStateListOf<SubTask>()
            val querySnapshot = subtaskRef.get().await()
            val sb = StringBuilder()
            querySnapshot.documents.forEachIndexed { i, doc ->
                val subtask = doc.toObject<SubTask>()
                sb.append("$subtask\n")
                res.add(subtask!!)
            }
            onResult.invoke(res)
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB(
                    "Successfully retrieved online sub-task data."
                ))
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB(
                    "Failed to retrieve online sub-task data."
                ))
            }
        }
    }

    private fun getActiveSprintCount(
        onSuccess: (Long) -> Unit
    ): Any? {

        viewModelScope.launch {
            val countQ = sprintRef
                .whereNotEqualTo("completed", true)
                .count()
            countQ.get(AggregateSource.SERVER).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    Log.d(tag,"Aggregate count returned ${snapshot.count} sprint")
                    onSuccess.invoke(task.result.count)
                } else {
                    Log.d(tag, "Count failed: ", task.exception)
                }

            }
        }
        return null
    }

    private fun addSprintHelper(id: Long = 0): Sprint {
//        var x = 0L
//        getActiveSprintCount { c ->
//            x = c
//        }
        return when(id) {
            0L -> {
                Sprint(
                    id = 0,
                    sid = 0,
                    title = "Dummy Sprint 0",
                    desc = "Dummy Sprint Placeholder Description",
                    active = false,
                    completed = true,
                    status = "Open",
                    createdBy = authRepo.getUID()
                )
            }
            1L -> {
                Sprint(
                    id = 1,
                    sid = 1,
                    title = "Sprint 1",
                    desc = "First Sprint Placeholder Description",
                    active = true,
                    completed = false,
                    status = "Open",
                    createdBy = authRepo.getUID()
                )
            }
            else -> {
                Sprint(
                    id = id,
                    sid = id,
                    title = "Sprint $id",
                    desc = "Sprint $id Firestore Description",
                    active = true,
                    completed = false,
                    createdBy = authRepo.getUID()
                )
            }
        }
    }

    private suspend fun addSprint(
        sprint: Sprint,
        onDone: (List<Sprint>, Long) -> Unit
    ) {
        try {
            sprintRef.add(sprint).await()
            list.add(sprint)
            Statics.newSprints.add(sprint)
            onDone.invoke(list, sprint.id!!)
            withContext(Dispatchers.Main) {
                _eventFlow.emit(UIEvent.ShowSNB("Sprint data successfully saved to Firestore"))
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
                _eventFlow.emit(UIEvent.ShowSNB(e.message?: "Error saving online sprint data"))
            }
        }


    }

    private suspend fun clone(
        tk: Task,
        sbS: List<Subtask>?,
        ids: Pair<Long, List<Long>>,
        onDone: (Story, List<SubTask>) -> Unit
    ) {
        val st = repo.toStory(tk, ids.first)
        storyRef.add(st).await()
        _stories.add(st)
        Statics.newStories.add(st)
        sbS?.forEachIndexed { index, s ->
            val sb = repo.toSub(s, ids.second[index])
            subtaskRef.add(sb).await()
            _subTasks.add(sb)
            Statics.newSubtasks.add(sb)

        }
    }

    private suspend fun clone(
        tS: List<TaskAndSubtasks>?,
        ids: Map<Long, List<Long>>,
        onDone: (List<Story>?) -> Unit
    ) {
        tS?.forEachIndexed { index, t ->
            val st = repo.toStory(t.task, ids.keys.first())
            storyRef.add(st).await()
            _stories.add(st)
            Statics.newStories.add(st)

        }
    }

    private suspend fun clone(
        sbS: List<Subtask>?,
        ids: List<Long>?,
        onDone: (List<SubTask>?) -> Unit
    ) {
        sbS?.forEachIndexed { index, s ->
            val sb = repo.toSub(s, ids?.get(index))
            subtaskRef.add(sb).await()
            _subTasks.add(sb)
            Statics.newSubtasks.add(sb)

        }
    }

    private suspend fun updateSprint(id: Long?, started: Boolean) {
        if (id == null || id < 0) { return }
        val querySnapshot = sprintRef
            .whereEqualTo("id", id)
            .limit(1)
            .get()
            .await()
        val sb = StringBuilder()
        querySnapshot.documents.forEachIndexed { i, doc ->
            sprintRef.document(doc.id)
                .update("started", started, "paused", !started)
                .await()
            val sp = doc.toObject<Sprint>()
            sp?.let {
                selectedSprint.value = it
                sb.append("$sp\n")
            }
        }
    }

    private fun sortStories(order: Pair<String, Any> = processSortOrder(state.value.taskOrder)) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot = storyRef
                    .orderBy(order.first, Query.Direction.valueOf(order.second as String))
                    .get()
                    .await()
                val sb = StringBuilder()
                querySnapshot.documents.forEachIndexed { i, doc ->
                    val story = doc.toObject<Story>()
                    sb.append("$story\n")
                }
                withContext(Dispatchers.Main) {
                    _eventFlow.emit(UIEvent.ShowSNB(
                        "Successfully retrieved online story data."
                    ))
                }
            } catch(e: Exception) {
                withContext(Dispatchers.Main) {
                    _eventFlow.emit(UIEvent.ShowSNB(
                        "Failed to retrieve online story data."
                    ))
                }
            }
        }
    }

    private fun processSortOrder(order: TaskOrder): Pair<String, Any> {
        return when (order.type) {
            is OrderType.Ascending -> {
                when(order) {
                    is TaskOrder.Priority -> Pair("priority", Query.Direction.ASCENDING)
                    is TaskOrder.Title -> Pair("title", Query.Direction.ASCENDING)
                    is TaskOrder.Points -> Pair("points", Query.Direction.ASCENDING)
                    is TaskOrder.Date ->  Pair("modDate", Query.Direction.ASCENDING)
                    is TaskOrder.Assignee ->  Pair("assignee", Query.Direction.ASCENDING)
                    is TaskOrder.Content -> Pair("body", Query.Direction.ASCENDING)
                    is TaskOrder.Color -> Pair("color", Query.Direction.ASCENDING)
                    is TaskOrder.ID -> Pair("id", Query.Direction.ASCENDING)
                    else -> Pair("createdBy", Query.Direction.DESCENDING)
                }
            }
            is OrderType.Descending -> {
                when(order) {
                    is TaskOrder.Priority -> Pair("priority", Query.Direction.DESCENDING)
                    is TaskOrder.Title -> Pair("title", Query.Direction.DESCENDING)
                    is TaskOrder.Points -> Pair("points", Query.Direction.DESCENDING)
                    is TaskOrder.Date -> Pair("modDate", Query.Direction.DESCENDING)
                    is TaskOrder.Assignee -> Pair("assignee", Query.Direction.DESCENDING)
                    is TaskOrder.Content -> Pair("body", Query.Direction.DESCENDING)
                    is TaskOrder.Color -> Pair("color", Query.Direction.DESCENDING)
                    is TaskOrder.ID -> Pair("id", Query.Direction.DESCENDING)
                    else -> Pair("createdBy", Query.Direction.DESCENDING)
                }
            }
        }
    }

}