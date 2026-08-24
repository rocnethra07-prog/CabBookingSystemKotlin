package cab_booking.model

import cab_booking.model.types.Location
import cab_booking.model.types.UserRole
import cab_booking.util.IdGenerator
import cab_booking.util.Validator
import java.math.BigDecimal

class Driver(
    name: String,
    phone: String,
    email: String,
    val assignedVehicleId: String,
    val licenseNumber: String,
    currentLocation: Location,
    userId: String = IdGenerator.generateUserId()
) : User(
    name = name,
    phoneNumber = phone,
    email = email,
    role = UserRole.DRIVER,
    userId = userId
) {

    var currentLocation: Location = currentLocation
        private set

    var totalEarnings: BigDecimal = BigDecimal.ZERO
        private set

    // True whenever the driver is free to be matched to a new ride or parcel.
    // Set to false the moment either is booked, and back to true only when
    // that ride/parcel reaches a final state (completed/delivered/cancelled).
    var isAvailable: Boolean = true
        private set

    fun setAvailability(availability: Boolean){
        this.isAvailable = availability
    }

    var totalRatings: Int = 0
        private set

    var totalRatingsCount: Int = 0
        private set

    fun updateTotalEarnings(earnings: BigDecimal){
        require(earnings >= BigDecimal.ZERO){ "Earnings of driver cannot be negative" }
        this.totalEarnings = earnings
    }

    fun updateCurrentLocation(location: Location) {
        this.currentLocation = location
    }

    fun updateTotalRatings(ratings: Int) {
        require(ratings >= 0){ "Total Ratings of driver cannot be negative" }
        this.totalRatings = ratings
    }

    fun updateTotalRatingCounts(ratingsCount: Int) {
        require(ratingsCount >= 0){ "Ratings Count of driver cannot be negative" }
        this.totalRatingCounts = ratingsCount
    }

    init {
        require(assignedVehicleId.isNotBlank()) { "Vehicle ID cannot be blank." }
        require(Validator.isValidLicenseNumber(licenseNumber)) { "Invalid license number. Format: TN012023001234." }
    }

    override fun toString(): String {

        return super.toString() + """
            
            Vehicle ID       : $assignedVehicleId
            License Number   : $licenseNumber
            Current Location : $currentLocation
            Available        : $isAvailable
            Earnings         : ₹$totalEarnings
        """.trimIndent()
    }
}