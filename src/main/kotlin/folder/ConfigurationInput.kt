package folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import websocket.SocketService

@Composable
fun ConfigurationInput(
    wasTheHost: Boolean?,
    lastUsedPort: Int?,
    lastUsedAddress: String?,
    configureClient: (String?) -> Unit,
    configureServer: (Int?) -> Unit,
) {

    val tabs = arrayOf("Client", "Server")

    val (selectedTabIndex, setSelectedTabIndex) = remember { mutableStateOf(wasTheHost?.let { if (it) 1 else 0 } ?: 0) }

    val minPort = 1024
    val maxPort = 49151

    val (portInput, setPortInput) = remember { mutableStateOf(lastUsedPort?.toString() ?: "") }

    val isValidPort by remember(portInput) {
        derivedStateOf { portInput.toIntOrNull() in (minPort..maxPort) }
    }

    val (addressInput, setAddressInput) = remember { mutableStateOf(lastUsedAddress ?: "") }

    val isValidAddress by remember(addressInput) {
        derivedStateOf {
            addressInput.matches(Regex(SocketService.REGEX_PATTERN))
        }
    }

    val close = {
        when (selectedTabIndex) {
            0 -> configureClient(addressInput.takeIf { isValidAddress })
            1 -> configureServer(portInput.takeIf { isValidPort }?.toIntOrNull())
        }
    }

    DisposableEffect(Unit) {
        onDispose(close)
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            Modifier.fillMaxWidth(.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TabRow(selectedTabIndex) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        text = { Text(tab) },
                        selected = selectedTabIndex == index,
                        onClick = { setSelectedTabIndex(index) })
                }
            }
            when (selectedTabIndex) {
                0 -> {
                    TextField(
                        addressInput,
                        setAddressInput,
                        placeholder = { Text("Type or paste your address here.") },
                        isError = addressInput.isNotEmpty() && !isValidAddress,
                        trailingIcon = {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (addressInput.isEmpty()) close() else setAddressInput("") },
                                    modifier = Modifier.padding(4.dp),
                                ) {
                                    Icon(Icons.Rounded.Clear, "clear input")
                                }
                                IconButton(onClick = close, enabled = isValidAddress) {
                                    Icon(Icons.Rounded.Done, "apply input")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.background),
                        shape = CutCornerShape(0f)
                    )
                }

                1 -> {
                    TextField(
                        portInput,
                        setPortInput,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("Type server port number in range $minPort - $maxPort.") },
                        isError = portInput.isNotEmpty() && !isValidPort,
                        trailingIcon = {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (portInput.isEmpty()) close() else setPortInput("") },
                                    modifier = Modifier.padding(4.dp),
                                ) {
                                    Icon(Icons.Rounded.Clear, "clear input")
                                }
                                IconButton(onClick = close, enabled = isValidPort) {
                                    Icon(Icons.Rounded.Done, "apply input")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.background),
                        shape = CutCornerShape(0f)
                    )
                }
            }
        }
    }
}