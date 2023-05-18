package com.example.agileandroidalpha.obsolete_excluded

import com.example.agileandroidalpha.obsolete_excluded.SubTaskOld
import com.example.agileandroidalpha.obsolete_excluded.TaskTmp

data class UserData (
    val assignedTasks: List<TaskTmp>? = null,
    val assignedSubTasks: List<SubTaskOld>? = null,
    val reporterTasks: List<TaskTmp>? = null,
    val reporterSubTasks: List<SubTaskOld>? = null,
        ){

    fun initData() {

    }
}