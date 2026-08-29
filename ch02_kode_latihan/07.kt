fun main() {
    // dummy initialization untuk pembuktian if expression
    val a = 10
    val b = 20
    val score = 85

    val max = if (a > b) a else b

    val grade = if (score >= 90) "A"
        else if (score >= 80) "B"
        else if (score >= 70) "C"
        else "D"

    println("Nilai max: $max")      
    println("Grade: $grade")         //untuk pembuktian error code teratasi
}