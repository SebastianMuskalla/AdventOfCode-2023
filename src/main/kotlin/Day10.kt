private const val horizontal = '-'
private const val vertical = '|'
private const val northeast = 'L'
private const val northwest = 'J'
private const val southwest = '7'
private const val southeast = 'F'

private val Char.connectsNorth : Boolean
    get() = this in listOf(vertical, northeast, northwest)

private val Char.connectsSouth : Boolean
    get() = this in listOf(vertical, southeast, southwest)

private val Char.connectsWest : Boolean
    get() = this in listOf(horizontal, southwest, northwest)

private val Char.connectsEast : Boolean
    get() = this in listOf(horizontal, northeast, southeast)

private val Char.pretty : Char
    get() = when (this) {
        horizontal -> '\u2500'
        vertical -> '\u2502'
        northeast -> '\u2570' // '\u2514'
        northwest -> '\u256F' // '\u2518'
        southwest -> '\u256E' // '\u2510'
        southeast -> '\u256D' //'\u250C'
        '.' -> ' '
        'I' -> '\u2588'
        'O' -> '\u2591'
        else -> this
    }

private fun List<List<Char>>.pretty() =
    this.joinToString(separator = "\n", prefix = "\n", postfix = "\n\n") { row -> row.map { it.pretty }.joinToString(separator = "") }

private fun partOne(lines : List<String>) : Int {

    var found = false
    var rowS = -1
    var colS = -1
    lines.forEachIndexed { rowIndex, row ->
        if (!found) {
            val colIndex = row.indexOf('S')
            if (colIndex != -1) {
                rowS = rowIndex
                colS = colIndex
                found = true
            }
        }
    }

    val connectedNorth = rowS != 0 &&
        lines[rowS - 1][colS].connectsSouth

    val connectedSouth = rowS != lines.lastIndex &&
        lines[rowS + 1][colS].connectsNorth

    val connectedWest = colS != 0 &&
        lines[rowS][colS - 1].connectsEast

    val connectedEast = colS != lines[rowS].lastIndex &&
        lines[rowS][colS + 1].connectsWest

    val replacement = when {
        connectedNorth && connectedSouth -> vertical
        connectedNorth && connectedEast -> northeast
        connectedNorth && connectedWest -> northwest
        connectedEast && connectedWest -> horizontal
        connectedSouth && connectedEast -> southeast
        connectedSouth && connectedWest -> southwest
        else -> error("Impossible combination")
    }

    val rows = lines.mapIndexed { rowIndex, row -> row.takeIf { rowIndex != rowS } ?: row.map { if (it != 'S') it else replacement }.joinToString(separator = "") }

    log(lines.joinToString(separator = "\n", prefix = "\n", postfix = "\n\n") { row -> row.map { it.pretty }.joinToString(separator = "") })
    log(rows.joinToString(separator = "\n", prefix = "\n", postfix = "\n\n") { row -> row.map { it.pretty }.joinToString(separator = "") })

    var row = rowS
    var col = colS
    var previous : Pair<Int, Int>? = null
    var i = 1
    do {
        i++
//        log("Move $i, current position is $row $col")
        when {
            rows[row][col].connectsNorth && (row - 1 to col) != previous -> {
                previous = row to col
                row -= 1
            }

            rows[row][col].connectsSouth && (row + 1 to col) != previous -> {
                previous = row to col
                row += 1
            }

            rows[row][col].connectsEast && (row to col + 1) != previous -> {
                previous = row to col
                col += 1
            }

            rows[row][col].connectsWest && (row to col - 1) != previous -> {
                previous = row to col
                col -= 1
            }
        }
    } while (row != rowS || col != colS)

    val solution = i / 2
    log("Length of loop $i, middle is $solution")

    return solution
}

/**
 * After trying to implement this in a clever way for an hour, I gave up and implemented a stupid version.
 */
