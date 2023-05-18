@file:OptIn(ExperimentalMaterialApi::class, ExperimentalTime::class)

package com.example.agileandroidalpha.core

import MyScreen
import android.app.Activity.RESULT_OK
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.agileandroidalpha.HomeScreen
import com.example.agileandroidalpha.LandingScreen
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.AddEditTaskScreen
import com.example.agileandroidalpha.feature_board.presentation.admin.AdminScreen
import com.example.agileandroidalpha.feature_board.presentation.archives.ArchiveScreen
import com.example.agileandroidalpha.feature_board.presentation.archives.ArchiveViewModel
import com.example.agileandroidalpha.feature_board.presentation.backlog.BacklogScreen
import com.example.agileandroidalpha.feature_board.presentation.backlog.BacklogVM
import com.example.agileandroidalpha.feature_board.presentation.chat.ChatScreen
import com.example.agileandroidalpha.feature_board.presentation.chat.ChatViewModel
import com.example.agileandroidalpha.feature_board.presentation.edit_subtask.EditSubTaskScreen
import com.example.agileandroidalpha.feature_board.presentation.edit_subtask.EditSubTaskViewModel
import com.example.agileandroidalpha.feature_board.presentation.my.MyViewModel
import com.example.agileandroidalpha.feature_board.presentation.search.SearchScreen
import com.example.agileandroidalpha.feature_board.presentation.search.SearchViewModel
import com.example.agileandroidalpha.feature_board.presentation.settings.SettingsScreen
import com.example.agileandroidalpha.feature_board.presentation.sprint.EditSprintScreen
import com.example.agileandroidalpha.feature_board.presentation.sprint.EditSprintViewModel
import com.example.agileandroidalpha.feature_board.presentation.tasks.TasksScreen
import com.example.agileandroidalpha.feature_board.presentation.users.UserProfileScreen
import com.example.agileandroidalpha.feature_board.presentation.util.Screen
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.currentUser
import com.example.agileandroidalpha.firebase.login.LoginScreen
import com.example.agileandroidalpha.firebase.login.LogoutScreen
import com.example.agileandroidalpha.firebase.presentation.GoogleAuthClient
import com.example.agileandroidalpha.firebase.presentation.GoogleScreen
import com.example.agileandroidalpha.firebase.presentation.GoogleVM
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

/*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.google.android.ads.mediationtestsuite.viewmodels.ViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun TaskViewController(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val menuItems = listOf("Item #1", "Item #2")
    val scaffoldState = rememberScaffoldState()
    val snackbarCoroutineScope = rememberCoroutineScope()
    TaskViewControllerTheme {
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                TaskViewControllerTopBar(menuItems) { s ->
                    snackbarCoroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(s)
                    }
                }
            },
            bottomBar = {
                TaskViewControllerBottomBar(navController)
            }
        ) {
            TaskViewControllerNavHost(
                navController = navController,
                factory = factory
            )
        }
    }
}

@Composable
fun TaskViewControllerTopBar(menuItems: List<String>,
                             onClick: (String) -> Unit) {
    var menuOpened by remember { mutableStateOf(false) }
    TopAppBar(title = {
        Text(text = stringResource(id = R.string.app_name))
    },
        actions = {
            Box {
                IconButton(onClick = {
                    menuOpened = true
                }) {
                    Icon(Icons.Default.MoreVert, "")
                }
                DropdownMenu(expanded = menuOpened,
                    onDismissRequest = {
                        menuOpened = false
                    }) {
                    menuItems.forEachIndexed { index, s ->
                        if (index > 0) Divider()
                        DropdownMenuItem(onClick = {
                            menuOpened = false
                            onClick(s)
                        }) {
                            Text(s)
                        }
                    }
                }
            }
        }
    )
}
}
*/

