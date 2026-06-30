package cab_booking.model

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

    val licenseNumber: String = licenseNumber.trim().uppercase()

    var currentLocation: Location = currentLocation
        private set

    var earnings: BigDecimal = BigDecimal.ZERO
        private set

    var isAvailable: Boolean = true
        private set

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
        require(cabId.isNotBlank()) {
            "Cab ID cannot be blank."
        }

        require(licenseNumber.isNotBlank()) {
            "Invalid license number."
        }
    }

    fun updateLocation(location: Location) {
        if(currentLocation == location) return
        currentLocation = location
    }

    fun markAvailable() {
        isAvailable = true
    }

    fun markUnavailable() {
        isAvailable = false
    }

    fun addEarnings(amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) {
            "Amount must be greater than zero."
        }

        earnings += amount
    }

    fun addRating(rating: Int) {
        require(rating in 1..5) {
            "Rating must be between 1 and 5."
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