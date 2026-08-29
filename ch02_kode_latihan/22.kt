import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class User(val name: String)

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class Repository {
    suspend fun getUser(id: Int): User = User("Andi")
}

open class ViewModel {
    val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
}

val repository = Repository()

class UserViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val uiState: StateFlow<UiState<User>> = _uiState.asStateFlow()

    fun loadUser(id: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val user = repository.getUser(id)
                _uiState.value = UiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

fun main() {
    val viewModel = UserViewModel()
    viewModel.loadUser(1)
}