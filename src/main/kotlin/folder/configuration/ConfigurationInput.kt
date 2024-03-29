package folder.configuration

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
    onActiveTab: (ConfigurationTab?) -> Unit,
    configureClient: (String?) -> Unit,
    configureServer: (Int?) -> Unit,
) {

    DisposableEffect(Unit) {
        onDispose { onActiveTab(null) }
    }

    val tabs = ConfigurationTab.values()

    val (selectedTab, setSelectedTab) = remember {
        mutableStateOf(
            wasTheHost?.let {
                if (it) ConfigurationTab.SERVER else ConfigurationTab.CLIENT
            } ?: ConfigurationTab.CLIENT
        )
    }

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
        when (selectedTab) {
            ConfigurationTab.CLIENT -> configureClient(addressInput.takeIf { isValidAddress })
            ConfigurationTab.SERVER -> configureServer(portInput.takeIf { isValidPort }?.toIntOrNull())
        }
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
            TabRow(selectedTab.ordinal) {
                tabs.forEach { tab ->
                    Tab(
                        text = { Text(tab.name.lowercase().replaceFirstChar { it.uppercaseChar() }) },
                        selected = selectedTab == tab,
                        onClick = {
                            onActiveTab(tab)
                            setSelectedTab(tab)
                        })
                }
            }
            when (selectedTab) {
                ConfigurationTab.CLIENT -> {
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

                ConfigurationTab.SERVER -> {
                    TextField(
                        portInput,
                        setPortInput,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("Choose port in range: $minPort - $maxPort, default is ${SocketService.DEFAULT_PORT}") },
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