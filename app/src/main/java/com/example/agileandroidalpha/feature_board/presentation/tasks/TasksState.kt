package com.example.agileandroidalpha.feature_board.presentation.tasks

import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithTasksAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithUsersAndTasks
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.SubtaskWithImages
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.TaskWithSubtasks
import com.example.agileandroidalpha.feature_board.domain.util.OrderType
import com.example.agileandroidalpha.feature_board.domain.util.TaskOrder
import com.example.agileandroidalpha.firebase.firestore.model.ChatMessage
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask

data class TasksState(
    val index: Int = 0,
    val sprintId: Long = 1L,
    val sprintTitle: String = "",
    val sprintActive: Boolean = true,
    val sprintStarted: Boolean = false,
    val sprintPaused: Boolean = false,
    var selectedSprint: Sprint? = null,
    val messages: List<ChatMessage> = emptyList(),
    val msgMap: Map<ChatMessage, FireUser> = emptyMap(),
    val current: Sprint? = null,
    val started: Boolean = false,
    val active: Boolean? = false,
    val paused: Boolean = true,
    var flagForUpdate: Boolean = false,
    val currentUser: FireUser? = null,
    var selectedStory: Story? = null,
    val selectedStoryId: Long? = null,
    var selectedSub: SubTask? = null,
    val selectedSubId: Long? = null,
    val users: List<FireUser> = emptyList(),
    val sprints: List<Sprint> = emptyList(),
    val stories: List<Story> = emptyList(),
    val fullStories: Map<Story, List<SubTask>> = emptyMap(),
    val subs: List<SubTask> = emptyList(),
    val subtaskMap: Map<SubTask, Subtask> = emptyMap(),
    val storyMap: Map<Story, Task> = emptyMap(),
    val sprintMap: Map<Sprint, SprintRoom> = emptyMap(),
    val currentSprint: SprintRoom? = null, //SprintRoom(info = SprintInfo()),
    val timeRem: Int = 21,
    val elapsed: Int = 0,
    val totalPoints: Long = 0,
    val remPoints: Long = 0,
    val currentSprintTasks: SprintWithTasksAndSubtasks? = null,
    val currentSprintFullInfo: SprintWithUsersAndTasks? = null,
    val sprintCount: Int = 0,
    val sprintList: List<SprintRoom> = emptyList(),
    val availSprints: Map<Long, String> = emptyMap(),
    val tasksInfo: List<TaskWithSubtasks> = emptyList(),
    val allTasks: List<TaskAndSubtasks> = emptyList(),
    val rooms: List<SprintRoom> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val subtasks:  List<Subtask> = emptyList(),
    val selectedSubtaskWithImages: SubtaskWithImages? = null,
    val taskToSubtasks: Map<Task, List<Subtask>> = emptyMap(),
    val creator: String? = null,
    val creatorID: Long? = null,
    val expanded: List<Boolean> = emptyList(),
    val checkMarks: List<Boolean> = emptyList(),
    val taskOrder: TaskOrder = TaskOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false,
    val allComplete: Boolean = false
)
