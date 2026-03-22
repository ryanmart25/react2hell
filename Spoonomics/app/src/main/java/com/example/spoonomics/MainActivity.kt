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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spoonomics.ui.theme.SpoonomicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpoonomicsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CuteCanvasWebScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppRoot(){
    val NavController = rememberNavController()
    SpoonomicsTheme{
        Surface(
            modifier = Modifier
        ){
            NavHost(
                navController = NavController,
                startDestination = UserDestination.Home.route
            ){
                composable(UserDestination.Home.route){
                    val HomeViewModel: HomeViewModel = viewModel()
                    UserHomeRoute(
                        viewModel= HomeViewModel,
                        onNavigateToTaskCreation = {
                            NavController.navigate(UserDestination.TaskCreation.route)
                        }
                    )
                }
                composable(UserDestination.TaskCreation.route){
                    val TaskCreationViewModel: TaskCreationViewModel = viewModel()
                    TaskCreationRoute(
                        viewModel= TaskCreationViewModel,
                        onTaskSave = {
                            NavController.popBackStack()
                        },
                        onBack = {
                            NavController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun CuteCanvasWebScreen(modifier: Modifier = Modifier){
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                // Tailwind needs JavaScript to compile in the browser
                settings.javaScriptEnabled = true

                // This ensures links open inside your app, not in Chrome
                webViewClient = WebViewClient()

                // Point it to your shiny new assets folder!
                loadUrl("file:///android_asset/userHome.html")
            }
        }
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpoonomicsTheme {
        Greeting("Android")
    }
}