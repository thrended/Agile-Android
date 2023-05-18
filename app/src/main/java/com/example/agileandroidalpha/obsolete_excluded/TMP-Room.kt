package com.example.agileandroidalpha.obsolete_excluded

//
//@Entity
//data class `TMP-Room`(
//    @PrimaryKey val id: Int,
//    @ColumnInfo(name = "title") val title: String?,
//    @ColumnInfo(name = "boardOLD") val boardOLD: BoardOLD?,
//    @ColumnInfo(name = "projectOLD") val projectOLD: ProjectOLD?,
//    @ColumnInfo(name = "start_date") val startDate: Date?,
//    @ColumnInfo(name = "end_date") val endDate: Date?,
//    @ColumnInfo(name = "remaining_time") val remTime: Duration?,
//    @ColumnInfo(name = "sprint_duration") val sprintDuration: Duration?,
//    @ColumnInfo(name = "tasks") val taskOLDS: List<TaskOLD>?,
//    @ColumnInfo(name = "num_tasks") val numTasks: Int?,
//    @ColumnInfo(name = "active") val active: Boolean?,
//    @ColumnInfo(name = "completed") val completed: Boolean?,
//    @ColumnInfo(name = "approval_state") val approved: Boolean?,
//    @ColumnInfo(name = "percent_done") val completionPercent: Double?,
//    @ColumnInfo(name = "story_points") val storyPoints: Int?,
//    @ColumnInfo(name = "owner") val owner: AbstractUser?,
//    @ColumnInfo(name = "manager") val manager: AbstractUser?,
//    @ColumnInfo(name = "users") val users: List<AbstractUser>?,
//    @ColumnInfo(name = "groups") val groups: List<AbstractUser>?,
//    @ColumnInfo(name = "user_avatars") val avatars: List<R.drawable>?,
//)
//package com.example.agileandroidalpha.feature_board.domain.model
//
//import androidx.room.*
//
//@Entity(
//    primaryKeys = ["sprintIdRef", "userIdRef"],
//    indices = [ Index(value = ["userIdRef"])],
//    foreignKeys = [
//        ForeignKey(
//            entity = SprintRoom::class,
//            parentColumns = ["sprintId"],
//            childColumns = ["sprintIdRef"],
//            onDelete = ForeignKey.CASCADE,
//            onUpdate = ForeignKey.CASCADE,
//        ),
//        ForeignKey(
//            entity = FireUser::class,
//            parentColumns = ["userId"],
//            childColumns = ["userIdRef"],
//            onDelete = ForeignKey.CASCADE,
//            onUpdate = ForeignKey.CASCADE,
//        )
//    ]
//)
//data class SprintUserCrossRef(
//    val sprintIdRef: Long,
//    val userIdRef: Long
//)
//
//class SprintWithUsersAndTasks(
//    @Embedded val sprint: SprintRoom,
//    @Relation(
//        entity = FireUser::class,
//        parentColumn = "sprintId",
//        entityColumn = "userId",
//        associateBy = Junction(
//            value = SprintUserCrossRef::class,
//            parentColumn = "sprintIdRef",
//            entityColumn = "userIdRef"
//        )
//    )
//    val users: List<FireUser>,
//    @Relation(
//        entity = Task::class,
//        parentColumn = "sprintId",
//        entityColumn = "SID"
//    )
//    val tasks: List<TaskAndSubtasks>
//)
//
//class UserWithSprintsAndTasks(
//    @Embedded val user: FireUser,
//    @Relation(
//        entity = SprintRoom::class,
//        parentColumn = "userId",
//        entityColumn = "sprintId",
//        associateBy = Junction(
//            value = SprintUserCrossRef::class,
//            parentColumn = "userIdRef",
//            entityColumn = "sprintIdRef"
//        )
//    )
//    val sprints: List<SprintRoom>,
//    @Relation(
//        entity = Task::class,
//        parentColumn = "userId",
//        entityColumn = "UID",
//        associateBy = Junction(
//            value = UserTaskCrossRef::class,
//            parentColumn = "taskIdRef",
//            entityColumn = "userIdRef"
//        )
//    )
//    val tasks: List<TaskAndSubtasks>
//)
//
//data class UserWithTasksAndSubtasks(
//    @Embedded val user: FireUser,
//    @Relation(
//        entity = Task::class,
//        parentColumn = "userId",
//        entityColumn = "UID",
//        associateBy = Junction(
//            value = UserTaskCrossRef::class,
//            parentColumn = "taskIdRef",
//            entityColumn = "userIdRef"
//        )
//    )
//    val tasks: List<TaskAndSubtasks>
//)
//
//data class SprintWithTasksAndSubtasks(
//    @Embedded val sprint: SprintRoom,
//    @Relation(
//        entity = Task::class,
//        parentColumn = "sprintId",
//        entityColumn = "SID"
//    )
//    val tasks: List<TaskAndSubtasks>,
//)
//
//@Entity(
//    primaryKeys = ["userIdRef", "taskIdRef"],
//    indices = [ Index(value = ["taskIdRef"])],
//    foreignKeys = [
//        ForeignKey(
//            entity = FireUser::class,
//            parentColumns = ["userId"],
//            childColumns = ["userIdRef"],
//            onDelete = ForeignKey.CASCADE,
//            onUpdate = ForeignKey.CASCADE,
//        ),
//        ForeignKey(
//            entity = Task::class,
//            parentColumns = ["taskId"],
//            childColumns = ["taskIdRef"],
//            onDelete = ForeignKey.CASCADE,
//            onUpdate = ForeignKey.CASCADE,
//        )
//    ]
//)
//data class UserTaskCrossRef(
//    val userIdRef: Long,
//    val taskIdRef: Long
//)
//
//data class TaskWithUsers(
//    @Embedded val task: Task,
//    @Relation(
//        entity = FireUser::class,
//        parentColumn = "UID",
//        entityColumn = "userId",
//        associateBy = Junction(
//            value = UserTaskCrossRef::class,
//            parentColumn = "userIdRef",
//            entityColumn = "taskIdRef"
//        )
//    )
//    val users: List<FireUser>
//)
//
//class FullBoard(
//    @Embedded val board: Board,
//    @Relation(
//        entity = SprintRoom::class,
//        parentColumn = "sprintId",
//        entityColumn = "boardId"
//    )
//    val sprints: List<SprintWithUsersAndTasks>,
//    @Relation(
//        entity = FireUser::class,
//        parentColumn = "userId",
//        entityColumn = "boardId"
//    )
//    val users: List<UserWithSprintsAndTasks>
//)
//
//class BoardWithSprintsAndUsers(
//    @Embedded val board: Board,
//    @Relation(
//        entity = SprintRoom::class,
//        parentColumn = "sprintId",
//        entityColumn = "boardId"
//    )
//    val sprints: List<SprintWithUsersAndTasks>
//)
//
//class BoardWithUsersAndSprints(
//    @Embedded val board: Board,
//    @Relation(
//        entity = FireUser::class,
//        parentColumn = "userId",
//        entityColumn = "boardId"
//    )
//    val users: List<UserWithSprintsAndTasks>
//)
//
//class BoardWithSprints(
//    @Embedded val board: Board,
//    @Relation(
//        entity = SprintRoom::class,
//        parentColumn = "sprintId",
//        entityColumn = "boardId"
//    )
//    val sprints: List<SprintWithTasksAndSubtasks>
//)