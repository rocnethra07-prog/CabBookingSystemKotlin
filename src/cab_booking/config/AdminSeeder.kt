package cab_booking.config

import cab_booking.builder.UserRegistrationData
import cab_booking.model.UserRole
import cab_booking.service.AuthService

object AdminSeeder {

    private const val ADMIN_EMAIL = "admin@cabbooking.com"
    private const val ADMIN_NAME = "System Admin"
    private const val ADMIN_PHONE = "9999999999"
    private const val ADMIN_PASSWORD = "Admin@123"

    fun seed(authService: AuthService) {

        if (!authService.isEmailRegistered(ADMIN_EMAIL)) {

            val adminData = UserRegistrationData.Builder()
                .name(ADMIN_NAME)
                .phone(ADMIN_PHONE)
                .email(ADMIN_EMAIL)
                .password(ADMIN_PASSWORD)
                .role(UserRole.ADMIN)
                .build()

            authService.registerUser(adminData)
        }
    }
}