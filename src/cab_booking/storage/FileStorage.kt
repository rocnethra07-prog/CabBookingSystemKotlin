package cab_booking.storage

import java.io.File

// One entity per line, fields split by commas.
// A subclass only says how one object becomes a line and back again.
abstract class FileStorage<T>(private val filePath: String) {

    protected abstract fun toLine(item: T): String

    protected abstract fun fromLine(parts: List<String>): T

    fun save(items: List<T>) {
        val file = File(filePath)
        file.parentFile?.mkdirs()

        file.bufferedWriter().use { writer ->
            items.forEach { item ->
                writer.write(toLine(item))
                writer.newLine()
            }
        }
    }

    fun load(): List<T> {
        val file = File(filePath)
        if (!file.exists()) return emptyList()

        return file.readLines()
            .filter { it.isNotBlank() }
            .map { line -> fromLine(line.split(",")) }
    }
}
