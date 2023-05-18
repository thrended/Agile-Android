package com.example.agileandroidalpha.obsolete_excluded

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.Status

data class BasicSubInfo(
    var name: String? = "A SubTask",
    var title: String? = "A SubTask Title",
    var desc: String? = "Enter a description",
    var priority: Priority? = Priority.Medium,
    @ColumnInfo(name = "status") var status: Status? = Status.TO_DO
)

@Entity
data class OldSubTask (
    @PrimaryKey(autoGenerate = true) val subtaskId: Int,
    @Embedded var info: BasicSubInfo? = BasicSubInfo(),
    var checked: Boolean? = false,

    )