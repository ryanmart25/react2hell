package com.example.spoonomics

import android.app.Application
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spoonomics.ui.theme.SpoonomicsTheme

// -------------------------------------------------------
// SURVEY GATE: set to true to show survey on launch,
// false to go straight to home
// -------------------------------------------------------
const val SHOW_SURVEY_ON_LAUNCH = true

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as Application

    val startDestination = if (SHOW_SURVEY_ON_LAUNCH) {
        UserDestination.Survey.route
    } else {
        UserDestination.Home.route
    }

    SpoonomicsTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable(UserDestination.Survey.route) {
                    SurveyWebScreen(
                        onSurveySubmit = {
                            navController.navigate(UserDestination.Home.route) {
                                popUpTo(UserDestination.Survey.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(UserDestination.Home.route) {
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                @Suppress("UNCHECKED_CAST")
                                return HomeViewModel(application) as T
                            }
                        }
                    )
                    UserHomeRoute(
                        viewModel = homeViewModel,
                        onNavigateToTaskCreation = {
                            navController.navigate(UserDestination.TaskCreation.route)
                        }
                    )
                }
                composable(UserDestination.TaskCreation.route) {
                    val taskCreationViewModel: TaskCreationViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                @Suppress("UNCHECKED_CAST")
                                return TaskCreationViewModel(application) as T
                            }
                        }
                    )
                    TaskCreationRoute(
                        viewModel = taskCreationViewModel,
                        onTaskSave = { navController.popBackStack() },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun SurveyWebScreen(onSurveySubmit: () -> Unit) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                addJavascriptInterface(object : Any() {
                    @android.webkit.JavascriptInterface
                    fun onSubmit() {
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            onSurveySubmit()
                        }
                    }
                }, "Android")
                webViewClient = WebViewClient()
                loadUrl("file:///android_asset/survey.html")
            }
        }
    )
}

@Composable
fun CuteCanvasWebScreen(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl("file:///android_asset/userHome.html")
            }
        }
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}