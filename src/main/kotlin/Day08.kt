private fun partOne(lines : List<String>) : Int {

    val instructions = lines[0]

    val graph = buildMap {
        lines.subList(2, lines.size).map { line ->
            val splits = line.split(" = ")
            val source = splits[0]
            val destinations = splits[1].removeSurrounding("(", ")").split(", ").let { it[0] to it[1] }
            put(source, destinations)
        }
    }

    log(graph)

    var position = "AAA"
    generateSequence(0) { it + 1 }.forEach {
        val instruction = instructions[it % instructions.length]
        if (instruction == 'L') {
            position = graph[position]!!.first
        }
        if (instruction == 'R') {
            position = graph[position]!!.second
        }
        // log("$it: New position $position after applying $instruction")
        if (position == "ZZZ") {
            return it + 1
        }
    }
    error("unreachable")
}

private fun partTwo(lines : List<String>) : Long {

    val instructions = lines[0]

    val graph = buildMap {
        lines.subList(2, lines.size).map { line ->
            val splits = line.split(" = ")
            val source = splits[0]
            val destinations = splits[1].removeSurrounding("(", ")").split(", ").let { it[0] to it[1] }
            put(source, destinations)
        }
    }

    log(instructions)
    log(graph)

    fun lcm(
        a : Long,
        b : Long,
    ) : Long {
        val maximumResult = a * b
        var candidate = maxOf(a, b)
        while (candidate <= maximumResult) {
            if (candidate % a == 0L && candidate % b == 0L) {
                return candidate
            }
            candidate += maxOf(a, b)
        }
        return maximumResult
    }

    fun lcm(list : List<Long>) : Long =
        when (list.size) {
            1 -> list[0]
            2 -> lcm(list[0], list[1])
            // divide and conquer, baby!
            else -> lcm(lcm(list.subList(0, list.size / 2)), lcm(list.subList(list.size / 2, list.size)))
        }

    val positionsA = graph.keys.filter { it[2] == 'A' }

    var positions = positionsA
    val lastSeenZ = mutableListOf<Long>().apply { repeat(6) { add(-1) } }
    val lastSeenZCycle = mutableListOf<Long>().apply { repeat(6) { add(-1) } }

    generateSequence(0L) { it + 1 }.forEach { step ->

        val instruction = instructions[(step % instructions.length).toInt()]

        var reachedTarget = true // to detect short solutions like the given example

        positions = buildList {
            positions.forEachIndexed { i, oldPosition ->
                val newPosition = if (instruction == 'L') {
                    graph[oldPosition]!!.first
                } else {
                    graph[oldPosition]!!.second
                }

                if (newPosition[2] == 'Z') {
                    if (lastSeenZ[i] != -1L) {
                        // seen Z a second time, so we have a cycle
                        lastSeenZCycle[i] = step - lastSeenZ[i]
                    }
                    lastSeenZ[i] = step
                } else {
                    // at least one position is not Z
                    reachedTarget = false
                }
                add(newPosition)
            }
        }

        if (reachedTarget) {
            val solution = step + 1
            log("Explicitly found solution $solution")
            return solution
        }

        if (-1L !in lastSeenZCycle) {
            log("Detected a loop for each path: $lastSeenZCycle")
            return lcm(lastSeenZCycle)
        }
    }

    error("Hopefully unreachable")
}

private fun main() {
    aoc("08-example1.txt", 2) { partOne(it) }
    aoc("08-example2.txt", 6) { partOne(it) }
    aoc("08-input.txt", 21251) { partOne(it) }
    aoc("08-example3.txt", 6L) { partTwo(it) }
    aoc("08-input.txt", 11678319315857L) { partTwo(it) }
}
