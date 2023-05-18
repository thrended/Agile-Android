package com.example.agileandroidalpha.feature_board.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.CoffeeMaker
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.agileandroidalpha.core.BottomNavBar
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.core.TopAppBar
import com.example.agileandroidalpha.feature_board.presentation.util.Screen
import com.example.agileandroidalpha.ui.theme.Grapefruit
import com.example.agileandroidalpha.ui.theme.Hazel
import com.example.agileandroidalpha.ui.theme.Vermilion
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    user: FirebaseUser?,
    vM: SettingsViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "More Options",
                icon = Icons.Filled.Task,
                onIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
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
        BoxWithConstraints(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .align((Alignment.Center)),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment =  Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Colorize,
                            contentDescription = "",
                            modifier = Modifier
                                .scale(3.0f)
                        )
                    }
                    //ImageCard(painter = painterResource(id = R.drawable.profsad), contentDescription = "hlo", title = "Hlo")
                }
                Spacer(modifier = Modifier.height(80.dp))
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Coffee,
                            contentDescription = "",
                            modifier = Modifier
                                .scale(3.0f)
                        )
                    }
                    Spacer(modifier = Modifier.width(80.dp))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.CoffeeMaker,
                            contentDescription = "",
                            modifier = Modifier
                                .scale(3.0f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.CorporateFare,
                            contentDescription = "",
                            modifier = Modifier
                                .scale(3.0f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
                Row (
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                )
                {
                    var confirmRes by remember { mutableStateOf(false) }
                    var confirmDel by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = {
                            if (confirmRes) {
                                scope.launch {
                                    vM.resetPassword()
                                    delay(500L)
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = "Password reset email sent. Check your inbox.",
                                        actionLabel = "Password Reset",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                            else confirmRes = true
                        }
                    ) {
                        Text(
                            text = if(!confirmRes) "Reset Password" else "Confirm Password Reset?",
                            color = if(!confirmRes) Grapefruit else Vermilion,
                            fontWeight = if(!confirmRes) FontWeight.SemiBold else FontWeight.Bold,
                            fontSize = if(!confirmRes) 15.sp else 20.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                    OutlinedButton(
                        onClick = {
                            if (confirmDel) {
                                scope.launch {
                                    if (vM.deleteUser()){
                                        delay(500L)
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = "Your account has been permanently deleted. " +
                                                    "Now Logging out...",
                                            actionLabel = "Password Reset",
                                            duration = SnackbarDuration.Short
                                        )
                                    } else {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = "Re-authentication of the user is required" +
                                                    "before this action can be taken. Last login date" +
                                                    "was over 30 days ago. Logging out...",
                                            actionLabel = "Password Reset",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                    navController.navigate(Screen.Logout.route)
                                    delay(1000L)
                                    navController.navigate(Screen.Splash.route)
                                }
                            }
                            else confirmDel = true
                        }
                    ) {
                        Text(
                            text = if(!confirmDel) "Delete User" else "Confirm Delete? (Permanent)",
                            color = if(!confirmDel) Grapefruit else Vermilion,
                            fontWeight = if(!confirmDel) FontWeight.SemiBold else FontWeight.Bold,
                            fontSize = if(!confirmDel) 15.sp else 20.sp,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                user?.let {
                    val verTxt = if (user.isEmailVerified) "Already Verified" else "Verify Email"
                    val disTxt = if (vM.getUser().value.isDisabled) "Already Disabled" else "Disable Account"
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        OutlinedButton(
                            enabled = !user.isEmailVerified,
                            onClick = {
                                scope.launch {
                                    vM.sendEmailVerification()
                                    delay(500L)
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = "Email verification sent.",
                                        actionLabel = "Email verification",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        ) {
                            Text(
                                text = verTxt,
                                color = Hazel,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                            )
                        }
                        OutlinedButton(
                            enabled = !vM.getUser().value.isBanned
                                    && !vM.getUser().value.isRestricted
                                    && !vM.getUser().value.isWarned,
                            onClick = {
                                scope.launch {
                                    vM.disableAccount()
                                    delay(500L)
                                    if (vM.getUser().value.isDisabled) {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = "Account Disabled. Your user data will be frozen " +
                                                    "and chat options disabled until reactivation.",
                                            actionLabel = "Disable Account",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                    else {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = "Your account has been reactivated and all " +
                                                    "user features are once again available to use.",
                                            actionLabel = "Reactivate Account",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = disTxt,
                                color = Hazel,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}