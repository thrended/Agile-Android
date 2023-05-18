@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class
)

package com.example.agileandroidalpha.feature_board.presentation.users

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forward
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.core.BottomNavBar
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.feature_board.presentation.util.Screen
import com.example.agileandroidalpha.firebase.login.UserData
import com.example.agileandroidalpha.ui.theme.Blueberry
import com.example.agileandroidalpha.ui.theme.Cerulean
import com.example.agileandroidalpha.ui.theme.Cranberry
import com.example.agileandroidalpha.ui.theme.DarkRaspberry
import com.example.agileandroidalpha.ui.theme.DeepPeach
import com.example.agileandroidalpha.ui.theme.IceBlue
import com.example.agileandroidalpha.ui.theme.SaffronOj
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun UserProfileScreen(
    navController: NavController,
    basicInfo: UserData?,
    modifier: Modifier = Modifier,
    vM: UserVM = hiltViewModel(),
    onMoreOptions: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val state = vM.state.value
    val user = vM.user
    val anon = (vM.user?.isAnonymous?: true || vM.user?.email?.isBlank() ?: true)
    var sheet by rememberSaveable { mutableStateOf(0) }
    var hide by rememberSaveable { mutableStateOf(true) }
    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val sheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            com.example.agileandroidalpha.core.TopAppBar(
                title = "User Profile",
                icon = Icons.Filled.Task,
                onIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                icon2 = Icons.Default.CloudSync,
                onIcon2Click = {
                  scope.launch {
                      vM.updateUser(basicInfo) {
                          Toast.makeText(
                              context,
                              "Successfully updated user information.",
                              Toast.LENGTH_SHORT
                          ).show()
                      }
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.TasksScreen.route)
                },
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier
                    .displayCutoutPadding()
            ) {
                Icon(imageVector = Icons.Default.Forward, contentDescription = "Continue")
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
                .padding(padding),
            contentAlignment = Alignment.Center
        )
        {
        if(anon){
            Text("Login to access user features")
        }
        else{
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                if(basicInfo?.photoLink != null) {
                    AsyncImage(
                        model = basicInfo.photoLink,
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.profsad_small)
                    )
                    //Spacer(modifier = Modifier.height(5.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                )
                {
                    if (!basicInfo?.username.isNullOrBlank()) {
                        Text(
                            text = basicInfo?.username ?: "No username",
                            textAlign = TextAlign.Center,
                            fontSize = 33.sp,
                            fontWeight = SemiBold,
                        )
                        if (basicInfo?.isEmailVerified == true) {
                            Text(
                                modifier = Modifier.offset(x = 15.dp, y = 15.dp),
                                text = "⭐ Verified User",
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                color = SaffronOj,
                                fontWeight = SemiBold,
                                fontStyle = Italic
                            )
                        } else {
                            Text(
                                modifier = Modifier.offset(x = 15.dp, y = 15.dp),
                                text = "☆ Unverified User",
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                color = Cranberry,
                                fontWeight = SemiBold,
                                fontStyle = Italic
                            )
                        }
                        //Spacer(modifier = Modifier.height(5.dp))
                    }
                }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(0.6f),
                    value = state.name,
                    label = { Text (
                        text = "Display Name",
                        color = Cerulean
                    ) },
                    placeholder = { Text (
                        text = state.name,
                        color = Cerulean
                    ) },
                    onValueChange = {
                        vM.change(it)
                    },
                    trailingIcon = {
                        IconButton(onClick = { }, enabled = false) {
                            Icon(
                                imageVector = Icons.Filled.PersonPin,
                                contentDescription = "visible")

                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )
                //Spacer(modifier = Modifier.height(5.dp))
                TextField(
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth(0.75f),
                    value = state.email,
                    label = { Text (
                        text = "Email",
                        color = Cerulean
                    ) },
                    placeholder = { Text (
                        text = "Change email",
                        color = Color.LightGray
                    ) },
                    onValueChange = {
                        vM.change(it, 2)
                    },
                    trailingIcon = {
                        IconButton(onClick = { }, enabled = false) {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = "visible")

                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    value = state.photo?: "",
                    label = { Text (
                        text = "Profile Picture",
                        color = Cerulean
                    ) },
                    placeholder = { Text (
                        text = "Add a photo",
                        color = Color.LightGray
                    ) },
                    onValueChange = {
                        vM.change(it, 1)
                    },
                    trailingIcon = {
                        IconButton(onClick = { }, enabled = false) {
                            Icon(
                                imageVector = Icons.Filled.Cameraswitch,
                                contentDescription = "visible")

                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )
                OutlinedTextField(
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth(0.75f),
                    value = state.phone,
                    label = { Text (
                        text = "Phone",
                        color = Cerulean
                    ) },
                    placeholder = { Text (
                        text = "Add a phone number",
                        color = Color.LightGray
                    ) },
                    onValueChange = {
                        vM.change(it, 3)
                    },
                    trailingIcon = {
                        IconButton(onClick = { }, enabled = false) {
                            Icon(
                                imageVector = Icons.Filled.PhonelinkSetup,
                                contentDescription = "visible")

                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                    value = state.password,
                    label = { Text (
                        text = "Password",
                        color = Cerulean
                    ) },
                    placeholder = { Text (
                        text = "Change password",
                        color = Color.LightGray
                    ) },
                    onValueChange = {
                        vM.changePassword(it)
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
                        }
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row (
                    horizontalArrangement = Arrangement.SpaceAround,
                )
                {

                    OutlinedButton(onClick = onSignOut) {
                        Text(text = "Sign out")
                    }
                    Spacer(modifier = Modifier.width(40.dp))
                    FilledTonalButton(
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            containerColor = IceBlue,
                            contentColor = DeepPeach,
                            disabledContentColor = Blueberry
                        ),
                        onClick = { vM.updateProfile(state.name, state.photo ?: "", ph = state.phone) }
                    ) {
                        Text("Save Changes")
                    }
                }

                OutlinedButton(
                    onClick = onMoreOptions
                ) {
                    Text(
                        text = "More Options",
                        color = DarkRaspberry,
                        fontWeight = SemiBold,
                        fontSize = 15.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(200.dp))
//            Column(
//                modifier = modifier
//                    .padding(padding),
//                verticalArrangement = Arrangement.Bottom
//            )
//            {
//                Row(
//                    modifier = Modifier
//                        .padding(all = 9.dp),
//                    horizontalArrangement = Arrangement.SpaceAround,
//                    verticalAlignment = Alignment.CenterVertically
//                )
//                {
//                    Button(onClick = onClick)
//                    {
//                        Text(text = "Navigate to Tasks")
//                    }
//                    Button(onClick = onClick2)
//                    {
//                        Text(text = "Navigate to Subtasks")
//                    }
//                }
//                Button(onClick = onClick3)
//                {
//                    Text(text = "Settings")
//                }
//            }
        }
        }
    }
}