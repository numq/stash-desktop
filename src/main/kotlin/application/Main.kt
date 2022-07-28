package application

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import di.appModule
import files.FilesScreen
import org.koin.core.context.GlobalContext.startKoin

fun main() = Window {

    startKoin {
        modules(appModule)
    }

    val scaffoldState = rememberScaffoldState()

    MaterialTheme {
        Scaffold(scaffoldState = scaffoldState) {
            Surface(
                modifier = Modifier.fillMaxSize().padding(it),
                color = MaterialTheme.colors.background
            ) {
                FilesScreen(scaffoldState)
            }
        }
    }
}