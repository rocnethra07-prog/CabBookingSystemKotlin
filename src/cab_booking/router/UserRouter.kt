//package cab_booking.router
//
//import cab_booking.controller.*
//import cab_booking.model.User
//import cab_booking.model.types.UserRole
//import cab_booking.service.DriverService
//
//object UserRouter{
//    fun route(user: User){
//        when(user.userRole){
//            UserRole.ADMIN -> AdminController.adminDashboard()
//            UserRole.DRIVER -> {
//                val driver = DriverService.findDriverById(user.userId)
//                DriverController.driverDashboard(driver)
//            }
//            UserRole.RIDER -> RiderController.riderDashboard(user)
//        }
//    }
//}