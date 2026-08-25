package cab_booking.storage

import cab_booking.model.Customer

// The file itself says these rows are customers, so no role column is needed.
object CustomerFileStorage : FileStorage<Customer>("data/customers.csv") {

    override fun toLine(item: Customer) =
        "${item.userId},${item.name},${item.phoneNumber},${item.email}"

    override fun fromLine(parts: List<String>) = Customer(
        userId = parts[0],
        name = parts[1],
        phoneNumber = parts[2],
        email = parts[3]
    )
}

