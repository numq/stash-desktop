package notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AnimatedNotification(notification: Notification, onFinish: () -> Unit) {
    val (isNotificationVisible, setIsNotificationVisible) = remember { mutableStateOf(false) }
    val duration = notification.millis / 2
    val animationDuration = (notification.millis / 2).toInt()
    val animationSpec = tween<Float>(
        durationMillis = animationDuration,
        easing = LinearEasing
    )
    LaunchedEffect(notification) {
        setIsNotificationVisible(true)
        delay(duration)
        setIsNotificationVisible(false)
        delay(duration)
        onFinish()
    }
    AnimatedVisibility(
        visible = isNotificationVisible,
        enter = fadeIn(animationSpec),
        exit = fadeOut(animationSpec)
    ) {
        Card {
            Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                Text(notification.text, color = Color.LightGray)
            }
        }
    }
}