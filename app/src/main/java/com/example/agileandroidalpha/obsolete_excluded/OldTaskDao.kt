package com.example.agileandroidalpha.obsolete_excluded

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OldTaskDao {
    @Insert
    fun insertTasks(vararg taskOLDS: TaskOLD)

    @Insert
    suspend fun insertTask(taskOLD: TaskOLD)

    @Insert
    fun insertTaskWithSubs(taskOLD: TaskOLD, subtasks: List<OldSubTask>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(taskOLD: TaskOLD)

    @Update
    fun updateTasks(vararg taskOLDS: TaskOLD)

    @Update
    fun updateTask(taskOLD: TaskOLD)

    @Update
    fun updateSubs(taskOLD: TaskOLD, subtasks: List<OldSubTask>)

//    @Update
//    fun addSub(task_subtask: Task, subtask: SubTaskOld)

    @Delete
    fun delete(taskOLD: TaskOLD)

    @Delete
    fun deleteTaskAndSubs(taskOLD: TaskOLD, subtasks: List<OldSubTask>)

//    @Delete
//    fun deleteByUser(user: UserOLD, tasks: List<Task>)

    @Query("SELECT * FROM task_table")
    fun getTasksAll(): Flow<List<TaskOLD>>

    @Query("SELECT * FROM task_table ORDER BY title ASC")
    fun getTasksAbc(): Flow<List<TaskOLD>>

    @Query("SELECT * FROM task_table ORDER BY priority DESC")
    fun getTasksByPriorityDesc(): Flow<List<TaskOLD>>

    @Query("SELECT * FROM task_table ORDER BY priority ASC")
    fun getTasksByPriorityAsc(): Flow<List<TaskOLD>>

    @Query("SELECT * FROM task_table WHERE taskId IN (:taskIds)")
    fun loadAllByIds(taskIds: IntArray): Flow<List<TaskOLD>>

    /*@Query("SELECT * tasks FROM task_table WHERE owner = :u")
    fun getAllByUser(u: UserOLD): Flow<List<Task>>

    @Query("SELECT * tasks FROM task_table WHERE assignee = :u")
    fun getAllAssignedToUser(u: UserOLD): Flow<List<Task>>

    @Query("SELECT * tasks FROM task_table WHERE reporter = :u")
    fun getAllReporterUser(u: UserOLD): Flow<List<Task>>*/

    @Query("DELETE FROM task_table")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM task_table")
    fun count(): Int

//    @Transaction
//    @Query("SELECT * FROM task_table")
//    fun oldGetTasksWithSubtasks(): Flow<List<TaskWithSubtasks>>
}