@ExperimentalTime
@ExperimentalMaterial3Api
@OptIn(ExperimentalMaterialApi::class, FlowPreview::class)
@Composable
fun MainNavigation (
    navController: NavHostController,
    lifecycleScope: LifecycleCoroutineScope,
    context: Context,
    auth: FirebaseAuth,
    googleAuth: GoogleAuthClient,
    backStackEntry: State<NavBackStackEntry?>,
    currentScreen: Screen,
    modifier: Modifier = Modifier
) {

    NavigationBar() {

    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Backlog.route) {
            val viewModel = hiltViewModel<BacklogVM>()
            val state = viewModel.state.collectAsStateWithLifecycle().value
            val extra = viewModel.tasksState.collectAsStateWithLifecycle().value
           // val spS = viewModel.sprintState.collectAsStateWithLifecycle().value
            BacklogScreen(
                navController = navController,
                state = state,
                extra = extra,
                //sprintState = spS,
                onEvent = viewModel::onEvent
            )
        }
        composable(Screen.Splash.route) {
            LandingScreen(
                onTimeout = {navController.navigate(Screen.Home.route)})
        }
        composable(route = Screen.TasksScreen.route +
                "?uid={uid}&sid={sid}",
            arguments = listOf(
                navArgument(
                    name = "uid"
                ) {
                    type = NavType.StringType
                    defaultValue = "Anonymous"
                },
                navArgument(
                    name = "sid"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
//            val viewModel = hiltViewModel<TasksViewModel>()
//            val state = viewModel.state
//            val states = mutableListOf(viewModel.selectedSprint, viewModel.selectedSprintId,
//                viewModel.selectedSprintTitle, viewModel.numSprints, viewModel.started,
//                viewModel.points, viewModel.rem, viewModel.cd)
//            val flow by viewModel.test.collectAsStateWithLifecycle()
            val uid = it.arguments?.getString("uid") ?: "Anonymous"
            val sid = it.arguments?.getLong("sid") ?: -1L
            TasksScreen(
                navController = navController,
                uid,
                sid
            )
        }
        composable(
            route = Screen.EditSubTaskScreen.route +
                "?id={id}&tid={tid}&sid={sid}&color={color}",
            arguments = listOf(
                navArgument(
                    name = "id"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(
                    name = "tid"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(
                    name = "sid"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(
                    name = "color"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            val id = it.arguments?.getLong("id") ?: -1L
            val taskId = it.arguments?.getLong("tid") ?: -1L
            val sprintId = it.arguments?.getLong("sid") ?: -1L
            val color = it.arguments?.getInt("taskColor") ?: -1
            val vm = hiltViewModel<EditSubTaskViewModel>()
            val state by vm.state.collectAsStateWithLifecycle()
            val me = vm.me.collectAsStateWithLifecycle().value

            EditSubTaskScreen(
                navController = navController,
                state = state,
                me = me,
                id = id,
                tid = taskId,
                sid = sprintId,
                color = color,
                onEvent = vm::onEvent
            )
        }
        composable(
            route = Screen.EditSprintScreen.route +
                    "?id={id}&uid={uid}&color={color}",
            arguments = listOf(
                navArgument(
                    name = "id"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(
                    name = "uid"
                ) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(
                    name = "color"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            val id = it.arguments?.getLong("id") ?: -1L
            val uid = it.arguments?.getString("uid") ?: ""
            val color = it.arguments?.getInt("color") ?: -1

            val vm = hiltViewModel<EditSprintViewModel>()
            val state by vm.state.collectAsStateWithLifecycle()

            EditSprintScreen(
                navController = navController,
                state = state,
                id = id,
                uid = uid,
                color = color,
                onEvent = vm::onEvent
            )
        }
        composable(
            route = Screen.AddEditTaskScreen.route +
                    "?sid={sid}&id={id}&taskColor={taskColor}",
            arguments = listOf(
                navArgument(
                    name = "sid"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(
                    name = "id"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(
                    name = "taskColor"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            val sprintId = it.arguments?.getLong("sid") ?: -1L
            val taskId = it.arguments?.getLong("id") ?: -1L
            val color = it.arguments?.getInt("taskColor") ?: -1

            //val vm = viewModel<AddEditTaskViewModel>()
            //val state by vm.state.collectAsStateWithLifecycle()

            AddEditTaskScreen(
                navController = navController,
                //state = state,
                sid = sprintId,
                id = taskId,
                taskColor = color
            )
        }
        composable(Screen.Home.route) {
            val vm = viewModel<GoogleVM>()

            HomeScreen(
                navController = navController,
                basicInfo = googleAuth.getUserData(),
                onContinueClicked = {
                    if(googleAuth.getUserData() != null) {
                        lifecycleScope.launch {
                            googleAuth.signOut()
                            vm.reset()
                        }
                    }
                    navController.navigate(Screen.Login.route) {
                       launchSingleTop = true
                       popUpTo(Screen.Home.route) {
                           inclusive = true
                       }
                    }
                },
                user = auth.currentUser
            )
        }
        composable(Screen.GoogleScreen.route) {
            val vm = viewModel<GoogleVM>()
            val state by vm.state.collectAsStateWithLifecycle()
            val user = auth.currentUser

            LaunchedEffect(key1 = Unit) {
                if(googleAuth.getUserData() != null && user != null /*!user?.displayName.isNullOrBlank()*/
                    && googleAuth.checkUserInDB(user)) {
                    Toast.makeText(
                        context,
                        "Signing in as ${user.displayName ?: user.email}",
                        Toast.LENGTH_LONG
                    ).show()
                    delay(1500L)
                    navController.navigate(Screen.UserProfile.route) {
                        launchSingleTop = true
                        popUpTo(Screen.GoogleScreen.route) {
                            inclusive = true
                        }
                    }
                    vm.reset()
                }
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { res ->
                    if(res.resultCode == RESULT_OK) {
                        lifecycleScope.launch {
                            val loginResult = googleAuth.signInWithIntent(
                                intent = res.data ?: return@launch
                            )
                            vm.onLoginResult(loginResult)
                        }
                    }
                }
            )

            LaunchedEffect(key1 = state.isSuccessful) {
                if(state.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Successfully Signed in",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(Screen.UserProfile.route)
                    vm.reset()
                }
            }

            GoogleScreen(
                navController = navController,
                state = state,
                onLogin = {
                    lifecycleScope.launch {
                        val intentSender = googleAuth.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                intentSender ?: return@launch
                            ).build()
                        )
                    }
                },
                onLoginWithPassword = {
                    lifecycleScope.launch {
                        val intentSender = googleAuth.signIn(signUp = false)
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                intentSender ?: return@launch
                            ).build()
                        )
                    }
                },
                onLogout = {
                    lifecycleScope.launch {
                        googleAuth.signOut()
                        vm.reset()
                    }
                }
            )
        }
        composable(Screen.My.route) {
            val vm = hiltViewModel<MyViewModel>()
            val state by vm.state.collectAsStateWithLifecycle()
            val extra by vm.tasksState.collectAsStateWithLifecycle()

            MyScreen(
                navController = navController,
                state = state,
                extra = extra,
                onEvent = vm::onEvent
            )
        }
        composable(Screen.Archives.route) {
            val vm = hiltViewModel<ArchiveViewModel>()
            val state by vm.state.collectAsStateWithLifecycle()
            val extra by vm.tasksState.collectAsStateWithLifecycle()

            ArchiveScreen(
                navController = navController,
                state = state,
                extra = extra,
                onEvent = vm::onEvent
            )
        }
        composable(Screen.Search.route) {
            val viewModel = hiltViewModel<SearchViewModel>()
            val state = viewModel.state.collectAsStateWithLifecycle().value
            val text = viewModel.text.collectAsStateWithLifecycle().value
            val mode = viewModel.mode.collectAsStateWithLifecycle().value
            val searching = viewModel.isSearching.collectAsStateWithLifecycle().value
            val currentUser = viewModel.currentUser.collectAsStateWithLifecycle().value
            val chats = viewModel.chats.collectAsStateWithLifecycle().value
            val users = viewModel.users.collectAsStateWithLifecycle().value
            val sprints = viewModel.sprints.collectAsStateWithLifecycle().value
            val stories = viewModel.stories.collectAsStateWithLifecycle().value
            val subtasks = viewModel.subtasks.collectAsStateWithLifecycle().value

            SearchScreen(
                navController = navController,
                state = state,
                text = text,
                mode = mode,
                searching = searching,
                currentUser = currentUser,
                chats = chats,
                users = users,
                sprints = sprints,
                stories = stories,
                subtasks = subtasks,
                onEvent = viewModel::onEvent
            )
        }
        composable(Screen.Chat.route) {
            val viewModel = hiltViewModel<ChatViewModel>()
            val state = viewModel.state.collectAsStateWithLifecycle().value
            val log = viewModel.log.collectAsStateWithLifecycle().value
            val no = viewModel.roomNo.collectAsStateWithLifecycle().value
            val user = viewModel.state.collectAsStateWithLifecycle().value.user
            val eventFlow = viewModel.eventFlow
            if (auth.currentUser == null || auth.currentUser?.isAnonymous == true) {
                Toast.makeText(
                    context,
                    "Only Registered Users may enter the Chat Room",
                    Toast.LENGTH_LONG
                ).show()
            } else if(currentUser?.isBanned == true || currentUser?.isDisabled == true) {
                if (currentUser?.isBanned == true) {
                    Toast.makeText(
                        context,
                        "You have been BANNED from entering the Chat Room due to abusive behavior",
                        Toast.LENGTH_LONG
                    ).show()
                }
                if (currentUser?.isDisabled == true) {
                    Toast.makeText(
                        context,
                        "Your user account has been disabled and chat privileges have been revoked",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                ChatScreen(
                    navController = navController,
                    state = state,
                    log = log,
                    user = user,
                    roomNo = no,
                    onEvent = viewModel::onEvent,
                    eventFlow = eventFlow
                )
            }
        }
        composable(Screen.AdminPanel.route) {
            AdminScreen(navController = navController)
        }
        composable(Screen.UserProfile.route
//                            "?uid={uid}",
//            arguments = listOf(
//                navArgument(
//                    name = "uid"
//                ) {
//                    type = NavType.LongType
//                    defaultValue = -1L
//                },)
        ) { //val uid = it.arguments?.getLong("uid") ?: -1L
            
            UserProfileScreen(
                navController = navController,
                basicInfo = googleAuth.getUserData(),
                onMoreOptions = {
                navController.navigate(Screen.Settings.route) {
                    launchSingleTop = true
                    popUpTo(Screen.UserProfile.route) {
                        inclusive = true
                    }
                }
            }, onSignOut = {
                    lifecycleScope.launch {
                        lifecycleScope.launch {
                            if (googleAuth.getUserData() != null) {
                                googleAuth.signOut()
                            } else {
                                auth.signOut()
                            }
                            navController.navigate(Screen.Splash.route)
                        }
                    }
                }

            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                user = auth.currentUser
            )


//            onClick = {
//                navController.navigate(Screen.TasksScreen.route) {
//                    launchSingleTop = true
//                    popUpTo(Screen.UserProfile.route) {
//                        inclusive = true
//                    }
//                }
//            }, onClick2 = {
//            navController.navigate(Screen.Subtasks.route) {
//                launchSingleTop = true
//                popUpTo(Screen.UserProfile.route) {
//                    inclusive = true
//                }
//            }
//        }, onClick3 = {
//            navController.navigate(Screen.Settings.route) {
//                launchSingleTop = true
//                popUpTo(Screen.UserProfile.route) {
//                    inclusive = true
//                }
//            }
//        },
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Logout.route) {
            auth.signOut()
            LogoutScreen(
                navController = navController,
                onTimeout = {navController.navigate(Screen.Splash.route)},
                googleAuth = googleAuth,
                auth = auth,
                showMsg = "Successfully Logged Out."
            )
        }
//        composable(route = "tasks/{$arg}",
//            arguments = listOf(
//                navArgument("arg") {
//                    type = NavType.ParcelableType(TaskTmp::class.java)
//                }
//            )
//        ) { //backStackEntry ->
//            //val taskAdded = backStackEntry.arguments?.getString("{taskID}")
//            TaskScreen (
//                onAddTask =
//                { _, taskID: String ->
//                    navController.navigate(newRoute(newTask, taskID)) {
//                        launchSingleTop = true
//                    }
//                    Log.d(taskID, "New task_subtask ID value")
//                }, onEditTask =
//                { _, taskID: String ->
//                    navController.navigate(Screen.EditTask.createRoute(taskID)) {
//                        launchSingleTop = true
//                    }
//                    Log.d(taskID, "New task_subtask ID value")
//                }
//            )
//
//        }
//        composable(Screen.Tasks.route) { //backStackEntry ->
//            //val taskAdded = backStackEntry.arguments?.getString("{taskID}")
//            TaskScreen (
//                onAddTask =
//                { _, taskID: String ->
//                    navController.navigate(newRoute(newTask, taskID)) {
//                        launchSingleTop = true
//                    }
//                    Log.d(taskID, "New task_subtask ID value")
//                }, onEditTask =
//                { _, taskID: String ->
//                    navController.navigate(Screen.EditTask.createRoute(taskID)) {
//                        launchSingleTop = true
//                    }
//                    Log.d(taskID, "New task_subtask ID value")
//                }
//            )
//
//        }
//        composable(
//            route = "$newTask?{taskId}",
//            arguments = listOf(
//                navArgument("taskId") {
//                    type = NavType.StringType
//                }
//            )
//        ) { //backStackEntry ->
//            //val taskID = backStackEntry.arguments?.getString("{taskID}")
//            //requireNotNull(taskID) { " Invalid null value for taskID " }
//            NewTaskScreen (
//                onCancel = {
//                    navController.popBackStack()
//                },
//                onSave = { task ->
//                    // save to TaskViewModel
//                    // Parcelize
//                    navController.navigate(newRoute(tasksUri, task.id.toString())) {
//                        launchSingleTop = true
//                        popUpTo(Screen.Tasks.route)
//                    }
//                }
//            )
//        }
//        composable(Screen.EditTask.route) {
//            //EditTaskScreen()
//        }
//        composable(Screen.Subtasks.route) {
//            SubTaskScreen()
//        }
        composable(Screen.NewSubtask.route) {
            //NewSubTaskScreen()
        }
        composable(Screen.Help.route) {
            //Help()
        }
        composable(Screen.Support.route) {
            //Support()
        }
        composable(Screen.Projects.route) {
            //Projects()
        }
        composable(Screen.Boards.route) {
            //Boards()
        }
        composable(Screen.Sprints.route) {
            //Sprints()
        }
        composable(Screen.NewSprint.route) {
            //NewSprint()
        }
        composable(Screen.EditSprint.route) {
            //GetSprint()
        }
        composable(Screen.Story.route) {
            //UserStoryMap()
        }
        composable(Screen.Planning.route) {
            //Planning()
        }
        composable(Screen.Review.route) {
            //Review()
        }
        composable(Screen.Retro.route) {
            //Retrospective()
        }
        composable(Screen.Active.route) {
            //ActiveSprint()
        }
        composable(Screen.StandUp.route) {
            //StandUp()
        }
        composable(Screen.DailyNotes.route) {
            //DailyNotes()
        }
        composable(Screen.Releases.route) {
            //Releases()
        }
        composable(Screen.Reports.route) {
            //Reports()
        }
        composable(Screen.Issues.route) {
            //Issues()
        }
        composable(Screen.Components.route) {
            //Components()
        }
        composable(Screen.TimeSheets.route) {
            //TimeSheets()
        }
        composable(Screen.Tests.route) {
            //Tests()
        }
        composable(Screen.Automation.route) {
            //Automation()
        }
        composable(Screen.Notes.route) {
            //Notes()
        }
        composable(Screen.NewNote.route) {
            //NewNote()
        }
        composable(Screen.EditNote.route) {
            //EditNote()
        }

        composable(Screen.UserPanel.route) {
            //UserProfile()
        }
        composable(Screen.ViewProfile.route) {
            //ViewProfile()
        }
        composable(Screen.EditProfile.route) {
            //EditProfile()
        }
        composable(Screen.Backup.route) {
            //Backup()
        }
        composable(Screen.Recover.route) {
            //Recover()
        }
        composable(Screen.Test.route) {
            //Test()
        }
    }
}