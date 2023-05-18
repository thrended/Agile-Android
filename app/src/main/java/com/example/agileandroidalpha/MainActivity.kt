@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalTime::class, ExperimentalTime::class
)

package com.example.agileandroidalpha

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.core.MainNavigation
import com.example.agileandroidalpha.feature_board.presentation.util.Screen
import com.example.agileandroidalpha.firebase.firestore.model.ChatMessage
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.presentation.GoogleAuthClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColors(
            primary = Color(0xFFBB86FC),
            primaryVariant = Color(0xFF3700B3),
            secondary = Color(0xFF03DAC5)
        )
    } else {
        lightColors(
            primary = Color(0xFF6200EE),
            primaryVariant = Color(0xFF3700B3),
            secondary = Color(0xFF03DAC5)
        )
    }
    val typography = Typography(
        body1 = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

@ExperimentalTime
@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val sprintRef = Firebase.firestore.collection("sprints")
    private val storyRef = Firebase.firestore.collection("stories")
    private val subtaskRef = Firebase.firestore.collection("subtasks")
    private val userRef = Firebase.firestore.collection("users")
    private val chatRef = Firebase.firestore.collection("chats")
    private val activityTAG = "MainActivity"
    private val googleAuth by lazy {
        GoogleAuthClient(
            context = applicationContext,
            oneTap = Identity.getSignInClient(applicationContext)
        )
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                if (auth.currentUser != null) {
                    Toast.makeText(
                        this@MainActivity,
                        "Welcome, ${auth.currentUser?.email ?: "Anonymous"}" ,
                        Toast.LENGTH_LONG
                    ).show()
                }
                MainApp()
            }
        }
    }

    private fun subscribeToRealtimeUpdates() {
        sprintRef.addSnapshotListener { snapshot, exception ->
            exception?.let {
                Toast.makeText(
                    this,
                    "An exception occurred while trying to subscribe to real-time updates.",
                    Toast.LENGTH_LONG)
                    .show()
            }
            snapshot?.let {
                val sb = StringBuilder()
                snapshot.documents.forEachIndexed { i, doc ->
                    val sprint = doc.toObject<Sprint>()
                    sb.append("$sprint")
                }
            }
        }
        subtaskRef.addSnapshotListener { snapshot, exception ->
            exception?.let {
                Toast.makeText(
                    this,
                    "An exception occurred while trying to subscribe to real-time updates.",
                    Toast.LENGTH_LONG)
                    .show()
            }
            snapshot?.let {
                val sb = StringBuilder()
                snapshot.documents.forEachIndexed { i, doc ->
                    val story = doc.toObject<Story>()
                    sb.append("$story\n")
                }
            }
        }
        storyRef.addSnapshotListener { snapshot, exception ->
            exception?.let {
                Toast.makeText(
                    this,
                    "An exception occurred while trying to subscribe to real-time updates.",
                    Toast.LENGTH_LONG)
                    .show()
            }
            snapshot?.let {
                val sb = StringBuilder()
                snapshot.documents.forEachIndexed { i, doc ->
                    val story = doc.toObject<Story>()
                    sb.append("$story\n")
                }
            }
        }
        userRef.addSnapshotListener { snapshot, exception ->
            exception?.let {
                Toast.makeText(
                    this,
                    "An exception occurred while trying to subscribe to real-time updates.",
                    Toast.LENGTH_LONG)
                    .show()
            }
            snapshot?.let {
                val sb = StringBuilder()
                snapshot.documents.forEachIndexed { i, doc ->
                    val user = doc.toObject<FireUser>()
                    sb.append("$user\n")
                }
            }
        }
        chatRef.addSnapshotListener { snapshot, exception ->
            exception?.let {
                Toast.makeText(
                    this,
                    "An exception occurred while trying to subscribe to real-time updates.",
                    Toast.LENGTH_LONG)
                    .show()
            }
            snapshot?.let {
                val sb = StringBuilder()
                snapshot.documents.forEachIndexed { i, doc ->
                    val message = doc.toObject<ChatMessage>()
                    sb.append("$message\n")
                }
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            Toast.makeText(this, "You Signed In successfully", Toast.LENGTH_LONG).show()
            //ContextCompat.startActivity(Intent(this, AnotherActivity::class.java))
        } else {
            Toast.makeText(this, "You are not signed in", Toast.LENGTH_LONG).show()
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
//        if (currentUser == null) {
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    startActivity(Intent(MainActivity(), FirebaseUIActivity::class.java))
//                } catch (e: Exception) {
//                    print(e)
//                    //Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
//                }
//            }
//        }
        currentUser?.let {
            auth.updateCurrentUser(it)
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl

            // Check if user's email is verified
            val emailVerified = it.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = it.uid
        }

        subscribeToRealtimeUpdates()
    }

@ExperimentalMaterial3Api
@Composable
fun MainApp(
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentScreen = Screen.fromScreen(
        backStackEntry.value?.destination?.route
    )
    Scaffold(

        drawerContent = {
            DroidDrawer(
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
        scaffoldState = scaffoldState
        //add topAppBar and all other things here
    ) { innerPadding ->
        MainNavigation(
            navController = navController,
            lifecycleScope = lifecycleScope,
            context = applicationContext,
            auth = Firebase.auth,
            googleAuth = googleAuth,
            backStackEntry = backStackEntry,
            currentScreen = currentScreen,
            modifier = Modifier.padding(innerPadding))

    }
}

}

@Composable
fun ImageCard(
    painter: Painter,
    contentDescription: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Box(modifier = Modifier
            .height(200.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startY = 300f
                        )
                    )
            ) {}
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                androidx.compose.material.Text(title, style = TextStyle(color = Color.White, fontSize = 16.sp))
            }
        }
    }
}
