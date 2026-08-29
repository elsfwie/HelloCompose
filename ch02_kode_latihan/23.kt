import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty

class User(val name: String)

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class ViewModel {
    val uiState: StateFlow<UiState<User>> = MutableStateFlow(UiState.Loading)
}

fun <T> StateFlow<T>.collectAsStateWithLifecycle(): StateFlow<T> = this
operator fun <T> StateFlow<T>.getValue(thisRef: Any?, property: KProperty<*>): T = this.value

fun CircularProgressIndicator() {}
fun UserCard(user: User) {}
fun ErrorMessage(message: String) {}

fun main() {
    val viewModel = ViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> UserCard((uiState as UiState.Success).data)
        is UiState.Error -> ErrorMessage((uiState as UiState.Error).message)
    }
}