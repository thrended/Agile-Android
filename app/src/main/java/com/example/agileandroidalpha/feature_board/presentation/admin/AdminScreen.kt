package com.example.agileandroidalpha.feature_board.presentation.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.agileandroidalpha.core.BottomNavBar
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.feature_board.domain.model.User
import com.example.agileandroidalpha.feature_board.domain.model.UserInfo
import com.example.agileandroidalpha.feature_board.domain.model.UserSettings
import com.example.agileandroidalpha.feature_board.domain.model.getUserBrief
import com.example.agileandroidalpha.feature_board.presentation.users.UserItem
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun AdminScreen(
    navController: NavController,
    VM: AdminVM  = hiltViewModel()
) {
    val state = VM.state.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton =
        {
            FloatingActionButton(
                onClick = { /*TODO*/ },
                backgroundColor = MaterialTheme.colors.primary,
            ){
                Icon(
                    imageVector = Icons.Default.SupervisedUserCircle,
                    contentDescription = "Manage Users"
                )
            }
        },
        topBar = {
            com.example.agileandroidalpha.core.TopAppBar(
                title = "Agile Droid Administrator Panel",
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
                //currentScreen = Screen.AdminPanel.route,
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
        bottomBar = {
            BottomNavBar(
                navController = navController,
                onBarClicked = {
                    navController.navigate(it.route)
                },
            )
        },
        scaffoldState = scaffoldState
    ) { padding -> 16.dp
//        Box(modifier = Modifier.padding(
//            PaddingValues(0.dp, 0.dp, 0.dp, padding.calculateBottomPadding())))
//        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )  {
                FilledTonalButton(onClick = { VM.onEvent(AdminEvent.WipeData) }) {
                    Text(text = "Wipe all local Room data")
                }
                BoxWithConstraints() {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OutlinedTextField(
                            value = "Click to Add a Test User",
                            onValueChange = {},
                            shape = RoundedCornerShape(15.dp)
                        )

                        FilledIconButton(
                            onClick = {
                                VM.onEvent(AdminEvent.AddTestUser( genTestUser( state.testUserData ) ) )
                            },
                            modifier = Modifier
                                .padding(padding)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = "Add a Test User"
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    OutlinedCard(
                        modifier = Modifier
                            .padding(all = 20.dp),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(text = "User Management")
                    }
                    var bool by remember { mutableStateOf(false) }
                    if(bool) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            state.userData.forEachIndexed() { i, user ->
                                UserItem(
                                    getUserBrief(user.user),
                                    user.tasks,
                                    user.sprints,
                                )
                            }
                        }
                    }
                    else Text(text="Click to expand")
                    IconButton(onClick = { bool = !bool }) {
                        Icon(
                            imageVector = if (!bool) Icons.Default.ExpandMore
                                        else Icons.Default.ExpandLess,
                            contentDescription = "expand"
                        )
                    }
                }
            }
        //}
    }

}

fun genTestUser(list: List<Pair<String,String>>): User {
    val seed = list[Random.nextInt(list.size)]
    val lvl = Random.nextInt(100)
    return User(
        username = seed.first,
        password = seed.second,
        info = UserInfo(
            privilegeLvl = when (seed.first) {
                "adm" -> { 100 }
                "Administrator" -> { 150 }
                "root" -> { 250 }
                "Natalie" -> { 500 }
                "Brown" -> { 999 }
                "nm" -> { -1000000 }
                else -> lvl },
            admin = lvl >= 75,
            active = seed.first != "nm"
        ),
        settings = UserSettings()
    )
}