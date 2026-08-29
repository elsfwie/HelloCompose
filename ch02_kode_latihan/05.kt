fun main() {
    val name: String? = getUserName()
    val length1 = name?.length
    val length2 = name?.length ?: 0
    val length3 = name!!.length
    if (name != null) {
        println(name.length) 
    }

}	
