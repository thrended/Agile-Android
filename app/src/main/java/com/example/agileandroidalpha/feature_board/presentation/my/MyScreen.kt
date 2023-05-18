import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agileandroidalpha.core.BottomNavBar
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.core.TopAppBar
import com.example.agileandroidalpha.feature_board.presentation.backlog.BacklogEvent
import com.example.agileandroidalpha.feature_board.presentation.backlog.BacklogState
import com.example.agileandroidalpha.feature_board.presentation.sprint.components.SprintItem
import com.example.agileandroidalpha.feature_board.presentation.sprint.components.calc
import com.example.agileandroidalpha.feature_board.presentation.tasks.TasksState
import com.example.agileandroidalpha.feature_board.presentation.util.Screen
import com.example.agileandroidalpha.ui.theme.Cerulean
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyScreen(
    navController: NavController,
    state: BacklogState,
    extra: TasksState,
    //sprintState: SprintState,
    onEvent: (BacklogEvent) -> Unit = {}
) {
    //val state = VM.state.value
    //val excluded = VM.excluded.value
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var dialogOpen by remember {
        mutableStateOf(false)
    }
    var storyDialogOpen by remember {
        mutableStateOf(false)
    }
    var refresher by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = state.reload) {
//        Toast.makeText(
//            context,
//            "LaunchEffect launched; refreshing Sprint Screen.",
//            Toast.LENGTH_SHORT
//        ).show()
        state.reload = false
    }

    LaunchedEffect(key1 = refresher) {
        delay(1250L)
        refresher = !refresher
        delay(1250L)
        onEvent(BacklogEvent.Refresh)
        delay(1250L)
        refresher = !refresher
        delay(1250L)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "My Tasks",
                icon = Icons.Filled.Task,
                onIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerContent = {
            DroidDrawer(
                //currentScreen = Screen.TasksScreen.route,
                navController = navController,
                closeDrawerAction = {
                    // here - Drawer close
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                clickDrawerAction = {
                    scope.launch {
                        scaffoldState.drawerState.close()
                        navController.navigate(it)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.AddChart, contentDescription = "Add Task")
            }
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                onBarClicked = {
                    navController.navigate(it.route)
                },
            )
        },
        scaffoldState = scaffoldState,
    ) { padding ->
        16.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if(state.sprints.isNotEmpty()) {
                val collapsedState = remember(state.stories) {
                    state.stories.map { i -> i.done }.toMutableStateList()
                }
                val collapsedComments = remember(state.sprints) {
                    state.sprints.map {i -> i.comments!= null}.toMutableStateList()
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    itemsIndexed(state.sprints) { i, sp ->
                        SprintItem(
                            sprint = sp,
                            stories = state.stories.filter { s -> s.sid == sp.id },
                            subtasks = state.subtasks.filter { s -> s.sid == sp.id },
                            userList = state.users.filter { u ->
                                sp.associatedUsers?.contains(u.id) == true || sp.uris?.contains(u.photo) == true
                                        || sp.uidList?.contains(u.uid) == true
                            },
                            user = state.currentUser,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
//                                    Toast
//                                        .makeText(
//                                            context,
//                                            "Navigating to EditSprintScreen + ?sid=${sp.id}&uid=${sp.uid}&color=${sp.color}",
//                                            Toast.LENGTH_LONG
//                                        )
//                                        .show()
                                    navController.navigate(
                                        Screen.EditSprintScreen.route +
                                                "?id=${sp.id}&uid=${sp.uid ?: ""}&spColor=${sp.color}"
                                    )
                                },
                            collapsed = collapsedState[i],
                            collapsed2 = collapsedComments[i],
                            onOpenDialog = {
                                dialogOpen = !dialogOpen
                                state.selectedSprint = it
                                extra.selectedSprint = it
                            },
                            onOpenStoryDialog = {
                                storyDialogOpen = !storyDialogOpen
                                state.selectedStory = it
                                extra.selectedStory = it
                            },
                            onCheckboxClick = { onEvent(BacklogEvent.ToggleDone(sp)) },
                            onCheckStoryClick = { sto, done, points ->
                                onEvent(
                                    BacklogEvent.RecalculateProgress(
                                        sp, sto, done, points,
                                        calc(sp, state.stories.filter { s -> s.sid == sp.id }),
                                        sp.remPoints, sp.totalPoints
                                    )
                                )
                            },
                            onCompleteClick = { _, _ -> },
                            onDeleteClick = { spr, sts, sbs, u ->
                                try {
                                    onEvent(BacklogEvent.Delete(spr, sts, sbs, u))
                                    scope.launch {
                                        val result =
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                message = "Sprint ${spr.id} - ${spr.title} was deleted",
                                                actionLabel = "Undo"
                                            )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            onEvent(BacklogEvent.Restore)
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast
                                        .makeText(
                                            context,
                                            "An error occurred during sprint deletion #${spr.id}",
                                            Toast.LENGTH_LONG
                                        )
                                        .show()
                                }
                            },
                            onDeleteStory = {},
                            onDragStory = { _, _, _ -> },
                            onEditStory = { story ->
//                                Toast.makeText(
//                                    context,
//                                    "Navigating to EditStoryScreen + " +
//                                            "?sid=${story.sid}&id=${story.id}&taskColor=${story.color}",
//                                    Toast.LENGTH_LONG
//                                ).show()
                                navController.navigate(
                                    Screen.AddEditTaskScreen.route +
                                            "?sid=${story.sid}&id=${story.id}&taskColor=${story.color}"
                                )

                            },
                            onExpandClick = { bool ->
                                collapsedState[i] = bool
                            },
                            onExpandComments = { bool ->
                                collapsedComments[i] = bool
                            },
                            onExtendClick = {

                            },
                            onMoveStory = { st, spr, id, wt ->
                                onEvent(BacklogEvent.MoveStory(st, spr, id, wt))
                            },
                            onRevoke = { onEvent(BacklogEvent.RevokeApproval(sp)) },
                        )

                        //Text("Sprint #${sp.id} : active = ${sp.active} : completed = ${sp.completed}")
                        Spacer(Modifier.height(25.dp))
                    }
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center
                ) {
//                    Card(
//                        border = BorderStroke(10.dp, Turquoise),
//                        backgroundColor = Blueberry,
//                        modifier = Modifier.size(400.dp)
//                    ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 25.dp, start = 25.dp)
                            .fillMaxWidth(0.9f),
                        text = "No personal tasks or subtasks.",
                        color = Cerulean,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Justify
                    )
//                    }
                }
            }
        }
    }
}