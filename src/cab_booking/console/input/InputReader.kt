package cab_booking.console.input

import cab_booking.exception.OperationCancelledException
import cab_booking.model.Parcel
import cab_booking.model.types.Location
import cab_booking.model.types.ParcelCategory
import cab_booking.model.types.ParcelMode
import cab_booking.model.types.VehicleCategory
import cab_booking.util.Validator
import java.math.BigDecimal

object InputReader {

    // Typing this word at any prompt cancels whatever is being filled in and
    // hands control back to the screen that started it, instead of forcing
    // the person to keep answering until it's valid.
    private const val CANCEL_KEYWORD = "cancel"

    // Every prompt reads through here so the cancel word works everywhere at once.
    private fun readLine(prompt: String): String {
        print(prompt)
        val input = readln().trim()
        if (input.equals(CANCEL_KEYWORD, ignoreCase = true)) {
            throw OperationCancelledException()
        }
        return input
    }

    private fun promptUntilValidInput(prompt: String, errorMessage: String, validator: (String) -> Boolean) : String{
        while (true) {
            val input = readLine(prompt)
            if(validator(input)){
                return input
            }
            println("[x] $errorMessage")
        }
    }

    fun promptNonEmptyInput(prompt: String, errorMessage: String) =
        promptUntilValidInput(prompt, errorMessage){ it.isNotBlank() }


    fun promptName(
        prompt: String = "Enter Name (or 'cancel' to go back): ",
        errorMessage: String = "Name must contain minimum 3 characters. Please try again"
    ) =
        promptUntilValidInput(prompt, errorMessage ) { Validator.isValidName(it) }


    fun promptPhone(
        prompt: String = "Enter Phone (or 'cancel' to go back): ",
        errorMessage: String = "Invalid Phone Number. Please enter a valid 10 digit number"
    ) =
        promptUntilValidInput(prompt, errorMessage) { Validator.isValidPhone(it) }


    fun promptEmail(
        prompt: String = "Enter email (or 'cancel' to go back): ",
        errorMessage: String = "Invalid email format. Please enter a valid email"
    ) =
        promptUntilValidInput(prompt, errorMessage){ Validator.isValidEmail(it) }.lowercase()


    fun promptPassword(
        prompt: String = "Enter password (or 'cancel' to go back): ",
        errorMessage: String = "Password must be at least 8 characters, with an uppercase letter, a lowercase letter, and a special character (@#$%^&+=!-_). Spaces are not allowed."
    ) =
        promptUntilValidInput(prompt, errorMessage){ Validator.isValidPassword(it) }


    fun promptLicenseNumber(
        prompt: String = "License Number (or 'cancel' to go back): ",
        errorMessage: String = "Invalid license number. Format: TN012023001234."
    ) =
        promptUntilValidInput(prompt, errorMessage){ Validator.isValidLicenseNumber(it) }.uppercase()

    fun promptRegistrationNumber(
        prompt: String = "Registration Number (or 'cancel' to go back): ",
        errorMessage: String =  "Invalid registration number. Format: TN01AB0001."
    ) =
        promptUntilValidInput(prompt, errorMessage) { Validator.isValidRegistrationNumber(it) }.uppercase()


    fun chooseLocation(prompt: String = "Select Location: "): Location {

        while (true) {

            println(prompt)

            Location.entries.forEachIndexed { index, location ->
                println("${index + 1}. $location")
            }

            val choice = readLine("Choose (or 'cancel' to go back): ").toIntOrNull()

            if (choice == null || choice !in 1..Location.entries.size) {
                println("[x] Invalid choice. Please try again.")
                continue
            }

            return Location.entries[choice - 1]
        }
    }


    fun promptConfirmation(prompt: String): Boolean {

        while (true) {
            when (readLine("$prompt (Y/N or 'cancel' to go back) : ").trim().uppercase()) {
                "Y", "YES" -> return true
                "N", "NO" -> return false
                else -> println("[x] Please enter Y or N.")
            }
        }
    }

    fun promptOptionalName(currentValue: String): String {

        while (true) {
            print("Name [$currentValue] (Press Enter to keep the current value,  or 'cancel' to go back) : ")
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
            print("Phone [$currentValue] (Press Enter to keep the current value,  or 'cancel' to go back) : ")
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
            println("-".repeat(50))
            VehicleCategory.entries.forEachIndexed { index, category ->
                println(
                    "  ${index + 1}. $category  " +
                            "Base ₹${category.basePrice}  +  ₹${category.perKmRate}/km"
                )
            }
            println("-".repeat(50))

            val choice = readLine("$prompt (or 'cancel' to go back): ").toIntOrNull()

            if (choice == null || choice !in 1..VehicleCategory.entries.size) {
                println("[x] Invalid choice. Please try again.")
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
            println("-".repeat(50))
            categories.forEachIndexed { index, category ->
                val fare = fareEstimates.getValue(category)
                println("  ${index + 1}. $category  ₹$fare")
            }
            println("-".repeat(50))

            val choice = readLine("$prompt (or 'cancel' to go back): ").toIntOrNull()

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

            when (readLine(readLine("Choose (or 'cancel' to go back): "))) {
                "1" -> return ParcelMode.SEND
                "2" -> return ParcelMode.RECEIVE
                else -> println("[x] Invalid choice. Please try again.")
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

            val choice = readLine("Choose (or 'cancel' to go back): ").toIntOrNull()

            if (choice == null || choice !in 1..ParcelCategory.entries.size) {
                println("[x] Invalid choice. Please try again.")
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
            val value = readLine(prompt).toBigDecimalOrNull()

            if (value == null || value < Parcel.MIN_WEIGHT_KG || value > Parcel.MAX_WEIGHT_KG) {
                println("[x] Invalid weight. Enter a value between ${Parcel.MIN_WEIGHT_KG} kg and ${Parcel.MAX_WEIGHT_KG} kg.")
                continue
            }
            return value
        }
    }

    fun promptRating(prompt: String = "Enter Rating (1-5), or 'cancel' to skip: "): Int {
        while (true) {
            val value = readLine(prompt).toIntOrNull()

            if (value == null || value !in 1..5) {
                println("[x] Please enter a rating between 1 and 5.")
                continue
            }
            return value
        }
    }

}