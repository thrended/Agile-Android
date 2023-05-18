package com.example.agileandroidalpha.firebase.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agileandroidalpha.core.BottomNavBar
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.core.TopAppBar
import com.example.agileandroidalpha.firebase.login.GoogleSignInState
import com.example.agileandroidalpha.ui.theme.BP
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun GoogleScreen(
    navController: NavController,
    state: GoogleSignInState,
    onLogin: () -> Unit,
    onLoginWithPassword: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = state.error, key2 = state.isSuccessful) {
        state.error?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
        if (state.isSuccessful) {
            Toast.makeText(
                context,
                "Successfully Signed in",
                Toast.LENGTH_LONG
            ).show()

        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = "Google Sign-In",
                icon = Icons.Filled.Login,
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
                androidx.compose.material.Icon(
                    imageVector = Icons.Default.AddChart,
                    contentDescription = "Add Task"
                )
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
        scaffoldState = scaffoldState
    ) { padding ->

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilledTonalButton(
                    onClick = onLogin,
                    enabled = Firebase.auth.currentUser == null,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Sign in with Google",
                        color = BP,
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                FilledTonalButton(
                    onClick = onLoginWithPassword,
                    enabled = Firebase.auth.currentUser != null,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Sign in with Google (Existing Users)",
                        color = BP,
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                FilledTonalButton(
                    onClick = onLogout,
                    enabled = Firebase.auth.currentUser != null,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Sign out",
                        color = BP,
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                )
                {
                    OutlinedIconButton(
                        onClick = { navController.navigateUp() },
                        enabled = Firebase.auth.currentUser != null,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                        Text(
                            text = "Back",
                            color = BP,
                        )
                    }
                }
            }
        }

    }
}