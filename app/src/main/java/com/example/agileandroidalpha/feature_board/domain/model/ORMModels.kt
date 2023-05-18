package com.example.agileandroidalpha.feature_board.domain.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

@Entity(
    primaryKeys = ["sprintIdRef", "userIdRef"],
    indices = [ Index(value = ["userIdRef"])],
    foreignKeys = [
        ForeignKey(
            entity = SprintRoom::class,
            parentColumns = ["sprintId"],
            childColumns = ["sprintIdRef"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userIdRef"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ]
)
data class SprintUserCrossRef(
    val sprintIdRef: Long,
    val userIdRef: Long
)

class SprintWithUsersAndTasks(
    @Embedded val sprint: SprintRoom,
    @Relation(
        entity = User::class,
        parentColumn = "sprintId",
        entityColumn = "userId",
        associateBy = Junction(
            value = SprintUserCrossRef::class,
            parentColumn = "sprintIdRef",
            entityColumn = "userIdRef"
        )
    )
    val users: List<User>,
    @Relation(
        entity = Task::class,
        parentColumn = "sprintId",
        entityColumn = "sprintId"
    )
    val tasks: List<TaskAndSubtasks>
)

class UserWithSprintsAndTasks(
    @Embedded val user: User,
    @Relation(
        entity = SprintRoom::class,
        parentColumn = "userId",
        entityColumn = "sprintId",
        associateBy = Junction(
            value = SprintUserCrossRef::class,
            parentColumn = "userIdRef",
            entityColumn = "sprintIdRef"
        )
    )
    val sprints: List<SprintRoom>,
    @Relation(
        entity = Task::class,
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val tasks: List<TaskAndSubtasks>
)

data class UserWithTasksAndSubtasks(
    @Embedded val user: User,
    @Relation(
        entity = Task::class,
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val tasks: List<TaskAndSubtasks>
)

data class SprintWithTasksAndSubtasks(
    @Embedded val sprint: SprintRoom,
    @Relation(
        entity = Task::class,
        parentColumn = "sprintId",
        entityColumn = "sprintId"
    )
    val tasks: List<TaskAndSubtasks>,
) {
    companion object {
        fun create(sp: SprintRoom, tks: List<TaskAndSubtasks>) = SprintWithTasksAndSubtasks(sp, tks)
    }
}

//class FullBoard(
//    @Embedded val board: Board,
//    @Relation(
//        entity = SprintRoom::class,
//        parentColumn = "id",
//        entityColumn = "boardId"
//    )
//    val sprints: List<SprintWithUsersAndTasks>,
//    @Relation(
//        entity = FireUser::class,
//        parentColumn = "id",
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