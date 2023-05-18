package com.example.agileandroidalpha.feature_board.presentation.util

//sealed class ScreensOLD(val route: String) {
//    object TasksScreen: ScreensOLD ("tasks_screen")
//    object AddEditTaskScreen: ScreensOLD ("add_edit_task_screen")
//}

const val newTask = "new-task_subtask"
const val tasksUri = "tasks"
const val arg = "arg"
fun newRoute(root: String, argument: String) = "$root/$argument"

sealed class Screen(val route: String) {
    object Chat: Screen("chat")
    object Search: Screen("search")
    object My: Screen("my_activities")
    object TasksScreen: Screen("tasks_screen")
    object EditSubTaskScreen: Screen("edit_subtask_screen")
    object AddEditTaskScreen: Screen("add_edit_task_screen")
    object EditSprintScreen: Screen("edit_sprint_screen")
    object Splash : Screen("splash")
    object Home : Screen("home")
    object AdminPanel : Screen("admin-panel?{uid}")
    object UserPanel : Screen("user-panel?{uid}")
    object UserProfile : Screen("user-profile?{uid}")
    object ViewProfile : Screen("view-profile?{uid}")
    object EditProfile : Screen("edit-profile?{uid}")
    object GoogleScreen : Screen("google_screen")
    object Debug: Screen("debug")
    object Tasks : Screen("tasks")
    object NewTask : Screen("new-task_subtask") {
        fun createRoute(taskId: String) = "$newTask?$taskId"
    }
    object EditTask : Screen("edit-task_subtask?{taskId}") {
        fun createRoute(taskId: String) = "edit-task_subtask?$taskId"
    }
    object Subtasks : Screen("subtasks")
    object NewSubtask : Screen("new-subtask?{id}")
    object EditSubtask : Screen("edit-subtask?{id}")
    object Settings : Screen("settings")
    object Help : Screen("help")
    object Support : Screen("support")
    object Projects : Screen("projects")
    object Boards : Screen("boards")
    object Sprints : Screen("sprints")
    object NewSprint : Screen("new-sprint?{sprintId}")
    object EditSprint : Screen("edit-sprint?{sprintId}")
    object FutureSprints : Screen("future_sprints")
    object Story : Screen("story")
    object Planning : Screen("planning")
    object Review : Screen("review")
    object Retro : Screen("retro")
    object Backlog : Screen("backlog")
    object Active : Screen("active-sprint?{sprintId}")
    object StandUp : Screen("stand-up")
    object DailyNotes : Screen("daily-tasks?{date}")
    object Releases : Screen("releases")
    object Reports : Screen("reports")
    object Issues : Screen("issues")
    object Components : Screen("components")
    object TimeSheets : Screen("time-sheets")
    object Tests : Screen("tests")
    object Automation : Screen("automation")
    object Notes : Screen("tasks")
    object NewNote : Screen("new-note")
    object EditNote : Screen("edit-note")
    object Archives : Screen("archives")
    object Login : Screen("login")
    object Logout : Screen("logout")
    object Backup : Screen("Backup?{uid}")
    object Recover : Screen("Recover?{uid}")
    object Test : Screen("test")

    companion object {
        fun fromScreen(route: String?): Screen =
            when (route?.substringBefore("?")) {
                "chat" -> Chat
                "search" -> Search
                "my_activities" -> My
                "google_screen" -> GoogleScreen
                "tasks_screen" -> TasksScreen
//                "add_sprint" -> AddSprintScreen
                "edit_sprint_screen" -> EditSprintScreen
                "add_edit_task_screen" -> AddEditTaskScreen
                "edit_subtask_screen" -> EditSubTaskScreen
                "splash" -> Splash
                "home" -> Home
                "admin-panel" -> AdminPanel
                "user-panel" -> UserPanel
                "tasks" -> Tasks
                "new-task_subtask" -> NewTask
                "edit-task_subtask" -> EditTask
                "subtasks" -> Subtasks
                "new-subtask" -> NewSubtask
                "edit-subtask" -> EditSubtask
                "settings" -> Settings
                "help" -> Help
                "support" -> Support
                "projects" -> Projects
                "boards" -> Boards
                "sprints" -> Sprints
                "new-sprint" -> NewSprint
                "edit-sprint" -> EditSprint
                "story" -> Story
                "planning"-> Planning
                "review" -> Review
                "retro" -> Retro
                "backlog" -> Backlog
                "active" -> Active
                "stand-up" -> StandUp
                "daily-tasks" -> DailyNotes
                "releases" -> Releases
                "reports" -> Reports
                "issues" -> Issues
                "components" -> Components
                "time-sheets" -> TimeSheets
                "tests" -> Tests
                "automation" -> Automation
                "new-note" -> NewNote
                "edit-note" -> EditNote
                "archives" -> Archives
                "login" -> Login
                "logout" -> Logout
                "user-profile" -> UserProfile
                "view-profile" -> ViewProfile
                "edit-profile" -> EditProfile
                "backup" -> Backup
                "recover" -> Recover
                "test" -> Test
                //add the remaining screens
                // a null route resolves to LogInScreen.
                null -> Splash
                else -> throw IllegalArgumentException("Screen $route is not yet supported.")
            }
    }
}