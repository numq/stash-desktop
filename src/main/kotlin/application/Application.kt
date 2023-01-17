package application

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import di.appModule
import navigation.Navigation
import org.koin.core.context.GlobalContext.startKoin

fun main() {
    startKoin {
        modules(appModule)
    }
    application {
        val closeWindow = ::exitApplication
        Window(undecorated = true, onCloseRequest = closeWindow) {
            MaterialTheme(colors = darkColors()) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    WindowDraggableArea {
                        TopAppBar {
                            Row(
                                Modifier.fillMaxWidth().padding(4.dp).padding(start = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Stash")
                                IconButton(onClick = {
                                    closeWindow()
                                }) {
                                    Icon(Icons.Rounded.Close, "close app")
                                }
                            }
                        }
                    }
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        Navigation()
                    }
                }
            }
        }
    }
}