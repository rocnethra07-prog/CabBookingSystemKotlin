package cab_booking.router

import cab_booking.controller.*
import cab_booking.model.Driver
import cab_booking.model.User
import cab_booking.model.UserRole
import cab_booking.service.DriverService

class UserRouter(val adminController: AdminController,val driverController: DriverController, val riderController: RiderController, val driverService: DriverService) {
    fun route(user: User){
        when(user.userRole){
            UserRole.ADMIN -> adminController.adminDashboard()
            UserRole.DRIVER -> {
                val driver: Driver? = driverService.findDriverById(user.userId)
                if(driver == null){
                    throw IllegalArgumentException("Driver not found ${user.userId}")
                }
                driverController.driverDashboard(driver)
            }
            UserRole.RIDER -> riderController.riderDashboard(user)
        }
    }
}