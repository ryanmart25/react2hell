package com.example.spoononomics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- 1. DESIGN SYSTEM COLORS ---
val EdPrimary = Color(0xFF555C8C)
val EdPrimaryDim = Color(0xFF49507F)
val EdTertiary = Color(0xFF71557C)
val EdSurface = Color(0xFFFBF8FE)
val EdContainerLow = Color(0xFFF5F2FB)
val EdContainerLowest = Color(0xFFFFFFFF)
val EdContainerHigh = Color(0xFFE9E7F1)
val EdOnSurface = Color(0xFF31323B)
val EdOutlineVariant = Color(0xFFB2B1BC)

// Gradient for the primary CTA
val PrimaryGradient = Brush.linearGradient(
    colors = listOf(EdPrimary, EdPrimaryDim)
)

@Composable
fun PatientSurvey(
    onNavigateBack: () -> Unit = {},
    onSaveSurvey: (Int, Boolean, String) -> Unit = { _, _, _ -> }
) {
    // --- 2. LOCAL UI STATE (No DB wiring yet) ---
    var startingSpoons by remember { mutableFloatStateOf(15f) }
    var sleptWell by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    // --- 3. THE SCREEN LAYOUT ---
    Scaffold(
        containerColor = EdSurface,
        topBar = {
            SurveyTopBar(onBackClick = onNavigateBack)
        },
        bottomBar = {
            SurveyBottomBar(
                onSaveClick = {
                    onSaveSurvey(startingSpoons.toInt(), sleptWell, notes)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp) // The "Whitespace" rule
        ) {

            // Hero Header
            Text(
                text = "Daily Check-In",
                color = EdOnSurface,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                // Note: You would apply your Plus Jakarta Sans font family here!
            )

            // Section 1: The Spoon Slider (Container Low)
            Surface(
                color = EdContainerLow,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "STARTING ENERGY",
                        color = EdTertiary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${startingSpoons.toInt()}",
                            color = EdPrimary,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Spoons",
                            color = EdOutlineVariant,
                            fontSize = 16.sp
                        )
                    }

                    Slider(
                        value = startingSpoons,
                        onValueChange = { startingSpoons = it },
                        valueRange = 0f..30f,
                        colors = SliderDefaults.colors(
                            thumbColor = EdPrimary,
                            activeTrackColor = EdPrimary,
                            inactiveTrackColor = EdContainerHigh
                        )
                    )
                }
            }

            // Section 2: Toggles (Container Lowest + Ambient Shadow)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SurveyToggleCard(
                    title = "Slept Well?",
                    subtitle = "7+ hours of uninterrupted sleep",
                    isChecked = sleptWell,
                    onCheckedChange = { sleptWell = it }
                )
            }

            // Section 3: Notes (Container High Input)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "NOTES",
                    color = EdTertiary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )

                // Custom TextField without the Material bottom line
                BasicTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    textStyle = TextStyle(color = EdOnSurface, fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(EdContainerHigh)
                        .padding(16.dp),
                    decorationBox = { innerTextField ->
                        if (notes.isEmpty()) {
                            Text("Any context for today?", color = EdOutlineVariant)
                        }
                        innerTextField()
                    }
                )
            }

            // Extra spacer so the bottom bar doesn't cover content
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// --- 4. REUSABLE COMPONENTS ---

@Composable
fun SurveyTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 16.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "BACK",
            color = EdPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onBackClick() }
        )
        Text(
            text = "SURVEY",
            color = EdOutlineVariant,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun SurveyToggleCard(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        color = EdContainerLowest,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            // Simulating the 6% ambient shadow from your CSS
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp), spotColor = EdOnSurface.copy(alpha = 0.06f))
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = EdOnSurface, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, color = EdOutlineVariant, fontSize = 12.sp)
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = EdContainerLowest,
                    checkedTrackColor = EdPrimary,
                    uncheckedThumbColor = EdOutlineVariant,
                    uncheckedTrackColor = EdContainerHigh
                )
            )
        }
    }
}

@Composable
fun SurveyBottomBar(onSaveClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(PrimaryGradient)
                .clickable { onSaveClick() }
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Save Check-In",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Preview to see it in Android Studio instantly
@Preview(showBackground = true)
@Composable
fun PreviewDailySurvey() {
    MaterialTheme {
        PatientSurvey()
    }
}