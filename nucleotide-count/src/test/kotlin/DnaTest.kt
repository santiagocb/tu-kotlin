import org.junit.Test
import kotlin.test.assertEquals

class DnaTest {

    @Test
    fun emptyDnaStringHasNoNucleotides() {
        val dna = Dna("")
        val expected = mapOf('A' to 0, 'C' to 0, 'G' to 0, 'T' to 0)

        assertEquals(expected, dna.nucleotideCounts)
    }

    @Test
    fun canCountOneNucleotideInSingleCharacterInput() {
        val dna = Dna("G")
        val expected = mapOf('A' to 0, 'C' to 0, 'G' to 1, 'T' to 0)

        assertEquals(expected, dna.nucleotideCounts)
    }

    @Test
    fun canCountRepeatedNucleotide() {
        val dna = Dna("GGGGGGG")
        val expected = mapOf('A' to 0, 'C' to 0, 'G' to 7, 'T' to 0)

        assertEquals(expected, dna.nucleotideCounts)
    }

    @Test
    fun canCountMultipleNucleotides() {
        val dna = Dna("AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC")
        val expected = mapOf('A' to 20, 'C' to 12, 'G' to 17, 'T' to 21)

        assertEquals(expected, dna.nucleotideCounts)
    }

    @Test(expected = IllegalArgumentException::class)
    fun validatesNucleotides() {
        Dna("AGXXACT")
    }

}