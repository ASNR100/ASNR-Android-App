package com.asnr.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import kotlin.math.*

private val Black = Color(0xFF101010)
private val Panel = Color(0xFF1B1B1B)
private val Gold = Color(0xFFE6B85C)
private val White = Color(0xFFF5F2EB)
private val Muted = Color(0xFF99938A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CalculatorApp()
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    var display by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }
    var history by remember { mutableStateOf(listOf<String>()) }
    var scientific by remember { mutableStateOf(false) }
    var degrees by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf("EN") }
    var lastWasResult by remember { mutableStateOf(false) }

    val isRtl = language == "FA" || language == "AR"

    val title = when (language) {
        "FA" -> "ماشین حساب حرفه‌ای"
        "AR" -> "الآلة الحاسبة الاحترافية"
        else -> "PRO CALCULATOR"
    }

    val historyTitle = when (language) {
        "FA" -> "تاریخچه محاسبات"
        "AR" -> "سجل الحسابات"
        else -> "HISTORY"
    }

    val scientificTitle = when (language) {
        "FA" -> "علمی"
        "AR" -> "علمي"
        else -> "SCI"
    }

    val clearHistoryText = when (language) {
        "FA" -> "پاک کردن تاریخچه"
        "AR" -> "مسح السجل"
        else -> "CLEAR HISTORY"
    }

    fun reset() {
        display = "0"
        expression = ""
        lastWasResult = false
    }

    fun append(value: String) {
        if (lastWasResult && value.firstOrNull()?.isDigit() == true) {
            display = value
            expression = ""
        } else if (display == "0" && value != ".") {
            display = value
        } else {
            display += value
        }
        lastWasResult = false
    }

    fun calculate() {
        try {
            val result = evaluateExpression(display, degrees)
            val formatted = DecimalFormat("0.##########").format(result)
            expression = "$display ="
            display = formatted
            history = listOf("$expression $formatted") + history
            lastWasResult = true
        } catch (_: Exception) {
            display = "Error"
            lastWasResult = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "◉",
                    color = Gold,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    title,
                    color = White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    language = when (language) {
                        "EN" -> "FA"
                        "FA" -> "AR"
                        else -> "EN"
                    }
                }) {
                    Text(language, color = Gold, fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = { scientific = !scientific }) {
                    Text(
                        scientificTitle,
                        color = if (scientific) Black else Gold,
                        modifier = Modifier
                            .background(
                                if (scientific) Gold else Panel,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 125.dp, max = 160.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Panel)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    expression,
                    color = Muted,
                    fontSize = 16.sp,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    display,
                    color = Gold,
                    fontSize = if (display.length > 12) 30.sp else 40.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "DEG / RAD",
                color = Muted,
                fontSize = 12.sp
            )
            Switch(
                checked = degrees,
                onCheckedChange = { degrees = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Black,
                    checkedTrackColor = Gold
                )
            )
            Text(
                if (degrees) "DEG" else "RAD",
                color = Gold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (scientific) {
            val sciRows = listOf(
                listOf("sin", "cos", "tan", "√"),
                listOf("ln", "log", "x²", "1/x"),
                listOf("π", "e", "!", "abs")
            )
            sciRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { key ->
                        CalculatorKey(
                            label = key,
                            modifier = Modifier.weight(1f).height(43.dp),
                            background = Panel,
                            foreground = Gold
                        ) {
                            try {
                                val x = display.toDouble()
                                val angle = if (degrees) x * PI / 180.0 else x
                                val value = when (key) {
                                    "sin" -> sin(angle)
                                    "cos" -> cos(angle)
                                    "tan" -> tan(angle)
                                    "√" -> sqrt(x)
                                    "ln" -> ln(x)
                                    "log" -> log10(x)
                                    "x²" -> x * x
                                    "1/x" -> 1.0 / x
                                    "π" -> PI
                                    "e" -> E
                                    "!" -> {
                                        require(x >= 0 && x <= 170 && x % 1.0 == 0.0)
                                        (1..x.toInt()).fold(1.0) { a, b -> a * b }
                                    }
                                    "abs" -> abs(x)
                                    else -> x
                                }
                                expression = "$key($display)"
                                display = DecimalFormat("0.##########").format(value)
                                lastWasResult = true
                            } catch (_: Exception) {
                                display = "Error"
                                lastWasResult = true
                            }
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        val rows = listOf(
            listOf("AC", "⌫", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "−"),
            listOf("1", "2", "3", "+"),
            listOf("±", "0", ".", "=")
        )

        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                row.forEach { key ->
                    val special = key in listOf("÷", "×", "−", "+", "=")
                    val utility = key in listOf("AC", "⌫", "%", "±")
                    CalculatorKey(
                        label = key,
                        modifier = Modifier.weight(1f).height(55.dp),
                        background = when {
                            key == "=" -> Gold
                            special -> Color(0xFF3A3020)
                            utility -> Color(0xFF292929)
                            else -> Panel
                        },
                        foreground = when {
                            key == "=" -> Black
                            special || utility -> Gold
                            else -> White
                        }
                    ) {
                        when (key) {
                            "AC" -> reset()
                            "⌫" -> {
                                display = if (display.length > 1) {
                                    display.dropLast(1)
                                } else "0"
                                lastWasResult = false
                            }
                            "%" -> {
                                try {
                                    display = (display.toDouble() / 100.0).toString()
                                } catch (_: Exception) {
                                    display = "Error"
                                }
                            }
                            "±" -> {
                                try {
                                    display = (-display.toDouble()).toString()
                                } catch (_: Exception) {
                                    display = "Error"
                                }
                            }
                            "=" -> calculate()
                            else -> append(
                                when (key) {
                                    "÷" -> "/"
                                    "×" -> "*"
                                    "−" -> "-"
                                    else -> key
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(7.dp))
        }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                historyTitle,
                color = Gold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { history = emptyList() }) {
                Text(clearHistoryText, color = Muted, fontSize = 10.sp)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .heightIn(max = 75.dp)
        ) {
            items(history.take(5)) { item ->
                Text(
                    item,
                    color = Muted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        Spacer(Modifier.height(5.dp))
        Text(
            "DESIGNED WITH PRECISION",
            color = Color(0xFF625640),
            fontSize = 9.sp,
            letterSpacing = 2.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CalculatorKey(
    label: String,
    modifier: Modifier = Modifier,
    background: Color,
    foreground: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        color = background,
        contentColor = foreground,
        shadowElevation = if (label == "=") 5.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = if (label.length > 2) 14.sp else 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun evaluateExpression(input: String, degrees: Boolean): Double {
    val s = input.replace("×", "*")
        .replace("÷", "/")
        .replace("−", "-")
        .replace(" ", "")

    if (s.isEmpty()) return 0.0

    class Parser(private val text: String) {
        private var pos = 0

        fun parse(): Double {
            val value = expression()
            if (pos < text.length) error("Unexpected input")
            if (!value.isFinite()) error("Invalid result")
            return value
        }

        private fun expression(): Double {
            var value = term()
            while (pos < text.length) {
                when (text[pos]) {
                    '+' -> {
                        pos++
                        value += term()
                    }
                    '-' -> {
                        pos++
                        value -= term()
                    }
                    else -> return value
                }
            }
            return value
        }

        private fun term(): Double {
            var value = factor()
            while (pos < text.length) {
                when (text[pos]) {
                    '*' -> {
                        pos++
                        value *= factor()
                    }
                    '/' -> {
                        pos++
                        value /= factor()
                    }
                    '%' -> {
                        pos++
                        value %= factor()
                    }
                    else -> return value
                }
            }
            return value
        }

        private fun factor(): Double {
            if (pos < text.length && text[pos] == '-') {
                pos++
                return -factor()
            }
            if (pos < text.length && text[pos] == '+') {
                pos++
                return factor()
            }

            val start = pos
            while (pos < text.length &&
                (text[pos].isDigit() || text[pos] == '.')
            ) {
                pos++
            }

            if (start == pos) error("Number expected")
            return text.substring(start, pos).toDouble()
        }
    }

    return Parser(s).parse()
}