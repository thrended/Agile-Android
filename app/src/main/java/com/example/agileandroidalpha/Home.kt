package com.example.agileandroidalpha

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.agileandroidalpha.core.BottomNavBar
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.core.TopAppBar
import com.example.agileandroidalpha.feature_board.presentation.util.Screen
import com.example.agileandroidalpha.firebase.login.UserData
import com.example.agileandroidalpha.ui.theme.Amaranth
import com.example.agileandroidalpha.ui.theme.Iridium
import com.example.agileandroidalpha.ui.theme.IrishGreen
import com.example.agileandroidalpha.ui.theme.Ruby
import com.example.agileandroidalpha.ui.theme.TurquoiseNat
import com.example.agileandroidalpha.ui.theme.TurquoiseSVG
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    basicInfo: UserData?,
    onContinueClicked: () -> Unit,
    user: FirebaseUser?,
    modifier : Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Choose Sign in Method",
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
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            user?.let {
                if (basicInfo?.photoLink != null) {
                    AsyncImage(
                        model = basicInfo.photoLink,
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(125.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                if (!basicInfo?.username.isNullOrBlank()) {
                    Text(
                        text = basicInfo?.username ?: "No username",
                        textAlign = TextAlign.Center,
                        fontSize = 33.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                Spacer(modifier = Modifier.height(80.dp))
                Text(
                    text = if (user.isAnonymous) "Logged in as: Anonymous User"
                    else "Logged in as: ${it.email}",
                    color = Iridium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(50.dp))
                Card {
                    Image(
                        painter =
                        painterResource(id = R.drawable.sd),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            } ?: Text("You are not logged in")
            if (user == null) {
                Spacer(modifier = Modifier.height(25.dp))
                Card {
                    Image(
                        painter =
                        painterResource(id = R.drawable.sd),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Spacer(modifier = Modifier.height(25.dp))
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Welcome to Agile Droid",
                color = IrishGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            FilledTonalButton(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked,
                colors = buttonColors(
                    containerColor = TurquoiseNat,
                    contentColor = Amaranth,
                    disabledContainerColor = TurquoiseSVG,
                    disabledContentColor = Ruby
                )
            ) {
                Text("Sign in with Email")
            }
            Spacer(modifier = Modifier.height(20.dp))
            FilledTonalButton(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = {
                    navController.navigate(Screen.GoogleScreen.route)
                },
                colors = buttonColors(
                    containerColor = TurquoiseNat,
                    contentColor = Amaranth,
                    disabledContainerColor = TurquoiseSVG,
                    disabledContentColor = Ruby
                )
            ) {
                Text("Sign in with Google")
            }
        }
    }
}