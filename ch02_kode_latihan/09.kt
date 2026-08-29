fun main() {
    for (i in 1..5) println(i)
    for (i in 1 until 5) println(i)
    for (i in 5 downTo 1) println(i)
    for (i in 0..10 step 2) println(i)
    
    val names = listOf("Ali", "Budi", "Cici")
    for (name in names) println(name)
    names.forEachIndexed { index, name -> println("$index: $name") }

    var i = 0
    while (i <= 2) { 
        println(" while loop - Halo ${names[i]}")
        i++ 
    }

    var j = 0
    do { 
        println(" do while loop - Halo ${names[j]}")
        j++
    } while (j <= 2)
}