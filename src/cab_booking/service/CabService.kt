package cab_booking.service

import cab_booking.exception.CabNotFoundException
import cab_booking.model.Cab
import cab_booking.model.types.CabType
import cab_booking.repository.CabRepo

object CabService {
    fun isRegistrationNumberExists(registrationNumber: String): Boolean =
        CabRepo.existsByRegistrationNumber(registrationNumber)

    fun createCab(model: String, cabType: CabType, registrationNumber: String) : Cab {
        val cab = Cab(model, cabType, registrationNumber)
        CabRepo.save(cab)
        return cab
    }

    fun deleteCab(cabId: String) =
        CabRepo.deleteByKey(cabId)

    fun getAllCabs(): List<Cab> =
        CabRepo.findAll()

    fun getCabsByType(cabType: CabType): List<Cab> =
        CabRepo.findByCabType(cabType)

    fun getCabById(cabId: String): Cab =
        CabRepo.findByKey(cabId) ?: throw CabNotFoundException("Cab not found for ID: ${cabId}")

}
