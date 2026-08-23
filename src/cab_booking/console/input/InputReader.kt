package cab_booking.console.input

import cab_booking.model.Parcel
import cab_booking.model.types.Location
import cab_booking.model.types.ParcelCategory
import cab_booking.model.types.ParcelMode
import cab_booking.model.types.VehicleCategory
import cab_booking.util.Validator
import java.math.BigDecimal

object InputReader {
    private fun promptUntilValidInput(prompt: String, errorMessage: String, validator: (String) -> Boolean) : String{
        while (true) {
            print(prompt)
            val input = readln().trim()
            if(validator(input)){
                return input
            }
            println("[x] $errorMessage")
        }
    }

    fun promptNonEmptyInput(prompt: String, errorMessage: String) =
        promptUntilValidInput(prompt, errorMessage){ it.isNotBlank() }


    fun promptName(
        prompt: String = "Enter Name: ",
        errorMessage: String = "Name must contain minimum 3 characters. Please try again"
    ) =
        promptUntilValidInput(prompt, errorMessage ) { Validator.isValidName(it) }


    fun promptPhone(
        prompt: String = "Enter Phone: ",
        errorMessage: String = "Invalid Phone Number. Please enter a valid 10 digit number"
    ) =
        promptUntilValidInput(prompt, errorMessage) { Validator.isValidPhone(it) }


    fun promptEmail(
        prompt: String = "Enter email: ",
        errorMessage: String = "Invalid email format. Please enter a valid email"
    ) =
        promptUntilValidInput(prompt, errorMessage){ Validator.isValidEmail(it) }.lowercase()


    fun promptPassword(
        prompt: String = "Enter password: ",
        errorMessage: String = "Password must be at least 8 characters, with an uppercase letter, a lowercase letter, and a special character (@#$%^&+=!-_). Spaces are not allowed."
    ) =
        promptUntilValidInput(prompt, errorMessage){ Validator.isValidPassword(it) }


    fun promptLicenseNumber(
        prompt: String = "License Number : ",
        errorMessage: String = "Invalid license number. Format: TN012023001234."
    ) =
        promptUntilValidInput(prompt, errorMessage){ Validator.isValidLicenseNumber(it) }.uppercase()

    fun promptRegistrationNumber(
        prompt: String = "Registration Number : ",
        errorMessage: String =  "Invalid registration number. Format: TN01AB0001."
    ) =
        promptUntilValidInput(prompt, errorMessage) { Validator.isValidRegistrationNumber(it) }.uppercase()


    fun chooseLocation(prompt: String = "Select Location: "): Location {

        while (true) {

            println(prompt)

            Location.entries.forEachIndexed { index, location ->
                println("${index + 1}. $location")
            }

            print("Choose: ")

            val choice = readln().toIntOrNull()

            if (choice == null || choice !in 1..Location.entries.size) {
                println("! Invalid choice. Please try again.")
                continue
            }

            return Location.entries[choice - 1]
        }
    }


    fun promptConfirmation(prompt: String): Boolean {

        while (true) {

            print("$prompt (Y/N): ")

            when (readln().trim().uppercase()) {
                "Y", "YES" -> return true
                "N", "NO" -> return false
                else -> println("! Please enter Y or N.")
            }
        }
    }

    fun promptOptionalName(currentValue: String): String {

        while (true) {
            print("Name [$currentValue] (Press Enter to keep the current value) : ")
            val input = readln().trim()
            if (input.isBlank()) return currentValue
            if (!Validator.isValidName(input)) {
                println("Invalid name.")
                continue
            }
            return input
        }
    }

    fun promptOptionalPhone(currentValue: String): String {

        while (true) {
            print("Phone [$currentValue] (Press Enter to keep the current value) : ")
            val input = readln().trim()
            if (input.isBlank()) return currentValue
            if (!Validator.isValidPhone(input)) {
                println("Invalid phone number.")
                continue
            }
            return input

        }
    }

    fun chooseVehicleCategory(prompt: String = "Select Vehicle Type"): VehicleCategory {
        while (true) {
            println("\n$prompt")
            println("-".repeat(46))
            VehicleCategory.entries.forEachIndexed { index, category ->
                println(
                    "  ${index + 1}. $category  " +
                            "Base ₹${category.basePrice}  +  ₹${category.perKmRate}/km"
                )
            }
            println("-".repeat(46))
            print("Choose: ")

            val choice = readln().toIntOrNull()

            if (choice == null || choice !in 1..VehicleCategory.entries.size) {
                println("! Invalid choice. Please try again.")
                continue
            }
            return VehicleCategory.entries[choice - 1]
        }
    }

    fun chooseVehicleCategoryByFare(
        fareEstimates: Map<VehicleCategory, BigDecimal>,
        prompt: String = "Choose a ride"
    ): VehicleCategory {
        val categories = fareEstimates.keys.toList()

        while (true) {
            println("\n$prompt")
            println("-".repeat(46))
            categories.forEachIndexed { index, category ->
                val fare = fareEstimates.getValue(category)
                println("  ${index + 1}. $category  ₹$fare")
            }
            println("-".repeat(46))
            print("Choose: ")

            val choice = readln().toIntOrNull()

            if (choice == null || choice !in 1..categories.size) {
                println("[x] Invalid choice. Please try again.")
                continue
            }
            return categories[choice - 1]
        }
    }

    fun chooseParcelMode(prompt: String = "What are you doing?"): ParcelMode {
        while (true) {
            println("\n$prompt")
            println("  1. Send a parcel     - you have it now, it goes to someone else")
            println("  2. Receive a parcel  - someone else has it, it comes to you")
            print("Choose: ")

            when (readln().trim()) {
                "1" -> return ParcelMode.SEND
                "2" -> return ParcelMode.RECEIVE
                else -> println("! Invalid choice. Please try again.")
            }
        }
    }

    fun chooseParcelCategory(prompt: String = "Select Parcel Category"): ParcelCategory {
        while (true) {
            println("\n$prompt")
            ParcelCategory.entries.forEachIndexed { index, category ->
                val surcharge = if (category.handlingSurcharge > BigDecimal.ZERO) " (+₹${category.handlingSurcharge} handling)" else ""
                println("  ${index + 1}. $category$surcharge")
            }
            print("Choose: ")

            val choice = readln().toIntOrNull()

            if (choice == null || choice !in 1..ParcelCategory.entries.size) {
                println("! Invalid choice. Please try again.")
                continue
            }
            return ParcelCategory.entries[choice - 1]
        }
    }

    fun promptWeight(
        prompt: String = "Parcel Weight in kg (${Parcel.MIN_WEIGHT_KG} - ${Parcel.MAX_WEIGHT_KG}): "
    ): BigDecimal {
        while (true) {
            print(prompt)
            val value = readln().trim().toBigDecimalOrNull()

            if (value == null || value < Parcel.MIN_WEIGHT_KG || value > Parcel.MAX_WEIGHT_KG) {
                println("! Invalid weight. Enter a value between ${Parcel.MIN_WEIGHT_KG} kg and ${Parcel.MAX_WEIGHT_KG} kg.")
                continue
            }
            return value
        }
    }


}