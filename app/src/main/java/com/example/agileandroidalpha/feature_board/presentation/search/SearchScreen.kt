package com.example.agileandroidalpha.feature_board.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AllOut
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ImagesearchRoller
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.QueryBuilder
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.ScreenSearchDesktop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.core.BottomNavBar
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.core.TopAppBar
import com.example.agileandroidalpha.feature_board.presentation.chat.ChatMessageItem
import com.example.agileandroidalpha.feature_board.presentation.search.components.MiniSprintItem
import com.example.agileandroidalpha.feature_board.presentation.search.components.MiniStoryItem
import com.example.agileandroidalpha.feature_board.presentation.search.components.MiniSubTaskItem
import com.example.agileandroidalpha.feature_board.presentation.search.components.MiniUserItem
import com.example.agileandroidalpha.feature_board.presentation.util.Screen
import com.example.agileandroidalpha.firebase.firestore.model.ChatMessage
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask
import com.example.agileandroidalpha.ui.theme.Aquamarine
import com.example.agileandroidalpha.ui.theme.BlueDiamond
import com.example.agileandroidalpha.ui.theme.CharcoalBlue
import com.example.agileandroidalpha.ui.theme.DarkBlueGray
import com.example.agileandroidalpha.ui.theme.DarkTeal
import com.example.agileandroidalpha.ui.theme.GrayGoose
import com.example.agileandroidalpha.ui.theme.Iron
import com.example.agileandroidalpha.ui.theme.MetallicSilver
import com.example.agileandroidalpha.ui.theme.Quartz
import com.example.agileandroidalpha.ui.theme.WesternCharcoal
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun SearchScreen(
    navController: NavController,
    state: SearchState,
    text: String,
    mode: String,
    searching: Boolean,
    currentUser: FireUser?,
    chats: List<ChatMessage>,
    users: List<FireUser>,
    sprints: List<Sprint>,
    stories: List<Story>,
    subtasks: List<SubTask>,
    onEvent: (SearchEvent) -> Unit
)
{

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Search",
                icon = Icons.Filled.Task,
                onIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                icon2 = if(searching) Icons.Filled.SavedSearch
                    else Icons.Filled.SearchOff
                ,
                onIcon2Click = {
                    scope.launch {

                    }
                },
                icon3 = Icons.Filled.QueryBuilder,//Icons.Filled.CallToAction,
                onIcon3Click =
                {

                },
                icon4 = Icons.Filled.PersonSearch,
                onIcon4Click =
                {

                },
                title2 = null,
                title3 = null
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
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier
                    .displayCutoutPadding()
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Add Task")
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
    ) { padding -> 16.dp
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(Modifier.height(12.5.dp))
            val modesList = listOf("All", "Chats", "Users", "Sprints", "Stories", "Subtasks")
            var expanded by remember { mutableStateOf(false) }
            var selectedIndex by remember { mutableStateOf(0) }
            var textSize by remember { mutableStateOf(Size.Zero) }
            val iconLead = when (mode) {
                "All" -> Icons.Filled.AllInclusive
                "Chats" -> Icons.Filled.ChatBubbleOutline
                "Users" -> Icons.Filled.PersonSearch
                "Sprints" -> Icons.Filled.AllOut
                "Stories" -> Icons.Filled.ScreenSearchDesktop
                else -> Icons.Filled.ImagesearchRoller
            }
            val iconTrail = if (searching) Icons.Filled.SearchOff else Icons.Filled.ManageSearch
            //val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .wrapContentSize(Alignment.TopCenter),
                    expanded = expanded,
                    onExpandedChange = {
//                    scope.launch {
//                        onEvent(SprintEvent.Reload)
//                    }
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        enabled = false,
                        readOnly = true,
                        value = mode,
                        leadingIcon = {
                            Icon(
                                imageVector = iconLead,
                                contentDescription = "dropdown menu icon",
                            )
                        },
                        label = { Text("Search Filter") },
                        trailingIcon = {
                            Icon(
                                imageVector = iconTrail,
                                contentDescription = "Start/Pause",

                                )
                        },
                        textStyle = MaterialTheme.typography.h5,
                        onValueChange = { },
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textSize = coordinates.size.toSize()
                            }
                            .clickable(onClick = { expanded = true })
                            .wrapContentSize(Alignment.TopCenter)
                            .background(
                                color = Quartz,
                                shape = RoundedCornerShape(20.dp)
                            )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest =
                        {
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(
                                Aquamarine
                            )
                            .alignByBaseline()
                            .wrapContentSize(Alignment.TopCenter),
                    ) {
                        modesList.forEachIndexed { idx, mud ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedIndex = idx
                                    expanded = false
                                    onEvent(SearchEvent.ChangeMode(mud))
                                }
                            ) {
                                Text(text = modesList[idx])
                            }

                        }
                    }
                }
            }
            Spacer(Modifier.height(12.5.dp))
            TextField(
                value = text,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = CharcoalBlue,
                    unfocusedTextColor = WesternCharcoal
                ),
                onValueChange = {
                    onEvent(SearchEvent.ChangeSearchText(it))
                },
                label = {
                    Text(
                        text = "Search",
                        color = DarkTeal
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Search for something . . .",
                        color = MetallicSilver
                    )
                },

            )
            if(searching) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (currentUser?.photo != null) {
                        AsyncImage(
                            model = currentUser.photo,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(125.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.profsad_small)
                        )
                    }
                    CircularProgressIndicator(
                        modifier = Modifier
                            .scale(3.33f)
                            .align(Alignment.Center)
                    )
                }
            }
            else if (users.isEmpty() && sprints.isEmpty() && stories.isEmpty() && subtasks.isEmpty()){
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_scan),
                        contentDescription = "No results found",
                        modifier = Modifier
                            .scale(6.66f)
                            .align(Alignment.Center)
                    )
                    Text(
                        modifier = Modifier
                            .scale(3.33f)
                            .align(Alignment.Center),
                        text = "No search results."
                    )
                }
            }
            else if (text.isNotBlank()) {
                Spacer(modifier = Modifier.height(11.dp))
                when (mode) {
                    "All" -> {
                        Text("${chats.size} Chat Messages found.")
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("${users.size} Users found.")
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("${sprints.size} Sprints found.")
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("${stories.size} Stories found.")
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("${subtasks.size} Subtasks found.")
                    }
                    "Chats" -> {
                        Text("${chats.size} Chat Messages found.")
                    }
                    "Users" -> {
                        Text("${users.size} Users found.")
                    }
                    "Sprints" -> {
                        Text("${sprints.size} Sprints found.")
                    }
                    "Stories" -> {
                        Text("${stories.size} Stories found.")
                    }
                    "Subtasks" -> {
                        Text("${subtasks.size} Subtasks found.")
                    }
                    else -> {}
                }
                Spacer(modifier = Modifier.height(11.dp))
            }
            Spacer(modifier = Modifier.height(22.dp))
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .weight(1f)) {
                if (text.isNotBlank()) {
                    when (mode) {
                        "All" -> {
                            itemsIndexed(chats) { i, m ->
                                ChatMessageItem(
                                    msg = m,
                                    sender = m.sender,
                                    pic = m.senderPic,
                                    color = if (m.sender == currentUser?.name) Iron
                                            else DarkBlueGray,
                                    bgColor = if (m.sender == currentUser?.name) BlueDiamond
                                            else GrayGoose,
                                    modifier = Modifier
                                        .clickable {

                                        }
                                )
                            }
                            itemsIndexed(users) { i, ur ->
                                MiniUserItem(
                                    ur = ur,
                                    modifier = Modifier
                                        .clickable {

                                        }
                                )
                            }
                            itemsIndexed(sprints) { i, sp ->
                                MiniSprintItem(
                                    sp = sp,
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate(
                                                Screen.EditSprintScreen.route +
                                                        "?id=${sp.id}&uid=${sp.uid ?: ""}&spColor=${sp.color}"
                                            )
                                        }
                                )
                            }
                            itemsIndexed(stories) { i, st ->
                                MiniStoryItem(
                                    st = st,
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate(
                                                Screen.AddEditTaskScreen.route +
                                                        "?sid=${st.sid}&id=${st.id}&taskColor=${st.color}"
                                            )
                                        }
                                )
                            }
                            itemsIndexed(subtasks) { i, sb ->
                                MiniSubTaskItem(
                                    sb = sb,
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate(
                                                Screen.EditSubTaskScreen.route +
                                                        "?id=${sb.id}&tid=${sb.storyId}&sid=${sb.sid}&color=${sb.color}"
                                            )
                                        }
                                )
                            }
                        }

                        "Chats" -> itemsIndexed(chats) { i, m ->
                            ChatMessageItem(
                                msg = m,
                                sender = m.sender,
                                pic = m.senderPic,
                                color = if (m.sender == currentUser?.name) Iron
                                else DarkBlueGray,
                                bgColor = if (m.sender == currentUser?.name) BlueDiamond
                                else GrayGoose,
                                modifier = Modifier
                                    .clickable {

                                    }
                            )
                        }

                        "Users" -> itemsIndexed(users) { i, ur ->
                            MiniUserItem(
                                ur = ur,
                                modifier = Modifier
                                    .clickable {

                                    }
                            )
                        }

                        "Sprints" -> itemsIndexed(sprints) { i, sp ->
                            MiniSprintItem(
                                sp = sp,
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(
                                            Screen.EditSprintScreen.route +
                                                    "?id=${sp.id}&uid=${sp.uid ?: ""}&spColor=${sp.color}"
                                        )
                                    }
                            )
                        }

                        "Stories" -> itemsIndexed(stories) { i, st ->
                            MiniStoryItem(
                                st = st,
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(
                                            Screen.AddEditTaskScreen.route +
                                                    "?sid=${st.sid}&id=${st.id}&taskColor=${st.color}"
                                        )
                                    }
                            )
                        }

                        "Subtasks" -> itemsIndexed(subtasks) { i, sb ->
                            MiniSubTaskItem(
                                sb = sb,
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(
                                            Screen.EditSubTaskScreen.route +
                                                    "?id=${sb.id}&tid=${sb.storyId}&sid=${sb.sid}&color=${sb.color}"
                                        )
                                    }
                            )
                        }

                        else -> {

                        }
                    }
                }
