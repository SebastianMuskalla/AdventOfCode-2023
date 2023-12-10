private fun partOne(lines : List<String>) : Long {

    return lines.sumOf { line ->

        val wip = mutableListOf(line.split(' ').map { it.toLong() }.toMutableList())

        do {
            val previous = wip.last()
            val next = previous.zipWithNext { a, b -> b - a }.toMutableList()
            wip += next
        } while (next.any { it != 0L })

        log("Needed ${wip.size} steps to reach all 0")

        wip.last() += 0

        wip.indices.reversed().forEach { i ->
            if (i == wip.lastIndex) {
                wip[i] += 0
            } else {
                val below = wip[i + 1].last()
                val before = wip[i].last()
                wip[i] += (before + below)
            }
        }

        log(wip)
        wip[0].last()
            .also { log("Computed solution $it for $line") }
    }
}

private fun partTwo(lines : List<String>) : Long {

    return lines.sumOf { line ->

        val wip = mutableListOf(line.split(' ').map { it.toLong() }.reversed().toMutableList())

        do {
            val previous = wip.last()
            val next = previous.zipWithNext { a, b -> a - b }.toMutableList()
            wip += next
        } while (next.any { it != 0L })

        log("Needed ${wip.size} steps to reach all 0")

        wip.last() += 0

        wip.indices.reversed().forEach { i ->
            if (i == wip.lastIndex) {
                wip[i] += 0
            } else {
                val below = wip[i + 1].last()
                val before = wip[i].last()
                wip[i] += (before - below)
            }
        }

        log(wip)
        wip[0].last()
            .also { log("Computed solution $it for $line") }
    }
}

private fun main() {
    aoc("09-example.txt", "114") { partOne(it) }
    aoc("09-input.txt", "1581679977") { partOne(it) }
    aoc("09-example.txt", "2") { partTwo(it) }
    aoc("09-input.txt", "889") { partTwo(it) }
}
