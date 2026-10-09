package com.darren.week3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

//icons hehe (thx ai)
private val WhiteLightningIcon: ImageVector
    get() = ImageVector.Builder(
        name = "WhiteLightning",
        defaultWidth = 100.dp,
        defaultHeight = 100.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).addPath(
        pathData = PathParser().parsePathString("M7,2v11h3v9l7-12h-4l4-8H7z").toNodes(),
        fill = SolidColor(Color.White)
    ).build()

private val WhiteWarningIcon: ImageVector
    get() = ImageVector.Builder(
        name = "WhiteWarning",
        defaultWidth = 100.dp,
        defaultHeight = 100.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).addPath(
        pathData = PathParser().parsePathString("M1,21h22L12,2L1,21z M13,18h-2v-2h2v2z M13,14h-2v-4h2v4z").toNodes(),
        fill = SolidColor(Color.White)
    ).build()

private val WhiteRunnerIcon: ImageVector
    get() = ImageVector.Builder(
        name = "WhiteRunner",
        defaultWidth = 100.dp,
        defaultHeight = 100.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).addPath(
        pathData = PathParser().parsePathString("M13.5,5.5c1.1,0,2-.9,2-2s-.9-2-2-2-2,.9-2,2,.9,2,2,2z M9.8,8.9L7,23h2.1l1.8-8l2.1,2v6h2v-7.5l-2.1-2l.6-3c1.3,1.5,3.3,2.5,5.5,2.5v-2c-1.9,0-3.5-1-4.3-2.4l-1-1.6c-.4-.6-1-1-1.7-1-.3,0-.5,.1-.8,.1L6,8.3V13h2V9.6l1.8-.7z").toNodes(),
        fill = SolidColor(Color.White)
    ).build()

private val WhiteCheckCircleIcon: ImageVector
    get() = ImageVector.Builder(
        name = "WhiteCheckCircle",
        defaultWidth = 100.dp,
        defaultHeight = 100.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).addPath(
        pathData = PathParser().parsePathString("M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10-4.48 10-10S17.52,2 12,2z").toNodes(),
        fill = SolidColor(Color.White)
    ).addPath(
        pathData = PathParser().parsePathString("M9.99,16.18L6.41,12.59L5,14L9.99,19L19,9.99L17.59,8.58z").toNodes(),
        fill = SolidColor(Color(0xFF4CAF50))
    ).build()

enum class ReactionState { START, WAITING, READY, TRIAL_DONE, FAILED, FINAL }

