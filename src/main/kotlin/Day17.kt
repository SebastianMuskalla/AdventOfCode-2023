private fun partOne(lines : List<String>) : Long {

    val matrix = lines.map { row -> row.map { it.toString().toInt() } }

    val dijkstra = mutableMapOf<Triple<Int, Int, Boolean>, Pair<Long, Triple<Int, Int, Boolean>?>>().withDefault { Long.MAX_VALUE to null }

    val target = matrix.lastIndex to matrix[0].lastIndex

    dijkstra[Triple(0, 0, false)] = 0L to null
    dijkstra[Triple(0, 0, true)] = 0L to null

    val todo = mutableSetOf<Triple<Int, Int, Boolean>>()

    todo.addAll(matrix.indices.flatMap { rowIndex ->
        matrix[0].indices.flatMap { colIndex ->
            listOf(
                Triple(rowIndex, colIndex, true),
                Triple(rowIndex, colIndex, false),
            )
        }
    })

    while (todo.isNotEmpty()) {
        val minNode = todo.minBy { dijkstra.getValue(it).first }

        val minNodeInfo = dijkstra.getValue(minNode)

        todo.remove(minNode)

        val directions = if (minNode.third) { // entered vertically
            listOf(0 to 1, 0 to 2, 0 to 3, 0 to -1, 0 to -2, 0 to -3) // continue horizontally
        } else { // entered horizontally
            listOf(1 to 0, 2 to 0, 3 to 0, -1 to 0, -2 to 0, -3 to 0) // continue vertically
        }

        val neighbors = directions.mapNotNull { direction ->
            val newPosition = Triple(minNode.first + direction.first, minNode.second + direction.second, !minNode.third)

            if (newPosition.first !in matrix.indices || newPosition.second !in matrix[0].indices)
                return@mapNotNull null

            val cost : Long =
                (1 .. direction.second).sumOf { matrix[minNode.first][minNode.second + it].toLong() } +
                    ((-1).downTo(direction.second)).sumOf { matrix[minNode.first][minNode.second + it].toLong() } +
                    (1 .. direction.first).sumOf { matrix[minNode.first + it][minNode.second].toLong() } +
                    ((-1).downTo(direction.first)).sumOf { matrix[minNode.first + it][minNode.second].toLong() }

            log("Computed cost $cost of going from $minNode to $newPosition")
            newPosition to cost
        }

        neighbors.filter { it.first in todo }.forEach { (neighbor, cost) ->
            val newDistance = minNodeInfo.first + cost
            if (newDistance < dijkstra.getValue(neighbor).first) {
                dijkstra[neighbor] = newDistance to minNode
            }
        }
    }

    return minOf(
        // we need to check both versions of target
        dijkstra.getValue(Triple(target.first, target.second, true)).first,
        dijkstra.getValue(Triple(target.first, target.second, false)).first,
    )

}

private fun partTwo(lines : List<String>) : Long {

    val matrix = lines.map { row -> row.map { it.toString().toInt() } }

    val dijkstra = mutableMapOf<Triple<Int, Int, Boolean>, Pair<Long, Triple<Int, Int, Boolean>?>>().withDefault { Long.MAX_VALUE to null }

    val target = matrix.lastIndex to matrix[0].lastIndex

    dijkstra[Triple(0, 0, false)] = 0L to null
    dijkstra[Triple(0, 0, true)] = 0L to null

    val todo = mutableSetOf<Triple<Int, Int, Boolean>>()

    todo.addAll(matrix.indices.flatMap { rowIndex ->
        matrix[0].indices.flatMap { colIndex ->
            listOf(
                Triple(rowIndex, colIndex, true),
                Triple(rowIndex, colIndex, false),
            )
        }
    })

    while (todo.isNotEmpty()) {
        val minNode = todo.minBy { dijkstra.getValue(it).first }

        val minNodeInfo = dijkstra.getValue(minNode)

        todo.remove(minNode)

        val directions = (4 .. 10).flatMap {
            if (minNode.third) { // entered vertically
                listOf(0 to it, 0 to -it) // continue horizontally
            } else { // entered horizontally
                listOf(it to 0, -it to 0) // continue vertically
            }
        }

        val neighbors = directions.mapNotNull { direction ->
            val newPosition = Triple(minNode.first + direction.first, minNode.second + direction.second, !minNode.third)

            if (newPosition.first !in matrix.indices || newPosition.second !in matrix[0].indices)
                return@mapNotNull null

            val cost : Long =
                (1 .. direction.second).sumOf { matrix[minNode.first][minNode.second + it].toLong() } +
                    ((-1).downTo(direction.second)).sumOf { matrix[minNode.first][minNode.second + it].toLong() } +
                    (1 .. direction.first).sumOf { matrix[minNode.first + it][minNode.second].toLong() } +
                    ((-1).downTo(direction.first)).sumOf { matrix[minNode.first + it][minNode.second].toLong() }

            log("Computed cost $cost of going from $minNode to $newPosition")
            newPosition to cost
        }

        neighbors.filter { it.first in todo }.forEach { (neighbor, cost) ->
            val newDistance = minNodeInfo.first + cost
            if (newDistance < dijkstra.getValue(neighbor).first) {
                dijkstra[neighbor] = newDistance to minNode
            }
        }
    }

    return minOf(
        // we need to check both versions of target
        dijkstra.getValue(Triple(target.first, target.second, true)).first,
        dijkstra.getValue(Triple(target.first, target.second, false)).first,
    )

}

private fun main() {
    aoc("17-example.txt", "102") { partOne(it) }
    // takes ~1 minute
    aoc("17-input.txt", "886") { partOne(it) }
    aoc("17-example.txt", "94") { partTwo(it) }
    // takes ~1 minute
    aoc("17-input.txt", "1055") { partTwo(it) }
}
