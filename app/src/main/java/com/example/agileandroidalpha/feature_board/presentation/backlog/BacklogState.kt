package com.example.agileandroidalpha.feature_board.presentation.backlog

import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithTasksAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithUsersAndTasks
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.User
import com.example.agileandroidalpha.feature_board.domain.util.OrderType
import com.example.agileandroidalpha.feature_board.domain.util.SprintOrder
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask

data class BacklogState(
    var reload: Boolean = false,
    val weights: List<Int> = emptyList(),
    val sprintMap: Map <Sprint, Map<Story, List<SubTask> > > = emptyMap(),
    val sprints: List<Sprint> = emptyList(),
    val users: List<FireUser> = emptyList(),
    val storyMap: Map<Story, List<SubTask>> = emptyMap(),
    val stories: List<Story> = emptyList(),
    val subtasks: List<SubTask> = emptyList(),
    val currentUser: FireUser? = null,
    var selectedSprint: Sprint? = null,
    var selectedStory: Story? = null,
    val sprintSnapshot: List<SprintWithTasksAndSubtasks> = emptyList(),
    val sprintList: List<SprintWithUsersAndTasks> = emptyList(),
    val sprintOrder: SprintOrder = SprintOrder.Default(OrderType.Ascending),
    val roomUsers: Map<SprintRoom, List<User>> = emptyMap(),
    val taskMap: Map<SprintRoom, List<TaskAndSubtasks>> = emptyMap(),
    val tasks: List<TaskAndSubtasks> = emptyList(),
    val sprintDetail: Map <SprintRoom, Pair<List<User>, List<TaskAndSubtasks> > > = emptyMap()
)
