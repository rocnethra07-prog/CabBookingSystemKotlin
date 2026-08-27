package cab_booking.storage

import cab_booking.model.Admin
import cab_booking.model.Customer
import cab_booking.repository.CredentialRepo
import cab_booking.repository.DriverRepo
import cab_booking.repository.ParcelDeliveryRepo
import cab_booking.repository.RideRepo
import cab_booking.repository.UserRepo
import cab_booking.repository.VehicleRepo
import cab_booking.util.IdGenerator

// Moves data between the files and the repositories.
object DataStore {

    fun loadAll() {
        VehicleFileStorage.load().forEach { VehicleRepo.save(it) }
        // A driver is also a user, and login looks users up by email,
        // so the same object goes into both repos.
        DriverFileStorage.load().forEach { driver ->
            DriverRepo.save(driver)
            UserRepo.save(driver)
        }
        CustomerFileStorage.load().forEach { UserRepo.save(it) }
        AdminFileStorage.load().forEach { UserRepo.save(it) }
        CredentialFileStorage.load().forEach { CredentialRepo.save(it) }
        RideFileStorage.load().forEach { RideRepo.save(it) }
        ParcelDeliveryFileStorage.load().forEach { ParcelDeliveryRepo.save(it) }

        syncIdCounters()

        println(
            "Loaded ${UserRepo.findAll().size} users, ${DriverRepo.findAll().size} drivers, " +
                    "${VehicleRepo.findAll().size} vehicles, ${RideRepo.findAll().size} rides, " +
                    "${ParcelDeliveryRepo.findAll().size} parcel deliveries."
        )
    }

    fun saveAll() {
        VehicleFileStorage.save(VehicleRepo.findAll())
        DriverFileStorage.save(DriverRepo.findAll())
        // Each kind of user has its own file. Drivers went to drivers.csv above.
        CustomerFileStorage.save(UserRepo.findAll().filterIsInstance<Customer>())
        AdminFileStorage.save(UserRepo.findAll().filterIsInstance<Admin>())
        CredentialFileStorage.save(CredentialRepo.findAll())
        RideFileStorage.save(RideRepo.findAll())
        ParcelDeliveryFileStorage.save(ParcelDeliveryRepo.findAll())

//        println("Data saved.")
    }

    // Move the counters past the highest saved ID so no ID is handed out twice.
    private fun syncIdCounters() {
        IdGenerator.syncUserCounter(UserRepo.findAll().map { it.userId })
        IdGenerator.syncVehicleCounter(VehicleRepo.findAll().map { it.vehicleId })

        // Rides and deliveries share one counter.
        IdGenerator.syncBookingCounter(
            RideRepo.findAll().map { it.bookingId } + ParcelDeliveryRepo.findAll().map { it.bookingId }
        )
    }
}