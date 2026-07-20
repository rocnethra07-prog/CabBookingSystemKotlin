package cab_booking.config

import cab_booking.model.Cab
import cab_booking.model.Driver
import cab_booking.model.UserAuthInfo
import cab_booking.model.types.CabType
import cab_booking.model.types.Location
import cab_booking.repository.AuthRepo
import cab_booking.repository.CabRepo
import cab_booking.repository.DriverRepo
import cab_booking.repository.UserRepo

//object instead of class so that there is no need of creating a DriverSeeder object
object DriverSeeder {

    private const val SEED_PASSWORD = "Driver@123"

    //Loads sample cabs and drivers straight into the repositories
    fun seed() {

        // ---- MINI ----
        seedDriver("Arun Kumar", "9000000001", "arunkumar@cabbooking.com", "TN01MI0001", "Maruti Alto", "TN01AB0001", CabType.MINI, Location.GUDUVANCHERY)
        seedDriver("Bala Murugan", "9000000002", "balamurugan@cabbooking.com", "TN01MI0002", "Hyundai Santro", "TN01AB0002", CabType.MINI, Location.GUINDY)
        seedDriver("Chandra Sekar", "9000000003", "chandrasekar@cabbooking.com", "TN01MI0003", "Tata Tiago", "TN01AB0003", CabType.MINI, Location.URAPPAKKAM)
        seedDriver("Dinesh Babu", "9000000004", "dineshbabu@cabbooking.com", "TN01MI0004", "Maruti WagonR", "TN01AB0004", CabType.MINI, Location.POTHERI)
        seedDriver("Elango Raja", "9000000005", "elangoraja@cabbooking.com", "TN01MI0005", "Renault Kwid", "TN01AB0005", CabType.MINI, Location.TAMBARAM)

        // ---- SEDAN ----
        seedDriver("Farook Ahmed", "9000000006", "farookahmed@cabbooking.com", "TN01SD0001", "Honda City", "TN01CD0001", CabType.SEDAN, Location.MEENAMBAKKAM)
        seedDriver("Gopal Krishnan", "9000000007", "gopalkrishnan@cabbooking.com", "TN01SD0002", "Maruti Ciaz", "TN01CD0002", CabType.SEDAN, Location.MAMBALAM)
        seedDriver("Hari Haran", "9000000008", "hariharan@cabbooking.com", "TN01SD0003", "Hyundai Verna", "TN01CD0003", CabType.SEDAN, Location.ANNANAGAR)
        seedDriver("Iniyan Selvam", "9000000009", "iniyanselvam@cabbooking.com", "TN01SD0004", "Skoda Slavia", "TN01CD0004", CabType.SEDAN, Location.TNAGAR)
        seedDriver("Jayaraman Pillai", "9000000010", "jayaramanpillai@cabbooking.com", "TN01SD0005", "Volkswagen Virtus", "TN01CD0005", CabType.SEDAN, Location.PORUR)

        // ---- SUV ----
        seedDriver("Karthik Raja", "9000000011", "karthikraja@cabbooking.com", "TN01SV0001", "Mahindra XUV700", "TN01EF0001", CabType.SUV, Location.GUDUVANCHERY)
        seedDriver("Lokesh Waran", "9000000012", "lokeshwaran@cabbooking.com", "TN01SV0002", "Toyota Innova Crysta", "TN01EF0002", CabType.SUV, Location.GUINDY)
        seedDriver("Manikandan Vel", "9000000013", "manikandanvel@cabbooking.com", "TN01SV0003", "Hyundai Alcazar", "TN01EF0003", CabType.SUV, Location.URAPPAKKAM)
        seedDriver("Naveen Chezhian", "9000000014", "naveenchezhian@cabbooking.com", "TN01SV0004", "Kia Seltos", "TN01EF0004", CabType.SUV, Location.POTHERI)
        seedDriver("Om Prakash", "9000000015", "omprakash@cabbooking.com", "TN01SV0005", "Tata Safari", "TN01EF0005", CabType.SUV, Location.TAMBARAM)
    }

    private fun seedDriver(
        name: String,
        phone: String,
        email: String,
        licenseNumber: String,
        model: String,
        registrationNumber: String,
        cabType: CabType,
        location: Location
    ) {
        if (UserRepo.existsByEmail(email)) {
            return
        }

        if(CabRepo.existsByRegistrationNumber(registrationNumber)){
            return
        }

        if(DriverRepo.existsByLicense(licenseNumber)){
            return
        }

        val cab = Cab(
            registrationNumber = registrationNumber,
            model = model,
            cabType = cabType
        )

        val driver = Driver(
            name = name,
            phone = phone,
            email = email,
            cabId = cab.cabId,
            licenseNumber = licenseNumber,
            currentLocation = location
        )

        CabRepo.save(cab)
        DriverRepo.save(driver)
        UserRepo.save(driver)
        AuthRepo.save(UserAuthInfo(driver.userId, SEED_PASSWORD))
    }
}