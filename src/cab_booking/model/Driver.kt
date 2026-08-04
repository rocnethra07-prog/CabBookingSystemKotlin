package cab_booking.model

import cab_booking.model.types.Location
import cab_booking.model.types.UserRole
import java.math.BigDecimal

class Driver(
    name: String,
    phone: String,
    email: String,
    val cabId: String,
    licenseNumber: String,
    var currentLocation: Location
) : User(
    name = name,
    phone = phone,
    email = email,
    userRole = UserRole.DRIVER
) {

    // Public properties are used for simple property access (driver.licenseNumber)
    // Private set for public var so that properties cannot be modified directly but can be read easily (driver.earnings)
    val licenseNumber: String = licenseNumber.trim().uppercase()

    var earnings: BigDecimal = BigDecimal.ZERO
        set(value){
            if(field == value) return
            require(value >= BigDecimal.ZERO){ "Earnings of driver cannot be negative" }
            field = value
        }

    var isAvailable: Boolean = true

    var totalRating: Int = 0
    var ratingCount: Int = 0

    init {
        require(cabId.isNotBlank()) { "Cab ID cannot be blank." }
        require(licenseNumber.isNotBlank()) { "Invalid license number." }
    }

    override fun toString(): String {

        return super.toString() + """
            
            Cab ID           : $cabId
            License Number   : $licenseNumber
            Current Location : $currentLocation
            Available        : $isAvailable
            Earnings         : ₹$earnings
        """.trimIndent()
    }
}