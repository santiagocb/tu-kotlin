object Luhn {

    fun isValid(candidate: String): Boolean {

        val candidateWithoutSpaces = removeAllSpaces(candidate)

        if (candidateWithoutSpaces.length < 2) return false
        if (candidateWithoutSpaces.any { !it.isDigit() }) return false

        return candidateWithoutSpaces
            .reversed()
            .map { it - '0' }
            .mapIndexed { index, v -> if (index % 2 == 1) (v * 2).toString().map { it.toString().toInt() }.sum() else v }
            .sum() % 10 == 0
    }
}

fun removeAllSpaces(input: String): String {
    return input.replace("\\s".toRegex(), "")
}