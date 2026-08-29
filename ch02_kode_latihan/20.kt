import kotlinx.coroutines.*

class MutableStateFlow<T>(var value: T)
class Repository {
    suspend fun getUsers(): List<String> {
        delay(300)
        return listOf("User A", "User B")
    }
    suspend fun getPosts(): List<String> {
        delay(300)
        return listOf("Post 1", "Post 2")
    }
}
class Api {
    suspend fun fetchData(): String {
        delay(300)
        return "Data dari API"
    }
}

val repository = Repository()
val api = Api()
val _uiState = MutableStateFlow<List<String>>(emptyList())

fun updateUI(vararg args: Any) {
    println("UI Updated dengan: ${args.joinToString()}")
}

suspend fun fetchAndSave() {
    val data = withContext(Dispatchers.IO) {
        api.fetchData()
    }
    updateUI(data)
}

fun main() = runBlocking {
    val viewModelScope = this

    viewModelScope.launch {
        val users = repository.getUsers()
        _uiState.value = users
        println("UiState Value: ${_uiState.value}")
    }

    viewModelScope.launch {
        val usersDeferred = async { repository.getUsers() }
        val postsDeferred = async { repository.getPosts() }

        val users = usersDeferred.await()
        val posts = postsDeferred.await()

        updateUI(users, posts)
    }

    fetchAndSave()
}