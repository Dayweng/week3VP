package com.darren.week3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun CatClickerScreen() {
    //palyer coins
    var coins by remember { mutableStateOf(0) }

    var valuePerTap by remember { mutableStateOf(1.0) }

    var upgradeCost by remember { mutableStateOf(10) }

    var mouthOpen by remember { mutableStateOf(false) }

    //for cat open mouth
    var tapToken by remember { mutableStateOf(0) }

    LaunchedEffect(tapToken) {
        if (tapToken > 0) {
            kotlinx.coroutines.delay(150)
            mouthOpen = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.a273765cdfe8bd1bdb0b028c71bf5aae),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            //coin bar
            Card(modifier = Modifier.padding(bottom = 24.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your Coins", fontSize = 14.sp)
                    Text(text = "$coins", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Text("${formatValue(valuePerTap)} coins per tap", fontSize = 12.sp)
                }
            }

            Text("Tap the Cat!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            //cat button
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        coins += floor(valuePerTap).roundToInt()
                        mouthOpen = true
                        tapToken++
                    },
                contentAlignment = Alignment.Center
            ) {
                val catImageRes = if (mouthOpen) {
                    R.drawable._7926b940e8de3b11b3058d43872e4a81_3 // open mouth cat
                } else {
                    R.drawable._7926b940e8de3b11b3058d43872e4a81_2 // closed mouth cat
                }
                Image(
                    painter = painterResource(id = catImageRes),
                    contentDescription = "Cat",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            //bottom card
            val nextValue = valuePerTap * 1.5
            val valueIncrease = nextValue - valuePerTap
            val canAfford = coins >= upgradeCost

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Give Me Your Coin", fontWeight = FontWeight.Bold)
                    Text("Next upgrade: +${formatValue(valueIncrease)} coins per tap", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            coins -= upgradeCost
                            valuePerTap = nextValue
                            upgradeCost *= 2
                        },
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) Color(0xFF4CAF50) else Color.LightGray
                        )
                    ) {
                        Text(
                            if (canAfford) "Pay for $upgradeCost coins"
                            else "Find ${upgradeCost - coins} more coins"
                        )
                    }
                }
            }
        }
    }
}

//int or float formatter
private fun formatValue(value: Double): String {
    return if (value == floor(value)) value.toInt().toString() else "%.1f".format(value)
}

@Preview(showBackground = true)
@Composable
fun CatClickerPreview() {
    CatClickerScreen()
}