fun transcribeToRna(dna: String): String {

    val transcribedRnaMap: Map<Char, Char> = mapOf(
        'G' to 'C',
        'C' to 'G',
        'T' to 'A',
        'A' to 'U')

    return dna
        .map { transcribedRnaMap[it] }
        .joinToString(separator = "")
}