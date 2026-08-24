package cab_booking.storage

import cab_booking.repository.AuthRepo
import cab_booking.repository.DriverRepo
import cab_booking.repository.UserRepo
import cab_booking.repository.VehicleRepo
import cab_booking.util.IdGenerator

object DataStore {

    fun loadAll() {
        VehicleFileStorage.load().forEach { VehicleRepo.save(it) }
        DriverFileStorage.load().forEach { driver -> DriverRepo.save(driver) }
        UserFileStorage.load().forEach { UserRepo.save(it) }
        CredentialFileStorage.load().forEach { AuthRepo.save(it) }

        syncIdCounters()

        println(
            "Loaded ${UserRepo.findAll().size} users, ${DriverRepo.findAll().size} drivers, " +
                    "${VehicleRepo.findAll().size} vehicles"
        )
    }

    fun saveAll() {
        VehicleFileStorage.save(VehicleRepo.findAll())
        DriverFileStorage.save(DriverRepo.findAll())
        UserFileStorage.save(UserRepo.findAll())
        CredentialFileStorage.save(AuthRepo.findAll())

        println("Data saved.")
    }

    // Run after loading so newly created users and vehicles never
    // get an ID that is already sitting in one of the files.
    private fun syncIdCounters() {
        IdGenerator.syncUserCounter(UserRepo.findAll().map { it.userId })
        IdGenerator.syncVehicleCounter(VehicleRepo.findAll().map { it.vehicleId })
    }
}
