package cab_booking.storage

import cab_booking.model.User
import cab_booking.model.types.UserRole

// Customers and the admin. Drivers go to drivers.csv instead.
object UserFileStorage : FileStorage<User>("data/users.csv") {

    override fun toLine(item: User) =
        "${item.userId},${item.name},${item.phoneNumber},${item.email},${item.role.name}"

    override fun fromLine(parts: List<String>) = User(
        userId = parts[0],
        name = parts[1],
        phoneNumber = parts[2],
        email = parts[3],
        role = UserRole.valueOf(parts[4])
    )
}
