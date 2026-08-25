package cab_booking.repository
import cab_booking.exception.ActiveParcelNotFoundException
import cab_booking.model.ParcelDelivery
import cab_booking.model.types.DispatchStatus

object ParcelDeliveryRepo : InMemoryRepo<ParcelDelivery>() {

    override fun getKey(entity: ParcelDelivery) = entity.bookingId

    private val activeStatuses = setOf(DispatchStatus.BOOKED, DispatchStatus.STARTED)

    fun findCurrentParcelDeliveryOfDriver(driverId: String): ParcelDelivery =
        findParcelDelivery { it.driverId == driverId && it.status in activeStatuses } ?: throw ActiveParcelNotFoundException()

    fun findCurrentParcelDeliveryOfCustomer(customerId: String): ParcelDelivery =
        findParcelDelivery { it.customerId == customerId && it.status in activeStatuses } ?: throw ActiveParcelNotFoundException()

    fun hasCurrentParcelDeliveryOfCustomer(customerId: String): Boolean =
        storage.values.any {
            it.customerId == customerId && it.status in activeStatuses
        }

    fun hasCurrentParcelDeliveryOfDriver(driverId: String): Boolean =
        storage.values.any {
            it.driverId == driverId && it.status in activeStatuses
        }

    private fun findParcelDelivery(predicate: (ParcelDelivery) -> Boolean) =
        storage.values.find(predicate)

}