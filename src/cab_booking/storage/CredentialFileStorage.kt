package cab_booking.storage

import cab_booking.auth.UserCredential
import java.io.File
import java.time.LocalDateTime

object CredentialFileStorage : FileStorage<UserCredential> {

    private const val CREDENTIALS_FILE_PATH = "data/credentials.csv"

    override fun save(items: List<UserCredential>) {
        val file = File(CREDENTIALS_FILE_PATH)
        file.parentFile?.mkdirs()
        file.bufferedWriter().use { writer ->
            items.forEach { credential ->
                val lockedAtText = credential.getLockedAtTime()?.toString() ?: "NA"
                writer.write("${credential.userId},${credential.getHashedPassword()},$lockedAtText")
                writer.newLine()
            }
        }
    }

    override fun load(): List<UserCredential> {
        val file = File(CREDENTIALS_FILE_PATH)
        if (!file.exists()) return emptyList()

        return file.readLines()
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.split(",")
                val credential = UserCredential.fromStoredHash(
                    userId = parts[0],
                    passwordHash = parts[1]
                )

                val lockedAtText = parts[2]
                if (lockedAtText != "NA") {
                    credential.restoreLock(LocalDateTime.parse(lockedAtText))
                }

                credential
            }
    }
}