private fun partTwo(lines : List<String>) : Long {

    var found = false
    var rowS = -1
    var colS = -1
    lines.forEachIndexed { rowIndex, row ->
        if (!found) {
            val colIndex = row.indexOf('S')
            if (colIndex != -1) {
                rowS = rowIndex
                colS = colIndex
                found = true
            }
        }
    }

    val connectedNorth = rowS != 0 &&
        lines[rowS - 1][colS].connectsSouth

    val connectedSouth = rowS != lines.lastIndex &&
        lines[rowS + 1][colS].connectsNorth

    val connectedWest = colS != 0 &&
        lines[rowS][colS - 1].connectsEast

    val connectedEast = colS != lines[rowS].lastIndex &&
        lines[rowS][colS + 1].connectsWest

    val replacement = when {
        connectedNorth && connectedSouth -> vertical
        connectedNorth && connectedEast -> northeast
        connectedNorth && connectedWest -> northwest
        connectedEast && connectedWest -> horizontal
        connectedSouth && connectedEast -> southeast
        connectedSouth && connectedWest -> southwest
        else -> error("Impossible combination")
    }

    val rows = lines.mapIndexed { rowIndex, row -> row.takeIf { rowIndex != rowS } ?: row.map { if (it != 'S') it else replacement }.joinToString(separator = "") }

    log(lines.joinToString(separator = "\n", prefix = "\n", postfix = "\n\n") { row -> row.map { it.pretty }.joinToString(separator = "") })
    log(rows.joinToString(separator = "\n", prefix = "\n", postfix = "\n\n") { row -> row.map { it.pretty }.joinToString(separator = "") })

    val loop = mutableListOf(rowS to colS)

    var row = rowS
    var col = colS
    var previous : Pair<Int, Int>? = null
    var i = 1
    do {
        i++
//        log("Move $i, current position is $row $col")
        when {
            rows[row][col].connectsNorth && (row - 1 to col) != previous -> {
                previous = row to col
                row -= 1
            }

            rows[row][col].connectsSouth && (row + 1 to col) != previous -> {
                previous = row to col
                row += 1
            }

            rows[row][col].connectsEast && (row to col + 1) != previous -> {
                previous = row to col
                col += 1
            }

            rows[row][col].connectsWest && (row to col - 1) != previous -> {
                previous = row to col
                col -= 1
            }
        }
        loop += row to col
    } while (row != rowS || col != colS)

    /**
     * Idea: Use a flood fill (good old paint bucket from MS paint)
     * - Pixels on the perimeter that are not part of the loop are definitely outside of the loop
     *   -> Set them to 'O'
     * - Apply flood fill to these pixels (i.e. set their neighbours to 'O', then their neighbours ... until we hit the loop)
     *
     * Problem: Loops may have no gap that allows paint to come through (see second version of first example)
     *
     * Solution: Scale up the whole grid by a factor of two,
     * insert '-' and '|' to make sure that the loop is connected.
     *
     * When counting the non-painted pixels, disregard all pixels where a coordinate is odd
     * (these are the ones that were inserted due to the upscaling)
     */

    val loopUpscaled = mutableListOf<MutableList<Char>>()
    repeat(rows.size * 2) {
        loopUpscaled.add(mutableListOf<Char>().apply { repeat(rows[0].length * 2) { add('.') } })
    }

    loop.forEachIndexed { loopIndex, (rowIndex, colIndex) ->

        loopUpscaled[rowIndex * 2][colIndex * 2] = rows[rowIndex][colIndex]

        val (rowNext, colNext) = (loopIndex + 1).takeIf { it <= loop.lastIndex }?.let { loop[it] } ?: return@forEachIndexed

        when {
            rowIndex == rowNext && colNext == colIndex - 1 ->
                loopUpscaled[rowIndex * 2][colIndex * 2 - 1] = horizontal

            rowIndex == rowNext && colNext == colIndex + 1 ->
                loopUpscaled[rowIndex * 2][colIndex * 2 + 1] = horizontal

            colIndex == colNext && rowNext == rowIndex + 1 ->
                loopUpscaled[rowIndex * 2 + 1][colIndex * 2] = vertical

            colIndex == colNext && rowNext == rowIndex - 1 ->
                loopUpscaled[rowIndex * 2 - 1][colIndex * 2] = vertical

            else ->
                error("Shouldn't happen ($rowIndex, $colIndex) -> ($rowNext, $colNext")
        }
    }

    log(loopUpscaled.pretty())

    val perimeterPainted = loopUpscaled.map { it.toMutableList() }.toMutableList()

    // fill outer perimeter
    perimeterPainted[0].forEachIndexed { index, char ->
        if (char == '.') {
            perimeterPainted[0][index] = 'O'
        }
    }
    perimeterPainted[perimeterPainted.lastIndex].forEachIndexed { index, char ->
        if (char == '.') {
            perimeterPainted[perimeterPainted.lastIndex][index] = 'O'
        }
    }
    perimeterPainted.forEachIndexed { index, row ->
        if (row[0] == '.') {
            perimeterPainted[index][0] = 'O'
        }
    }
    perimeterPainted.forEachIndexed { index, row ->
        if (row[row.lastIndex] == '.') {
            perimeterPainted[index][row.lastIndex] = 'O'
        }
    }

    log("Initial stuff outside")
    log(perimeterPainted.pretty())

    tailrec fun paintBucket(cnv : MutableList<MutableList<Char>>) : MutableList<MutableList<Char>> {

        var newFilled = 0

        cnv.indices.forEach { rowIndex ->
            cnv[rowIndex].indices.forEach { colIndex ->
                if (cnv[rowIndex][colIndex] == 'O') {

                    if (rowIndex > 0 && cnv[rowIndex - 1][colIndex] == '.') {
                        cnv[rowIndex - 1][colIndex] = 'O'
                        newFilled++
                    }
                    if (rowIndex < cnv.lastIndex && cnv[rowIndex + 1][colIndex] == '.') {
                        cnv[rowIndex + 1][colIndex] = 'O'
                        newFilled++
                    }
                    if (colIndex > 0 && cnv[rowIndex][colIndex - 1] == '.') {
                        cnv[rowIndex][colIndex - 1] = 'O'
                        newFilled++
                    }
                    if (colIndex < cnv[rowIndex].lastIndex && cnv[rowIndex][colIndex + 1] == '.') {
                        cnv[rowIndex][colIndex + 1] = 'O'
                        newFilled++
                    }
                }
            }
        }


        log("Filled $newFilled new positions in this iteration")
//        log(cnv.pretty())

        return if (newFilled > 0) {
            paintBucket(cnv)
        } else {
            cnv
        }
    }

    val fullyPainted = paintBucket(perimeterPainted)

    log(fullyPainted.pretty())

    return fullyPainted.sumOfIndexed { rowIndex, row ->

        if (rowIndex % 2 != 0)
            return@sumOfIndexed 0L

        row.sumOfIndexed { colIndex, char ->
            1L.takeIf { colIndex % 2 == 0 && char == '.' } ?: 0
        }

    }
}

private fun main() {
    aoc("10-example1.txt", "4") { partOne(it) }
    aoc("10-example2.txt", "8") { partOne(it) }
    aoc("10-input.txt", "6860") { partOne(it) }
    aoc("10-example3.txt", "4") { partTwo(it) }
    aoc("10-example4.txt", "4") { partTwo(it) }
    aoc("10-example5.txt", "8") { partTwo(it) }
    aoc("10-example6.txt", "10") { partTwo(it) }
    aoc("10-input.txt", "343") { partTwo(it) }
}
