package cab_booking.config

import cab_booking.auth.UserCredential
import cab_booking.model.Driver
import cab_booking.model.Vehicle
import cab_booking.model.types.Location
import cab_booking.model.types.VehicleCategory
import cab_booking.repository.AuthRepo
import cab_booking.repository.DriverRepo
import cab_booking.repository.UserRepo
import cab_booking.repository.VehicleRepo

// Hardcoded sample data so the app has something bookable on first run.
object DriverSeeder {

    private const val SEED_PASSWORD = "Driver@123"

    fun seed() {

        // ---- BIKE ----
        seedDriver("Arun Kumar", "9000000001", "arunkumar@cabbooking.com", "TN012023000001", "Hero Splendor", "TN01AB0001", VehicleCategory.BIKE, Location.GUDUVANCHERY)
        seedDriver("Bala Murugan", "9000000002", "balamurugan@cabbooking.com", "TN012023000002", "TVS Sport", "TN01AB0002", VehicleCategory.BIKE, Location.GUINDY)
        seedDriver("Chandra Sekar", "9000000003", "chandrasekar@cabbooking.com", "TN012023000003", "Bajaj Pulsar", "TN01AB0003", VehicleCategory.BIKE, Location.TAMBARAM)

        // ---- AUTO ----
        seedDriver("Dinesh Babu", "9000000004", "dineshbabu@cabbooking.com", "TN012023000004", "Bajaj RE Auto", "TN01BC0001", VehicleCategory.AUTO, Location.POTHERI)
        seedDriver("Elango Raja", "9000000005", "elangoraja@cabbooking.com", "TN012023000005", "TVS King Auto", "TN01BC0002", VehicleCategory.AUTO, Location.MAMBALAM)
        seedDriver("Farook Ahmed", "9000000006", "farookahmed@cabbooking.com", "TN012023000006", "Piaggio Ape Auto", "TN01BC0003", VehicleCategory.AUTO, Location.PORUR)

        // ---- MINI ----
        seedDriver("Gopal Krishnan", "9000000007", "gopalkrishnan@cabbooking.com", "TN012023000007", "Maruti Alto", "TN01CD0001", VehicleCategory.MINI, Location.URAPPAKKAM)
        seedDriver("Hari Haran", "9000000008", "hariharan@cabbooking.com", "TN012023000008", "Tata Tiago", "TN01CD0002", VehicleCategory.MINI, Location.MEENAMBAKKAM)
        seedDriver("Iniyan Selvam", "9000000009", "iniyanselvam@cabbooking.com", "TN012023000009", "Renault Kwid", "TN01CD0003", VehicleCategory.MINI, Location.ANNANAGAR)

        // ---- SEDAN ----
        seedDriver("Jayaraman Pillai", "9000000010", "jayaramanpillai@cabbooking.com", "TN012023000010", "Honda City", "TN01DE0001", VehicleCategory.SEDAN, Location.TNAGAR)
        seedDriver("Karthik Raja", "9000000011", "karthikraja@cabbooking.com", "TN012023000011", "Hyundai Verna", "TN01DE0002", VehicleCategory.SEDAN, Location.GUDUVANCHERY)
        seedDriver("Lokesh Waran", "9000000012", "lokeshwaran@cabbooking.com", "TN012023000012", "Skoda Slavia", "TN01DE0003", VehicleCategory.SEDAN, Location.GUINDY)

        // ---- SUV ----
        seedDriver("Manikandan Vel", "9000000013", "manikandanvel@cabbooking.com", "TN012023000013", "Mahindra XUV700", "TN01EF0001", VehicleCategory.SUV, Location.TAMBARAM)
        seedDriver("Naveen Chezhian", "9000000014", "naveenchezhian@cabbooking.com", "TN012023000014", "Toyota Innova Crysta", "TN01EF0002", VehicleCategory.SUV, Location.POTHERI)
        seedDriver("Om Prakash", "9000000015", "omprakash@cabbooking.com", "TN012023000015", "Kia Seltos", "TN01EF0003", VehicleCategory.SUV, Location.MAMBALAM)
    }

    private fun seedDriver(
        name: String,
        phone: String,
        email: String,
        licenseNumber: String,
        model: String,
        registrationNumber: String,
        vehicleCategory: VehicleCategory,
        location: Location
    ) {
        if (
            UserRepo.existsByEmail(email) ||
            VehicleRepo.existsByRegistrationNumber(registrationNumber) ||
            DriverRepo.existsByLicense(licenseNumber)
        ) {
            return
        }

        val vehicle = Vehicle(
            model = model,
            registrationNumber = registrationNumber,
            vehicleCategory = vehicleCategory
        )

        val driver = Driver(
            name = name,
            phone = phone,
            email = email,
            vehicleId = vehicle.vehicleId,
            licenseNumber = licenseNumber,
            currentLocation = location
        )

        try {
            VehicleRepo.save(vehicle)
            DriverRepo.save(driver)
            UserRepo.save(driver)
            AuthRepo.save(UserCredential(driver.userId, SEED_PASSWORD))
        }
        catch (_: IllegalArgumentException) {
            VehicleRepo.deleteByKey(vehicle.vehicleId)
            DriverRepo.deleteByKey(driver.userId)
            UserRepo.deleteByEmail(driver.email)
        }
    }
}