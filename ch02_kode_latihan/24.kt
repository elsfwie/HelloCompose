import kotlinx.coroutines.*
import kotlin.reflect.KProperty

class Bundle
open class ComponentActivity {
    open fun onCreate(savedInstanceState: Bundle?) {}
    fun setContent(content: () -> Unit) { content() }
}

annotation class Composable

class MutableState<T>(var value: T)
operator fun <T> MutableState<T>.getValue(thisRef: Any?, property: KProperty<*>): T = this.value
operator fun <T> MutableState<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) { this.value = value }

fun <T> remember(calculation: () -> T): T = calculation()
fun mutableFloatStateOf(value: Float) = MutableState(value)
fun <T> mutableStateOf(value: T) = MutableState(value)
fun rememberCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.Default)

object Modifier {
    fun fillMaxSize() = this
    fun padding(dp: Any) = this
    fun height(dp: Any) = this
    fun fillMaxWidth() = this
}

val Int.dp: Int get() = this

object Arrangement { val Center = Any() }
object Alignment { val CenterHorizontally = Any() }

object MaterialTheme {
    object typography {
        val headlineSmall = Any()
    }
}

fun Column(modifier: Any = Modifier, verticalArrangement: Any = Any(), horizontalAlignment: Any = Any(), content: () -> Unit) { content() }
fun Text(text: String, style: Any = Any()) {}
fun Spacer(modifier: Any) {}
fun LinearProgressIndicator(progress: () -> Float, modifier: Any) {}
fun Button(onClick: () -> Unit, enabled: Boolean = true, content: () -> Unit) { onClick(); content() }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DownloaderScreen()
        }
    }
}

@Composable
fun DownloaderScreen() {
    var progress by remember { mutableFloatStateOf(0f) }
    var isDownloading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isDownloading) "Mengunduh..." else "Siap",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("${(progress * 100).toInt()}%")
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                scope.launch {
                    isDownloading = true
                    progress = 0f
                    repeat(100) {
                        delay(50)
                        progress += 0.01f
                    }
                    isDownloading = false
                }
            },
            enabled = !isDownloading
        ) {
            Text("Mulai Download")
        }
    }
}

fun main() {
    val activity = MainActivity()
    activity.onCreate(null)
}