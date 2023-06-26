package notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedNotification(notification: Notification, onClose: () -> Unit) {

    val coroutineScope = rememberCoroutineScope()

    val (isNotificationVisible, setIsNotificationVisible) = remember { mutableStateOf(false) }

    val duration = (notification.millis.coerceAtLeast(Notification.SHORT_DELAY) / 2)

    val animationDuration = (notification.millis.coerceAtLeast(Notification.SHORT_DELAY) / 2).toInt()

    val animationSpec = tween<Float>(
        durationMillis = animationDuration,
        easing = LinearEasing
    )

    val hide = suspend {
        setIsNotificationVisible(false)
        delay(duration)
        onClose()
    }

    LaunchedEffect(notification) {
        setIsNotificationVisible(true)
        delay(duration)
        when (notification) {
            is Notification.Infinite -> Unit
            else -> hide()
        }
    }

    AnimatedVisibility(
        visible = isNotificationVisible,
        enter = fadeIn(animationSpec),
        exit = fadeOut(animationSpec)
    ) {
        Card(Modifier.clickable(onClick = {
            coroutineScope.launch { hide() }
        })) {
            Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                Text(notification.text, color = Color.LightGray)
            }
        }
    }
}