private val amountPerColor = mapOf("red" to 12, "green" to 13, "blue" to 14)

private fun partOne(lines : List<String>) =
    lines
        .sumOf { line ->
            val list1 = line.split(": ")

            val gameId = list1[0].removePrefix("Game ").toLong()
            val rounds = list1[1].split("; ")

            rounds.forEach { round ->
                val colors = round.split(", ")
                colors.forEach { color ->
                    amountPerColor.forEach { (col, maxAmount) ->
                        val amount = color.removeSuffix(" $col")
                        if (amount != color && amount.toLong() > maxAmount) {
                            return@sumOf 0L
                        }
                    }
                }
            }

            gameId
        }

private fun partTwo(lines : List<String>) =
    lines
        .sumOf { line ->
            val list1 = line.split(": ")

            val rounds = list1[1].split("; ")

            val amountPerColor = mutableMapOf("red" to 0L, "green" to 0L, "blue" to 0L)

            rounds.forEach { round ->
                val colors = round.split(", ")
                colors.forEach { color ->
                    amountPerColor.forEach { (col, currentAmount) ->
                        val amount = color.removeSuffix(" $col")
                        if (amount != color && amount.toLong() > currentAmount) {
                            amountPerColor[col] = amount.toLong()
                        }
                    }
                }
            }

            amountPerColor.map { it.value }.fold(1L) { a, b -> a * b }
        }

private fun main() {
    aoc("02-example.txt", "8") { partOne(it) }
    aoc("02-input.txt", "2377") { partOne(it) }
    aoc("02-example.txt", "2286") { partTwo(it) }
    aoc("02-input.txt", "71220") { partTwo(it) }
}
