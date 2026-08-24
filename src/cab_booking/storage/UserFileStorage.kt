package cab_booking.storage

import cab_booking.model.User
import cab_booking.model.types.UserRole
import java.io.File

// Riders and the admin. Drivers are users too, but they are written to drivers.csv
// with their extra details, so DataStore keeps them out of this file.
object UserFileStorage : FileStorage<User> {

    private const val USERS_FILE_PATH= "data/users.csv"

    override fun save(items: List<User>) {
        val file = File(USERS_FILE_PATH)
        file.parentFile?.mkdirs()
        file.bufferedWriter().use { writer ->
            items.forEach { user ->
                writer.write(
                    "${user.userId},${user.name},${user.phone},${user.email},${user.userRole.name}"
                )
                writer.newLine()
            }
        }
    }

    override fun load(): List<User> {
        val file = File(USERS_FILE_PATH)
        if (!file.exists()) return emptyList()

        return file.readLines()
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.split(",")
                User(
                    name = parts[1],
                    phone = parts[2],
                    email = parts[3],
                    userRole = UserRole.valueOf(parts[4]),
                    userId = parts[0]
                )
            }
    }
}
