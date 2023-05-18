package com.example.agileandroidalpha.feature_board.data.repository

import com.example.agileandroidalpha.feature_board.data.data_source.AllDao
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
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import kotlinx.coroutines.flow.Flow

class RoomRepoObj (
    private val dao: AllDao
): RoomRepo {

    override suspend fun countBoards(): Int {
        return dao.getBoardCount()
    }
    override suspend fun countUsers(): Int {
        return dao.getUserCount()
    }
    override suspend fun countSprints(): Int {
        return dao.getSprintCount()
    }
    override suspend fun countTasks(): Int {
        return dao.getTaskCount()
    }

    override suspend fun nukeBoard() {
        dao.nukeBoard()
    }

    override suspend fun nukeUsers() {
        dao.nukeUsers()
    }

    override suspend fun nukeSprints() {
        dao.nukeSprints()
    }

    override suspend fun nukeTasks() {
        dao.nukeTasks()
    }

    override suspend fun nukeSubtasks() {
        dao.nukeSubtasks()
    }

    override suspend fun countSubtasks(): Int {
        return dao.getSubtaskCount()
    }
//    override suspend fun countAttachments(): Int {
//        return dao.getAttachmentCount()
//    }

    override fun getSubtasks(id: Long): Flow<TaskWithSubtasks?> {
        return dao.getSubtasks(id)
    }

    override fun getSubs(id: Long): Flow<List<Subtask>> {
        return dao.getSubs(id)
    }

    override fun getTasks(): Flow<List<Task>> {
        return dao.getTasks()
    }

    override fun getTasksFull(): Flow<List<TaskWithSubtasksTMP>> {
        return dao.getTasksFull()
    }

    override fun getTasksAndSubtasks(): Flow<List<TaskAndSubtasks>> {
        return dao.getTasksAndSubtasks()
    }

    override fun getTasksBySprint(id: Long): Flow<List<TaskAndSubtasks>> {
        return dao.getTasksBySprint(id)
    }

    override fun getBacklog(): Flow<List<SprintWithUsersAndTasks>> {
        return dao.getBacklog()
    }

    override fun getActiveSprint(): Flow<SprintWithUsersAndTasks> {
        return dao.getActiveSprint()
    }

    override fun getAllActive(): Flow<List<SprintWithUsersAndTasks>> {
        return dao.getAllActive()
    }

    override suspend fun getSprintsBasicInfo(): List<SprintRoom>? {
        return dao.getSprintsBasicInfo()
    }

    override fun getAllSprintBasicInfo(): Flow<List<SprintRoom>> {
        return dao.getAllSprintBasicInfo()
    }

    override fun getSprint(id: Long): Flow<SprintWithTasksAndSubtasks> {
        return dao.getSprint(id)
    }

    override fun loadSprintFull(id: Long): Flow<SprintWithUsersAndTasks> {
        return dao.loadSprintFull(id)
    }

    override fun loadSprintById(id: Long): Flow<SprintRoom> {
        return dao.loadSprintById(id)
    }

    override suspend fun loadSprint(id: Long): SprintWithUsersAndTasks? {
        return dao.loadSprint(id)
    }

    override suspend fun getSprintById(id: Long): SprintRoom? {
        return dao.getSprintById(id)
    }

    override suspend fun getSubsById(id: Long): List<Subtask>? {
        return dao.getSubsById(id)
    }

    override suspend fun getTaskById(id: Long): Task? {
        return dao.getTaskById(id)
    }

    override suspend fun getTaskByIdFull(id: Long): TaskWithSubtasksTMP? {
        return dao.getTaskByIdFull(id)
    }

    override suspend fun getTaskSingle(id: Long): TaskAndSubtasks? {
        return dao.getTaskSingle(id)
    }

    override suspend fun insertSprint(sprint: SprintRoom): Long {
        return dao.insertSprint(sprint)
    }

    override suspend fun insertTasks(tasks: List<Task>?): List<Long> {
        return dao.insertTasks(tasks)
    }

    override suspend fun insertTask(task: Task): Long {
        return dao.insertTask(task)
    }

    override suspend fun insertSubtasks(subtasks: List<Subtask>?): List<Long> {
        return dao.insertSubtasks(subtasks)
    }

    override suspend fun insertSubtask(subtask: Subtask): Long {
        return dao.insertSubtask(subtask)
    }

    override suspend fun getUsers(): List<UserWithTasksAndSubtasks>? {
        return dao.getUsers()
    }

    override fun loadUsersAbc(): Flow<List<UserWithSprintsAndTasks>> {
        return dao.loadUsersAbc()
    }

    override suspend fun upsertSprint(sprint: SprintRoom): Long {
        return dao.upsertSprint(sprint)
    }

    override suspend fun upsert(taskWithSubs: TaskWithSubtasks): Long {
        return dao.upsertTaskWithSubs(taskWithSubs)
    }

    override suspend fun upsertTask(task: Task): Long {
        return dao.upsertTask(task)
    }

    override suspend fun upsertTasks(tasks: List<Task>?): List<Long> {
        return dao.upsertTasks(tasks)
    }

    override suspend fun upsertSubtask(subtask: Subtask): Long {
        return dao.upsertSubtask(subtask)
    }

    override suspend fun upsertSubtasks(subtasks: List<Subtask>?): List<Long> {
        return dao.upsertSubtasks(subtasks)
    }

    override suspend fun upsertImage(img: Attachment): Long {
        return dao.upsertImage(img)
    }

    override suspend fun upsertImages(images: List<Attachment>?): List<Long> {
        return dao.upsertImages(images)
    }

    override suspend fun upsertUser(user: User?): Long {
        return dao.upsertUser(user)
    }

    override suspend fun deleteSprint(sprint: SprintRoom) {
        dao.deleteSprint(sprint)
    }
//    override suspend fun insertTaskFull(task_subtask: TaskWithSubtasksTMP) {
//        dao.insertTaskFull(task_subtask)
//    }

    override suspend fun delete(taskWithSubs: TaskWithSubtasks) {
        dao.delete(taskWithSubs)
    }

    override suspend fun deleteTask(task: Task) {
        dao.deleteTask(task)
    }

    override suspend fun deleteTasks(tasks: List<Task>?) {
        dao.deleteTasks(tasks)
    }

    override suspend fun deleteSubtask(sub: Subtask) {
        dao.deleteSubtask(sub)
    }

    override suspend fun deleteSubs(subs: List<Subtask>?) {
        dao.deleteSubs(subs)
    }

    override suspend fun updateSprint(sprint: SprintRoom): Int {
        return dao.updateSprint(sprint)
    }

//    override suspend fun deleteTaskFull(task_subtask: TaskWithSubtasksTMP) {
//        dao.deleteTaskFull(task_subtask)
//    }

    override suspend fun updateTask(task: Task): Int {
        return dao.updateTask(task)
    }

    override suspend fun updateSubtask(subtask: Subtask): Int {
        return dao.updateSubtask(subtask)
    }

    override suspend fun updateSubtasks(subtasks: List<Subtask>?): Int {
        return dao.updateSubtasks(subtasks)
    }

//    override suspend fun updateTaskFull(task_subtask: TaskWithSubtasksTMP) {
//        dao.updateTaskFull(task_subtask)
//    }
}