import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class User(val name: String, val isActive: Boolean) {
    fun toDisplayModel(): String = "User: $name"
}

class Api {
    suspend fun getLatestUser(): User = User("Andi", true)
}

class MutableStateFlow<T>(var value: T)

val api = Api()
val _uiState = MutableStateFlow<String>("")

fun getUserUpdates(): Flow<User> = flow {
    while (true) {
        val user = api.getLatestUser()
        emit(user)
        delay(5000)
    }
}

fun main() = runBlocking {
    val viewModelScope = this

    viewModelScope.launch {
        getUserUpdates()
            .filter { it.isActive }
            .map { it.toDisplayModel() }
            .collect { user ->
                _uiState.value = user
            }
    }
}