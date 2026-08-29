data class User(val name: String, val age: Int, val email: String)

class Context
object Color { const val BLACK = 0 }
class TextView(context: Context) {
    var text: String = ""
    var textSize: Float = 0f
    fun setTextColor(color: Int) {}
}
object Log {
    fun d(tag: String, message: String) {
        println("[$tag]: $message")
    }
}

fun createUser(): User = User("Budi", 25, "budi@example.com")

fun main() {
    val user: User? = User("Andi", 22, "andi@example.com")
    val context = Context()

    val result = user?.let {
        "${it.name} (${it.age} tahun)"
    }
    println(result)

    val textView = TextView(context).apply {
        text = "Hello"
        textSize = 16f
        setTextColor(Color.BLACK)
    }

    val greeting = user?.run {
        "Selamat datang, $name!"
    }
    println(greeting)

    val newUser = createUser().also {
        Log.d("TAG", "User created: ${it.name}")
    }

    val message = with(newUser) {
        "Nama: $name, Email: $email"
    }
    println(message)
}