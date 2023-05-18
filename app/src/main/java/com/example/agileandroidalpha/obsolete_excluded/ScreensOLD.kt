package com.example.agileandroidalpha.obsolete_excluded

const val newTask = "new-task_subtask"
const val tasksUri = "tasks"
const val arg = "arg"
fun newRoute(root: String, argument: String) = "$root/$argument"

sealed class ScreensOLD(val route: String) {
    object TasksScreensOLD: ScreensOLD("tasks_screen")
    object AddEditTaskScreensOLD: ScreensOLD("add_edit_task_screen")
    object Splash : ScreensOLD("splash")
    object Home : ScreensOLD("home")
    object AdminPanel : ScreensOLD("admin-panel/{uid}")
    object UserPanel : ScreensOLD("user-panel/{uid}")
    object Tasks : ScreensOLD("tasks")
    object NewTask : ScreensOLD("new-task_subtask") {
        fun createRoute(taskId: String) = "$newTask/$taskId"
    }
    object EditTask : ScreensOLD("edit-task_subtask/{taskId}") {
        fun createRoute(taskId: String) = "edit-task_subtask/$taskId"
    }
    object Subtasks : ScreensOLD("subtasks")
    object NewSubtask : ScreensOLD("new-subtask/{taskId}")
    object EditSubtask : ScreensOLD("edit-subtask/{taskId}")
    object Settings : ScreensOLD("settings")
    object Help : ScreensOLD("help")
    object Support : ScreensOLD("support")
    object Projects : ScreensOLD("projects")
    object Boards : ScreensOLD("boards")
    object Sprints : ScreensOLD("sprints")
    object NewSprint : ScreensOLD("new-sprint/{sprintId}")
    object EditSprint : ScreensOLD("edit-sprint/{sprintId}")
    object Stories : ScreensOLD("story")
    object Planning : ScreensOLD("planning")
    object Review : ScreensOLD("review")
    object Retro : ScreensOLD("retro")
    object Backlog : ScreensOLD("backlog")
    object Active : ScreensOLD("active-sprint/{sprintId}")
    object StandUp : ScreensOLD("stand-up")
    object DailyNotes : ScreensOLD("daily-tasks/{date}")
    object Releases : ScreensOLD("releases")
    object Reports : ScreensOLD("reports")
    object Issues : ScreensOLD("issues")
    object Components : ScreensOLD("components")
    object TimeSheets : ScreensOLD("time-sheets")
    object Tests : ScreensOLD("tests")
    object Automation : ScreensOLD("automation")
    object Notes : ScreensOLD("tasks")
    object NewNote : ScreensOLD("new-note")
    object EditNote : ScreensOLD("edit-note")
    object Archives : ScreensOLD("archives")
    object Login : ScreensOLD("login")
    object Logout : ScreensOLD("logout")
    object UserProfile : ScreensOLD("user-profile/{uid}")
    object ViewProfile : ScreensOLD("view-profile/{uid}")
    object EditProfile : ScreensOLD("edit-profile/{uid")
    object Backup : ScreensOLD("Backup/{uid}")
    object Recover : ScreensOLD("Recover/{uid}")
    object Test : ScreensOLD("test")

    companion object {
        fun fromScreen(route: String?): ScreensOLD =
            when (route?.substringBefore("/")) {
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
                "stories" -> Stories
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
                //add the remaining ScreensOLD
                // a null route resolves to LogInScreen.
                null -> Splash
                else -> throw IllegalArgumentException("ScreensOLD $route is not yet supported.")
            }
    }
}