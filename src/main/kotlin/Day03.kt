private fun partOne(lines : List<String>) : Long {

    val numbersWithPositions =
        lines
            .flatMapIndexed { row, line ->
                line
                    .mapIndexedNotNull { col, char ->
                        (col to char).takeIf { char.isDigit() }
                    }
                    .fold(Int.MIN_VALUE to listOf<Pair<Int, String>>()) { (lastIndex, results), (index, char) ->

                        if (lastIndex == index - 1) {
                            val (oldIndex, oldEntry) = results.last()
                            val newEntry = "$oldEntry$char"
                            index to results.dropLast(1) + (oldIndex to newEntry)
                        } else {
                            index to results + (index to char.toString())
                        }
                    }
                    .second
                    .map { (startCol, number) ->
                        Triple(row, startCol, number)
                    }
            }

    val symbolPositions =
        lines
            .flatMapIndexed { row : Int, line : String ->
                line.mapIndexedNotNull { col, char ->
                    (row to col).takeUnless { char.isDigit() || char == '.' }
                }
            }

    return numbersWithPositions.sumOf { (row, col, num) ->

        if (symbolPositions.any { (sRow, sCol) ->
                // same row
                (sRow == row && sCol == col - 1)
                    || (sRow == row && sCol == col + num.length)
                    // next or previous row
                    || (
                    (sRow == row + 1 || sRow == row - 1) &&
                        (sCol in (col - 1) .. (col + num.length))
                    )
            }) {
            num.toLong()
        } else {
            0L
        }
    }
}

private fun partTwo(lines : List<String>) : Long {

    val numbersWithPositions =
        lines
            .flatMapIndexed { row, line ->
                line
                    .mapIndexedNotNull { col, char ->
                        (col to char).takeIf { char.isDigit() }
                    }
                    .fold(Int.MIN_VALUE to listOf<Pair<Int, String>>()) { (lastIndex, results), (index, char) ->

                        if (lastIndex == index - 1) {
                            val (oldIndex, oldEntry) = results.last()
                            val newEntry = "$oldEntry$char"
                            index to results.dropLast(1) + (oldIndex to newEntry)
                        } else {
                            index to results + (index to char.toString())
                        }
                    }
                    .second
                    .map { (startCol, number) ->
                        Triple(row, startCol, number)
                    }
            }

    val symbolPositions =
        lines
            .flatMapIndexed { row : Int, line : String ->
                line.mapIndexedNotNull { col, char ->
                    (row to col).takeUnless { char.isDigit() || char == '.' }
                }
            }

    return symbolPositions.sumOf { (sRow, sCol) ->

        val gearNumbers = numbersWithPositions
            .filter { (row, col, num) ->
                // same row
                (sRow == row && sCol == col - 1)
                    || (sRow == row && sCol == col + num.length)
                    // next or previous row
                    || (
                    (sRow == row + 1 || sRow == row - 1) &&
                        (sCol in (col - 1) .. (col + num.length))
                    )
            }

        if (gearNumbers.size != 2) {
            0L
        } else {
            gearNumbers[0].third.toLong() * gearNumbers[1].third.toLong()
        }
    }
}

private fun main() {
    aoc("03-example.txt", 4361L) { partOne(it) }
    aoc("03-input.txt", 528799L) { partOne(it) }
    aoc("03-example.txt", 467835L) { partTwo(it) }
    aoc("03-input.txt", 84907174L) { partTwo(it) }
}
