private fun partOne(lines : List<String>) : Int =
    lines
        .sumOf { line ->

            val suffix = line.split(": ")[1]
            val segments = suffix.split(" | ")
            val winningNumbers = segments[0].split(' ').mapNotNull { str -> str.trim().takeIf { it.isNotBlank() }?.toLong() }.toSet()
            val myNumbers = segments[1].split(' ').mapNotNull { str -> str.trim().takeIf { it.isNotBlank() }?.toLong() }
            val overlap = myNumbers.intersect(winningNumbers).size

            if (overlap == 0) {
                0
            }
            else {
                2.toBigInteger().pow(overlap - 1).toInt()
            }
        }

private fun partTwo(lines : List<String>) : Int {


    fun winningNumbersPerLine(line : String) : Int {
        val suffix = line.split(": ")[1]
        val segments = suffix.split(" | ")
        val winningNumbers = segments[0].split(' ').mapNotNull { str -> str.trim().takeIf { it.isNotBlank() }?.toLong() }.toSet()
        val myNumbers = segments[1].split(' ').mapNotNull { str -> str.trim().takeIf { it.isNotBlank() }?.toLong() }
        return myNumbers.intersect(winningNumbers).size
    }

    val amountOfCards = mutableListOf<Int>()
    repeat(lines.size) { amountOfCards.add(1) }

    for (i in 0 ..< amountOfCards.size) {

        val winNum = winningNumbersPerLine(lines[i])

        for (j in i + 1 ..< minOf(i + winNum + 1, amountOfCards.size)) {
            amountOfCards[j] += amountOfCards[i]
        }
    }

    return amountOfCards.sum()
}

private fun main() {
    aoc("04-example.txt", 13) { partOne(it) }
    aoc("04-input.txt", 25174) { partOne(it) }
    aoc("04-example.txt", 30) { partTwo(it) }
    aoc("04-input.txt", 6420979) { partTwo(it) }
}
