private fun buildAlmanacEntry(rules : List<String>) : (Long) -> Long {

    val parsedRules = rules.map { rule ->
        val numbers = rule.split(' ')
        Triple(numbers[0].toLong(), numbers[1].toLong(), numbers[2].toLong())
    }.sortedBy { it.second }

    return fun(input : Long) : Long {
        val (ruleTarget, ruleSource, range) = parsedRules.lastOrNull { input >= it.second } ?: return input.also { log("No rule found, returning $it") }

        log("Input $input, found rule $ruleSource $ruleTarget $range")
        val diff = input - ruleSource
        return if (diff >= range) {
            input
        } else {
            ruleTarget + diff
        }.also { log("Returning $it") }
    }
}

private fun partOne(lines : List<String>) : Long {

    val seedLine = lines[0]

    val mapLines = lines.mapIndexedNotNull { index, line -> index.takeIf { line.contains(" map:") } }

    val mapRanges = (mapLines + (lines.size + 1)).zipWithNext()

    val almanac = mapRanges.map { (startIndex, endIndex) ->
        buildAlmanacEntry(lines.subList(startIndex + 1, endIndex - 1))
    }

    val seeds = seedLine.removePrefix("seeds: ").split(' ').map { it.toLong() }

    return seeds.minOf { seed ->
        var value = seed
        almanac.forEach { value = it(value) }
        value
    }
}

private infix fun LongRange.intersect(other : LongRange) : LongRange =
    (maxOf(this.first, other.first) .. minOf(this.last, other.last)).takeUnless { it.isEmpty() } ?: LongRange.EMPTY

private operator fun LongRange.minus(other : LongRange) : Collection<LongRange> {
    if (this.isEmpty()) return emptyList()
    if ((this intersect other).isEmpty()) return listOf(this) // includes case other.isEmpty()

    val partOne = (this.first ..< other.first) intersect this
    val partTwo = ((other.last + 1) .. this.last) intersect this

    return listOfNotNull(partOne.takeUnless { it.isEmpty() }, partTwo.takeUnless { it.isEmpty() })
}

private data class Rule(
    val destination : Long,
    val source : Long,
    val range : Long,
) {
    val sourceRange = source ..< source + range

    fun applyTo(number : Long) : Long {
        require(sourceRange.contains(number)) { "$number is not in $sourceRange" }
        return destination + (number - source)
    }

    fun applyTo(interval : LongRange) : Pair<LongRange, Collection<LongRange>> {

        if (interval.isEmpty()) return (LongRange.EMPTY to emptyList())

        val unmappedPart = interval - this.sourceRange
        val mappedSource = interval intersect this.sourceRange

        val mappedTarget = if (mappedSource.isEmpty()) {
            LongRange.EMPTY
        } else {
            this.applyTo(mappedSource.first) .. this.applyTo(mappedSource.last)
        }

        return (mappedTarget to unmappedPart).also { log("        Applying rule $this to $interval yields $it") }
    }
}

private fun applyRules(
    rules : Collection<Rule>,
    interval : LongRange,
) : Collection<LongRange> {

    if (interval.isEmpty()) return emptyList()

    var unmapped = setOf(interval)
    val result = mutableSetOf<LongRange>()

    rules.forEach { rule ->

        unmapped = buildSet {
            unmapped.forEach { x ->

                val (good, bad) = rule.applyTo(x)

                if (!good.isEmpty()) {
                    result += good
                }

                addAll(bad.filter { !it.isEmpty() })
            }
        }
    }

    return buildSet {
        addAll(unmapped)
        addAll(result)
    }.also { log("    Applying all rules to $interval yields $it") }
}

private fun partTwo(lines : List<String>) : Long {

    val seedLine = lines[0]

    val mapLines = lines.mapIndexedNotNull { index, line -> index.takeIf { line.contains(" map:") } }

    val mapRanges = (mapLines + (lines.size + 1)).zipWithNext()

    val rulesList = mapRanges.map { (startIndex, endIndex) ->
        lines.subList(startIndex + 1, endIndex - 1).map { ruleLine ->
            val ruleParts = ruleLine.split(' ').map { it.toLong() }
            Rule(source = ruleParts[1], destination = ruleParts[0], range = ruleParts[2])
        }
    }

    val seeds = seedLine.removePrefix("seeds: ").split(' ').map { it.toLong() }
        .fold(listOf<LongRange>() to -1L) { (accumulatorList, accumulatorValue), range ->
            if (accumulatorValue == -1L) {
                accumulatorList to range
            } else {
                accumulatorList.plusElement(accumulatorValue ..< (accumulatorValue + range)) to -1L
            }
        }
        .first

    var list = seeds

    log("Initially $list")

    rulesList.forEach { rules ->
        list = buildList {
            list.forEach { interval ->
                addAll(applyRules(rules, interval))
            }
        }
        log("After step, got $list")
    }

    return list.filter { !it.isEmpty() }.minOf { it.first }
}

private fun main() {
    aoc("05-example.txt", 35L) { partOne(it) }
    aoc("05-input.txt", 165788812L) { partOne(it) }
    aoc("05-example.txt", 46L) { partTwo(it) }
    aoc("05-input.txt", 1928058L) { partTwo(it) }
}
