package cab_booking.console.input

object ConsoleFormater {
    fun header(title: String) {
        println()
        println("=".repeat(50))
        println(title)
        println("=".repeat(50))
    }

    fun subHeader(title: String) {
        println()
        println("------ $title ------")
    }

    fun divider() {
        println("-".repeat(50))
    }
}