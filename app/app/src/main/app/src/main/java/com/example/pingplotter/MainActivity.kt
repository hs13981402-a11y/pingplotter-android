package com.example.pingplotter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { PingScreen() } }
    }
}

fun runPing(host: String): String = try {
    val p = ProcessBuilder("ping", "-c", "4", "-W", "2", host)
        .redirectErrorStream(true).start()
    val out = p.inputStream.bufferedReader().readText()
    p.waitFor()
    out
} catch (e: Exception) {
    "Error: ${e.message}"
}

@Composable
fun PingScreen() {
    var host by remember { mutableStateOf("8.8.8.8") }
    var output by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
        OutlinedTextField(
            value = host,
            onValueChange = { host = it },
            label = { Text("Host") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(enabled = !busy, onClick = {
            busy = true
            output = "..."
            thread {
                val r = runPing(host.trim())
                output = r
                busy = false
            }
        }) { Text("Ping") }
        Spacer(Modifier.height(8.dp))
        Text(output, Modifier.verticalScroll(rememberScrollState()))
    }
}
