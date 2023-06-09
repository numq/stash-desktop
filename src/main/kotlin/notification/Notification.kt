package notification

sealed class Notification private constructor(
    open val text: String,
    val millis: kotlin.Long,
) {
    data class Short(override val text: String) : Notification(text, 2000L)
    data class Long(override val text: String) : Notification(text, 5000L)
}