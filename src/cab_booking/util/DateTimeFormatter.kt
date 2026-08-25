package cab_booking.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val DISPLAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

fun LocalDateTime.toDisplayString(): String = this.format(DISPLAY_FORMAT)
