package notification

sealed class Notification private constructor(
    open val text: String,
    val millis: kotlin.Long,
) {
    companion object {
        const val SHORT_DELAY = 2000L
        const val LONG_DELAY = 5000L
    }

    data class Short(override val text: String) : Notification(text, SHORT_DELAY)
    data class Long(override val text: String) : Notification(text, LONG_DELAY)
    data class Infinite(override val text: String) : Notification(text, 0L)
}