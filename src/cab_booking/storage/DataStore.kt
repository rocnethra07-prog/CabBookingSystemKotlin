package cab_booking.storage

import cab_booking.repository.AuthRepo
import cab_booking.repository.DriverRepo
import cab_booking.repository.UserRepo
import cab_booking.repository.VehicleRepo
import cab_booking.util.IdGenerator

object DataStore {

    private val vehicleStorage = VehicleFileStorage("data/vehicles.csv")
    private val driverStorage = DriverFileStorage("data/drivers.csv")
    private val userStorage = UserFileStorage("data/users.csv")
    private val credentialStorage = CredentialFileStorage("data/credentials.csv")

    fun loadAll() {
        vehicleStorage.load().forEach { VehicleRepo.save(it) }
        driverStorage.load().forEach { driver -> DriverRepo.save(driver) }
        userStorage.load().forEach { UserRepo.save(it) }
        credentialStorage.load().forEach { AuthRepo.save(it) }

        syncIdCounters()

        println(
            "Loaded ${UserRepo.findAll().size} users, ${DriverRepo.findAll().size} drivers, " +
                    "${VehicleRepo.findAll().size} vehicles"
        )
    }

    fun saveAll() {
        vehicleStorage.save(VehicleRepo.findAll())
        driverStorage.save(DriverRepo.findAll())
        userStorage.save(UserRepo.findAll())
        credentialStorage.save(AuthRepo.findAll())

        println("Data saved.")
    }

    // Run after loading so newly created users and vehicles never
    // get an ID that is already sitting in one of the files.
    private fun syncIdCounters() {
        IdGenerator.syncUserCounter(UserRepo.findAll().map { it.userId })
        IdGenerator.syncVehicleCounter(VehicleRepo.findAll().map { it.vehicleId })
    }
}