//                itemsIndexed(users) { i, ur ->
//                    MiniUserItem(
//                        ur = ur,
//                        modifier = Modifier
//                            .clickable {
//
//                            }
//                    )
//                }
//                itemsIndexed(sprints) { i, sp ->
//                    Text(
//                        text = "Sprint #${sp.id}\nTitle : ${sp.title}\nCreated by : ${sp.creator?: "Unknown"}\n" +
//                                "${sp.remPoints} / ${sp.totalPoints} completed \n Status: ${sp.status}\n" +
//                                "Resolution: ${sp.resolution}"
//                    )
//
//                }
//                itemsIndexed(stories) { i, st ->
//                    Text(
//                        text = "Story #${st.id}\nTitle : ${st.title}\nDescription : ${st.desc}\n" +
//                                " Body Text : ${st.body}\n DoD : ${st.dod}" +
//                                "Type: ${st.type}       Priority: ${st.priority}         Points: ${st.points} \n" +
//                                "Status: ${st.status}" +            "Resolution: ${st.resolution}" +
//                                " Created by : ${st.creator?: "Unknown"}\n " +
//                                "Assigned to: ${st.assignee?: "None"}\n " +
//                                "Reported by: ${st.reporter?: "None"}"
//                    )
//                }
//                itemsIndexed(subtasks) { i, sb->
//                    Text(
//                        text = "Story #${sb.id}\nTitle : ${sb.title}\nDescription : ${sb.desc}\n" +
//                                " Body Text : ${sb.body}\n DoD : ${sb.dod}" +
//                                "Type: ${sb.type}       Priority: ${sb.priority}         Points: ${sb.points} \n" +
//                                "Status: ${sb.status}" +            "Resolution: ${sb.resolution}" +
//                                " Created by : ${sb.creator?: "Unknown"}\n " +
//                                "Assigned to: ${sb.assignee?: "None"}\n " +
//                                "Reported by: ${sb.reporter?: "None"}"
//                    )
//                }
            }
        }
    }
}