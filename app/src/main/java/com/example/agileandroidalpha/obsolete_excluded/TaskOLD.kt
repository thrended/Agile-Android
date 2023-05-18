package com.example.agileandroidalpha.obsolete_excluded

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.Status
import java.sql.Types
import java.util.Date


data class BasicInfo(
    var name: String? = "New Task",
    var title: String? = "Title",
    var descShort: String? = "",
    var descLong: String? = "",
    @ColumnInfo(name = "priority") var priority: Priority? = Priority.Medium,
    @ColumnInfo(name = "points", typeAffinity = ColumnInfo.INTEGER) var points: Int? = 1,

    )

@Entity(tableName = "task_table")
data class TaskOLD(

    @PrimaryKey(autoGenerate = true) val taskId: Int,
    @Embedded var info: BasicInfo? = BasicInfo(),
    val creDate: Date? = Date(), // Int? = 0, // Date? = Date(),
    var modDate: Date? = Date(), // Int? = 0, // ? = Date(),
    var accDate: Date? = Date(), // Int? = 0, // Date? = Date(),
    @ColumnInfo(name = "status", typeAffinity = ColumnInfo.TEXT) var status: Status? = Status.Open,
    @ColumnInfo(name = "done", typeAffinity = Types.BOOLEAN) var isDone: Boolean? = false,
    val altID: String? = "$taskId",

    /*@PrimaryKey(autoGenerate = true) val taskId: Int,
    @ColumnInfo(name = "title", typeAffinity = TEXT) var title: String = "New Task",
    @ColumnInfo(name = "desc_brief", typeAffinity = TEXT) var descBrief: String? = "$title Your ad here",
    @ColumnInfo(name = "desc_verbose", typeAffinity = TEXT) var descVerbose: String? = "$title Was something needed?",
    @ColumnInfo(name = "priority", typeAffinity = TEXT) var priority: Priority? = Priority.Medium,
    @ColumnInfo(name = "points", typeAffinity = INTEGER) var points: Int? = 0,
    @Embedded var info: BasicInfo? = BasicInfo(),
    val creDate: Date? = Date(), // Int? = 0, // Date? = Date(),
    var modDate: Date? = Date(), // Int? = 0, // ? = Date(),
    var accDate: Date? = Date(), // Int? = 0, // Date? = Date(),
    @ColumnInfo(name = "created_by", typeAffinity = TEXT) val createdBy: String? = "Admin",
    @ColumnInfo(name = "owner", typeAffinity = TEXT) var owner: String? = "Admin",
    @ColumnInfo(name = "assignee", typeAffinity = TEXT) var assignee: String? = "Admin",
    @ColumnInfo(name = "reporter", typeAffinity = TEXT) var reporter: String? = "Admin",
    @ColumnInfo(name = "labels", typeAffinity = TEXT) var labels: String? = "", //List<String>? = emptyList(),
    @ColumnInfo(name = "boardOLD", typeAffinity = TEXT) var boardOLD: String? = "Default",
    @ColumnInfo(name = "sprint", typeAffinity = TEXT) var sprint: String? = "Current",
    @ColumnInfo(name = "status", typeAffinity = TEXT) var status: Status? = Status.Open,
    @ColumnInfo(name = "done", typeAffinity = BOOLEAN) var isDone: Boolean? = false,
    @ColumnInfo(name = "altID", typeAffinity = TEXT) val altID: String? = "$taskId",*/

)
//{
//    @Dao
//    interface BasicTaskDao {
//        @Query("SELECT * FROM task_table")
//        fun loadAll(): List<TaskOLD>
//
//        @Query("SELECT id, title, desc_brief, priority, points, assignee, boardOLD, " +
//                "status, done FROM task_table")
//        fun loadBasic(): List<TaskListModel>
//
//        @Insert
//        fun insert(entity: TaskOLD)
//
//        @Insert
//        fun insertBasic(entity: TaskListModel)
//    }
//}

//@Entity
//data class TaskWithSubtasks(
//    @Embedded val taskOLD: TaskOLD,
//    @Relation(
//        parentColumn = "taskId",
//        entityColumn = "subtaskId"
//    )
//    val subtasks: List<OldSubTask>
//)

data class TaskListModel(
    val id: Int,

    )