
package com.asnr.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

private val Background = Color(0xFF101114)
private val Panel = Color(0xFF1C1E23)
private val Gold = Color(0xFFE7B95D)
private val White = Color(0xFFF5F5F5)
private val Muted = Color(0xFF9699A3)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    var stored by remember { mutableStateOf<Double?>(null) }
    var operation by remember { mutableStateOf<String?>(null) }
    var fresh by remember { mutableStateOf(false) }
    var history by remember { mutableStateOf(listOf<String>()) }
    var showHistory by remember { mutableStateOf(false) }

    fun format(value: Double): String {
        if (!value.isFinite()) return "Error"
        return DecimalFormat("#,##0.##########").format(value)
    }

    fun calculate() {
        val a = stored ?: return
        val b = display.replace(",", "").toDoubleOrNull() ?: return
        val op = operation ?: return
        val result = when (op) {
            "+" -> a + b
            "−" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) Double.NaN else a / b
            else -> b
        }
        val answer = format(result)
        history = (listOf("$a $op $b = $answer") + history).take(20)
        display = answer
        stored = null
        operation = null
        fresh = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("ASNR", color = Gold, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("CALCULATOR", color = Muted, fontSize = 11.sp, letterSpacing = 3.sp)
            }
            TextButton(onClick = { showHistory = !showHistory }) {
                Text(if (showHistory) "CALCULATOR" else "HISTORY", color = Gold)
            }
        }

        if (showHistory) {
            Text("Calculation history", color = White, fontSize = 20.sp)
            if (history.isEmpty()) {
                Text("No calculations yet", color = Muted)
            } else {
                history.forEach {
                    Text(it, color = White, modifier = Modifier.padding(vertical = 5.dp))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        } else {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = operation?.let { " $it " } ?: " ",
                color = Gold,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Text(
                text = display,
                color = White,
                fontSize = if (display.length > 12) 32.sp else 46.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            )

            val rows = listOf(
                listOf("AC", "±", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "−"),
                listOf("1", "2", "3", "+"),
                listOf("⌫", "0", ".", "=")
            )

            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { key ->
                        val isOperator = key in listOf("÷", "×", "−", "+", "=")
                        val isSpecial = key in listOf("AC", "±", "%", "⌫")
                        val color = when {
                            isOperator -> Gold
                            isSpecial -> Panel
                            else -> Color(0xFF292B31)
                        }
                        Button(
                            onClick = {
                                when (key) {
                                    "AC" -> {
                                        display = "0"
                                        stored = null
                                        operation = null
                                        fresh = false
                                    }
                                    "⌫" -> {
                                        if (!fresh) display =
                                            display.dropLast(1).ifEmpty { "0" }
                                    }
                                    "±" -> {
                                        val value = display.replace(",", "").toDoubleOrNull()
                                        if (value != null) display = format(-value)
                                    }
                                    "%" -> {
                                        val value = display.replace(",", "").toDoubleOrNull()
                                        if (value != null) display = format(value / 100)
                                    }
                                    "+", "−", "×", "÷" -> {
                                        stored = display.replace(",", "").toDoubleOrNull()
                                        operation = key
                                        fresh = true
                                    }
                                    "=" -> calculate()
                                    "." -> {
                                        if (fresh) {
                                            display = "0."
                                            fresh = false
                                        } else if (!display.contains(".")) {
                                            display += "."
                                        }
                                    }
                                    else -> {
                                        if (fresh || display == "0" || display == "Error") {
                                            display = key
                                            fresh = false
                                        } else if (display.length < 16) {
                                            display += key
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).height(66.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = color,
                                contentColor = if (isOperator) Background else White
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                key,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Text(
            "Designed for everyday calculations",
            color = Muted,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        )
    }
}
