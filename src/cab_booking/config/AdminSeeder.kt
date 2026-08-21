package cab_booking.config

import cab_booking.model.types.UserRole
import cab_booking.service.AuthService
import cab_booking.service.UserService

//object instead of class so that there is no need of creating a AdminSeeder object
object AdminSeeder {

    const val ADMIN_EMAIL = "admin@cabbooking.com"
    private const val ADMIN_NAME = "System Admin"
    private const val ADMIN_PHONE = "9999999999"
    private const val ADMIN_PASSWORD = "Admin@123"

    fun seed() {
        if (!UserService.isEmailRegistered(ADMIN_EMAIL)) {
            try {
                AuthService.registerUser(ADMIN_NAME, ADMIN_PHONE, ADMIN_EMAIL, ADMIN_PASSWORD, UserRole.ADMIN)
            }
            catch (_ : IllegalArgumentException){}
        }
    }
}