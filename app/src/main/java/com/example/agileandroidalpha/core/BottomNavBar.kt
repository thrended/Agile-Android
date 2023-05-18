package com.example.agileandroidalpha.core

import androidx.compose.foundation.layout.Column
import androidx.compose.material.BadgedBox
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CallToAction
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.agileandroidalpha.feature_board.presentation.util.BottomNavItem
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.sprintsList
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.storiesList
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.usersList
import com.example.agileandroidalpha.ui.theme.MintBlue
import com.example.agileandroidalpha.ui.theme.MintGreen
import com.example.agileandroidalpha.ui.theme.PastelBlue
import com.example.agileandroidalpha.ui.theme.RobinEgg
import com.example.agileandroidalpha.ui.theme.Teal
import com.example.agileandroidalpha.ui.theme.WildWatermelon
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    badge: Int = 0,
    items: List<BottomNavItem> = listOf(
        BottomNavItem(
            name = if (Firebase.auth.currentUser == null || Firebase.auth.currentUser?.isAnonymous == true)
                "Home" else "User Profile",
            route = if (Firebase.auth.currentUser == null || Firebase.auth.currentUser?.isAnonymous == true)
                "home" else "user-profile",
            icon = if (Firebase.auth.currentUser == null || Firebase.auth.currentUser?.isAnonymous == true)
                Icons.Default.Home else Icons.Default.SupervisedUserCircle,
            badgeCount = usersList.count()
        ),
        BottomNavItem(
            name = "Active Sprints",
            route = "tasks_screen",
            icon = Icons.Default.CallToAction,
            badgeCount = storiesList.count { s -> !s.done }
        ),
        BottomNavItem(
            name = "Backlog",
            route = "backlog",
            icon = Icons.Default.MoveDown,
            badgeCount = sprintsList.count { s -> !s.completed } . also {
            sprintsList.filter{ s -> !s.started }.forEach {
                storiesList.count { s -> s.sid == it.id}
            }}
        ),
        BottomNavItem(
            name = "Archives",
            route = "archives",
            icon = Icons.Default.AdminPanelSettings,
            badgeCount = sprintsList.count { s -> s.isArchived }
        ),
        BottomNavItem(
            name = "Chat",
            route = "chat",
            icon = Icons.Default.Chat
        ),
//        BottomNavItem(
//            name = "Settings",
//            route = "settings",
//            icon = Icons.Default.Settings
//        )
    ),
    bgColor: Color = Color (ColorUtils.blendARGB(
        MintGreen.toArgb(), MintBlue.toArgb(), 0.5f
    )),
    color: Color = Color (ColorUtils.blendARGB(
        WildWatermelon.toArgb(), Teal.toArgb(), Random.nextFloat()
    )),
    badgeColor: Color = Color (ColorUtils.blendARGB(
        PastelBlue.toArgb(), RobinEgg.toArgb(), 0.5f
    )),
    onBarClicked: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier,
        backgroundColor = bgColor,
        elevation = 12.dp
    ) {
        items.forEach { itm ->
            val selected = itm.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                onClick = { onBarClicked(itm) },
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        if(itm.badgeCount > 0) {
                            BadgedBox(badge = {
                                Text(text = itm.badgeCount.toString(), color = badgeColor)
                            }) {
                                Icon(imageVector = itm.icon, contentDescription = itm.name)
                            }
                        }
                        else {
                            Icon(imageVector = itm.icon, contentDescription = itm.name)
                        }
                        if (selected) {
                            Text(
                                text = itm.name, color = color, textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight(333)
                            )
                        }
                    }
                }

            )
        }
    }
}