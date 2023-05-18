package com.example.agileandroidalpha.obsolete_excluded

class ScrumData {

    val scrumBoards = listOf(

        ScrumModel(
            "Projects",
            ScrumScreen.Projects,
        ),

        ScrumModel(
            "Boards",
            ScrumScreen.Boards,
        ),

        ScrumModel(
            "Story",
            ScrumScreen.Story,
        ),

        ScrumModel(
            "Review",
            ScrumScreen.Review,
        ),

        ScrumModel(
            "Retrospective",
            ScrumScreen.Retrospective,
        ),

        ScrumModel(
            "Backlog",
            ScrumScreen.Backlog,
        ),

        ScrumModel(
            "Active",
            ScrumScreen.Active,
        ),

        ScrumModel(
            "Daily Stand-up",
            ScrumScreen.Standup,
        ),

        ScrumModel(
            "Releases",
            ScrumScreen.Releases,
        ),

        ScrumModel(
            "Reports",
            ScrumScreen.Reports,
        ),

        ScrumModel(
            "Issues",
            ScrumScreen.Issues,
        ),

        ScrumModel(
            "Components",
            ScrumScreen.Components,
        ),

        ScrumModel(
            "Tests",
            ScrumScreen.Tests,
        ),

        ScrumModel(
            "Test Automation",
            ScrumScreen.Test_Automation,
        ),

    )

    val scrumPages = listOf(

        ScrumModel(
            name = "Register",
            page = FuncScreen.Register,
        ),
        ScrumModel(
            name = "Login",
            page = FuncScreen.Login,
        ),
        ScrumModel(
            name = "Settings",
            page = FuncScreen.Settings,
        ),
        ScrumModel(
            name = "Task",
            page = FuncScreen.Task,
        ),
        ScrumModel(
            name = "Create",
            page = FuncScreen.Create,
        ),
        ScrumModel(
            name = "Edit",
            page = FuncScreen.Edit,
        ),
        ScrumModel(
            name = "Delete",
            page = FuncScreen.Delete,
        ),
        ScrumModel(
            name = "Artifacts",
            page = FuncScreen.Artifacts,
        ),
        ScrumModel(
            name = "Help",
            page = FuncScreen.Help,
        ),
        ScrumModel(
            name = "Teams",
            page = FuncScreen.Teams,
        ),
        ScrumModel(
            name = "Users",
            page = FuncScreen.Users,
        ),
        ScrumModel(
            name = "Chat",
            page = FuncScreen.Chat,
        ),
    )

    val scrumTabs = listOf(
        ScrumModel(
            name = "Chat Fast",
            tab = TabScreen.Test1
        ),

        ScrumModel(
            name = "Hlo",
            tab = TabScreen.Test2
        ),

        ScrumModel(
            name = "Respected Sir",
            tab = TabScreen.Test3
        )
    )

    val scrumActions = listOf(
        ScrumModel(
            name = "Generic Action"
        )
    )

    val scrumTasks = listOf(
        TaskTmp(
            1,
            "A Task",
            "A task_subtask description",

        ),

        TaskTmp(
            2,
            "Another task_subtask",
            "A task_subtask description",
        ),

        TaskTmp(
            3,
            "Yet another task_subtask",
            "A task_subtask description",
        )
    )

    val scrumSubtasks = listOf(
        SubTaskOld(
            TaskTmp(1, "A Task", "A task_subtask description"),
            1,
            "Hunt foton booster",
            "Hellsent",
        ),

        SubTaskOld(
            TaskTmp(1, "A Task", "A task_subtask description"),
            2,
            "Hunt PGF",
            "ReEdbot",
        ),
        SubTaskOld(
            TaskTmp(1, "A Task", "A task_subtask description"),
            3,
            "Hunt Red Ring",
            "Nightmare",
        )
    )
}