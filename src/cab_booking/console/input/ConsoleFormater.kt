package cab_booking.console.input

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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


private val DISPLAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

fun LocalDateTime.toDisplayString(): String = this.format(DISPLAY_FORMAT)
