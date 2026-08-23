package cab_booking.repository
import cab_booking.exception.ActiveParcelNotFoundException
import cab_booking.model.Parcel
import cab_booking.model.types.ParcelStatus

object ParcelRepo : InMemoryRepo<Parcel>() {

    override fun getKey(entity: Parcel) = entity.parcelId

    private val activeStatuses = setOf(ParcelStatus.BOOKED, ParcelStatus.PICKED_UP)

    fun findCurrentParcelOfDriver(driverId: String): Parcel =
        findParcel { it.driverId == driverId && it.parcelStatus in activeStatuses } ?: throw ActiveParcelNotFoundException()

    fun findCurrentParcelOfCustomer(customerId: String): Parcel =
        findParcel { it.customerId == customerId && it.parcelStatus in activeStatuses } ?: throw ActiveParcelNotFoundException()

    fun hasCurrentParcelOfCustomer(customerId: String): Boolean =
        storage.values.any {
            it.customerId == customerId && it.parcelStatus in activeStatuses
        }

    fun hasCurrentParcelOfDriver(driverId: String): Boolean =
        storage.values.any {
            it.driverId == driverId && it.parcelStatus in activeStatuses
        }

    private fun findParcel(predicate: (Parcel) -> Boolean) =
        storage.values.find(predicate)

}