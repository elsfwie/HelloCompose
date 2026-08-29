import java.util.Locale
//agar tidak error
open class View {
    var visibility: Int = VISIBLE

    companion object {
        const val VISIBLE = 0
        const val GONE = 8
    }
}

class Context

object Toast {
    const val LENGTH_SHORT = 0
    fun makeText(context: Context, message: String, duration: Int): Toast = Toast
    fun show() {
        println("[Toast]: Pesan berhasil ditampilkan!")
    }
}

fun String.titleCase(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }

fun Int.toRupiah(): String {
    val localeID = Locale("id", "ID")
    return "Rp ${String.format(localeID, "%,d", this)}"
}

fun View.show() { 
    this.visibility = View.VISIBLE 
}

fun View.hide() { 
    this.visibility = View.GONE 
}

fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun main() {
    println("hello world".titleCase()) // Output: Hello World
    println(50000.toRupiah())          // Output: Rp 50.000

    val myButton = View()
    myButton.hide()
    println("Status Visibility Button: ${myButton.visibility}")
    
    myButton.show()
    println("Status Visibility Button: ${myButton.visibility}")

    val context = Context()
    context.toast("Halo, extension toast berhasil!")
}