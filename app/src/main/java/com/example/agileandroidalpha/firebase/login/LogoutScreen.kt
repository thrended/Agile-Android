package com.example.agileandroidalpha.firebase.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.core.TopAppBar
import com.example.agileandroidalpha.feature_board.presentation.util.Screen
import com.example.agileandroidalpha.firebase.presentation.GoogleAuthClient
import com.example.agileandroidalpha.ui.theme.BP
import com.example.agileandroidalpha.ui.theme.Cerulean
import com.example.agileandroidalpha.ui.theme.Emerald
import com.example.agileandroidalpha.ui.theme.PaleYellow
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

private const val WAIT: Long = 2000

@Composable
fun LogoutScreen(
    navController: NavController,
    googleAuth: GoogleAuthClient,
    auth: FirebaseAuth,
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    showMsg: String? = null,
) {

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Logging Out",
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
                    // Nothing - logging out
                },
                clickDrawerAction = {
                    // Nothing - logging out
                }
            )
        },
        bottomBar = {
//            BottomAppBar() {
//
//            }
            androidx.compose.material3.BottomAppBar(windowInsets = WindowInsets.navigationBars) {
                showMsg?.let{
                    Text(showMsg)
                }
            }
        },
        scaffoldState = scaffoldState,
    )
    { padding ->
        var ticks by remember { mutableStateOf(0) }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            val captureTymOut by rememberUpdatedState(newValue = onTimeout)

            LaunchedEffect(key1 = true) {
                while(ticks < 125) {
                    delay(WAIT / 125)
                    ticks++
                }
                captureTymOut()

                scope.launch {
                    if (googleAuth.getUserData() != null) {
                        googleAuth.signOut()
                    } else {
                        auth.signOut()
                    }
                    navController.navigate(Screen.Splash.route)
                }
            }
            Image(painterResource(id = R.drawable.loading), contentDescription = "Logging out . . .")
        }

        Box(modifier = Modifier
            .padding(padding)
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .offset(0.dp), contentAlignment = Alignment.BottomCenter) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to Agile Droid!",
                    //modifier = Modifier.offset(y = (-50).dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W900,
                    color = BP
                )
                Spacer(modifier = Modifier.height(20.dp))
                LinearProgressIndicator(
                    modifier = modifier
                        .offset(0.dp),
                    progress = ticks / 120f,
                    color = Color( ColorUtils.blendARGB(PaleYellow.toArgb(), Emerald.toArgb(), ticks / 120f))
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Logging out . . . ${min(ticks.div(1.2).roundToInt(), 100)}% , Please Wait....",
                    //modifier = Modifier.offset(y = (-50).dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W900,
                    color = Cerulean
                )
                Divider(modifier = Modifier.offset(y = 50.dp))
            }
        }
    }
}