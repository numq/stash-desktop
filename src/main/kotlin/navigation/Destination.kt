package navigation

sealed class Destination private constructor() {
    object Folder : Destination()
}