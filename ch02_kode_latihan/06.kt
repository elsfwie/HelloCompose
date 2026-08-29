fun greet(name: String = "Dunia"): String = "Hello, $name!"

fun logMessage(message: String) {
    println(message)
}
//print untuk bukti perbaikan kode
fun main(){
    println(greet(name = "Budi"))
    logMessage(message = "hello")
}