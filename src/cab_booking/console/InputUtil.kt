package cab_booking.console

import cab_booking.model.types.CabType
import cab_booking.model.types.Location
import cab_booking.util.Validator

object InputUtil {
    private fun promptUntilValidInput(prompt: String, errorMessage: String, validator: (String) -> Boolean) : String{
        while (true){
            print(prompt)
            val input = readln().trim()
            if(validator(input)){
                return input
            }
            println("! $errorMessage !")
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
        promptUntilValidInput(prompt, errorMessage){ Validator.isValidEmail(it) }


    fun promptPassword(
        prompt: String = "Enter password: ",
        errorMessage: String = "Password must be at least 8 characters, with an uppercase letter, a lowercase letter, and a special character (@#$%^&+=!-_). Spaces are not allowed."
    ) =
        promptUntilValidInput(prompt, errorMessage){ Validator.isValidPassword(it) }


    fun chooseLocation(prompt: String = "Select Location: "): Location {

        while (true) {

            println(prompt)

            Location.entries.forEachIndexed { index, location ->
                println("${index + 1}. $location")
            }

            print("Choose: ")

            val choice = readln().toIntOrNull()

            if (choice != null && choice in 1..Location.entries.size) {
                return Location.entries[choice - 1]
            }

            println("! Invalid choice. Please try again.")
        }
    }

    fun chooseCabType(prompt: String = "Select Cab Type: "): CabType {

        while (true) {

            println(prompt)

            CabType.entries.forEachIndexed { index, cabType ->
                println(
                    "${index + 1}. $cabType\n" +
                            "   Base Fare : ₹${cabType.basePay}\n" +
                            "   Fare / km : ₹${cabType.perKmRate}\n"
                )
            }

            print("Choose: ")

            val choice = readln().toIntOrNull()

            if (choice != null && choice in 1..CabType.entries.size) {
                return CabType.entries[choice - 1]
            }

            println("! Invalid choice. Please try again.")
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
            if (Validator.isValidName(input)) {
                return input
            }
            println("Invalid name.")
        }
    }

    fun promptOptionalPhone(currentValue: String): String {

        while (true) {
            print("Phone [$currentValue] (Press Enter to keep the current value) : ")
            val input = readln().trim()
            if (input.isBlank()) return currentValue
            if (Validator.isValidPhone(input)) {
                return input
            }
            println("Invalid phone number.")
        }
    }
}