package cab_booking.model

import cab_booking.exception.CabBookingException
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
    currentLocation: Location
) : User(
    name = name,
    phone = phone,
    email = email,
    userRole = UserRole.DRIVER
) {

    // Public properties are used for simple property access (driver.licenseNumber)
    // Private set for public var so that properties cannot be modified directly but can be read easily (driver.earnings)
    val licenseNumber: String = licenseNumber.trim().uppercase()

    var currentLocation: Location = currentLocation
        set(value){
            if(value == field) return
            field = value
        }

    var earnings: BigDecimal = BigDecimal.ZERO
        private set

    var isAvailable: Boolean = true
        set(value){
            if(value == field) return
            field = value
        }


    // Private fields used to calculate the driver's average rating.
    // They are kept private because they are not needed outside this class.
    // Only the calculated averageRating is exposed.
    private var totalRating: Int = 0
    private var ratingCount: Int = 0

    val averageRating: Double
        get() {
            if (ratingCount == 0){
                return 0.0
            }
            else {
                return totalRating.toDouble() / ratingCount
            }
        }

    init {
        if(cabId.isBlank()) {
            throw CabBookingException("Cab ID cannot be blank.")
        }

        if(licenseNumber.isBlank()) {
            throw CabBookingException("Invalid license number.")
        }
    }

    fun addEarnings(amount: BigDecimal) {
        if(amount <= BigDecimal.ZERO) {
            throw CabBookingException("Amount must be greater than zero.")
        }
        earnings += amount
    }

    fun addRating(rating: Int) {
        if(rating !in 1..5) {
            throw CabBookingException("Rating must be between 1 and 5.")
        }

        totalRating += rating
        ratingCount++
    }

    override fun toString(): String {
        return """
            ${super.toString()}
Cab ID           : $cabId
License Number   : $licenseNumber
Current Location : $currentLocation
Available        : $isAvailable
Earnings         : ₹$earnings
        """.trimIndent()
    }
}