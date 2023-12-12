private fun isConsistentWith(
    pattern : String,
    specification : List<Int>,
    mask : List<Boolean>,
) : Boolean {

    val computedSpecification = buildList {
        var currentEntry = 0
        var maskIndex = 0
        pattern.forEach {
            when (it) {
                '#' -> currentEntry++
                '?' -> {
                    if (mask[maskIndex]) {
                        currentEntry++
                    } else {
                        if (currentEntry > 0) {
                            add(currentEntry)
                            currentEntry = 0
                        }
                    }
                    maskIndex++
                }

                else -> {
                    if (currentEntry > 0) {
                        add(currentEntry)
                        currentEntry = 0
                    }
                }
            }
        }
        if (currentEntry > 0) {
            add(currentEntry)
        }
    }

    return computedSpecification == specification
}

/** Slow solution: Branch on each '?' */
private fun bruteForce(
    pattern : String,
    specification : List<Int>,
    mask : List<Boolean> = emptyList(), // n-th entry = value for n-th '?' (true -> '#', false -> '.')
) : Long {
    log("Checking $pattern $specification $mask")
    if (pattern.count { it == '?' } == mask.size) {
        log("Done with recursion")
        return (if (isConsistentWith(pattern, specification, mask)) 1L else 0L).also { log(it) }
    }
    val springs = pattern.count { it == '#' } + mask.count { it }
    if (springs > specification.sum()) return 0L
    return bruteForce(pattern, specification, mask + true) + bruteForce(pattern, specification, mask + false)
}

/** A bit better: On each '?', branch on whether to place next specification group  */
private fun moreClever(
    pattern : String,
    specification : List<Int>,
    patternIndex : Int = 0, // where we are in pattern
    specificationIndex : Int = 0, // how many of the specifications have we already placed inside the pattern
) : Long {

    if (patternIndex >= pattern.length) {
        // have we placed all specifications?
        return if (specificationIndex >= specification.size) 1L else 0L
    }

    if (pattern[patternIndex] == '.') {
        return moreClever(pattern, specification, patternIndex + 1, specificationIndex)
    }

    if (specificationIndex >= specification.size) {
        // we have placed all specifications
        return if (pattern.substring(patternIndex .. pattern.lastIndex).contains('#'))
            0L // unmatched '#' in the rest of the string
        else
            1L // we are done if we replace all remaining '?' by '.'
    }

    val nextSpec = specification[specificationIndex]

    if (patternIndex + nextSpec > pattern.length) {
        // not enough space to place the next specification
        return 0L
    }

    if (pattern[patternIndex] == '#') {
        // next specification group starts here, check if that is possible
        return if ( // impossible to place specification group here
            pattern.substring(patternIndex ..< patternIndex + nextSpec).contains('.') || // '.' inside group
            (patternIndex + nextSpec <= pattern.lastIndex && pattern[patternIndex + nextSpec] == '#') // group not separated from next one
        ) {
            0L
        } else {
            // place allocation group and continue afterwards
            moreClever(pattern, specification, patternIndex + nextSpec + 1, specificationIndex + 1)
        }
    }

    // case '?' where we actually need to branch
    return moreClever(pattern, specification, patternIndex + 1, specificationIndex) + // '?' -> '.'
        // '?' -> '#'
        if ( // impossible to place specification group here
            pattern.substring(patternIndex ..< patternIndex + nextSpec).contains('.') || // '.' inside group
            (patternIndex + nextSpec <= pattern.lastIndex && pattern[patternIndex + nextSpec] == '#') // group not separated from next one
        ) {
            0L
        } else {
            // place allocation group and continue afterwards
            moreClever(pattern, specification, patternIndex + nextSpec + 1, specificationIndex + 1)
        }
}

/** Version of [moreClever] that caches results */
private fun withMemoization(
    pattern : String,
    specification : List<Int>,
) : Long {

    val memoized = mutableMapOf<Pair<Int, Int>, Long>()

    fun compute(
        patternIndex : Int,
        specificationIndex : Int,
    ) : Long {

        memoized[patternIndex to specificationIndex]?.let { return it }

        if (patternIndex >= pattern.length) {
            // have we placed all specifications?
            return (if (specificationIndex >= specification.size) 1L else 0L)
                .also { memoized[patternIndex to specificationIndex] = it }
        }

        if (pattern[patternIndex] == '.') {
            return compute(patternIndex + 1, specificationIndex)
                .also { memoized[patternIndex to specificationIndex] = it }
        }

        if (specificationIndex >= specification.size) {
            // we have placed all specifications
            return (if (pattern.substring(patternIndex .. pattern.lastIndex).contains('#'))
                0L // unmatched '#' in the rest of the string
            else
                1L // we are done if we replace all remaining '?' by '.'
                ).also { memoized[patternIndex to specificationIndex] = it }
        }

        val nextSpec = specification[specificationIndex]

        if (patternIndex + nextSpec > pattern.length) {
            // not enough space to place the next specification
            return 0L.also { memoized[patternIndex to specificationIndex] = it }
        }

        if (pattern[patternIndex] == '#') {
            // next specification group starts here, check if that is possible
            return (if ( // impossible to place specification group here
                pattern.substring(patternIndex ..< patternIndex + nextSpec).contains('.') || // '.' inside group
                (patternIndex + nextSpec <= pattern.lastIndex && pattern[patternIndex + nextSpec] == '#') // group not separated from next one
            ) {
                0L
            } else {
                // place allocation group and continue afterwards
                compute(patternIndex + nextSpec + 1, specificationIndex + 1)
            }).also { memoized[patternIndex to specificationIndex] = it }
        }

        // case '?' where we actually need to branch
        return (compute(patternIndex + 1, specificationIndex) + // '?' -> '.'
            // '?' -> '#'
            if ( // impossible to place specification group here
                pattern.substring(patternIndex ..< patternIndex + nextSpec).contains('.') || // '.' inside group
                (patternIndex + nextSpec <= pattern.lastIndex && pattern[patternIndex + nextSpec] == '#') // group not separated from next one
            ) {
                0L
            } else {
                // place allocation group and continue afterwards
                compute(patternIndex + nextSpec + 1, specificationIndex + 1)
            }).also { memoized[patternIndex to specificationIndex] = it }
    }

    return compute(0, 0)
}

private fun partOne(lines : List<String>) : Long {

    val instances = lines.map { line ->
        val (pattern, descriptionString) = line.split(' ')
        pattern to descriptionString.split(',').map { it.toInt() }
    }

    return instances.sumOf { (pattern, specification) ->
        val computed = withMemoization(pattern, specification)
        log("computed $computed instantiations for $pattern $specification")
        computed
    }
}

private fun partTwo(lines : List<String>) : Long {

    val instances = lines.map { line ->
        val (pattern, descriptionString) = line.split(' ')
        "$pattern?".repeat(5).dropLast(1) to "$descriptionString,".repeat(5).dropLast(1).split(',').map { it.toInt() }
    }

    return instances.sumOf { (pattern, specification) ->
        val computed = withMemoization(pattern, specification)
        log("computed $computed instantiations for $pattern $specification")
        computed
    }
}

private fun main() {
    aoc("12-example.txt", "21") { partOne(it) }
    aoc("12-input.txt", "7622") { partOne(it) }
    aoc("12-example.txt", "525152") { partTwo(it) }
    aoc("12-input.txt", "4964259839627") { partTwo(it) }
}
