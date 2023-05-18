package com.example.agileandroidalpha.feature_board.domain.use_case

import com.example.agileandroidalpha.feature_board.domain.use_case.sprint.GetSprint
import com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask.*

data class TaskUseCases(
    val getTasks: GetTasks,
    val deleteTask: DeleteTask,
    val doneTask: DoneTask,
    val markSubtask: MarkSubtask,
    val addTask: AddTask,
    val addSubtask: AddSubtask,
    val addSubtasks: AddSubtasks,
    val deleteSubtask: DeleteSubtask,
    val dragSubtask: DragSubtask,
    val editSubtask: EditSubtask,
    val getSprint: GetSprint,
    val getSubtasks: GetSubtasks,
    val getTask: GetTask,
) {
}