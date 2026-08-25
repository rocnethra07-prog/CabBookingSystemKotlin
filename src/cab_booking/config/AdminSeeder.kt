package cab_booking.config

import cab_booking.service.AuthService
import cab_booking.service.UserService

object AdminSeeder {

    const val ADMIN_EMAIL = "admin@gmail.com"
    private const val ADMIN_NAME = "System Admin"
    private const val ADMIN_PHONE = "9999999999"
    private const val ADMIN_PASSWORD = "Admin@123"

    fun seed() {
        if (!UserService.isEmailRegistered(ADMIN_EMAIL)) {
            try {
                AuthService.registerAdmin(ADMIN_NAME, ADMIN_PHONE, ADMIN_EMAIL, ADMIN_PASSWORD)
            }
            catch (_ : IllegalArgumentException){}
        }
    }
}