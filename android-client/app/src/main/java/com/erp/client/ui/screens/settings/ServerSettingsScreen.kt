package com.erp.client.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.erp.client.data.local.ServerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Lets the user point the app at the machine running the backend. Exposed so a
 * changed LAN address is a 10-second fix on the device rather than a rebuild.
 */
@Composable
fun ServerSettingsScreen(
    serverConfig: ServerConfig,
    onSaved: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    var address by remember { mutableStateOf(serverConfig.baseUrl()) }
    var status by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Server address", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Enter the IP address of the computer running the ERPX backend. " +
                "Find it on that machine with 'ipconfig' (Windows) or 'ifconfig' (Mac/Linux). " +
                "Both devices must be on the same Wi-Fi network.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = address,
            onValueChange = {
                address = it
                status = null
            },
            label = { Text("e.g. 192.168.1.5:8080") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )

        if (isChecking) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        status?.let {
            Text(
                text = it,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = {
                if (!ServerConfig.isPlausible(address)) {
                    isError = true
                    status = "That does not look like a valid address."
                    return@Button
                }
                isChecking = true
                status = null
                scope.launch {
                    val reachable = probe(ServerConfig.normalise(address))
                    isChecking = false
                    if (reachable) {
                        serverConfig.saveBaseUrl(address)
                        isError = false
                        status = "Connected. Saved."
                        onSaved()
                    } else {
                        isError = true
                        status = "Could not reach the server. Check the IP, that the " +
                            "backend is running, and that both devices are on the same Wi-Fi."
                    }
                }
            },
            enabled = !isChecking,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Test and save")
        }

        TextButton(
            onClick = {
                if (ServerConfig.isPlausible(address)) {
                    serverConfig.saveBaseUrl(address)
                    onSaved()
                } else {
                    isError = true
                    status = "That does not look like a valid address."
                }
            },
            enabled = !isChecking
        ) {
            Text("Save without testing")
        }

        onNavigateBack?.let {
            TextButton(onClick = it, enabled = !isChecking) {
                Text("Back")
            }
        }
    }
}

/**
 * Cheap reachability check against the backend's auth endpoint. Any HTTP status
 * proves something is listening and routable, which is what we need to confirm;
 * only a transport-level failure counts as unreachable.
 */
private suspend fun probe(baseUrl: String): Boolean = withContext(Dispatchers.IO) {
    runCatching {
        val connection = (URL("${baseUrl}api/auth/login").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 4000
            readTimeout = 4000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }
        connection.outputStream.use { it.write("{}".toByteArray()) }
        val code = connection.responseCode
        connection.disconnect()
        code > 0
    }.getOrDefault(false)
}
