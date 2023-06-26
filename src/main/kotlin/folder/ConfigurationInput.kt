package folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import websocket.SocketService

@Composable
fun ConfigurationInput(configure: (String?) -> Unit) {

    val (addressInput, setAddressInput) = remember { mutableStateOf("") }

    val isValidAddress by remember(addressInput) {
        derivedStateOf {
            addressInput.matches(Regex(SocketService.REGEX_PATTERN))
        }
    }

    val close = { configure(addressInput.takeIf { isValidAddress }) }

    DisposableEffect(Unit) {
        setAddressInput("")
        onDispose(close)
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        TextField(
            addressInput,
            setAddressInput,
            placeholder = { Text("Type or paste your address here.") },
            isError = addressInput.isNotEmpty() && !isValidAddress,
            trailingIcon = {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
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
            modifier = Modifier.fillMaxWidth(.5f).background(MaterialTheme.colors.background)
        )
    }
}