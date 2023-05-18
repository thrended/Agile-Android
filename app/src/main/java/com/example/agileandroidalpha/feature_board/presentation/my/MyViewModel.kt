package com.example.agileandroidalpha.feature_board.presentation.my

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
import com.example.agileandroidalpha.feature_board.presentation.backlog.BacklogEvent
import com.example.agileandroidalpha.feature_board.presentation.backlog.BacklogState
import com.example.agileandroidalpha.feature_board.presentation.sprint.SprintState
import com.example.agileandroidalpha.feature_board.presentation.tasks.TasksState
import com.example.agileandroidalpha.firebase.repository.AuthRepo
import com.example.agileandroidalpha.firebase.repository.FirestoreRepository
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
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
    val state = _state.asStateFlow()

    private val _tasksState = MutableStateFlow(TasksState())
    val tasksState = _tasksState.asStateFlow()

    private val _sprintState = MutableStateFlow(SprintState())
    val sprintState = _sprintState.asStateFlow()

    private var _excluded = mutableStateOf(0L)
    val excluded: State<Long> = _excluded

    private var focusId: Long? = null

    private var currentId: Long? = null

    private var getBacklogJob: Job? = null

    private var me: FireUser? = null

    init {
        loadMyActivities(SprintOrder.Default(OrderType.Ascending))
        viewModelScope.launch {
            if (user == null || user.isAnonymous) return@launch
            repo.loadFireStoreData() { sps, fireUsers, loggedInUser, stores, subtasks, map, allT ->
                me = loggedInUser
                if (me == null) return@loadFireStoreData
                val sprints = sps?.apply {
                    sortedByDescending { sp -> sp.modDate }
                }
                val sts = stores?.apply {
                    filter { s -> s.uid == me!!.uid || s.id == me!!.id || s.assId == me!!.id ||
                    s.repId == me!!.id || s.repUid == me!!.uid}
                }
                val sbs = subtasks?.apply {
                    filter { s -> s.assUri == me!!.photo || s.repUri == me!!.photo || s.assId == me!!.id ||
                            s.repId == me!!.id || s.repUid == me!!.uid || s.uid == me!!.uid || s.id == me!!.id
                            || s.uri == me!!.photo
                    }
                }
                _state.value = state.value.copy(
                    sprints = sprints.orEmpty(),
                    users = fireUsers.orEmpty(),
                    stories = sts.orEmpty(),
                    currentUser = loggedInUser,
                    subtasks = sbs.orEmpty(),
                    selectedSprint = if (!sprints.isNullOrEmpty()) sprints[0] else null,
                    selectedStory = if (!sts.isNullOrEmpty()) stores[0] else null,
                    storyMap = map,
                    sprintMap = sprints?.associateWith { s -> state.value.storyMap }.orEmpty(),
                    tasks = allT
                )
            }
            reload()
        }

    }

    private fun loadMyActivities(order: SprintOrder) {
        getBacklogJob?.cancel()
        getBacklogJob = sprintUseCases.loadSprints(order)
            .onEach { sp ->
                val sprints = sp.apply { sp.filter { s -> s.sprint.info.isArchived } }
                _state.value = state.value.copy(
                    sprintList = sprints,
                    sprintSnapshot = sprints.map { s ->
                        SprintWithTasksAndSubtasks(
                            s.sprint,
                            s.tasks
                        )
                    },
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

    fun onEvent(event: BacklogEvent) {

    }

    fun reload() = run {
        viewModelScope.launch {
            repo.fetchFirestoreChanges { sps, fireUsers, loggedInUser, stores, subtasks, map, allT, bin ->
                val sprints =
                    sps.apply { sps.filter { s -> s.isArchived }.sortedBy { sp -> sp.archiveDate } }
                _state.value = state.value.copy(
                    sprints = sprints,
                    users = fireUsers,
                    stories = stores,
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

}