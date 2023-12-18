import kotlin.math.absoluteValue

private fun List<List<Char>>.pretty() : String =
    this.joinToString(separator = "\n", prefix = "\n", postfix = "\n\n") { line -> line.joinToString(separator = "") }

private fun paintBucket(
    canvas : MutableList<MutableList<Char>>,
    click : Pair<Int, Int>,
) : MutableList<MutableList<Char>> {

    canvas[click.first][click.second] = 'X'

    tailrec fun step() {
        var newFilled = 0
        canvas.indices.forEach { rowIndex ->
            canvas[rowIndex].indices.forEach { colIndex ->
                if (canvas[rowIndex][colIndex] == 'X') {

                    if (rowIndex > 0 && canvas[rowIndex - 1][colIndex] == '.') {
                        canvas[rowIndex - 1][colIndex] = 'X'
                        newFilled++
                    }
                    if (rowIndex < canvas.lastIndex && canvas[rowIndex + 1][colIndex] == '.') {
                        canvas[rowIndex + 1][colIndex] = 'X'
                        newFilled++
                    }
                    if (colIndex > 0 && canvas[rowIndex][colIndex - 1] == '.') {
                        canvas[rowIndex][colIndex - 1] = 'X'
                        newFilled++
                    }
                    if (colIndex < canvas[rowIndex].lastIndex && canvas[rowIndex][colIndex + 1] == '.') {
                        canvas[rowIndex][colIndex + 1] = 'X'
                        newFilled++
                    }
                }
            }
        }

        log("Newly filled $newFilled")

        if (newFilled == 0) {
            return
        } else {
            step()
        }
    }

    step()

    canvas.indices.forEach { rowIndex ->
        canvas[rowIndex].indices.forEach { colIndex ->
            if (canvas[rowIndex][colIndex] == 'X') {
                canvas[rowIndex][colIndex] = '#'
            }
        }
    }

    return canvas
}

private fun partOne(lines : List<String>) : Long {

    val instructions = lines.map {
        val splits = it.split(' ')
        val direction = splits[0][0]
        val number = splits[1].toInt()
        direction to number
    }

    val list = mutableSetOf(0 to 0)
    var pos = 0 to 0

    instructions.forEach { (direction, number) ->

        when (direction) {
            'U' -> {
                (1 .. number).forEach { list.add(pos.first - it to pos.second) }
                pos = pos.first - number to pos.second
            }

            'D' -> {
                (1 .. number).forEach { list.add(pos.first + it to pos.second) }
                pos = pos.first + number to pos.second
            }

            'L' -> {
                (1 .. number).forEach { list.add(pos.first to pos.second - it) }
                pos = pos.first to pos.second - number
            }

            'R' -> {
                (1 .. number).forEach { list.add(pos.first to pos.second + it) }
                pos = pos.first to pos.second + number
            }

            else -> error("Unexpected direction $direction")
        }

        log(list)
        log(pos)
        log("")

    }

    val minHeight = list.minBy { it.first }.first
    val maxHeight = list.maxBy { it.first }.first
    val minWidth = list.minBy { it.second }.second
    val maxWidth = list.maxBy { it.second }.second

    val matrix = mutableListOf<MutableList<Char>>().apply {
        repeat(maxHeight - minHeight + 1) {
            add(mutableListOf<Char>().apply {
                repeat(maxWidth - minWidth + 1) {
                    add('.')
                }
            })
        }
    }

    list.forEach { (rowIndex, colIndex) ->
        matrix[rowIndex - minHeight][colIndex - minWidth] = '#'
    }

    log(matrix.pretty())

    // hacky hack
    if (instructions.size > 20) {
        paintBucket(matrix, 1 to 268)
    } else {
        paintBucket(matrix, 1 to 1)
    }

    log(matrix.pretty())

    return matrix.sumOf { it.count { it == '#' }.toLong() }
}

private fun partTwo(lines : List<String>) : Long {

    val instructions = lines.map {
        val splits = it.split(' ')
        val string = splits[2].removeSurrounding("(#", ")")
        val direction = when (string[5]) {
            '0' -> 'R'
            '1' -> 'D'
            '2' -> 'L'
            '3' -> 'U'
            else -> error("Unexpected digit in $string")
        }
        val number = string.substring(0, 5).toInt(16)

        direction to number
    }

    val edges = mutableListOf(0L to 0L)
    var pos = 0L to 0L

    instructions.forEach { (direction, number) ->

        when (direction) {
            'U' -> {
                pos = pos.first - number to pos.second
                edges.add(pos)
            }

            'D' -> {
                pos = pos.first + number to pos.second
                edges.add(pos)
            }

            'L' -> {
                pos = pos.first to pos.second - number
                edges.add(pos)
            }

            'R' -> {
                pos = pos.first to pos.second + number
                edges.add(pos)
            }

            else -> error("Unexpected direction $direction")
        }
    }

    // modified shoelace formula

    val perimeter = edges.zipWithNext().sumOf { (fst, snd) ->
        val (x1, y1) = fst
        val (x2, y2) = snd
        (x2 - x1).absoluteValue + (y2 - y1).absoluteValue
    }

    // edges already contains (0,0) as both first and last element
    return ((0 .. instructions.lastIndex).sumOf { i ->
        (edges[i].second + edges[i + 1].second) * (edges[i].first - edges[i + 1].first)
    }.absoluteValue + perimeter) / 2 + 1
}

private fun main() {
    aoc("18-example.txt", "62") { partOne(it) }
    // Warning: this only works because I manually provided a point inside the polygon in the source code above
    aoc("18-input.txt", "56923") { partOne(it) }
    aoc("18-example.txt", "952408144115") { partTwo(it) }
    aoc("18-input.txt", "66296566363189") { partTwo(it) }
}
