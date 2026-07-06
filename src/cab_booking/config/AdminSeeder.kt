package cab_booking.config

import cab_booking.builder.UserRegistrationData
import cab_booking.model.types.UserRole
import cab_booking.service.AuthService


//object instead of class because there is no need of creating a AdminSeeder object
object AdminSeeder {

   //constant values
    private const val ADMIN_EMAIL = "admin@cabbooking.com"
    private const val ADMIN_NAME = "System Admin"
    private const val ADMIN_PHONE = "9999999999"
    private const val ADMIN_PASSWORD = "Admin@123"

    fun seed(authService: AuthService) {

        if (!authService.isEmailRegistered(ADMIN_EMAIL)) {

            val adminData = UserRegistrationData(
                name = ADMIN_NAME,
                phone = ADMIN_PHONE,
                email = ADMIN_EMAIL,
                password = ADMIN_PASSWORD,
                role = UserRole.ADMIN)

            authService.registerUser(adminData)
        }
    }
}