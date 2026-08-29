import kotlinx.coroutines.*

suspend fun networkCall(): String {
    delay(1000)
    return "raw_data"
}

fun parseData(data: String): String = "parsed_$data"

suspend fun saveToDb(data: String) {
    delay(500)
}

fun updateUI(data: String) {
    println("UI Updated with: $data")
}

suspend fun loadData() {
    val data = networkCall()
    val parsed = parseData(data)
    saveToDb(parsed)
    updateUI(parsed)
}

fun main() = runBlocking {
    loadData()
}