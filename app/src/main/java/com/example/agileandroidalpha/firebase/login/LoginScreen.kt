@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.example.agileandroidalpha.firebase.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.agileandroidalpha.ImageCard
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.core.TopAppBar
import com.example.agileandroidalpha.feature_board.presentation.util.Screen
import com.example.agileandroidalpha.ui.theme.Cerulean
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    vM: LoginVM = hiltViewModel()
) {
    val state = vM.state
    val user = vM.user
    val currentUser = vM.currentUser
    val context = LocalContext.current
    var sheet by rememberSaveable { mutableStateOf(0) }
    var hide by rememberSaveable { mutableStateOf(true) }
    var hide2 by rememberSaveable { mutableStateOf(true) }
    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val sheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val focusManager = LocalFocusManager.current
    BottomSheetScaffold(
        sheetContent = {
            when(sheet) {
                0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                    Spacer(modifier = Modifier.height(40.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.75f),
                        value = state.regUser,
                        label = { Text (
                            text = "Username",
                            color = Cerulean
                        ) },
                        placeholder = { Text (
                            text = "Enter your email address (username)",
                            color = Cerulean
                        ) },
                        onValueChange = {
                            vM.changeRegisterText(it)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {focusManager.clearFocus()})
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        value = state.regPass,
                        placeholder = { Text (
                            text = "Enter a password between 6 and 64 characters",
                            color = Color.LightGray
                        ) },
                        onValueChange = {
                            vM.changeRegisterText(it, 1)
                        },
                        trailingIcon = {
                            IconButton(onClick = { hide = !hide }) {
                                Icon(
                                    imageVector = if(hide) Icons.Filled.VisibilityOff
                                    else Icons.Filled.Visibility, contentDescription = "visible")
                            }
                        },
                        singleLine = true,
                        visualTransformation =
                            if (hide) PasswordVisualTransformation()
                            else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                hide = true
                            }
                        )

                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        value = state.confirmPass,
                        placeholder = { Text (
                            text = "Confirm your password",
                            color = Color.LightGray
                        ) },
                        onValueChange = {
                            vM.changeRegisterText(it, 2)
                        },
                        trailingIcon = {
                            IconButton(onClick = { hide2 = !hide2 }) {
                                Icon(
                                    imageVector = if(hide2) Icons.Filled.VisibilityOff
                                    else Icons.Filled.Visibility, contentDescription = "visible")
                            }
                        },
                        singleLine = true,
                        visualTransformation =
                            if (hide2) PasswordVisualTransformation()
                            else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                hide2 = true
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        OutlinedButton(
                            modifier = Modifier
                                .fillMaxWidth(0.4f),
                            onClick = {
                                scope.launch {
                                    sheetScaffoldState.bottomSheetState.collapse()
                                }
                            },
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Text ("Cancel")
                        }
                        OutlinedButton(
                            modifier = Modifier
                                .fillMaxWidth(0.4f),
                            onClick = {
                                focusManager.clearFocus()
                                vM.createUser { result ->
                                    scope.launch {
                                        if (result.first || vM.loggedIn) {
                                            Toast.makeText(
                                                context,
                                            "Successfully created the user. Your login details:" +
                                                "Username: ${result.second}\n" +
                                                "Password: ${result.third}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            sheetScaffoldState.bottomSheetState.collapse()
                                        } else {
                                            Toast.makeText(
                                                context,
                                            "Error during registration: invalid user format or password length. " +
                                                "Entered username was: ${result.second}\n" +
                                                "Entered password was: ${result.third}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            },
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text ("Register")
                        }
                    }
                    }
                }
                1 -> {

                }
                else -> {

                }
            }
        } ,
        scaffoldState = sheetScaffoldState
    ) {



    Scaffold(
        topBar = {
        TopAppBar(
            title = "Email Login and Registration",
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
                    }
                },
                disabled = true
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
//        bottomBar = {
//            BottomNavBar(
//                navController = navController,
//                onBarClicked = {
//                    navController.navigate(it.route)
//                },
//            )
//        },
        scaffoldState = scaffoldState
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .drawBehind {

                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                ImageCard(
                    painter = painterResource(id = R.drawable.profsad),
                    contentDescription = "User Login",
                    title = "Welcome. Please login using the preferred sign-in method or click the button at the bottom to register a new account.",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                //Spacer(modifier = Modifier.width(30.dp))
                Spacer(modifier = Modifier.height(40.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .align(Alignment.CenterHorizontally),
                    value = state.username,
                    label = { Text (
                        text = "Email Address",
                        color = Cerulean
                    ) },
                    placeholder = { Text (
                        text = "Enter your username",
                        color = Color.LightGray
                    ) },
                    onValueChange = {
                        vM.changeLoginText(it)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {focusManager.clearFocus()})
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.CenterHorizontally),
                    value = state.password,

                    label = { Text (
                        text = "Password",
                        color = Cerulean
                    ) },
                    placeholder = { Text (
                        text = "Enter password",
                        color = Color.LightGray
                    ) },
                    onValueChange = {
                        vM.changeLoginText(it, 1)
                    },
                    singleLine = true,
                    trailingIcon = {
                       IconButton(onClick = { hide = !hide }) {
                           Icon(
                               imageVector = if(hide) Icons.Filled.VisibilityOff
                                    else Icons.Filled.Visibility, contentDescription = "visible")
                       }
                    },
                    visualTransformation =
                        if (hide) PasswordVisualTransformation()
                        else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            hide = true
                        }
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth(0.6f),
                    enabled = !vM.loggedIn || vM.anon,
                    onClick = {
                        focusManager.clearFocus()
                        vM.login { result ->
                            scope.launch {
                            if(result.first) {
                                Toast.makeText(
                                    context,
                            "Login Success. Current user: " +
                                    "Username: ${result.second}\n" +
                                    "Password: ${result.third}" ,
                                    Toast.LENGTH_LONG
                                ).show()
                                sheetScaffoldState.bottomSheetState.collapse()
                                delay(1000L)
                                navController.navigate(Screen.TasksScreen.route) {
                                    launchSingleTop = true
                                    popUpTo(Screen.TasksScreen.route)
                                    { inclusive = true }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Invalid username or password login credentials. " +
                                        "Entered username was: ${result.second}\n" +
                                        "Entered password was: ${result.third}" ,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        }
                    },
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text ("Login w/ Email")
                }
                user?.email?.let {
                    Spacer(modifier = Modifier.height(20.dp))
                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth(0.8f),
                        enabled = true,
                        onClick = {
                            scope.launch {
                                if(vM.loggedIn) {
                                    Toast.makeText(
                                        context,
                                        "Continuing as current user: ${user.email}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    sheetScaffoldState.bottomSheetState.collapse()
                                    delay(750L)
                                    navController.navigate(Screen.UserProfile.route) {
                                        launchSingleTop = true
                                        popUpTo(Screen.UserProfile.route)
                                        { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Bad user login data. Please try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        if(user.email!!.isNotBlank()) {
                            Text("Continue as ${user.email}")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(0.6f),
                    enabled = currentUser != null || !vM.anon,
                    onClick = {
                        scope.launch {
                            navController.navigate(Screen.Logout.route)
                        }
                    },
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text ("Sign out of current user")
                }
                Spacer(modifier = Modifier.height(20.dp))
//                Text("Test stuff ${vM.user?.email}")
//                Text("Test stuff ${currentUser?.email}")
//                Text("Is anonymous: ${user?.isAnonymous}")
//                Text("Is anonymous: ${vM.user?.isAnonymous}")
//                Text("Is anonymous: ${vM.anon}")
//                Text("Is logged in: ${vM.loggedIn}")
//                Text("Login success : ${vM.state.isSuccessful}")
//                Text("Is anonymous: ${currentUser?.isAnonymous}")
//                Text("Is anonymous: ${currentUser?.isAnonymous}")
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(0.6f),
                    onClick = {
                        scope.launch {
                            sheetScaffoldState.bottomSheetState.expand()
                            hide = true
                        }
                    },
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text ("Register")
                }
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(0.6f),
                    onClick = {
                        scope.launch {
                            vM.anonLogin()
                            Toast.makeText(
                                context,
                                "Signing in anonymously. Features requiring registered users" +
                                        " have been disabled.",
                                Toast.LENGTH_SHORT
                            ).show()
                            delay(1000L)
                            navController.navigate(Screen.TasksScreen.route) {
                                launchSingleTop = true
                                popUpTo(Screen.TasksScreen.route)
                                { inclusive = true }
                            }
                        }
                    },
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text ("Sign in as Anonymous")
                }
            }

        }
    }
    }
}