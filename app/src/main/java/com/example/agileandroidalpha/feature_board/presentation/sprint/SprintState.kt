package com.example.agileandroidalpha.feature_board.presentation.sprint

import androidx.compose.ui.graphics.toArgb
import com.example.agileandroidalpha.feature_board.domain.model.SprintInfo
import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithTasksAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.User
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask
import com.google.firebase.auth.FirebaseUser
import kotlin.random.Random

data class SprintState(
    val auth: FirebaseUser? = null,
    val user: FireUser? = null,
    var updateFlag: Boolean = false,
    val sprint: Sprint? = null,
    val room: SprintRoom? = null,
    val id: Long? = null,
    var sid: Long? = null,
    val uid: String? = null,
    var uri: String? = null,
    var origId: Long? = null,
    var title: String? = null,
    var desc: String? = null,
    var duration: Int = -1,
    var freq: Int = 1,
    var countdown: Int = 99999,
    var elapsed: Int = 0,
    var startDate: Long = -1,
    var endDate: Long = -1,
    var meetingTime: String = "00:00",
    var reviewTime: String = "00:00",
    val totalPoints: Long = -1,
    val remPoints: Long = -1,
    val cloned: Boolean = false,
    val status: String? = "",
    var color: Int = Sprint.colors[Random.nextInt(Sprint.colors.size)].toArgb(),
    val active: Boolean = false,
    val started: Boolean = false,
    val paused: Boolean = false,
    var progress: Float? = null,
    var progressPct: Float? = null,
    var target: Float = 0.8f,
    var completed: Boolean = false,
    var resolution: String? = null,
    val isHidden: Boolean = false,
    val isApproved: Boolean = false,
    val isArchived: Boolean = false,
    val isReviewed: Boolean = false,
    val archiveDate: Long = 0,
    var manual: Boolean? = null,
    val createdBy: String? = null,
    val creatorID: Long? = null,
    var owner: String? = null,
    var ownerId: Long? = null,
    var ownerUid: String? = null,
    var ownerUri: String? = null,
    var ownerUser: FireUser? = null,
    var manager: String? = null,
    var managerId: Long? = null,
    var managerUid: String? = null,
    var managerUri: String? = null,
    var managerUser: FireUser? = null,
    var projectId: Long? = null,
    var boardId: Long? = null,
    var componentId: Long? = null,
    var epicId: Long? = null,
    var logo: Int? = null,
    var pic: String? = null,
    var icon: String? = null,
    var new: Boolean = true,
    var newComment: String = "",
    val restrictions: List<String>? = null,
    var comments: List<String>? = null,
    var signatures: List<String>? = null,
    var log: List<String>? = null,
    var clones: List<Long>? = null,
    var backlogWt: Int = 0,
    var authorizedUsers: List<String>? = null,
    val timestamp: Long = System.currentTimeMillis(),
    var uidList: List<String?>? = null,
    var uris: List<String>? = null,
    var uList: List<Long>? = null,
    val users: List<FireUser> = emptyList(),
    val stories: List<Story> = emptyList(),
    val subtasks: List<SubTask> = emptyList(),
    val subtaskMap: Map<Story, List<Subtask>> = emptyMap(),
    val info: SprintInfo? = null,
    val sprintTasks: SprintWithTasksAndSubtasks? = null,
    val tasks: List<TaskAndSubtasks> = emptyList(),
    val roomUsers: List<User> = emptyList(),
    val userToTask: Map<User, List<TaskAndSubtasks>> = emptyMap(),
    val taskToUser: Map<TaskAndSubtasks, List<User>> = emptyMap(),
    val taskToSubs: Map<Task, List<Subtask>> = emptyMap()
)
