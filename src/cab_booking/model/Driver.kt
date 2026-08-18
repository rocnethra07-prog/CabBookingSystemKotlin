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
    val licenseNumber: String,
    currentLocation: Location
) : User(
    name = name,
    phone = phone,
    email = email,
    userRole = UserRole.DRIVER
) {

    var currentLocation: Location = currentLocation
        private set

    var earnings: BigDecimal = BigDecimal.ZERO
        private set

    var isAvailable: Boolean = true
        private set

    fun setAvailability(availability: Boolean){
        this.isAvailable = availability
    }

    var totalRating: Int = 0
        private set

    var ratingCount: Int = 0
        private set

    fun updateEarnings(earnings: BigDecimal){
        require(earnings >= BigDecimal.ZERO){ "Earnings of driver cannot be negative" }
        this.earnings = earnings
    }

    fun updateCurrentLocation(location: Location) {
        this.currentLocation = location
    }

    fun updateTotalRating(ratings: Int) {
        require(ratings >= 0){ "Total Ratings of driver cannot be negative" }
        this.totalRating = ratings
    }

    fun updateRatingCount(ratingsCount: Int) {
        require(ratingsCount >= 0){ "Ratings Count of driver cannot be negative" }
        this.ratingCount = ratingsCount
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