package cab_booking.config

import cab_booking.auth.UserCredential
import cab_booking.model.User
import cab_booking.model.types.UserRole
import cab_booking.repository.AuthRepo
import cab_booking.repository.UserRepo
import cab_booking.service.AuthService

//object instead of class so that there is no need of creating a AdminSeeder object
object AdminSeeder {

    const val ADMIN_EMAIL = "admin@cabbooking.com"
    const val ADMIN_NAME = "System Admin"
    const val ADMIN_PHONE = "9999999999"
    private const val ADMIN_PASSWORD = "Admin@123"

    fun seed() {
        if (!AuthService.isEmailRegistered(ADMIN_EMAIL)) {
            AuthService.registerUser(ADMIN_NAME, ADMIN_NAME, ADMIN_EMAIL, ADMIN_PASSWORD, UserRole.ADMIN)
        }
    }
}