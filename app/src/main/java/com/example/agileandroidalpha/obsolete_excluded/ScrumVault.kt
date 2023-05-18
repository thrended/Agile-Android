package com.example.agileandroidalpha.obsolete_excluded

class ScrumVault constructor(
    private val data: ScrumData
) {
    val boards: List<ScrumModel> = data.scrumBoards
    val pages: List<ScrumModel> = data.scrumPages
    val tabs: List<ScrumModel> = data.scrumTabs
    val actions: List<ScrumModel> = data.scrumActions
    val tasks: List<TaskTmp> = data.scrumTasks
    val subtasks: List<SubTaskOld> = data.scrumSubtasks
}