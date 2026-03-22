package com.example.spoonomics

import android.webkit.WebView
import android.webkit.JavascriptInterface
import androidx.navigation.NavController
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

    @JavascriptInterface
    fun updateTaskDetails(
        taskId: Int,
        newName: String,
        newDesc: String,
        newSpoons: Int,
        isPriority: Boolean,
        isRecurring: Boolean
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val existingTask = taskDao.getTaskById(taskId)
            if (existingTask != null) {
                val updatedTask = existingTask.copy(
                    name = newName,
                    description = newDesc,
                    spoonAllocation = newSpoons,
                    priority = isPriority,
                    recurring = isRecurring
                )
                taskDao.updateTask(updatedTask)
                withContext(Dispatchers.Main) {
                    navController.popBackStack()
                }
            }
        }
    }

    @JavascriptInterface
    fun requestTaskData(taskId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = taskDao.getTaskById(taskId)
            if (task != null) {
                val json = JSONObject().apply {
                    put("name", task.name)
                    put("description", task.description)
                    put("spoons", task.spoonAllocation)
                    put("priority", task.priority)
                    put("recurring", task.recurring)
                }
                withContext(Dispatchers.Main) {
                    webView.evaluateJavascript("receiveTaskData(${json})", null)
                }
            }
        }
    }

    @JavascriptInterface
    fun requestAllTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            val tasksList = taskDao.getTasksForUser(1).first()
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
            withContext(Dispatchers.Main) {
                webView.evaluateJavascript("receiveAllTasks(${jsonArray})", null)
            }
        }
    }
}