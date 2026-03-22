package com.example.spoonomics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    uiState: ModelsAndState.TaskCreationUiState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImportanceChange: (ModelsAndState.TaskImportance) -> Unit,
    onSpoonsChange: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Task", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onCancelClick) { Text("Cancel") }
                    TextButton(onClick = onSaveClick) { Text("Save") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Task Name Section
            TaskInputSection(label = "TASK NAME") {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.taskName,
                        onValueChange = onNameChange,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    )
                    IconButton(
                        onClick = { /* Mic logic */ },
                        modifier = Modifier.size(56.dp).border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                    ) { Icon(painter = painterResource(R.drawable.microphone), contentDescription = "Mic", tint = Color(0xFFFF9AA2)) }
                }
            }

            // Description Section
            TaskInputSection(label = "DESCRIPTION") {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    minLines = 4
                )
            }

            // Importance Level
            TaskInputSection(label = "IMPORTANCE LEVEL") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ImportanceButton(
                        label = "Priority Task",
                        icon = painterResource(R.drawable.priority_high),
                        isSelected = uiState.importance == ModelsAndState.TaskImportance.PRIORITY,
                        activeColor = Color(0xFFFF5252),
                        onClick = { onImportanceChange(ModelsAndState.TaskImportance.PRIORITY) }
                    )
                    ImportanceButton(
                        label = "Daily Task",
                        icon = painterResource(R.drawable.calendar_today),
                        isSelected = uiState.importance == ModelsAndState.TaskImportance.DAILY,
                        activeColor = Color(0xFFB5EAD7),
                        onClick = { onImportanceChange(ModelsAndState.TaskImportance.DAILY) }
                    )
                }
            }

            // Spoons / Effort Section
            SpoonCounter(
                count = uiState.spoons,
                onCountChange = onSpoonsChange
            )
        }
    }
}
@Composable
fun TaskInputSection(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Gray
        )
        content()
    }
}

@Composable
fun ImportanceButton(
    label: String,
    icon: Painter,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) activeColor else Color(0xFFF3F4F6),
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(label, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SpoonCounter(count: Int, onCountChange: (Int) -> Unit) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(2.dp, Color(0xFFFFDAC1).copy(alpha = 0.3f)),
        color = Color.White.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Spoons", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text("HOW MUCH ENERGY?", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(onClick = { if (count > 0) onCountChange(count - 1) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Decrease")
                }
                Text(count.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black)
                IconButton(onClick = { onCountChange(count + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }
    }
}
@Preview(showBackground = true, backgroundColor = 0xFFFFFBFA)
@Composable
fun PreviewCreateTaskScreen() {
    MaterialTheme {
        CreateTaskScreen(
            uiState = ModelsAndState.TaskCreationUiState(
                taskName = "Deep Work Session",
                description = "Finalize the wireframes for the new dashboard...",
                importance = ModelsAndState.TaskImportance.PRIORITY,
                spoons = 4
            ),
            onBackClick = {},
            onSaveClick = {},
            onCancelClick = {},
            onNameChange = {},
            onDescriptionChange = {},
            onImportanceChange = {},
            onSpoonsChange = {}
        )
    }
}