@Composable
fun ReactionTestScreen() {

    var trialIndex by remember { mutableStateOf(0) }
    val trialTimes = remember { mutableStateListOf<Int?>(null, null, null) }
    var state by remember { mutableStateOf(ReactionState.START) }
    var readyStartTime by remember { mutableStateOf(0L) }

    LaunchedEffect(state, trialIndex) {
        if (state == ReactionState.WAITING) {
            val randomDelay = Random.nextLong(500L, 4500L)
            delay(randomDelay)
            readyStartTime = System.currentTimeMillis()
            state = ReactionState.READY
        }
    }

    // Background color based on current state
    val validTimesBg = trialTimes.filterNotNull()
    val avgForBg = if (validTimesBg.isNotEmpty()) validTimesBg.average() else 0.0

    val backgroundColor = when (state) {
        ReactionState.START -> Color(0xFF4FC3C7)
        ReactionState.WAITING -> Color(0xFFBDBDBD)
        ReactionState.READY -> Color(0xFF4CAF50)
        ReactionState.TRIAL_DONE -> Color(0xFF4CAF50)
        ReactionState.FAILED -> Color(0xFFE53935)
        ReactionState.FINAL -> colorFor(avgForBg)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable {
                when (state) {
                    ReactionState.START -> {
                        trialTimes[0] = null; trialTimes[1] = null; trialTimes[2] = null
                        trialIndex = 0
                        state = ReactionState.WAITING
                    }
                    ReactionState.WAITING -> {
                        trialTimes[trialIndex] = null
                        state = ReactionState.FAILED
                    }
                    ReactionState.READY -> {
                        val reactionTime = (System.currentTimeMillis() - readyStartTime).toInt()
                        trialTimes[trialIndex] = reactionTime
                        state = ReactionState.TRIAL_DONE
                    }
                    ReactionState.TRIAL_DONE, ReactionState.FAILED -> {
                        advanceTrial(trialIndex, trialTimes) { newIndex, newState ->
                            trialIndex = newIndex
                            state = newState
                        }
                    }
                    ReactionState.FINAL -> {
                        state = ReactionState.START
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (state) {
                ReactionState.START -> {
                    Text("Reaction", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    Icon(
                        imageVector = WhiteLightningIcon,
                        contentDescription = "Lightning Icon",
                        tint = Color.White,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Test", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Click to Start", color = Color.White, fontSize = 16.sp)
                }

                ReactionState.WAITING -> {
                    Text("Get Ready", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    Icon(
                        imageVector = WhiteWarningIcon,
                        contentDescription = "Warning Icon",
                        tint = Color.White,
                        modifier = Modifier.size(110.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Wait for green light...", color = Color.White, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("DON'T CLICK YET!", color = Color.White, fontSize = 16.sp)
                }

                ReactionState.READY -> {
                    Text("GO!", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    Icon(
                        imageVector = WhiteRunnerIcon,
                        contentDescription = "Runner Icon",
                        tint = Color.White,
                        modifier = Modifier.size(110.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("CLICK NOW!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("TAP AS FAST AS YOU CAN!", color = Color.White, fontSize = 14.sp)
                }

                ReactionState.TRIAL_DONE -> {
                    Text("Trial ${trialIndex + 1} Complete!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    Icon(
                        imageVector = WhiteCheckCircleIcon,
                        contentDescription = "Check Circle Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(110.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Time: ${trialTimes[trialIndex]}ms", color = Color.White, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (trialIndex < 2) "Continue to Trial ${trialIndex + 2}" else "See Results",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TrialResultsCard(trialTimes)
                }

                ReactionState.FAILED -> {
                    Text("FAIL!", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("You clicked too early!", color = Color.White, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tap anywhere to try again", color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    TrialResultsCard(trialTimes)
                }

                ReactionState.FINAL -> {
                    val validTimes = trialTimes.filterNotNull()
                    val average = if (validTimes.isNotEmpty()) validTimes.average() else 0.0
                    val message = categoryFor(average)
                    val imageRes = imageFor(average)

                    Text(message, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = message,
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Average: ${average.toInt()}ms", color = Color.White, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tap anywhere to restart", color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    TrialResultsCard(trialTimes, showAverage = true, average = average.toInt())
                }
            }
        }
    }
}

private fun advanceTrial(
    currentIndex: Int,
    trialTimes: MutableList<Int?>,
    updateState: (Int, ReactionState) -> Unit
) {
    if (currentIndex < 2) {
        updateState(currentIndex + 1, ReactionState.WAITING)
    } else {
        updateState(currentIndex, ReactionState.FINAL)
    }
}

@Composable
private fun TrialResultsCard(trialTimes: List<Int?>, showAverage: Boolean = false, average: Int = 0) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Trial Results", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                trialTimes.forEachIndexed { index, time ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val numberColor = if (time != null) Color(0xFF4CAF50) else Color.Gray
                        Text(text = "${index + 1}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = numberColor)
                        Text(text = time?.let { "${it}ms" } ?: "-", fontSize = 12.sp, color = Color.DarkGray)
                    }
                }
            }
            if (showAverage) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Average Score", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                Text("${average}ms", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF6D00))
            }
        }
    }
}

private fun categoryFor(average: Double): String {
    return when {
        average < 180 -> "DANG YOU ARE SO FAST BRO!"
        average < 280 -> "YOUR REFLEX IS GOOD"
        average < 450 -> "MEH LIKE OTHER PERSON"
        else -> "YOU LIKE A SNAIL BRO"
    }
}

private fun imageFor(average: Double): Int {
    return when {
        average < 180 -> R.drawable._20250917_080739_2_removebg_preview
        average < 280 -> R.drawable._20250917_080744_2_removebg_preview
        average < 450 -> R.drawable._20250917_080749_2_removebg_preview
        else -> R.drawable._20250917_080754_2_removebg_preview
    }
}

private fun colorFor(average: Double): Color {
    return when {
        average < 180 -> Color(0xFF4CAF50)
        average < 280 -> Color(0xFF2196F3)
        average < 450 -> Color(0xFFFF9800)
        else -> Color(0xFFFF5722)
    }
}

@Preview(showBackground = true)
@Composable
fun ReactionTestPreview() {
    ReactionTestScreen()
}