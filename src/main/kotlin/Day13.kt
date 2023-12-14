private fun <T> transpose(matrix : List<List<T>>) : List<List<T>> {

    return buildList {

        matrix[0].indices.forEach { colIndex ->
            add(
                buildList {
                    matrix.indices.forEach { rowIndex ->
                        add(matrix[rowIndex][colIndex])
                    }
                }
            )
        }
    }
}

private fun isValidReflection(
    line : List<Char>,
    reflection : Int,
) : Boolean {
    require(reflection >= 0 && reflection < line.lastIndex) {
        "Trying illegal $reflection in $line of length ${line.lastIndex}"
    }

    val bound = minOf(reflection, line.size - reflection - 2)

    (0 .. bound).forEach { i ->
        if (line[reflection - i] != line[reflection + i + 1]) {
            return false
        }
    }
    return true
}

private fun findReflection(matrix : List<List<Char>>) : Int? {

    var candidates = (0 ..< matrix[0].lastIndex).toList()

    matrix.forEachIndexed { rowIndex, row ->

//        log("Candidates before line $rowIndex, ${row.joinToString(separator = "")}: $candidates")

        if (candidates.isEmpty()) {
            return null
        }

        candidates = buildList {
            candidates.forEach { candidate ->
                if (isValidReflection(row, candidate)) {
                    add(candidate)
                }
            }
        }

//        log("Candidates afterwards $candidates")
    }

    return when (candidates.size) {
        0 -> null
        1 -> candidates[0]
        else -> error("ambiguous reflections $candidates")
    }
}

private fun pretty(matrix : List<List<Char>>) : String =
    matrix.joinToString(separator = "\n", prefix = "\n", postfix = "\n\n") { line -> line.joinToString(separator = "") }

private fun partOne(lines : List<String>) : Long {

    val splitIndices = buildList {
        add(-1)
        addAll(lines.indices.filter { lines[it] == "" })
        add(lines.size)
    }

    log(splitIndices)

    val matrices = splitIndices.zipWithNext().map { (lower, upper) ->
        lines.subList(lower + 1, upper).map { it.map { char -> char } }
    }

    matrices.forEach { matrix ->
        log("MATRIX")
        log(pretty(matrix))
    }

    var verticalLines = 0L
    var horizontalLines = 0L

    log(matrices)
    matrices.forEach { matrix ->

        val verticalReflection = findReflection(matrix)

        if (verticalReflection != null) {
            verticalLines += verticalReflection + 1
            log("Found vertical reflection at $verticalReflection in ${pretty(matrix)}")
        } else {
            val transposed = transpose(matrix)
            val horizontalReflection = findReflection(transposed)
                ?: error("No vertical OR horizontal reflection found for ${pretty(matrix)}")
            log("Found horizontal reflection at $horizontalReflection in ${pretty(matrix)}")
            horizontalLines += horizontalReflection + 1
        }
    }

    return verticalLines + 100 * horizontalLines
}

private fun findReflections(matrix : List<List<Char>>) : List<Int> {

    var candidates = (0 ..< matrix[0].lastIndex).toList()

    matrix.forEachIndexed { rowIndex, row ->

//        log("Candidates before line $rowIndex, ${row.joinToString(separator = "")}: $candidates")

        if (candidates.isEmpty()) {
            return emptyList()
        }

        candidates = buildList {
            candidates.forEach { candidate ->
                if (isValidReflection(row, candidate)) {
                    add(candidate)
                }
            }
        }

//        log("Candidates afterwards $candidates")
    }

    return candidates
}

private fun partTwo(lines : List<String>) : Long {

    val splitIndices = buildList {
        add(-1)
        addAll(lines.indices.filter { lines[it] == "" })
        add(lines.size)
    }

    log(splitIndices)

    val matrices = splitIndices.zipWithNext().map { (lower, upper) ->
        lines.subList(lower + 1, upper).map { it.map { char -> char } }
    }

    matrices.forEach { matrix ->
        log("MATRIX")
        log(pretty(matrix))
    }

    var verticalLines = 0L
    var horizontalLines = 0L

    log(matrices)

    matrices.forEach outer@{ matrix ->

        val originalVerticalReflection = findReflection(matrix) ?: -1
        val originalTransposed = transpose(matrix)
        val originalHorizontalReflection = findReflection(originalTransposed) ?: -1

        (0 ..< matrix.size * matrix[0].size).forEach { replacementIndex ->

            val replacementRow = replacementIndex % matrix.size
            val replacementCol = replacementIndex / matrix.size

            val matrixWithReplacement = matrix.mapIndexed { rowIndex, row ->
                if (rowIndex != replacementRow) {
                    row
                } else {
                    row.mapIndexed { colIndex, char ->
                        if (colIndex == replacementCol) {
                            when (char) {
                                '#' -> '.'
                                '.' -> '#'
                                else -> error("unexpected $char")
                            }
                        } else {
                            char
                        }
                    }
                }
            }

            val verticalReflections = findReflections(matrixWithReplacement)
            val verticalReflectionsWithoutOriginal = verticalReflections.minus(originalVerticalReflection)

            if (verticalReflectionsWithoutOriginal.size > 1) {
                error("Ambiguous reflection")
            }

            if (verticalReflectionsWithoutOriginal.isNotEmpty()) {
                verticalLines += verticalReflectionsWithoutOriginal[0] + 1
                log("Found vertical reflection at $verticalReflections in ${pretty(matrixWithReplacement)}")
                return@outer
            } else {
                val transposed = transpose(matrixWithReplacement)

                val horizontalReflections = findReflections(transposed)
                val horizontalReflectionsWithoutOriginal = horizontalReflections.minus(originalHorizontalReflection)

                if (horizontalReflectionsWithoutOriginal.size > 1) {
                    error("Ambiguous reflection")
                }

                if (horizontalReflectionsWithoutOriginal.isNotEmpty()) {
                    log("Found horizontal reflection at $horizontalReflections in ${pretty(matrixWithReplacement)}")
                    horizontalLines += horizontalReflectionsWithoutOriginal[0] + 1
                    return@outer
                }
            }
        }
    }

    return verticalLines + 100 * horizontalLines
}

private fun main() {
    aoc("13-example.txt", "405") { partOne(it) }
    aoc("13-input.txt", "27300") { partOne(it) }
    aoc("13-example.txt", "400") { partTwo(it) }
    aoc("13-input.txt", "29276") { partTwo(it) }
}
