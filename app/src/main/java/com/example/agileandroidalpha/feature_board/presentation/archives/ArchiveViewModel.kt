package com.example.agileandroidalpha.feature_board.presentation.archives

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
class ArchiveViewModel @Inject constructor(
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
    init {
        loadArchive(SprintOrder.Default(OrderType.Ascending))
        viewModelScope.launch {
            repo.loadFireStoreData() { sps, fireUsers, loggedInUser, stores, subtasks, map, allT ->
                val sprints = sps?.apply { sps.filter { s -> s.isArchived }.sortedBy { sp -> sp.archiveDate } }
                _state.value = state.value.copy(
                    sprints = sprints.orEmpty(),
                    users = fireUsers.orEmpty(),
                    stories = stores.orEmpty(),
                    currentUser = loggedInUser,
                    subtasks = subtasks.orEmpty(),
                    selectedSprint = if(!sprints.isNullOrEmpty()) sprints[0] else null,
                    selectedStory = if(!stores.isNullOrEmpty()) stores[0] else null,
                    storyMap = map,
                    sprintMap = sprints?.associateWith { s -> state.value.storyMap }.orEmpty(),
                    tasks = allT
                )
            }
            reload()
        }

    }

    private fun loadArchive(order: SprintOrder) {
        getBacklogJob?.cancel()
        getBacklogJob = sprintUseCases.loadSprints(order)
            .onEach { sp ->
                val sprints = sp.apply { sp.filter { s -> s.sprint.info.isArchived } }
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

    fun onEvent(event: BacklogEvent) {
        when(event) {
            is BacklogEvent.Refresh -> {
                reload()
            }
            else -> {}
        }
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