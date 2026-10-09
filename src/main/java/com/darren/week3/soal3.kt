package com.darren.week3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class GameState { INITIAL, COUNTDOWN, RUNNING, GAMEOVER }
enum class Mode { COLOR, TEXT }
enum class ColorName(val label: String, val color: Color) {
    RED("RED", Color(0xFFE53935)),
    BLUE("BLUE", Color(0xFF1E88E5)),
    GREEN("GREEN", Color(0xFF43A047)),
    ORANGE("ORANGE", Color(0xFFFB8C00)),
    PURPLE("PURPLE", Color(0xFF8E24AA))
}

private const val QUESTION_TIME_MS = 5000
private const val TICK_MS = 100

@Composable
fun ColorWordMatchingScreen() {
    var gameState by remember { mutableStateOf(GameState.INITIAL) }
    var countdownText by remember { mutableStateOf("3") }

    var mode by remember { mutableStateOf(Mode.COLOR) }
    var wordName by remember { mutableStateOf(ColorName.RED) }
    var inkColor by remember { mutableStateOf(ColorName.BLUE) }
    var leftIsInk by remember { mutableStateOf(true) }

    var score by remember { mutableStateOf(0) }
    var mistakes by remember { mutableStateOf(0) }
    var bestScore by remember { mutableStateOf(0) }

    var timeLeftMs by remember { mutableStateOf(QUESTION_TIME_MS) }
    var questionId by remember { mutableStateOf(0) }

    val gameButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFFBAC7D0),
        contentColor = Color(0xFF2C3E50)
    )
    val gameButtonShape = RoundedCornerShape(50)

    // Randomize next question
    fun newQuestion() {
        mode = Mode.values().random()
        wordName = ColorName.values().random()
        inkColor = ColorName.values().random()
        leftIsInk = listOf(true, false).random()
        timeLeftMs = QUESTION_TIME_MS
        questionId++
    }

    // Validate answer
    fun submitAnswer(picked: ColorName?) {
        val correctAnswer = if (mode == Mode.COLOR) inkColor else wordName
        if (picked == correctAnswer) {
            score++
            newQuestion()
        } else {
            mistakes++
            if (mistakes >= 3) {
                bestScore = maxOf(bestScore, score)
                gameState = GameState.GAMEOVER
            } else {
                newQuestion()
            }
        }
    }

    // Countdown before starting
    LaunchedEffect(gameState) {
        if (gameState == GameState.COUNTDOWN) {
            countdownText = "3"; delay(700)
            countdownText = "2"; delay(700)
            countdownText = "1"; delay(700)
            countdownText = "Start!"; delay(700)
            score = 0
            mistakes = 0
            newQuestion()
            gameState = GameState.RUNNING
        }
    }

    // Question timer
    LaunchedEffect(questionId, gameState) {
        if (gameState == GameState.RUNNING) {
            while (timeLeftMs > 0) {
                delay(TICK_MS.toLong())
                timeLeftMs -= TICK_MS
            }
            submitAnswer(null)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (gameState) {
                GameState.INITIAL -> {
                    Text("Welcome", fontSize = 28.sp, color = Color(0xFF2C3238))
                    Text("to", fontSize = 22.sp, color = Color(0xFF2C3238))
                    Text("Color Word Matching", fontSize = 28.sp, color = Color(0xFF2C3238))
                    Spacer(modifier = Modifier.height(36.dp))
                    Button(
                        onClick = { gameState = GameState.COUNTDOWN },
                        colors = gameButtonColors,
                        shape = gameButtonShape,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text("Start Game", fontSize = 16.sp)
                    }
                }

                GameState.COUNTDOWN -> {
                    Text(countdownText, fontSize = 36.sp, color = Color(0xFF2C3238))
                }

                GameState.RUNNING -> {
                    // Header: mode, score, mistakes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mode: ${mode.name}", fontSize = 18.sp, color = Color(0xFF2C3238))
                        Text("✅ $score   ❌ $mistakes/3", fontSize = 18.sp, color = Color(0xFF2C3238))
                    }

                    Spacer(modifier = Modifier.height(72.dp))

                    Text("${timeLeftMs / 1000} s", fontSize = 22.sp, color = Color(0xFF2C3238))

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = wordName.label,
                        fontSize = 52.sp,
                        color = inkColor.color
                    )

                    Spacer(modifier = Modifier.height(80.dp))

                    val leftValue = if (leftIsInk) inkColor else wordName
                    val rightValue = if (leftIsInk) wordName else inkColor
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Button(
                            onClick = { submitAnswer(leftValue) },
                            colors = gameButtonColors,
                            shape = gameButtonShape,
                            modifier = Modifier.widthIn(min = 110.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Text(leftValue.label, fontSize = 14.sp)
                        }
                        Button(
                            onClick = { submitAnswer(rightValue) },
                            colors = gameButtonColors,
                            shape = gameButtonShape,
                            modifier = Modifier.widthIn(min = 110.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Text(rightValue.label, fontSize = 14.sp)
                        }
                    }
                }

                GameState.GAMEOVER -> {
                    Text("Game Over!", fontSize = 32.sp, color = Color(0xFF2C3238))
                    Spacer(modifier = Modifier.height(36.dp))
                    Text("You're Score", fontSize = 18.sp, color = Color(0xFF333333))
                    Text("$score", fontSize = 28.sp, color = Color(0xFF2C3238))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Best Score", fontSize = 14.sp, color = Color(0xFF555555))
                    Text("$bestScore", fontSize = 16.sp, color = Color(0xFF2C3238))
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { gameState = GameState.COUNTDOWN },
                        colors = gameButtonColors,
                        shape = gameButtonShape,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text("Restart Game", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { gameState = GameState.INITIAL },
                        colors = gameButtonColors,
                        shape = gameButtonShape,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text("Exit", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorWordMatchingPreview() {
    ColorWordMatchingScreen()
}