package com.example.spoonomics
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Theme Colors ---
val SoftPink = Color(0xFFFF9AAE)
val Mint = Color(181, 234, 215)
val Lavender = Color(199, 206, 234)
val Peach = Color(255, 218, 193)
val SoftYellow = Color(255, 244, 189)
val UrgentRed = Color(255, 82, 82)
val SurfaceColor = Color(0xFFFFFBFA)
val OnSurface = Color(0xFF4A4A4A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    uiState: ModelsAndState.HomeUiState,
    onNavigateToTaskCreation: () -> Unit
) {
    var isChatExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = SurfaceColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Tasks",
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(2.dp, SoftPink.copy(alpha = 0.2f), CircleShape)
                            .background(Lavender)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = { BottomNavBar(onTasksClick = {}, onStartDayClick = onNavigateToTaskCreation) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                // Main Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item { HeroStats(uiState) }

                    val highPriorityTasks = uiState.tasks.filter { it.priority }
                    val activeStreamTasks = uiState.tasks.filter { !it.priority }

                    if (highPriorityTasks.isNotEmpty()) {
                        item { SectionHeader("High Priority") }
                        items(highPriorityTasks) { task ->
                            TaskCard(
                                title = task.name,
                                description = task.description,
                                spoons = task.spoonAllocation,
                                tag = "Urgent",
                                containerColor = UrgentRed,
                                contentColor = Color.White
                            )
                        }
                    }

                    if (activeStreamTasks.isNotEmpty()) {
                        item { SectionHeader("Active Stream") }
                        items(activeStreamTasks) { task ->
                            TaskCard(
                                title = task.name,
                                description = task.description,
                                spoons = task.spoonAllocation,
                                tag = "Daily",
                                containerColor = Mint,
                                contentColor = OnSurface
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }

            // Floating Mascot and Input
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 100.dp, end = 16.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        AnimatedVisibility(
                            visible = isChatExpanded,
                            enter = expandHorizontally() + fadeIn(),
                            exit = shrinkHorizontally() + fadeOut()
                        ) {
                            ChatInputBar()
                        }

                        MascotButton(onClick = onNavigateToTaskCreation)
                    }

                    if (!isChatExpanded) {
                        Text(
                            "ADD TASK",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OnSurface.copy(alpha = 0.4f),
                            modifier = Modifier.padding(top = 8.dp, end = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeroStats(uiState: ModelsAndState.HomeUiState) {
    val pendingCount = uiState.tasks.count { !it.completeStatus }
    val doneCount = uiState.tasks.count { it.completeStatus }
    val totalCount = uiState.tasks.size

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBox(pendingCount.toString(), "Pending", SoftYellow, Modifier.weight(1f))
        StatBox("$doneCount/$totalCount", "Done!", Mint, Modifier.weight(1f))
        StatBox("4", "Goals", Lavender, Modifier.weight(1f))
    }
}

@Composable
fun StatBox(value: String, label: String, color: Color, modifier: Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color.White),
        modifier = modifier.height(80.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurface.copy(alpha = 0.6f))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    title: String,
    description: String,
    spoons: Int,
    tag: String,
    containerColor: Color,
    contentColor: Color,
    isInitialExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(isInitialExpanded) }

    Card(
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, Color.White.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color.White.copy(alpha = 0.3f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
                Surface(
                    color = Color.Black.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painter = painterResource(R.drawable.microphone), null, modifier = Modifier.size(14.dp), tint = contentColor)
                        Spacer(Modifier.width(4.dp))
                        Text("$spoons spoons", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = contentColor)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = contentColor)

            AnimatedVisibility(visible = expanded) {
                Column {
                    Text(
                        description,
                        fontSize = 14.sp,
                        color = contentColor.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("EDIT THIS TASK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = contentColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar() {
    Card(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(280.dp)
            .height(64.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        border = BorderStroke(2.dp, Peach.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(R.drawable.microphone), null, tint = SoftPink)
            Spacer(Modifier.width(12.dp))
            Text(
                "Try saying \"Add a meeting\"",
                color = OnSurface.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MascotButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val dy by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "float"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .size(64.dp)
            .offset(y = dy.dp),
        shape = CircleShape,
        color = Color.White,
        border = BorderStroke(4.dp, Color.White),
        shadowElevation = 8.dp
    ) {
        // Placeholder for Mascot Image
        Box(modifier = Modifier.background(SoftPink)) {
            Image(
                painter = painterResource(R.drawable.dog_icon),
                modifier = Modifier.clip(CircleShape),
                contentDescription = null,
            )
        }
    }
}

@Composable
fun BottomNavBar(
    onTasksClick: () -> Unit,
    onStartDayClick: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.95f),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        shadowElevation = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NavButton("Tasks", Icons.Default.CheckCircle, onClick = onTasksClick)
            NavButton("Start Day", icon = Icons.Default.PlayArrow, active = false, onClick = onStartDayClick)
        }
    }
}

@Composable
fun NavButton(label: String, icon: ImageVector, active: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = if (active) SoftPink else Color.Transparent),
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = if (active) Color.White else OnSurface)
            Spacer(Modifier.width(8.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (active) Color.White else OnSurface)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Preview
@Composable
fun UserHomeScreenPreview() {
    MaterialTheme {
        UserHomeScreen(
            uiState = ModelsAndState.HomeUiState(id = 1, isLoading = false),
            onNavigateToTaskCreation = {}
        )
    }
}
