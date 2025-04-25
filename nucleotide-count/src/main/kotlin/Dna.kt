class Dna(private val chain: String) {

    private val nucleotides = listOf('A', 'C', 'G', 'T')

    init {
        require(chain.all { nucleotides.contains(it) })
    }

    val nucleotideCounts: Map<Char, Int>
        get() {
            val nucleotidesChain = chain.map { it }
            return nucleotides.associateWith { n -> nucleotidesChain.count { it == n } }
        }
}