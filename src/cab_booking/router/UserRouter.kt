package cab_booking.router

import cab_booking.controller.*
import cab_booking.model.User
import cab_booking.model.types.UserRole
import cab_booking.service.DriverService

class UserRouter(private val adminController: AdminController,
                 private val driverController: DriverController,
                 private val riderController: RiderController,
                 private val driverService: DriverService
) {
    fun route(user: User){
        when(user.userRole){
            UserRole.ADMIN -> adminController.adminDashboard()
            UserRole.DRIVER -> {
                val driver = driverService.findDriverById(user.userId)
                driverController.driverDashboard(driver)
            }
            UserRole.RIDER -> riderController.riderDashboard(user)
        }
    }
}