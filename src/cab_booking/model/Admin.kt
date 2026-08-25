package cab_booking.model

import cab_booking.util.IdGenerator

class Admin(
    name: String,
    phoneNumber: String,
    email: String,
    userId: String = IdGenerator.generateUserId()
) : User(
    name = name,
    phoneNumber = phoneNumber,
    email = email,
    userId = userId
)