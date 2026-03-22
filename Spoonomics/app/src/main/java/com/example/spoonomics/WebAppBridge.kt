package com.example.spoonomics

import android.webkit.WebView
import android.webkit.JavascriptInterface
import androidx.navigation.NavController
import com.example.spoonomics.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

class WebAppBridge(
    private val taskDao: TaskDao,
    private val navController: NavController,
    private val webView: WebView
) {

    @JavascriptInterface
    fun navigateToEditScreen(taskId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            navController.navigate("edit_task/$taskId")
        }
    }

    // 2. Catches the save button click and accepts ALL the new form data
    @JavascriptInterface
    fun updateTaskDetails(
        taskId: Int,
        newName: String,
        newDesc: String,
        newSpoons: Int,
        isPriority: Boolean,
        isRecurring: Boolean
    ) {
        // Database queries MUST happen on the IO (background) thread
        CoroutineScope(Dispatchers.IO).launch {

            // Fetch the specific task from your database
            val existingTask = taskDao.getTaskById(taskId)

            if (existingTask != null) {
                // Use .copy() to update the properties based on your Task.kt schema
                val updatedTask = existingTask.copy(
                    name = newName,
                    description = newDesc,
                    spoonAllocation = newSpoons,
                    priority = isPriority,
                    recurring = isRecurring
                )

                // Save the fully updated task back to the database
                taskDao.updateTask(updatedTask)

                // Jump back to the main thread to return to the home screen
                CoroutineScope(Dispatchers.Main).launch {
                    navController.popBackStack()
                }
            }
        }
    }

    @JavascriptInterface
    fun requestTaskData(taskId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            // 1. Get the real task from the Room database
            val task = taskDao.getTaskById(taskId)

            if (task != null) {
                // 2. Package it safely into a JSON object
                val json = JSONObject().apply {
                    put("name", task.name)
                    put("description", task.description)
                    put("spoons", task.spoonAllocation)
                    put("priority", task.priority)
                    put("recurring", task.recurring)
                }

                // 3. Jump to the Main UI Thread to shout back to the HTML
                withContext(Dispatchers.Main) {
                    // This literally forces the browser to run: receiveTaskData({name: "QA", spoons: 15...})
                    webView.evaluateJavascript("receiveTaskData(${json.toString()})", null)
                }
            }
        }
    }

    @JavascriptInterface
    fun requestAllTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            // 1. Fetch the list from Room (Using User ID 1 based on our dummy data)
            // Because your DAO uses Flow, we use .first() to grab the current snapshot
            val tasksList = taskDao.getTasksForUser(1).first()

            // 2. Build a JSON Array to hold all the tasks
            val jsonArray = JSONArray()
            tasksList.forEach { task ->
                val jsonObject = JSONObject().apply {
                    put("id", task.id)
                    put("name", task.name)
                    put("description", task.description)
                    put("spoons", task.spoonAllocation)
                    put("priority", task.priority)
                }
                jsonArray.put(jsonObject)
            }

            // 3. Jump to the Main UI Thread to inject it into the HTML
            withContext(Dispatchers.Main) {
                // We send the array to a JS function called 'receiveAllTasks'
                webView.evaluateJavascript("receiveAllTasks(${jsonArray.toString()})", null)
            }
        }
    }
}
