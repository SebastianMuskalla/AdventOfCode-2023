private fun List<List<Char>>.pretty() : String =
    this.joinToString(separator = "\n", prefix = "\n", postfix = "\n\n") { line -> line.joinToString(separator = "") }

private fun List<List<Char>>.west() : List<List<Char>> {

    val matrix = this
    return buildList {
        matrix.forEach { row ->
            val newRow = mutableListOf<Char>()

            row.indices.forEach { colIndex ->
                when (row[colIndex]) {
                    '.' -> newRow += '.'
                    '#' -> newRow += '#'
                    'O' -> {
                        var newColindex = colIndex - 1
                        while (newColindex >= 0 && newRow[newColindex] == '.') {
                            newColindex--
                        }
                        newColindex++ // undo last decrement
                        if (newColindex in 0 ..< colIndex) {
                            newRow[newColindex] = 'O' // move newColindex
                            newRow += '.' // space left behind by 'O'
                        } else {
                            // we couldn't actually move 'O'
                            newRow += 'O'
                        }
                    }
                }
            }

            add(newRow)
        }
    }
}

private fun List<List<Char>>.east() =
    this.map { it.reversed() }.west().map { it.reversed() }

private fun List<List<Char>>.rotate90degreesCounterclockwise() : List<List<Char>> {
    val matrix = this
    return buildList {
        matrix[0].indices.reversed().forEach { colIndex ->
            // columns of old matrix become rows of new matrix in reversed order
            buildList {
                matrix.indices.forEach { rowIndex ->
                    add(matrix[rowIndex][colIndex])
                }
            }
                .let { add(it) }
        }
    }
}

private fun List<List<Char>>.rotate90degreesClockwise() : List<List<Char>> {
    val matrix = this
    return buildList {
        matrix[0].indices.forEach { colIndex ->
            // columns of old matrix become rows of new matrix in correct order
            // but each column itself is reversed
            buildList {
                matrix.indices.reversed().forEach { rowIndex ->
                    add(matrix[rowIndex][colIndex])
                }
            }
                .let { add(it) }
        }
    }
}

private fun List<List<Char>>.north() =
    this.rotate90degreesCounterclockwise().west().rotate90degreesClockwise()

private fun List<List<Char>>.south() =
    this.rotate90degreesClockwise().west().rotate90degreesCounterclockwise()

private fun partOne(lines : List<String>) : Long {

    val converted = lines.map { row -> row.map { it } }

    log(converted.pretty())

    val northed = converted.north()

    log(northed.pretty())

    return northed.sumOfIndexed { rowIndex, row ->
        (northed.lastIndex + 1 - rowIndex) * row.count { it == 'O' }.toLong()
    }

}

private fun List<List<Char>>.spin() : List<List<Char>> =
    this.north().west().south().east()

private fun partTwo(lines : List<String>) : Long {

    val matrix = lines.map { row -> row.map { it } }
    val history = mutableListOf(matrix)

    while (true) {
        val nextMatrix = history.last().spin()
        if (nextMatrix in history) {
            history += nextMatrix
            break
        } else {
            history += nextMatrix
        }
    }

    val firstIndexOfLoop = history.indexOf(history.last())

    val lastIndexOfLoop = history.lastIndex

    val loopLength = lastIndexOfLoop - firstIndexOfLoop

    val loopIndex = (1_000_000_000 - firstIndexOfLoop) % loopLength

    val configuration = history[firstIndexOfLoop + loopIndex]

    log(firstIndexOfLoop)
    log(lastIndexOfLoop)
    log(loopLength)

    return configuration.sumOfIndexed { rowIndex, row ->
        (configuration.lastIndex + 1 - rowIndex) * row.count { it == 'O' }.toLong()
    }
}

private fun main() {
    aoc("14-example.txt", "136") { partOne(it) }
    aoc("14-input.txt", "108792") { partOne(it) }
    aoc("14-example.txt", "64") { partTwo(it) }
    aoc("14-input.txt", "99118") { partTwo(it) }
}
