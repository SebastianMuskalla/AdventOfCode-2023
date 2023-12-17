import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP

private enum class Direction(
    val rowDelta : Int,
    val colDelta : Int,
    val char : Char,
) {
    RIGHT(0, 1, '>'),
    LEFT(0, -1, '<'),
    UP(-1, 0, '^'),
    DOWN(1, 0, 'v'),
}

private fun drawWithBeams(
    matrix : List<List<Char>>,
    beams : List<Pair<Pair<Int, Int>, Direction>>,
) {

    val withBeams = matrix.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char ->

            when (beams.count { (position, _) -> position == rowIndex to colIndex }) {
                0 -> char
                1 -> beams.first { (position, _) -> position == rowIndex to colIndex }.second.char
                else -> '*'
            }

        }
    }
    log(withBeams.joinToString(separator = "\n", prefix = "\n\n", postfix = "\n\n") { it.joinToString(separator = "") })
}

private fun drawWithAllBeams(
    matrix : List<List<Char>>,
    beams : Set<Pair<Pair<Int, Int>, Direction>>,
) {

    val withBeams = matrix.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char ->
            val count = beams.count { (position, _) -> position == rowIndex to colIndex }
            when (count) {
                0 -> char
                1 -> beams.first { (position, _) -> position == rowIndex to colIndex }.second.char
//                2 -> if (char in listOf('|', '-')) char else '2'
//                in 3 .. 9 -> count.toString()[0]
//                else -> '*'
                else -> if (char != '.') char else count.toString()[0]
            }

        }
    }
    log(withBeams.joinToString(separator = "\n", prefix = "\n\n", postfix = "\n\n") { it.joinToString(separator = "") })
}

private fun drawWithEnergized(
    matrix : List<List<Char>>,
    beams : Set<Pair<Pair<Int, Int>, Direction>>,
) {

    val withBeams = matrix.indices.map { rowIndex ->
        matrix[0].indices.map { colIndex ->
            if (beams.any { it.first == rowIndex to colIndex }) '#' else '.'
        }
    }
    log(withBeams.joinToString(separator = "\n", prefix = "\n\n", postfix = "\n\n") { it.joinToString(separator = "") })
}

private fun partOne(lines : List<String>) : Long {

    val matrix = lines.map { row -> row.map { it } }

    val initial = (0 to -1) to RIGHT
    var beams = listOf(initial)
    val cellsWithBeams = mutableSetOf<Pair<Pair<Int, Int>, Direction>>()

    drawWithBeams(matrix, beams)

    while (beams.isNotEmpty()) {

        val beamsToDo = beams.minus(cellsWithBeams)

        if (beamsToDo.isEmpty()) {
            break
        }

        cellsWithBeams.addAll(beamsToDo)

        beams = buildList {

            beamsToDo.forEach { (position, direction) ->

                val newPosition = position.first + direction.rowDelta to position.second + direction.colDelta

                if (newPosition.first in matrix.indices && newPosition.second in matrix[0].indices) {

                    when (matrix[newPosition.first][newPosition.second]) {
                        '.' -> {
                            add(newPosition to direction)
                        }

                        '/' -> {
                            val newDirection = when (direction) {
                                LEFT -> DOWN
                                DOWN -> LEFT
                                RIGHT -> UP
                                UP -> RIGHT
                            }
                            add(newPosition to newDirection)
                        }

                        '\\' -> {
                            val newDirection = when (direction) {
                                LEFT -> UP
                                DOWN -> RIGHT
                                RIGHT -> DOWN
                                UP -> LEFT
                            }
                            add(newPosition to newDirection)
                        }

                        '-' -> {
                            if (direction in listOf(LEFT, RIGHT)) {
                                add(newPosition to direction)
                            } else {
                                add(newPosition to LEFT)
                                add(newPosition to RIGHT)
                            }
                        }

                        '|' -> {
                            if (direction in listOf(DOWN, UP)) {
                                add(newPosition to direction)
                            } else {
                                add(newPosition to UP)
                                add(newPosition to DOWN)
                            }
                        }
                    }
                }
            }
        }

//        drawWithBeams(matrix, beams)
    }

    drawWithAllBeams(matrix, cellsWithBeams)
    drawWithEnergized(matrix, cellsWithBeams)

    return matrix.indices.sumOf { rowIndex ->
        matrix[rowIndex].indices.sumOf { colIndex ->
            if (cellsWithBeams.any { it.first == rowIndex to colIndex }) 1L else 0L
        }
    }
//    return cellsWithBeams.map { it.first }.toSet().size
}

private fun partTwo(lines : List<String>) : Long {

    val matrix = lines.map { row -> row.map { it } }

    fun withInitial(initial : Pair<Pair<Int, Int>, Direction>) : Long {

        var beams = listOf(initial)
        val cellsWithBeams = mutableSetOf<Pair<Pair<Int, Int>, Direction>>()

        drawWithBeams(matrix, beams)

        while (beams.isNotEmpty()) {

            val beamsToDo = beams.minus(cellsWithBeams)

            if (beamsToDo.isEmpty()) {
                break
            }

            cellsWithBeams.addAll(beamsToDo)

            beams = buildList {

                beamsToDo.forEach { (position, direction) ->

                    val newPosition = position.first + direction.rowDelta to position.second + direction.colDelta

                    if (newPosition.first in matrix.indices && newPosition.second in matrix[0].indices) {

                        when (matrix[newPosition.first][newPosition.second]) {
                            '.' -> {
                                add(newPosition to direction)
                            }

                            '/' -> {
                                val newDirection = when (direction) {
                                    LEFT -> DOWN
                                    DOWN -> LEFT
                                    RIGHT -> UP
                                    UP -> RIGHT
                                }
                                add(newPosition to newDirection)
                            }

                            '\\' -> {
                                val newDirection = when (direction) {
                                    LEFT -> UP
                                    DOWN -> RIGHT
                                    RIGHT -> DOWN
                                    UP -> LEFT
                                }
                                add(newPosition to newDirection)
                            }

                            '-' -> {
                                if (direction in listOf(LEFT, RIGHT)) {
                                    add(newPosition to direction)
                                } else {
                                    add(newPosition to LEFT)
                                    add(newPosition to RIGHT)
                                }
                            }

                            '|' -> {
                                if (direction in listOf(DOWN, UP)) {
                                    add(newPosition to direction)
                                } else {
                                    add(newPosition to UP)
                                    add(newPosition to DOWN)
                                }
                            }
                        }
                    }
                }
            }

//        drawWithBeams(matrix, beams)
        }

        drawWithAllBeams(matrix, cellsWithBeams)
        drawWithEnergized(matrix, cellsWithBeams)

        return matrix.indices.sumOf { rowIndex ->
            matrix[rowIndex].indices.sumOf { colIndex ->
                if (cellsWithBeams.any { it.first == rowIndex to colIndex }) 1L else 0L
            }
        }
    }

    val initials =
        matrix.indices.flatMap {
            listOf(
                (it to -1) to RIGHT,
                (it to matrix[0].size) to LEFT,
            )
        } +
            matrix[0].indices.flatMap {
                listOf(
                    (-1 to it) to DOWN,
                    (matrix.size to it) to UP,
                )
            }

    return initials.maxOf { withInitial(it) }
}

private fun main() {
    aoc("16-example.txt", "46") { partOne(it) }
    aoc("16-input.txt", "7728") { partOne(it) }
    aoc("16-example.txt", "51") { partTwo(it) }
    // needs ~15min on my machine
    aoc("16-input.txt", "8061") { partTwo(it) }
}
