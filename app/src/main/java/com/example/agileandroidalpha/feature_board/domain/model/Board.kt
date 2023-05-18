package com.example.agileandroidalpha.feature_board.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "board", indices = [Index(value = ["name"], unique = true)])
data class Board (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long? = null,
    @ColumnInfo(name = "name") val name: String// = "Agile Droid Board"
)
{

}