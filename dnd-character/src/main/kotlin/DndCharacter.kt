class DndCharacter {

    val strength: Int = ability()
    val dexterity: Int = ability()
    val constitution: Int = ability()
    val intelligence: Int = ability()
    val wisdom: Int = ability()
    val charisma: Int = ability()
    val hitpoints: Int = 10 + modifier(constitution)

    companion object {

        fun ability(): Int {
            val results = (1 .. 4).map { rollDice() }
            return results.sorted().dropLast(1).sum()
        }

        fun modifier(score: Int): Int {
            return (score - 10).floorDiv(2)
        }

        fun rollDice(): Int = (1..6).random()

    }

}