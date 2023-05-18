package com.example.agileandroidalpha.feature_board.data.data_source

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.example.agileandroidalpha.feature_board.domain.model.*
import com.example.agileandroidalpha.feature_board.domain.util.Converters

@Database(
    entities = [
        Board::class,
        SprintUserCrossRef::class,
        SprintRoom::class,
        User::class,
        Task::class,
        Subtask::class,
        Attachment::class,
        TaskWithSubtasks::class,
        TaskWithSubtasksDBModel::class
               ],
    version = 31,
//    autoMigrations = [
//        AutoMigration (
//            from = 8,
//            to = 9,
//            spec = AllDB.MyAutoMigration::class)
//    ],
//    exportSchema = true
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AllDB: RoomDatabase() {
    @DeleteTable.Entries(
        DeleteTable(tableName = "Subtask"),
        DeleteTable(tableName = "Task")
    )
    @RenameTable.Entries(
        RenameTable(fromTableName = "task_table", toTableName = "task"),
        RenameTable(fromTableName = "subtask_table", toTableName = "subtask"),
        RenameTable(fromTableName = "master_table", toTableName = "board"),
        RenameTable(fromTableName = "sprint_table", toTableName = "sprint"),
        RenameTable(fromTableName = "user_table", toTableName = "user"),
    )
    @RenameColumn.Entries(
        RenameColumn(tableName = "SprintUserCrossRef", fromColumnName = "sprintId", toColumnName = "sprintIdRef"),
        RenameColumn(tableName = "SprintUserCrossRef", fromColumnName = "userId", toColumnName = "userIdRef"),
        RenameColumn(tableName = "task_table", fromColumnName = "sprintId", toColumnName = "SID"),
        RenameColumn(tableName = "task_table", fromColumnName = "userId", toColumnName = "UID"),
        RenameColumn(tableName = "sprint_table", fromColumnName = "id", toColumnName = "sprintId"),
        RenameColumn(tableName = "user_table", fromColumnName = "id", toColumnName = "userId"),
        RenameColumn(tableName = "user_table", fromColumnName = "isActive", toColumnName = "active")
    )
    //@DeleteColumn.Entries(
    //  DeleteColumn(tableName = "task_tbl", columnName = "numSubtasks")
    //)
    class MyAutoMigration : AutoMigrationSpec

    abstract val allDao: AllDao

    companion object {
        const val DATABASE_NAME = "Agile_Droid_Room_DB"
    }
}