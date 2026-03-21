package com.example.spoonomics

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spoonomics.ui.theme.SpoonomicsTheme
import com.example.spoononomics.WebAppBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Use your custom database instance!
        val db = AppDatabase.getInstance(applicationContext)
        val taskDao = db.taskDao()
        val userDao = db.userDao()

        lifecycleScope.launch(Dispatchers.IO) {
            // 1. Check if we have any tasks in the database
            // (Using userId 1 since that's what we are about to create)
            val currentTasks = taskDao.getTasksForUser(1).firstOrNull() // TODO hy only one?

            if (currentTasks.isNullOrEmpty()) {
                // 2. Create the dummy User FIRST (Satisfies the Foreign Key)
                val dummyUser = User(id = 1, dailySpoons = 100, spoonsCompleted = 0)
                userDao.insertUser(dummyUser)

                // 3. Create the dummy Task tied to User #1
                val dummyTask = Task(
                    id = 1, // Force it to be ID 1 so your HTML button works!
                    userId = 1,
                    name = "Final Production QA",
                    description = "One last check to make sure everything is perfect before the big release!",
                    spoonAllocation = 15,
                    priority = true,
                    recurring = false
                )
                taskDao.insertTask(dummyTask)
            }
        }

        setContent {
            SpoonomicsTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            CuteCanvasWebScreen(
                                url = "file:///android_asset/userHome.html",
                                taskDao = taskDao,             // Pass the DAO
                                navController = navController  // Pass the NavController
                            )
                        }
                        composable(
                            route = "edit_task/{taskId}",
                            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getInt("taskId")
                            CuteCanvasWebScreen(
                                url = "file:///android_asset/edit_task.html?id=$taskId",
                                taskDao = taskDao,             // Pass the DAO
                                navController = navController  // Pass the NavController
                            )
                        }
                    }
                }
            }
        }
    }
}
// 3. Updated WebScreen Composable
@Composable
fun CuteCanvasWebScreen(
    url: String,
    taskDao: TaskDao,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()

                // Create the bridge HERE, passing 'this' (the WebView itself)
                val bridge = WebAppBridge(taskDao, navController, this)
                addJavascriptInterface(bridge, "AndroidBridge")

                loadUrl(url)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}