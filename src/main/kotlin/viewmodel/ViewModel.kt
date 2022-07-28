package viewmodel

import coroutine.CloseableCoroutineScope

abstract class ViewModel {
    internal val viewModelScope = CloseableCoroutineScope()
}