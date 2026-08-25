package cab_booking.storage

import cab_booking.auth.UserCredential
import java.time.LocalDateTime

// Only the hash is written, never the password.
object CredentialFileStorage : FileStorage<UserCredential>("data/credentials.csv") {

    private const val NOT_LOCKED = "NA"

    override fun toLine(item: UserCredential) =
        "${item.userId},${item.passwordHash},${item.lockedAt?.toString() ?: NOT_LOCKED}"

    override fun fromLine(parts: List<String>): UserCredential {
        val credential = UserCredential.fromStoredHash(
            userId = parts[0],
            passwordHash = parts[1]
        )

        if (parts[2] != NOT_LOCKED) {
            credential.restoreLock(LocalDateTime.parse(parts[2]))
        }

        return credential
    }
}
