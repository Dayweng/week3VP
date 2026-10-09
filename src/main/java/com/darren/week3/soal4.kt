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

enum class Rps(val emoji: String, val label: String) {
    ROCK("✊", "Rock"),
    PAPER("✋", "Paper"),
    SCISSOR("✌️", "Scissor")
}
enum class RpsGameState { INITIAL, PICK, REVEAL, FINISHED }

private const val BEST_OF = 5
private val WIN_TARGET = BEST_OF / 2 + 1

private fun beats(a: Rps, b: Rps): Boolean {
    return (a == Rps.ROCK && b == Rps.SCISSOR) ||
            (a == Rps.PAPER && b == Rps.ROCK) ||
            (a == Rps.SCISSOR && b == Rps.PAPER)
}

@Composable
fun RockPaperScissorsScreen() {
    var gameState by remember { mutableStateOf(RpsGameState.INITIAL) }

    var yourScore by remember { mutableStateOf(0) }
    var cpuScore by remember { mutableStateOf(0) }
    var bestScore by remember { mutableStateOf(0) }

    var playerChoice by remember { mutableStateOf<Rps?>(null) }
    var cpuChoice by remember { mutableStateOf<Rps?>(null) }
    var resultText by remember { mutableStateOf("") }

    var revealId by remember { mutableStateOf(0) }

    val gameButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFFBAC7D0),
        contentColor = Color(0xFF2C3E50)
    )
    val gameButtonShape = RoundedCornerShape(50)

    fun playRound(choice: Rps) {
        playerChoice = choice
        cpuChoice = Rps.values().random()

        resultText = when {
            playerChoice == cpuChoice -> "Draw"
            beats(playerChoice!!, cpuChoice!!) -> { yourScore++; "You Win!" }
            else -> { cpuScore++; "You Lose" }
        }

        gameState = RpsGameState.REVEAL
        revealId++
    }

    LaunchedEffect(revealId) {
        if (gameState == RpsGameState.REVEAL) {
            delay(700)
            if (maxOf(yourScore, cpuScore) >= WIN_TARGET) {
                bestScore = maxOf(bestScore, yourScore)
                gameState = RpsGameState.FINISHED
            } else {
                playerChoice = null
                cpuChoice = null
                gameState = RpsGameState.PICK
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Score Header (shown on all screens)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🧑 $yourScore - $cpuScore 🤖", fontSize = 18.sp, color = Color(0xFF2C3238))
                Text("Best of $BEST_OF", fontSize = 16.sp, color = Color(0xFF555555))
            }

            // Main Content Area
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (gameState) {
                    RpsGameState.INITIAL -> {
                        Text("Rock • Paper • Scissors", fontSize = 26.sp, color = Color(0xFF2C3238))
                        Spacer(modifier = Modifier.height(36.dp))
                        Button(
                            onClick = {
                                yourScore = 0
                                cpuScore = 0
                                gameState = RpsGameState.PICK
                            },
                            colors = gameButtonColors,
                            shape = gameButtonShape,
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 10.dp)
                        ) {
                            Text("Start", fontSize = 16.sp)
                        }
                    }

                    RpsGameState.PICK -> {
                        Text("Pick your move!", fontSize = 18.sp, color = Color(0xFF2C3238))
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("❔", fontSize = 44.sp)
                            Text("VS", fontSize = 32.sp, color = Color(0xFF2C3238))
                            Text("❔", fontSize = 44.sp)
                        }
                        Spacer(modifier = Modifier.height(48.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Rps.values().forEach { choice ->
                                Button(
                                    onClick = { playRound(choice) },
                                    colors = gameButtonColors,
                                    shape = gameButtonShape,
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Text("${choice.emoji} ${choice.label}", fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    RpsGameState.REVEAL -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(playerChoice?.emoji ?: "❔", fontSize = 44.sp)
                            Text("VS", fontSize = 32.sp, color = Color(0xFF2C3238))
                            Text(cpuChoice?.emoji ?: "❔", fontSize = 44.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(resultText, fontSize = 20.sp, color = Color(0xFF2C3238))
                    }

                    RpsGameState.FINISHED -> {
                        val matchResult = if (yourScore > cpuScore) "You Win the Match!" else "You Lose the Match"
                        Text(matchResult, fontSize = 28.sp, color = Color(0xFF2C3238))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Best Score: $bestScore", fontSize = 16.sp, color = Color(0xFF555555))
                        Spacer(modifier = Modifier.height(36.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            Button(
                                onClick = {
                                    yourScore = 0
                                    cpuScore = 0
                                    gameState = RpsGameState.PICK
                                },
                                colors = gameButtonColors,
                                shape = gameButtonShape,
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                            ) {
                                Text("Restart", fontSize = 16.sp)
                            }
                            Button(
                                onClick = { gameState = RpsGameState.INITIAL },
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
    }
}

@Preview(showBackground = true)
@Composable
fun RockPaperScissorsPreview() {
    RockPaperScissorsScreen()
}