package cab_booking.model

import cab_booking.model.types.Location
import cab_booking.model.types.UserRole
import cab_booking.util.Validator
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

    val licenseNumber: String = licenseNumber.trim().uppercase()

    var earnings: BigDecimal = BigDecimal.ZERO
        set(value){
            if(field == value) return
            require(value >= BigDecimal.ZERO){ "Earnings of driver cannot be negative" }
            field = value
        }

    var isAvailable: Boolean = true

    var totalRating: Int = 0
        set(value) {
            if(field == value) return
            require(value >= 0){ "Total Ratings of driver cannot be negative" }
            field = value
        }

    var ratingCount: Int = 0
        set(value){
            if(field == value) return
            require(value >= 0){ "Ratings Count of driver cannot be negative" }
            field = value
        }

    init {
        require(cabId.isNotBlank()) { "Cab ID cannot be blank." }
        require(Validator.isValidLicenseNumber(licenseNumber)) { "Invalid license number. Format: TN012023001234." }
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