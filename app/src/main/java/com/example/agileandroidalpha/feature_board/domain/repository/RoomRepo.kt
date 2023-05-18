package com.example.agileandroidalpha.feature_board.domain.repository

import com.example.agileandroidalpha.feature_board.domain.model.Attachment
import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithTasksAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithUsersAndTasks
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.TaskWithSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.TaskWithSubtasksTMP
import com.example.agileandroidalpha.feature_board.domain.model.User
import com.example.agileandroidalpha.feature_board.domain.model.UserWithSprintsAndTasks
import com.example.agileandroidalpha.feature_board.domain.model.UserWithTasksAndSubtasks
import kotlinx.coroutines.flow.Flow

interface RoomRepo {

    suspend fun countBoards(): Int

    suspend fun countUsers(): Int

    suspend fun countSprints(): Int

    suspend fun countSubtasks(): Int

    suspend fun countTasks(): Int

//    suspend fun countAttachments(): Int

    suspend fun nukeBoard()

    suspend fun nukeUsers()

    suspend fun nukeSprints()

    suspend fun nukeTasks()

    suspend fun nukeSubtasks()

    fun getSubtasks(id: Long): Flow<TaskWithSubtasks?>

    fun getSubs(id: Long): Flow<List<Subtask>>

    fun getTasks(): Flow<List<Task>>

    fun getTasksFull(): Flow<List<TaskWithSubtasksTMP>>

    fun getTasksAndSubtasks(): Flow<List<TaskAndSubtasks>>

    fun getTasksBySprint(id: Long): Flow<List<TaskAndSubtasks>>

    fun getBacklog(): Flow<List<SprintWithUsersAndTasks>>

    fun getActiveSprint(): Flow<SprintWithUsersAndTasks>

    fun getAllActive(): Flow<List<SprintWithUsersAndTasks>>

    suspend fun getSprintsBasicInfo(): List<SprintRoom>?

    fun getAllSprintBasicInfo(): Flow<List<SprintRoom>>

    fun getSprint(id: Long): Flow<SprintWithTasksAndSubtasks>

    fun loadSprintFull(id: Long): Flow<SprintWithUsersAndTasks>

    fun loadSprintById(id: Long): Flow<SprintRoom>

    suspend fun loadSprint(id: Long): SprintWithUsersAndTasks?

    suspend fun getSprintById(id: Long): SprintRoom?

    suspend fun getSubsById(id: Long): List<Subtask>?

    suspend fun getTaskById(id: Long): Task?

    suspend fun getTaskByIdFull(id: Long): TaskWithSubtasksTMP?

    suspend fun getTaskSingle(id: Long): TaskAndSubtasks?

    suspend fun insertSprint(sprint: SprintRoom): Long

    suspend fun insertTasks(tasks: List<Task>?): List<Long>

    suspend fun insertTask(task: Task): Long

    suspend fun insertSubtasks(subtasks: List<Subtask>?): List<Long>

    suspend fun insertSubtask(subtask: Subtask): Long

    suspend fun upsertSprint(sprint: SprintRoom): Long

    suspend fun upsert(taskWithSubs: TaskWithSubtasks): Long

    suspend fun upsertTask(task: Task): Long

    suspend fun upsertTasks(tasks: List<Task>?): List<Long>

    suspend fun upsertSubtask(subtask: Subtask): Long

    suspend fun upsertSubtasks(subtasks: List<Subtask>?): List<Long>

    suspend fun upsertImage(img: Attachment): Long

    suspend fun upsertImages(images: List<Attachment>?): List<Long>

    suspend fun upsertUser(user: User?): Long

    //suspend fun insertTaskFull(task_subtask: TaskWithSubtasksTMP)

    suspend fun deleteSprint(sprint: SprintRoom)

    suspend fun delete(taskWithSubs: TaskWithSubtasks)

    suspend fun deleteTask(task: Task)

    suspend fun deleteTasks(tasks: List<Task>?)

    suspend fun deleteSubtask(sub: Subtask)

    suspend fun deleteSubs(subs: List<Subtask>?)

    //suspend fun deleteTaskFull(task_subtask: TaskWithSubtasksTMP)

    suspend fun updateSprint(sprint: SprintRoom): Int

    suspend fun updateTask(task: Task): Int

    suspend fun updateSubtask(subtask: Subtask): Int

    suspend fun updateSubtasks(subtasks: List<Subtask>?): Int

    //suspend fun updateTaskFull(task_subtask: TaskWithSubtasksTMP)
    suspend fun getUsers(): List<UserWithTasksAndSubtasks>?

    fun loadUsersAbc(): Flow<List<UserWithSprintsAndTasks>>
}