data class User(val name: String)

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
//declare functions agar tidak error (akan dipanggil di handleState())
fun showLoadingSpinner() = println("Loading...")
fun showUser(user: User) = println("User: ${user.name}")
fun showError(message: String) = println("Error: $message")

fun handleState(state: UiState<User>) {
    when (state) {
        is UiState.Loading -> showLoadingSpinner()
        is UiState.Success -> showUser(state.data)
        is UiState.Error -> showError(state.message) 
    }
}

fun main() {
    val state1: UiState<User> = UiState.Loading
    val state2: UiState<User> = UiState.Success(User("Budi"))
    val state3: UiState<User> = UiState.Error("Koneksi gagal")

    handleState(state1) 
    handleState(state2)
    handleState(state3) 
}