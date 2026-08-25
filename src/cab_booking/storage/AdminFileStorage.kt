package cab_booking.storage

import cab_booking.model.Admin

object AdminFileStorage : FileStorage<Admin>("data/admins.csv") {

    override fun toLine(item: Admin) =
        "${item.userId},${item.name},${item.phoneNumber},${item.email}"

    override fun fromLine(parts: List<String>) = Admin(
        userId = parts[0],
        name = parts[1],
        phoneNumber = parts[2],
        email = parts[3]
    )
}
