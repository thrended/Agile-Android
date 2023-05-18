package com.example.agileandroidalpha.core

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.feature_board.presentation.util.Screen
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.ui.theme.AgileAndroidAlphaTheme
import com.example.agileandroidalpha.ui.theme.Cobalt
import com.example.agileandroidalpha.ui.theme.LBGold
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun DroidDrawer(
    navController: NavController,
    modifier: Modifier = Modifier,
    drawers: List<String> = listOf("Home", "Chat", "Active Sprints", "Backlog", "Archive",
        "Search", "My Activities", "User Profile", "Settings", "Logout", "Help", "Admin Panel",
        "Debug Menu", "Retrospective", "Story Map", "Epics", "Projects", "Teams", "Boards", "Issues",),
    drawerDetails: List<Long> = emptyList(),
    closeDrawerAction: () -> Unit = {},
    clickDrawerAction: (String) -> Unit = {},
    clickDrawerSpecial: (Long) -> Unit = {},
    header: String? = null,
    disabled: Boolean = false
) {
    val currentScreen = navController.currentBackStackEntryAsState().value?.destination?.route
    val user: FirebaseUser? = Firebase.auth.currentUser
    val fireUser = remember{mutableStateOf(FireUser())}
    fun getFireUser(uid: String? = user?.uid?: "") = CoroutineScope(Dispatchers.IO).launch {
        fireUser.value = matchUser(uid)?: FireUser(name = "Anonymous")
    }
    Column(
        modifier
            .fillMaxSize()
            .padding(start = 12.dp, top = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(R.drawable.drawer),
                contentDescription = stringResource(R.string.splash)
            )
            Spacer(modifier = Modifier.width(20.dp))
            user?.let { u ->
                val name = u.displayName?: u.email?: "Unnamed User"
                Text (text = name)
            }
            if ( user != null && !user.isAnonymous) {
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.profsad_small)
                )
            }
            else {
                Image(
                    painter = painterResource(
                        id = if (user != null && user.isAnonymous) R.drawable.alienhead
                        else R.drawable.android_logo
                    ),
                    contentDescription = stringResource(R.string.drawer_menu)

                )
            }
        }
        header?.let{
            Card(modifier = modifier
                .fillMaxWidth(0.3f)
            ) {
                Image(
                    painter = painterResource(
                        id = if (user != null && user.photoUrl != null) R.drawable.profsad_small
                        else if (user != null && !user.isAnonymous) R.drawable.alienhead
                        else R.drawable.android_logo),
                    contentDescription = stringResource(R.string.drawer_menu)

                )
                Text(
                    modifier = modifier
                        .align(Alignment.CenterHorizontally),
                    text = header,
                    textAlign = TextAlign.Justify,
                    fontWeight = FontWeight(1000),
                    fontSize = 25.sp
                )
            }
        }
        drawers.forEachIndexed { index, drawer ->
            Spacer(Modifier.height(18.dp))
            val enableCheck = !disabled && index <= 11 && drawerToS(drawer) != currentScreen
            Text(
                modifier = modifier
                    .align(Alignment.Start)
                    .clickable(
                        enabled = enableCheck,
                        onClick = {
                            if (drawerDetails.isNotEmpty()) clickDrawerSpecial(drawerDetails[index])
                            else clickDrawerAction(drawerToS(drawer))
                        }
                    ),
                color = if (enableCheck) Color.Unspecified
                        else if (drawerToS(drawer) == currentScreen) Color(ColorUtils.blendARGB(
                             LBGold.toArgb(), Cobalt.toArgb(), .067f))
                        else Color.LightGray,
                text = drawer,
                textAlign = TextAlign.Center,
                fontWeight = if (drawerToS(drawer) == currentScreen) FontWeight(750)
                            else null,
                style = MaterialTheme.typography.h6)
        }
    }
}

suspend fun enableRules(s: String): Boolean {
    return when(s) {
        
        else -> true
    }
}

suspend fun matchUser(id: String?): FireUser? {
    if (id.isNullOrBlank()) {
        return null
    }
    var fireUser: FireUser? = null
    try {
        val snapshot = Firebase.firestore.collection("users")
            .whereEqualTo("firebaseId", id)
            .limit(1)
            .get()
            .await()
        val sb = StringBuilder()
        snapshot.documents.forEach { doc ->
            fireUser = doc.toObject<FireUser>()
            sb.append("$fireUser\n")
        }
        Log.d("user", "Successfully retrieved firestore user $id")
    } catch(e: Exception) {
        Log.e("user", e.localizedMessage?: "Error loading firestore user data")
    }
    return fireUser
}

fun drawerToS(s: String): String {
    return when (s) {
        "Home" -> if (Firebase.auth.currentUser == null ||
                    Firebase.auth.currentUser?.isAnonymous == true) Screen.Home.route
                else Screen.UserProfile.route
        "Chat" -> Screen.Chat.route
        "Backlog" -> Screen.Backlog.route
        "Archive" -> Screen.Archives.route
        "Active Sprints" -> Screen.TasksScreen.route
        "My Activities" -> Screen.My.route
        "Search" -> Screen.Search.route
        "New Sprint" -> Screen.NewSprint.route
        "Future Sprints" -> Screen.FutureSprints.route
        "Boards" -> Screen.Boards.route
        "Admin Panel" -> Screen.AdminPanel.route
        "User Panel" -> Screen.UserPanel.route
        "User Profile" -> Screen.UserProfile.route
        "Settings" -> Screen.Settings.route
        "Login" -> Screen.Login.route
        "Logout" -> Screen.Logout.route
        "Help" -> Screen.Help.route
        "Debug Menu" -> Screen.Debug.route
        "Retrospective" -> Screen.Retro.route
        else -> s
    }
}

@Preview
@Composable
fun AGDroidDrawerPreview() {
    AgileAndroidAlphaTheme {
        //DroidDrawer()
    }
}