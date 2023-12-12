private fun partOne(lines : List<String>) : Long {

    val galaxies = buildList {
        lines.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, c ->
                if (c == '#') {
                    add(rowIndex to colIndex)
                }
            }
        }
    }

    val slowRows = buildList {
        lines.forEachIndexed { rowIndex, row ->
            if ('#' !in row) {
                add(rowIndex)
            }
        }
    }

    val slowCols = buildList {
        lines[0].indices.forEach { colIndex ->
            var found = false
            lines.forEach { row ->
                if (row[colIndex] == '#') {
                    found = true
                }
            }
            if (!found) {
                add(colIndex)
            }
        }
    }

    return galaxies.sumOfIndexed { galaxy1Index, galaxy1 ->
        galaxies.sumOfIndexed inner@{ galaxy2Index, galaxy2 ->

            if (galaxy1Index <= galaxy2Index) return@inner 0L

            log("Checking $galaxy1 $galaxy2")
            val (row1, row2) = listOf(galaxy1.first, galaxy2.first).sorted()
            val (col1, col2) = listOf(galaxy1.second, galaxy2.second).sorted()

            (row2 - row1).toLong() +
                (col2 - col1).toLong() +
                slowRows.count { it in row1 .. row2 }.toLong() +
                slowCols.count { it in col1 .. col2 }.toLong()
        }
    }
}

private fun partTwo(lines : List<String>) : Long {

    val galaxies = buildList {
        lines.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, c ->
                if (c == '#') {
                    add(rowIndex to colIndex)
                }
            }
        }
    }

    val slowRows = buildList {
        lines.forEachIndexed { rowIndex, row ->
            if ('#' !in row) {
                add(rowIndex)
            }
        }
    }

    val slowCols = buildList {
        lines[0].indices.forEach { colIndex ->
            var found = false
            lines.forEach { row ->
                if (row[colIndex] == '#') {
                    found = true
                }
            }
            if (!found) {
                add(colIndex)
            }
        }
    }

    return galaxies.sumOfIndexed { galaxy1Index, galaxy1 ->
        galaxies.sumOfIndexed inner@{ galaxy2Index, galaxy2 ->

            if (galaxy1Index <= galaxy2Index) return@inner 0L

            log("Checking $galaxy1 $galaxy2")
            val (row1, row2) = listOf(galaxy1.first, galaxy2.first).sorted()
            val (col1, col2) = listOf(galaxy1.second, galaxy2.second).sorted()

            (row2 - row1).toLong() +
                (col2 - col1).toLong() +
                slowRows.count { it in row1 .. row2 }.toLong() * (1000000 - 1) +
                slowCols.count { it in col1 .. col2 }.toLong() * (1000000 - 1)
        }
    }
}

private fun main() {
    aoc("11-example.txt", "374") { partOne(it) }
    aoc("11-input.txt", "9521550") { partOne(it) }
    aoc("11-example.txt", "82000210") { partTwo(it) }
    aoc("11-input.txt", "298932923702") { partTwo(it) }
}
