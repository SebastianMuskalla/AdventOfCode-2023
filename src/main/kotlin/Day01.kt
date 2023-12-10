private fun partOne(lines : List<String>) =
    lines
        .sumOf { line ->
            val firstDigit = line.first { it.isDigit() }
            val lastDigit = line.last { it.isDigit() }

            "$firstDigit$lastDigit".toLong()
        }

private val digitMapper = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

private fun partTwo(lines : List<String>) =
    lines
        .sumOf { line ->

            var firstIndex = line.indexOfFirst { it.isDigit() }.takeIf { it != -1 } ?: Int.MAX_VALUE
            var firstDigit = if (firstIndex < line.length) line[firstIndex].code - '0'.code else 0

            var lastIndex = line.indexOfLast { it.isDigit() }.takeIf { it != -1 } ?: Int.MIN_VALUE
            var lastDigit = if (lastIndex >= 0) line[lastIndex].code - '0'.code else 0

            digitMapper.forEachIndexed { digitMinusOne : Int, pattern : String ->
                val index = line.indexOf(pattern)
                if (index != -1 && index < firstIndex) {
                    firstIndex = index
                    firstDigit = digitMinusOne + 1
                }
            }

            digitMapper.forEachIndexed { digitMinusOne : Int, pattern : String ->
                val index = line.lastIndexOf(pattern)
                if (index != -1 && index > lastIndex) {
                    lastIndex = index
                    lastDigit = digitMinusOne + 1
                }
            }

            log("Line $line, firstDigit $firstDigit at $firstIndex, lastDigit $lastDigit at $lastIndex")

            firstDigit * 10L + lastDigit
        }

private fun main() {
    aoc("01-example1.txt", "142") { partOne(it) }
    aoc("01-input.txt", "54597") { partOne(it) }
    aoc("01-example2.txt", "281") { partTwo(it) }
    aoc("01-input.txt", "54504") { partTwo(it) }
}
