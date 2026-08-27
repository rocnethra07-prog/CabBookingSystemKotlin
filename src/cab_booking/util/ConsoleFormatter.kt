package cab_booking.util

object ConsoleFormatter {
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

    fun showError(message: String) {
        println("-".repeat(50))
        println("[x] $message")
        println("-".repeat(50))
    }
}