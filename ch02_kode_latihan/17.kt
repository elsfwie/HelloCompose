fun doOperation(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}

fun main() {
    val sum = doOperation(5, 3) { x, y -> x + y }
    println(sum) 

    val product = doOperation(5, 3) { x, y -> x * y }
    println(product) 

    val multiply: (Int, Int) -> Int = { x, y -> x * y }
    val greet: (String) -> String = { name -> "Hello, $name!" }
    val double: (Int) -> Int = { it * 2 }
}