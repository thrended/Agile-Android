package com.example.agileandroidalpha.feature_board.data.data_source

import androidx.room.*
import com.example.agileandroidalpha.feature_board.domain.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AllDao {

    @Query("SELECT COUNT(id) FROM board")
    suspend fun getBoardCount(): Int

    @Query("SELECT COUNT(userId) FROM user")
    suspend fun getUserCount(): Int

    @Query("SELECT COUNT(sprintId) FROM sprint")
    suspend fun getSprintCount(): Int

    @Query("SELECT COUNT(subId) FROM subtask")
    suspend fun getSubtaskCount(): Int

    @Query("SELECT COUNT(taskId) FROM task")
    suspend fun getTaskCount(): Int

    @Query("DELETE FROM board")
    fun nukeBoard()

    @Query("DELETE FROM user")
    fun nukeUsers()

    @Query("DELETE FROM sprint")
    fun nukeSprints()

    @Query("DELETE FROM task")
    fun nukeTasks()

    @Query("DELETE FROM subtask")
    fun nukeSubtasks()

//    @Query("SELECT COUNT(attachId) FROM attach")
//    suspend fun getAttachmentCount(): Int

    @Query("SELECT * FROM task_subtask WHERE taskId = :id")
    fun getSubtasks(id: Long): Flow<TaskWithSubtasks?>

    @Query("SELECT * FROM subtask WHERE parentId = :id")
    fun getSubs(id: Long): Flow<List<Subtask>>

    @Query("SELECT * FROM task")
    fun getTasks(): Flow<List<Task>>

    @Query("SELECT * FROM subtask WHERE parentId = :id")
    suspend fun getSubsById(id: Long): List<Subtask>?

    @Query("SELECT * FROM task WHERE taskId = :id")
    suspend fun getTaskById(id: Long): Task?

    @Transaction
    @Query("SELECT * FROM task WHERE taskId = :id")
    suspend fun getTaskSingle(id: Long): TaskAndSubtasks?

    @Transaction
    @Query("SELECT * FROM task")
    fun getTasksFull(): Flow<List<TaskWithSubtasksTMP>>

    @Transaction
    @Query("SELECT * FROM task WHERE sprintId = :id")
    fun getTasksBySprint(id: Long): Flow<List<TaskAndSubtasks>>

    @Transaction
    @Query("SELECT * FROM task")
    fun getTasksAndSubtasks(): Flow<List<TaskAndSubtasks>>

    @Transaction
    @Query("SELECT * FROM task WHERE taskId = :id")
    suspend fun getTaskByIdFull(id: Long): TaskWithSubtasksTMP?

    @Insert(entity = SprintRoom::class)
    suspend fun insertSprint(sprint: SprintRoom): Long

    @Insert(entity = Task::class)
    suspend fun insertTasks(tasks: List<Task>?): List<Long>

    @Insert(entity = Task::class)
    suspend fun insertTask(task: Task): Long

    @Insert(entity = Subtask::class)
    suspend fun insertSubtasks(subtasks: List<Subtask>?): List<Long>

    @Insert(entity = Subtask::class)
    suspend fun insertSubtask(subtask: Subtask): Long

    @Update(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTask(task: Task): Int

    @Update(entity = Subtask::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSubtask(subtask: Subtask): Int

    @Update(entity = Subtask::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSubtasks(subtasks: List<Subtask>?): Int

    @Update(entity = SprintRoom::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSprint(sprint: SprintRoom): Int

    @Upsert
    suspend fun upsertSprint(sprint: SprintRoom): Long

    @Upsert
    suspend fun upsertTaskWithSubs(taskWithSubs: TaskWithSubtasks): Long

    @Upsert
    suspend fun upsertSubtask(subtask: Subtask?): Long

    @Upsert
    suspend fun upsertSubtasks(subtasks: List<Subtask>?): List<Long>

    @Upsert
    suspend fun upsertTask(task: Task?): Long

    @Upsert
    suspend fun upsertTasks(tasks: List<Task>?): List<Long>

    @Upsert
    suspend fun upsertImage(img: Attachment): Long

    @Upsert
    suspend fun upsertImages(images: List<Attachment>?): List<Long>

    @Upsert
    suspend fun upsertUser(user: User?): Long

    @Delete
    suspend fun delete(taskWithSubs: TaskWithSubtasks)

    @Delete
    suspend fun deleteSprint(sprint: SprintRoom)

    @Delete
    suspend fun deleteTask(task: Task)

    @Delete
    suspend fun deleteTasks(tasks: List<Task>?)

    @Delete
    suspend fun deleteSubtask(sub: Subtask)

    @Delete
    suspend fun deleteSubs(subs: List<Subtask>?)

    /* *
    * New Board and SprintRoom Functions
    *
    * */

//    @Transaction
//    @Query(
//        "SELECT * FROM sprint" +
//        "JOIN task ON sprint.sprintId = task.SID"
//    )
//    suspend fun loadSprints(): Map<SprintRoom, List<TaskAndSubtasks>>

    @Transaction
    @Query("SELECT * FROM sprint WHERE sprintId = :id LIMIT 1")
    suspend fun getSprintById(id: Long): SprintRoom?

    @Transaction
    @Query(
        "SELECT * FROM sprint WHERE sprintId = :id LIMIT 1"
    )
    suspend fun loadSprint(id: Long): SprintWithUsersAndTasks?

    @Transaction
    @Query(
        "SELECT * FROM sprint WHERE sprintId = :id LIMIT 1"
    )
    fun loadSprintById(id: Long): Flow<SprintRoom>

//    @Transaction
//    @Query(
//        "SELECT * FROM user" +
//        "JOIN task on user.userId = task.UID"
//    )
//    suspend fun loadUser(): Map<FireUser, List<TaskAndSubtasks>>

//    @Transaction
//    @Query("SELECT * FROM board")
//    suspend fun getBoard(): FullBoard
//
//    @Transaction
//    @Query("SELECT * FROM board")
//    suspend fun getBoardBySprints(): BoardWithSprintsAndUsers
//
//    @Transaction
//    @Query("SELECT * FROM board")
//    suspend fun getBoardByUsers(): BoardWithUsersAndSprints
//
//    @Transaction
//    @Query("SELECT * FROM board")
//    suspend fun getBasicBoard(): BoardWithSprints

    @Transaction
    @Query("SELECT * FROM sprint")
    suspend fun getSprintsBasicInfo(): List<SprintRoom>?

    @Transaction
    @Query("SELECT * FROM sprint")
    fun getAllSprintBasicInfo(): Flow<List<SprintRoom>>

    @Transaction
    @Query("SELECT * FROM sprint")
    fun getAllSprints(): Flow<List<SprintWithUsersAndTasks>>

    @Transaction
    @Query("SELECT * FROM sprint WHERE active = 0 AND completed = 0 ORDER BY startDate ASC")
    fun getBacklog(): Flow<List<SprintWithUsersAndTasks>>

    @Transaction
    @Query("SELECT * FROM sprint WHERE active = 1 AND completed = 0 LIMIT 1")
    fun getActiveSprint(): Flow<SprintWithUsersAndTasks>

    @Transaction
    @Query("SELECT * FROM sprint WHERE active = 1 AND completed = 0 ORDER BY countdown ASC")
    fun getAllActive(): Flow<List<SprintWithUsersAndTasks>>

    @Transaction
    @Query("SELECT * FROM sprint")
    fun getAllSprintsTasks(): Flow<List<SprintWithTasksAndSubtasks>>

    @Transaction
    @Query("SELECT * FROM user")
    fun getSprintsByUserAll(): Flow<List<UserWithSprintsAndTasks>>

    @Transaction
    @Query("SELECT * FROM user")
    suspend fun getFullUsers(): List<UserWithTasksAndSubtasks>

    @Transaction
    @Query("SELECT * FROM user ORDER BY username ASC")
    fun loadUsersAbc(): Flow<List<UserWithSprintsAndTasks>>

    @Transaction
    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<UserWithTasksAndSubtasks>>

    @Transaction
    @Query("SELECT * FROM sprint WHERE sprintId = :id LIMIT 1")
    fun getSprint(id: Long): Flow<SprintWithTasksAndSubtasks>

    @Transaction
    @Query("SELECT * FROM sprint WHERE sprintId = :id LIMIT 1")
    fun loadSprintFull(id: Long): Flow<SprintWithUsersAndTasks>

    @Transaction
    @Query("SELECT * FROM sprint WHERE sprintId = :id")
    suspend fun getSprintAsync(id: Long): SprintWithUsersAndTasks?

    @Transaction
    @Query("SELECT * FROM sprint WHERE sprintId = :id")
    suspend fun getSprintTasks(id: Long): SprintWithTasksAndSubtasks?

    @Transaction
    @Query("SELECT * FROM user WHERE userId = :id")
    suspend fun getUser(id: Long): UserWithTasksAndSubtasks?

    @Transaction
    @Query("SELECT * FROM user")
    suspend fun getUsers(): List<UserWithTasksAndSubtasks>?

    @Transaction
    @Query("SELECT * FROM user WHERE userId = :id")
    suspend fun getSprintsByUser(id: Long): UserWithSprintsAndTasks?
}